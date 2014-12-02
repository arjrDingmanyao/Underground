package com.creditcloud.contract;

import com.creditcloud.claim.model.Claim;
import com.creditcloud.config.ContractSealConfig;
import com.creditcloud.config.FeeConfig;
import com.creditcloud.contract.api.ContractExtendService;
import com.creditcloud.contract.api.ContractTemplateService;
import com.creditcloud.contract.dao.ContractDAO;
import com.creditcloud.contract.local.ApplicationBean;
import com.creditcloud.contract.local.ContractExtendBean;
import com.creditcloud.contract.local.TagBridge;
import com.creditcloud.contract.utils.PDFUtils;
import com.creditcloud.model.client.Client;
import com.creditcloud.model.constant.TimeConstant;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.loan.Invest;
import com.creditcloud.model.loan.Loan;
import com.creditcloud.model.loan.LoanRepayment;
import com.creditcloud.model.loan.Repayment;
import com.creditcloud.model.misc.RealmEntity;
import com.creditcloud.model.user.User;
import com.creditcloud.model.user.corporation.CorporationUser;
import com.creditcloud.model.util.Enums;
import com.creditcloud.tag.api.TagService;
import com.creditcloud.tag.model.Tag;
import com.creditcloud.user.api.CorporationUserService;
import com.creditcloud.user.api.UserService;
import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;

@Remote
@Stateless
public class ContractExtendServiceBean
        implements ContractExtendService {

    private static final String LOANREQUEST_TITLE_TEMPLATE = "%s与%s的借款合同_%s_%s";

    private static final String LOAN_TITLE_TEMPLATE = "%s与%s的借款合同_%s_%s";

    private static final String LOAN_TITLE_TEMPLATE_FOR_MORTGAGEE = "%s的借款合同_%s_%s";

    @EJB
    TagBridge tagBridge;

    @EJB
    ApplicationBean appBean;

    @EJB
    UserService userService;

    @EJB
    ContractDAO contractDAO;

    @EJB
    ContractTemplateService contractTemplateService;

    @EJB
    TagService tagService;

    @EJB
    ContractExtendBean contractExtendBean;

    @EJB
    CorporationUserService corporationService;

    @Inject
    Logger logger;

    @Asynchronous
    public void generateLoanContract(Client client, Invest currentInvest, List<Invest> investList, Loan loan, List<LoanRepayment> repaymentList, FeeConfig feeConfig, String templateId) {
        List<ContractSeal> seals = generateContractSeal(loan);
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());
        if (!this.contractExtendBean.validInvest(currentInvest)) {
            this.logger.debug("invalid invest {}, status {}, will not generate contract.", currentInvest.getId(), currentInvest.getStatus());
            return;
        }
        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, currentInvest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Loan already exist.[loanId={}]", loan.getId());
        }
        this.contractExtendBean.expandInvest(currentInvest);
        for (Invest invest : investList) {
            this.contractExtendBean.expandInvest(invest);
        }
        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s与%s的借款合同_%s_%s", new Object[]{loan.getLoanRequest().getUser().getName(), currentInvest.getUser().getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.INVEST, currentInvest.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);

        Map<String, Object> values = this.contractExtendBean.generateValue(null, client, loan, currentInvest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, currentInvest.getUser().getId(), loan, repaymentList, this.contractExtendBean.filterInvest(investList), feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);
        ContractTemplate template;
        if ((templateId != null) && (this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, false) != null)) {
            template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, true);
            this.logger.debug("Specific template used.[templateId={}]", templateId);
        } else {
            Tag templateTag = this.tagService.refer(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, loan.getLoanRequest().getId()), Realm.CONTRACTTEMPLATE_O2O);
            if ((templateTag == null) || (this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), false) == null)) {
                template = this.contractTemplateService.getDefault(this.appBean.getClientCode(), null);
                this.logger.debug("Default template used.");
            } else {
                template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), true);
                this.logger.debug("Assigned tempalte loaded.[templateId={}]", template.getId());
            }
        }
        byte[] out = null;
        byte[] outWithSeal = null;
        try {
            out = PDFUtils.templateToPdfForInvestorExtend(fields, template.getContent(), this.appBean.getWatermark());
            if (CollectionUtils.isNotEmpty(seals)) {
                outWithSeal = PDFUtils.insertSeal(this.appBean.getClientCode(), out, seals, this.appBean.getContractSealConfig());
            }
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        if ((outWithSeal == null) || (outWithSeal.length == 0)) {
            this.logger.info("Contract generated content with seal failed");
            contract.setContent(out);
        } else {
            this.logger.info("Contract generated content with seal success");
            contract.setContent(outWithSeal);
        }
        Map<ContractParty, User> userRelated = new HashMap();
        userRelated.put(ContractParty.FIRST, currentInvest.getUser());
        userRelated.put(ContractParty.SECOND, loan.getLoanRequest().getUser());
        contract.setUserRelated(userRelated);

        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    @Asynchronous
    public void generateLoanContract(Client client, List<Invest> investList, Loan loan, List<LoanRepayment> repaymentList, FeeConfig feeConfig, String templateId) {
        List<ContractSeal> seals = generateContractSeal(loan);

        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.LOAN, loan.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Loan already exist.[loanId={}]", loan.getId());
        }
        for (Invest invest : investList) {
            this.contractExtendBean.expandInvest(invest);
        }
        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s的借款合同_%s_%s", new Object[]{loan.getLoanRequest().getUser().getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.LOAN, loan.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);

        Map<String, Object> values = this.contractExtendBean.generateValue(null, client, loan, investList, repaymentList, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, repaymentList, this.contractExtendBean.filterInvest(investList), feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);
        ContractTemplate template;
        if ((templateId != null) && (this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, false) != null)) {
            template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, true);
            this.logger.debug("Specific template used.[templateId={}]", templateId);
        } else {
            Tag templateTag = this.tagService.refer(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, loan.getLoanRequest().getId()), Realm.CONTRACTTEMPLATE_O2M);
            if ((templateTag == null) || (this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), false) == null)) {
                template = this.contractTemplateService.getDefault(this.appBean.getClientCode(), null);
                this.logger.debug("Default template used.");
            } else {
                template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), true);
                this.logger.debug("Assigned tempalte loaded.[templateId={}]", template.getId());
            }
        }
        byte[] out = null;
        byte[] outWithSeal = null;
        try {
            out = PDFUtils.templateToPdfForInvestorExtend(fields, template.getContent(), this.appBean.getWatermark());
            if (CollectionUtils.isNotEmpty(seals)) {
                outWithSeal = PDFUtils.insertSeal(this.appBean.getClientCode(), out, seals, this.appBean.getContractSealConfig());
            }
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        if ((outWithSeal == null) || (outWithSeal.length == 0)) {
            this.logger.info("Contract generated content with seal failed");
            contract.setContent(out);
        } else {
            this.logger.info("Contract generated content with seal success");
            contract.setContent(outWithSeal);
        }
        Map<ContractParty, User> userRelated = new HashMap();
        userRelated.put(ContractParty.SECOND, loan.getLoanRequest().getUser());
        contract.setUserRelated(userRelated);

        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    @Asynchronous
    public void generateMainLoanContract(Client client, Invest invest, Loan loan, List<Repayment> repayments, FeeConfig feeConfig, String templateId) {
        List<ContractSeal> seals = generateContractSeal(loan);

        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, invest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Invest already exist.[investId={}]", invest.getId());
        }
        this.contractExtendBean.expandInvest(invest);

        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s与%s的借款合同_%s_%s", new Object[]{invest.getUser().getName(), loan.getLoanRequest().getUser().getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.INVEST, invest.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);

        Map<ContractParty, User> userRelated = new HashMap();
        userRelated.put(ContractParty.FIRST, invest.getUser());
        userRelated.put(ContractParty.SECOND, loan.getLoanRequest().getUser());
        contract.setUserRelated(userRelated);

        Map<String, Object> values = this.contractExtendBean.generateValue(null, client, loan, invest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, invest, repayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);
        ContractTemplate template;
        if ((templateId != null) && (this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, false) != null)) {
            template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, true);
            this.logger.debug("Specific template used.[templateId={}]", templateId);
        } else {
            Tag templateTag = this.tagService.refer(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, loan.getLoanRequest().getId()), Realm.CONTRACTTEMPLATE_O2O);
            if ((templateTag == null) || (this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), false) == null)) {
                template = this.contractTemplateService.getDefault(this.appBean.getClientCode(), null);
                this.logger.debug("Default template used.");
            } else {
                template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), true);
                this.logger.debug("Assigned tempalte loaded.[templateId={}]", template.getId());
            }
        }
        byte[] out = null;
        byte[] outWithSeal = null;
        try {
            out = PDFUtils.templateToPdfForInvestorExtend(fields, template.getContent(), this.appBean.getWatermark());
            if (CollectionUtils.isNotEmpty(seals)) {
                outWithSeal = PDFUtils.insertSeal(this.appBean.getClientCode(), out, seals, this.appBean.getContractSealConfig());
            }
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        if ((outWithSeal == null) || (outWithSeal.length == 0)) {
            this.logger.info("Contract generated content with seal failed");
            contract.setContent(out);
        } else {
            this.logger.info("Contract generated content with seal success");
            contract.setContent(outWithSeal);
        }
        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    @Asynchronous
    public void generateLoanContract(Client client, Invest invest, Loan loan, List<Repayment> repayments, FeeConfig feeConfig, String templateId, Claim claim) {
        List<ContractSeal> seals = generateContractSeal(loan);
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, invest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Invest already exist.[investId={}]", invest.getId());
        }
        this.contractExtendBean.expandInvest(invest);

        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s与%s的借款合同_%s_%s", new Object[]{invest.getUser().getName(), loan.getLoanRequest().getUser().getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.INVEST, invest.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);

        Map<ContractParty, User> userRelated = new HashMap();
        userRelated.put(ContractParty.FIRST, invest.getUser());
        userRelated.put(ContractParty.SECOND, loan.getLoanRequest().getUser());
        contract.setUserRelated(userRelated);

        Map<String, Object> values = this.contractExtendBean.generateValue(null, client, loan, invest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, invest, repayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);

        fields.extendValues = this.contractExtendBean.generateExcendValue(client, claim);
        fields.RepaymentPlanList = claim.getRepaymentPlans();
        ContractTemplate template;
        if ((templateId != null) && (this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, false) != null)) {
            template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, true);
            this.logger.debug("Specific template used.[templateId={}]", templateId);
        } else {
            Tag templateTag = this.tagService.refer(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, loan.getLoanRequest().getId()), Realm.CONTRACTTEMPLATE);
            if ((templateTag == null) || (this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), false) == null)) {
                template = this.contractTemplateService.getDefault(this.appBean.getClientCode(), null);
                this.logger.debug("Default template used.");
            } else {
                template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), true);
                this.logger.debug("Assigned tempalte loaded.[templateId={}]", template.getId());
            }
        }
        byte[] out = null;
        byte[] outWithSeal = null;
        try {
            out = PDFUtils.claimTemplateToPdf(fields, template.getContent(), this.appBean.getWatermark());
            if (CollectionUtils.isNotEmpty(seals)) {
                outWithSeal = PDFUtils.insertSeal(this.appBean.getClientCode(), out, seals, this.appBean.getContractSealConfig());
            }
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        if ((outWithSeal == null) || (outWithSeal.length == 0)) {
            this.logger.info("Contract generated content with seal failed");
            contract.setContent(out);
        } else {
            this.logger.info("Contract generated content with seal success");
            contract.setContent(outWithSeal);
        }
        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    public List<ContractSeal> generateContractSeal(Loan loan) {
        List<ContractSeal> seals = null;
        if (this.appBean.getClientConfig().getClientFeatures().isEnablePDFSignature()) {
            User user = loan.getLoanRequest().getUser();

            User cp = null;
            RealmEntity guaranteeEntity = loan.getLoanRequest().getGuaranteeEntity();
            if (guaranteeEntity != null) {
                cp = this.userService.findByUserId(this.appBean.getClientCode(), guaranteeEntity.getEntityId());
            }
            seals = new ArrayList();
            ContractSealConfig contractSealConfig = this.appBean.getContractSealConfig();
            loan.getLoanRequest().getGuaranteeEntity();
            this.logger.info("enable contract seal config");
            if ((contractSealConfig != null) && (contractSealConfig.getContractSeal() != null)) {
                com.creditcloud.config.ContractSeal[] sealconfigs = contractSealConfig.getContractSeal();
                for (com.creditcloud.config.ContractSeal seal : sealconfigs) {
                    ContractParty party = (ContractParty) Enums.getEnumByNameOrNull(ContractParty.class, seal.getParty());
                    if (seal.isPersonalSeal()) {
                        switch(party.ordinal()){
                            case 1:
                                seals.add(this.contractExtendBean.generatePersonalSeal(this.appBean.getClient(), user, seal.getPage(), seal.getX(), seal.getY()));
                                break;
                            case 2:
                                if (cp != null) {
                                    seals.add(ContractSeal.generateContractSeal(seal.getCode(), seal.getPage(), seal.getX(), seal.getY()));
                                }
                                break;
                            case 3:
                                if (user.isEnterprise()) {
                                    CorporationUser cuser = this.corporationService.getById(this.appBean.getClientCode(), user.getId());
                                    if ((cuser != null) && (StringUtils.isNotEmpty(cuser.getContractSealCode()))) {
                                        seals.add(ContractSeal.generateContractSeal(cuser.getContractSealCode(), seal.getPage(), seal.getX(), seal.getY()));
                                    }
                                } else {
                                    seals.add(this.contractExtendBean.generatePersonalSeal(this.appBean.getClient(), user, seal.getPage(), seal.getX(), seal.getY()));
                                }
                                break;
                        }
                    } else {
                        seals.add(ContractSeal.generateContractSeal(seal.getCode(), seal.getPage(), seal.getX(), seal.getY()));
                    }
                }
            }
            this.logger.info("contract seals {}", seals);
        }
        return seals;
    }
}
