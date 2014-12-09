package com.creditcloud.contract;

import com.creditcloud.claim.model.Claim;
import com.creditcloud.common.utils.DTOUtils;
import com.creditcloud.config.Fee;
import com.creditcloud.config.FeeConfig;
import com.creditcloud.config.enums.FeePeriod;
import com.creditcloud.config.utils.FeeUtils;
import com.creditcloud.contract.api.ContractService;
import com.creditcloud.contract.api.ContractTemplateService;
import com.creditcloud.contract.dao.ContractDAO;
import com.creditcloud.contract.dao.ContractSealDAO;
import com.creditcloud.contract.local.ApplicationBean;
import com.creditcloud.contract.local.TagBridge;
import com.creditcloud.contract.utils.PDFUtils;
import com.creditcloud.fund.api.FundAccountService;
import com.creditcloud.fund.model.FundAccount;
import com.creditcloud.model.PersonalInfo;
import com.creditcloud.model.PlaceInfo;
import com.creditcloud.model.client.Client;
import com.creditcloud.model.constant.TimeConstant;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.loan.InvestStatus;
import com.creditcloud.model.loan.CreditAssign;
import com.creditcloud.model.loan.Duration;
import com.creditcloud.model.loan.Invest;
import com.creditcloud.model.loan.Loan;
import com.creditcloud.model.loan.LoanRepayment;
import com.creditcloud.model.loan.LoanRequest;
import com.creditcloud.model.loan.Repayment;
import com.creditcloud.model.misc.RealmEntity;
import com.creditcloud.model.user.User;
import com.creditcloud.model.user.corporation.CorporationInfo;
import com.creditcloud.model.user.corporation.CorporationUser;
import com.creditcloud.model.user.info.CareerInfo;
import com.creditcloud.model.user.info.CompanyInfo;
import com.creditcloud.model.user.info.UserInfo;
import com.creditcloud.tag.api.TagService;
import com.creditcloud.tag.model.Tag;
import com.creditcloud.user.api.CorporationUserService;
import com.creditcloud.user.api.UserInfoService;
import com.creditcloud.user.api.UserService;
import com.itextpdf.text.DocumentException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

@Remote
@Stateless
public class ContractServiceBean
        implements ContractService {

    private static final String LOANREQUEST_TITLE_TEMPLATE = "%s与%s的借款合同_%s_%s";

    private static final String LOAN_TITLE_TEMPLATE = "%s与%s的借款合同_%s_%s";

    private static final String LOAN_TITLE_TEMPLATE_FOR_MORTGAGEE = "%s的借款合同_%s_%s";

    private static final String BROKERAGE_CONTRACT_TITLE_TEMPLATE = "居间服务协议";

    @Inject
    org.slf4j.Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    ContractDAO contractDAO;

    @EJB
    UserService userService;

    @EJB
    UserInfoService userInfoService;

    @EJB
    FundAccountService fundAccountService;

    @EJB
    ContractTemplateService contractTemplateService;

    @EJB
    TagService tagService;

    @EJB
    TagBridge tagBridge;

    @EJB
    CorporationUserService corporationUserService;

    @EJB
    ContractSealDAO contractSealDAO;

    @Asynchronous
    public void generateAssignContract(Client client, Invest originalInvest, Invest invest, Loan loan, List<Repayment> repayments, FeeConfig feeConfig, String templateId, CreditAssign creditAssign) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, invest.getId()), ContractType.CREDITASSIGN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Invest already exist.[investId={}]", invest.getId());
        }
        expandInvest(invest);

        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s与%s的债权转让合同_%s_%s", new Object[]{originalInvest.getUser().getName(), invest.getUser().getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.INVEST, invest.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.CREDITASSIGN);

        Map<ContractParty, User> userRelated = new HashMap();
        userRelated.put(ContractParty.FIRST, originalInvest.getUser());
        userRelated.put(ContractParty.SECOND, invest.getUser());
        contract.setUserRelated(userRelated);

        Map<String, Object> values = generateValue(null, client, loan, invest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfFieldForAssign(contract.getId(), client, loan, originalInvest, invest, repayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values, creditAssign);
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
        try {
            byte[] out = PDFUtils.claimTemplateToPdf(fields, template.getContent(), this.appBean.getWatermark());
//            byte[] out = PDFUtils.templateToPdf(fields, template.getContent(), this.appBean.getWatermark());
            contract.setContent(out);
            
           
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        //为受让人增加一份合同
        this.contractDAO.addNew(contract);
        
        //在为转让人添加一份合同，相当于是一式两份
        contract.setEntity(new RealmEntity(Realm.INVEST, originalInvest.getId()));
        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }
    
    @Asynchronous
    public void testGenerateContract(Client client, Loan loan, String templateId, List<ContractSeal> seals) {
        StopWatch sw = new StopWatch();
        sw.start();

        this.appBean.checkClientCode(client.getCode());

        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s与%s", new Object[]{templateId, TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.CONTRACTTEMPLATE, templateId));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);

        Map<ContractParty, User> userRelated = new HashMap();
        userRelated.put(ContractParty.FIRST, loan.getLoanRequest().getUser());
        contract.setUserRelated(userRelated);
        if ((templateId != null) && (this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, false) != null)) {
            ContractTemplate template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, true);
            this.logger.debug("Specific template used.[templateId={}]", templateId);
        } else {
            return;
        }
        try {
            ContractTemplate template = null;
            byte[] out = PDFUtils.insertSeal(this.appBean.getClientCode(), template.getContent(), seals, this.appBean.getContractSealConfig());
            contract.setContent(out);

            this.contractDAO.addNew(contract);

            this.logger.info("test Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(ContractServiceBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ContractServiceBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Asynchronous
    public void generateLoanContract(Client client, Invest invest, Loan loan, List<Repayment> repayments, FeeConfig feeConfig, String templateId, Map<String, Object> values, List<ContractSeal> seals) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());
        if (!validInvest(invest)) {
            this.logger.debug("invalid invest {}, status {}, will not generate contract.", invest.getId(), invest.getStatus());
            return;
        }
        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, invest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Invest already exist.[investId={}]", invest.getId());
        }
        this.logger.info("prepare to apply personal seal");
        System.out.println("prepare to apply personal seal");

        expandInvest(invest);

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

        values = generateValue(values, client, loan, invest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, invest, repayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);
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
            out = PDFUtils.templateToPdf(fields, template.getContent(), this.appBean.getWatermark());

            outWithSeal = PDFUtils.insertSeal(this.appBean.getClientCode(), out, seals, this.appBean.getContractSealConfig());
        } catch (IOException | DocumentException ex) {
            this.logger.error("Contract generated content failed! [exception={}]", ex);
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
    public void generateLoanContract(Client client, LoanRequest loanRequest, List<Repayment> repaymentList, FeeConfig feeConfig, String templateId, byte[] writing, int page, float percentX, float percentY) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.LOANREQUEST, loanRequest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Loan already exist.[loanRequestId={}]", loanRequest.getId());
        }
        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s与%s的借款合同_%s_%s", new Object[]{client.getName(), loanRequest.getUser().getName(), loanRequest.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.LOANREQUEST, loanRequest.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);
        contract.setContent(generateContent(contract, client, loanRequest, repaymentList, feeConfig, templateId, writing, page, percentX, percentY));

        Map<ContractParty, User> userRelated = new HashMap();
        userRelated.put(ContractParty.FIRST, loanRequest.getUser());
        contract.setUserRelated(userRelated);

        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    @Asynchronous
    public void generateLoanContract(Client client, Invest currentInvest, List<Invest> investList, Loan loan, List<LoanRepayment> repaymentList, FeeConfig feeConfig, String templateId) {
        generateLoanContract(client, currentInvest, investList, loan, repaymentList, feeConfig, templateId, new ArrayList());
    }

    @Asynchronous
    public void generateLoanContract(Client client, Invest currentInvest, List<Invest> investList, Loan loan, List<LoanRepayment> repaymentList, FeeConfig feeConfig, String templateId, List<ContractSeal> seals) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());
        if (!validInvest(currentInvest)) {
            this.logger.debug("invalid invest {}, status {}, will not generate contract.", currentInvest.getId(), currentInvest.getStatus());
            return;
        }
        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, currentInvest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Loan already exist.[loanId={}]", loan.getId());
        }
        expandInvest(currentInvest);
        for (Invest invest : investList) {
            expandInvest(invest);
        }
        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s与%s的借款合同_%s_%s", new Object[]{loan.getLoanRequest().getUser().getName(), currentInvest.getUser().getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.INVEST, currentInvest.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);

        Map<String, Object> values = generateValue(null, client, loan, currentInvest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, currentInvest.getUser().getId(), loan, repaymentList, filterInvest(investList), feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);
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
            out = PDFUtils.templateToPdfForInvestor(fields, template.getContent(), this.appBean.getWatermark());
            if (seals != null) {
                outWithSeal = PDFUtils.insertSeal(this.appBean.getClientCode(), out, seals, this.appBean.getContractSealConfig());
            }
        } catch (IOException | DocumentException ex) {
            this.logger.error("Contract generated content failed! [exception={}]", ex);
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
        generateLoanContract(client, investList, loan, repaymentList, feeConfig, templateId, new ArrayList());
    }

    @Asynchronous
    public void generateLoanContract(Client client, List<Invest> investList, Loan loan, List<LoanRepayment> repaymentList, FeeConfig feeConfig, String templateId, List<ContractSeal> seals) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.LOAN, loan.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Loan already exist.[loanId={}]", loan.getId());
        }
        for (Invest invest : investList) {
            expandInvest(invest);
        }
        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s的借款合同_%s_%s", new Object[]{loan.getLoanRequest().getUser().getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.LOAN, loan.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);

        Map<String, Object> values = generateValue(null, client, loan, investList, repaymentList, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, repaymentList, filterInvest(investList), feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);
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
            out = PDFUtils.templateToPdfForObligator(fields, template.getContent(), this.appBean.getWatermark());
            if (seals != null) {
                outWithSeal = PDFUtils.insertSeal(this.appBean.getClientCode(), out, seals, this.appBean.getContractSealConfig());
            }
        } catch (IOException | DocumentException ex) {
            this.logger.error("Contract generated content failed! [exception={}]", ex);
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
    public void generateLoanContract(Client client, Invest invest, Loan loan, List<Repayment> repayments, FeeConfig feeConfig, String templateId) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, invest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Invest already exist.[investId={}]", invest.getId());
        }
        expandInvest(invest);

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

        Map<String, Object> values = generateValue(null, client, loan, invest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, invest, repayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);
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
        try {
            byte[] out = PDFUtils.templateToPdf(fields, template.getContent(), this.appBean.getWatermark());
            contract.setContent(out);
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    @Asynchronous
    public void generateLoanContract(Client client, Invest invest, Loan loan, List<Repayment> repayments, FeeConfig feeConfig, String templateId, Map<String, Object> values) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());
        if (!validInvest(invest)) {
            this.logger.debug("invalid invest {}, status {}, will not generate contract.", invest.getId(), invest.getStatus());
            return;
        }
        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, invest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Invest already exist.[investId={}]", invest.getId());
        }
        expandInvest(invest);

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

        values = generateValue(values, client, loan, invest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, invest, repayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);
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
        try {
            byte[] out = PDFUtils.templateToPdf(fields, template.getContent(), this.appBean.getWatermark());
            contract.setContent(out);
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    public byte[] getContractContent(String clientCode, String contractId) {
        this.appBean.checkClientCode(clientCode);
        com.creditcloud.contract.entities.Contract result = (com.creditcloud.contract.entities.Contract) this.contractDAO.find(contractId);
        if (result != null) {
            return result.getContent();
        }
        return null;
    }

    public Contract getLoanContract(String clientCode, RealmEntity contractEntity, boolean withContent) {
        return getContractByEntityAndType(clientCode, contractEntity, withContent, ContractType.LOAN);
    }
    
    public Contract getAssignContract(String clientCode, RealmEntity contractEntity, boolean withContent) {
        return getContractByEntityAndType(clientCode, contractEntity, withContent, ContractType.CREDITASSIGN);
    }

    protected List<Invest> filterInvest(List<Invest> investList) {
        CollectionUtils.filter(investList, new Predicate() {
            public boolean evaluate(Object object) {
                Invest invest = (Invest) object;
                return (invest.getStatus().equals(InvestStatus.FROZEN)) || (invest.getStatus().equals(InvestStatus.FINISHED)) || (invest.getStatus().equals(InvestStatus.SETTLED));
            }
        });
        return investList;
    }

    protected boolean validInvest(Invest invest) {
        return (invest.getStatus().equals(InvestStatus.FROZEN)) || (invest.getStatus().equals(InvestStatus.FINISHED)) || (invest.getStatus().equals(InvestStatus.SETTLED));
    }

    protected void generationDuration(Loan loan, Map<String, Object> values) {
        Duration duration = loan.getLoanRequest().getDuration();
        if (duration != null) {
            StringBuilder loanDuration = new StringBuilder();
            if (duration.getYears() > 0) {
                loanDuration.append(duration.getYears()).append("年");
            }
            if (duration.getMonths() > 0) {
                loanDuration.append(duration.getMonths()).append("月");
            }
            if (duration.getDays() > 0) {
                loanDuration.append(duration.getDays()).append("天");
            }
            values.put("loanDuration", loanDuration.toString());
        }
    }

    protected Map<String, Object> generateValue(Map<String, Object> values, Client client, Loan loan, Invest invest, FeeConfig feeConfig, Contract contract) {
        if (values == null) {
            values = new HashMap();
        }
        generationDuration(loan, values);
        values.put("loanRequest", loan.getLoanRequest());

        User user = loan.getLoanRequest().getUser();
        List<FundAccount> accounts = this.fundAccountService.listAccountByUser(client.getCode(), user.getId());
        for (FundAccount account : accounts) {
            if (account.isDefaultAccount()) {
                values.put("obligatorFundAccount", account);
                break;
            }
        }
        List<Tag> keyValueTags = this.tagService.listTagByRealm(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, loan.getLoanRequest().getId()), Realm.KEYVALUE);
        for (Tag tag : keyValueTags) {
            String[] split = tag.getName().split(":");
            if (split.length == 2) {
                values.put(split[0], split[1]);
            } else {
                this.logger.warn("Invalid KeyValue tag name.[name={}]", tag.getName());
            }
        }
        if ((loan.getLoanRequest().getGuaranteeEntity() != null) && (!StringUtils.isEmpty(loan.getLoanRequest().getGuaranteeEntity().getEntityId()))) {
            String corpId = loan.getLoanRequest().getGuaranteeEntity().getEntityId();
            CorporationUser corp = this.corporationUserService.getById(this.appBean.getClientCode(), corpId);
            CorporationInfo info = this.corporationUserService.getInfoById(this.appBean.getClientCode(), corpId);
            if (corp != null) {
                values.put("guaranteeCorporationUser", corp);
                generateGuaranteeLegalPerson(values, corp.getLegalPersonId());
            }
            if (info != null) {
                values.put("guaranteeCorporationInfo", info);
            }
        }
        if ((loan.getLoanRequest().getRequestProvider() != null) && (!StringUtils.isEmpty(loan.getLoanRequest().getRequestProvider().getEntityId()))) {
            String corpId = loan.getLoanRequest().getRequestProvider().getEntityId();
            CorporationUser corp = this.corporationUserService.getById(this.appBean.getClientCode(), corpId);
            CorporationInfo info = this.corporationUserService.getInfoById(this.appBean.getClientCode(), corpId);
            if (corp != null) {
                values.put("requestProviderCorporationUser", corp);
                generateRequestProviderLegalPerson(values, corp.getLegalPersonId());
            }
            if (info != null) {
                values.put("requestProviderCorporationInfo", info);
            }
        }
        UserInfo info = this.userInfoService.getUserInfoByUserId(this.appBean.getClientCode(), user.getId());
        if (info != null) {
            values.put("userInfo", info);
        }
        String userId = this.tagBridge.getShadowBorrower(loan.getLoanRequest().getId());
        if (!StringUtils.isEmpty(userId)) {
            User shadowBorrower = this.userService.findByUserId(this.appBean.getClientCode(), userId);
            if (shadowBorrower == null) {
                this.logger.warn("shadowBorrower null");
            } else {
                contract.setName(String.format("%s与%s的借款合同_%s_%s", new Object[]{invest.getUser().getName(), shadowBorrower.getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

                values.put("shadowBorrower", shadowBorrower);
            }
        }
        return generateFeeToValue(values, loan, invest, feeConfig);
    }

    protected Map<String, Object> generateValue(Map<String, Object> values, Client client, Loan loan, List<Invest> invests, List<LoanRepayment> repayments, FeeConfig feeConfig, Contract contract) {
        if (values == null) {
            values = new HashMap();
        }
        generationDuration(loan, values);
        values.put("loanRequest", loan.getLoanRequest());

        User user = loan.getLoanRequest().getUser();
        List<FundAccount> accounts = this.fundAccountService.listAccountByUser(client.getCode(), user.getId());
        for (FundAccount account : accounts) {
            if (account.isDefaultAccount()) {
                values.put("obligatorFundAccount", account);
                break;
            }
        }
        List<Tag> keyValueTags = this.tagService.listTagByRealm(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, loan.getLoanRequest().getId()), Realm.KEYVALUE);
        for (Tag tag : keyValueTags) {
            String[] split = tag.getName().split(":");
            if (split.length == 2) {
                values.put(split[0], split[1]);
            } else {
                this.logger.warn("Invalid KeyValue tag name.[name={}]", tag.getName());
            }
        }
        if ((loan.getLoanRequest().getGuaranteeEntity() != null) && (!StringUtils.isEmpty(loan.getLoanRequest().getGuaranteeEntity().getEntityId()))) {
            String corpId = loan.getLoanRequest().getGuaranteeEntity().getEntityId();
            CorporationUser corp = this.corporationUserService.getById(this.appBean.getClientCode(), corpId);
            CorporationInfo info = this.corporationUserService.getInfoById(this.appBean.getClientCode(), corpId);
            if (corp != null) {
                values.put("guaranteeCorporationUser", corp);
                generateGuaranteeLegalPerson(values, corp.getLegalPersonId());
            }
            if (info != null) {
                values.put("guaranteeCorporationInfo", info);
            }
        }
        if ((loan.getLoanRequest().getRequestProvider() != null) && (!StringUtils.isEmpty(loan.getLoanRequest().getRequestProvider().getEntityId()))) {
            String corpId = loan.getLoanRequest().getRequestProvider().getEntityId();
            CorporationUser corp = this.corporationUserService.getById(this.appBean.getClientCode(), corpId);
            CorporationInfo info = this.corporationUserService.getInfoById(this.appBean.getClientCode(), corpId);
            if (corp != null) {
                values.put("requestProviderCorporationUser", corp);
                generateRequestProviderLegalPerson(values, corp.getLegalPersonId());
            }
            if (info != null) {
                values.put("requestProviderCorporationInfo", info);
            }
        }
        UserInfo info = this.userInfoService.getUserInfoByUserId(this.appBean.getClientCode(), user.getId());
        if (info != null) {
            values.put("userInfo", info);
        }
        String userId = this.tagBridge.getShadowBorrower(loan.getLoanRequest().getId());
        if (!StringUtils.isEmpty(userId)) {
            User shadowBorrower = this.userService.findByUserId(this.appBean.getClientCode(), userId);
            if (shadowBorrower == null) {
                this.logger.warn("shadowBorrower null");
            } else {
                contract.setName(String.format("%s的借款合同_%s_%s", new Object[]{shadowBorrower.getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

                values.put("shadowBorrower", shadowBorrower);
            }
        }
        return generateFeeToValue(values, loan, invests, repayments, feeConfig);
    }

    private Map<String, String> generateExcendValue(Client client, Claim claim) {
        Map<String, String> extendvalues = new HashMap();
        if (null != claim) {
            CorporationInfo corporationInfo = this.corporationUserService.getInfoById(client.getCode(), claim.getUserId());
            extendvalues.put("JKRCompanyName", claim.getCorporationUser().getName());
            extendvalues.put("JKROrgCode", claim.getCorporationUser().getOrgCode());
            extendvalues.put("JKRAddress", null == corporationInfo.getAddress() ? "" : corporationInfo.getAddress());
            String JKRLegalRepresentative = claim.getCorporationUser().getUser().getName();
            if (StringUtils.isNotBlank(claim.getCorporationUser().getLegalPersonId())) {
                User legalUser = this.userService.findByUserId(client.getCode(), claim.getCorporationUser().getLegalPersonId());
                if (null != legalUser) {
                    JKRLegalRepresentative = legalUser.getName();
                }
            }
            extendvalues.put("JKRLegalRepresentative", null == JKRLegalRepresentative ? "" : JKRLegalRepresentative);
            extendvalues.put("claimContractName", null == claim.getContractName() ? "" : claim.getContractName());
            extendvalues.put("claimContractCode", null == claim.getContractCode() ? "" : claim.getContractCode());
            extendvalues.put("claimSignedDate", null == claim.getSignedDate() ? "" : PDFUtils.toPdfDateString(claim.getSignedDate()));
            extendvalues.put("claimSignedAddress", null == claim.getSignedAddress() ? "" : claim.getSignedAddress());
            extendvalues.put("claimTotalAmount", null == claim.getTotalAmount() ? "" : claim.getTotalAmount().toString());
            extendvalues.put("claimBalance", null == claim.getBalance() ? "" : claim.getBalance().toEngineeringString());
            extendvalues.put("claimStartDate", null == claim.getStartDate() ? "" : PDFUtils.toPdfDateString(claim.getStartDate()));
            extendvalues.put("claimEndDate", null == claim.getEndDate() ? "" : PDFUtils.toPdfDateString(claim.getEndDate()));
        }
        return extendvalues;
    }

    private Map<String, Object> generateFeeToValue(Map<String, Object> values, Loan loan, Invest invest, FeeConfig feeConfig) {
        Fee manageFee = feeConfig.getLoanManageFee();
        if (manageFee != null) {
            values.put("loanManageFee", manageFee);
        }
        return values;
    }

    private Map<String, Object> generateFeeToValue(Map<String, Object> values, Loan loan, List<Invest> invests, List<LoanRepayment> repayments, FeeConfig feeConfig) {
        Fee manageFee = feeConfig.getLoanManageFee();
        if (manageFee != null) {
            values.put("loanManageFee", manageFee);

            BigDecimal loanManageFeeMonthly = BigDecimal.ZERO;
            if (manageFee.getPeriod() == FeePeriod.MONTHLY) {
                for (Invest invest : invests) {
                    loanManageFeeMonthly = loanManageFeeMonthly.add(FeeUtils.calculate(manageFee, invest.getAmount()));
                }
            }
            values.put("loanManageFeeMonthly", loanManageFeeMonthly);
        }
        return values;
    }

    private byte[] generateContent(Contract contract, Client client, LoanRequest loanRequest, List<Repayment> repaymentList, FeeConfig feeConfig, String templateId, byte[] writing, int page, float percentX, float percentY) {
        HashMap<String, String> values = new HashMap();
        if (StringUtils.isEmpty(loanRequest.getSerial())) {
            values.put("no", client.getCode() + contract.getId().substring(0, 8).toUpperCase());
        } else {
            values.put("no", loanRequest.getSerial());
        }
        values.put("clientUrl", client.getUrl());

        User second = loanRequest.getUser();
        values.put("secondName", second.getName());
        values.put("secondLoginName", second.getLoginName());
        values.put("secondIdNumber", second.getIdNumber());
        values.put("secondMobile", second.getMobile());

        UserInfo secondInfo = this.userInfoService.getUserInfoByUserId(this.appBean.getClientCode(), second.getId());
        if (secondInfo != null) {
            PersonalInfo personalInfo = secondInfo.getPersonal();
            if (personalInfo != null) {
                PlaceInfo placeInfo = personalInfo.getPlace();
                if (placeInfo != null) {
                    values.put("secondCurrentAddress", !StringUtils.isEmpty(placeInfo.getCurrentAddress()) ? placeInfo.getCurrentAddress() : "");
                }
            }
            CareerInfo careerInfo = secondInfo.getCareer();
            if (careerInfo != null) {
                values.put("secondPosition", !StringUtils.isEmpty(careerInfo.getPosition()) ? careerInfo.getPosition() : "");
                CompanyInfo companyInfo = careerInfo.getCompany();
                values.put("secondCompanyName", !StringUtils.isEmpty(companyInfo.getName()) ? companyInfo.getName() : "");
                values.put("secondCompanyAddress", !StringUtils.isEmpty(companyInfo.getAddress()) ? companyInfo.getAddress() : "");
            }
        }
        values.put("loanRequestAmount", "人民币" + loanRequest.getAmount() + "元整");
        values.put("loanRequestRate", String.format("%1$.1f", new Object[]{Float.valueOf(loanRequest.getRate() / 100.0F)}));

        Date date = loanRequest.getTimeSubmit();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Duration duration = loanRequest.getDuration();
        c.add(5, duration.getDays());
        c.add(1, duration.getYears());
        c.add(2, duration.getMonths());

        values.put("loanRequestTimeSubmit", TimeConstant.CHINESE_DATE_FORMAT.format(date));
        values.put("loanRequestTimeFinished", TimeConstant.CHINESE_DATE_FORMAT.format(c.getTime()));

        values.put("timeNow", TimeConstant.CHINESE_DATE_FORMAT.format(new Date()));
        values.put("loanRequestTimeSubmit", TimeConstant.CHINESE_DATE_FORMAT.format(Long.valueOf(loanRequest.getTimeSubmit().getTime())));

        values.put("loanRequestDuration", String.valueOf(duration.getTotalMonths()));
        if ((templateId == null) || (this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, false) == null)) {
            this.logger.warn("pdf template by Id [] not found", templateId);
            return null;
        }
        ContractTemplate template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, true);
        this.logger.debug("Specific template used.[templateId={}]", templateId);

        List<PDFUtils.Table> tables = new ArrayList();

        tables.add(toTable(loanRequest, repaymentList));

        return PDFUtils.output(values, tables, template.getContent(), writing, page, percentX, percentY);
    }

    private PDFUtils.Table toTable(LoanRequest loanRequest, List<Repayment> repaymentList) {
        List<List<String>> cells = new ArrayList();
        List<String> header = new ArrayList();
        header.add("还款期数");
        header.add("还款日期");
        header.add("本金");
        header.add("利息");
        header.add("手续费");
        header.add("合计");
        cells.add(header);

        double totalFee = loanRequest.getAmount() * 0.04D;
        int i = 0;
        for (int n = repaymentList.size(); i < n; i++) {
            double fee = i == 0 ? totalFee : 0.0D;
            List<String> cell = new ArrayList();
            Repayment repayment = (Repayment) repaymentList.get(i);
            cell.add("第" + String.valueOf(i + 1) + "期");
            cell.add(TimeConstant.SIMPLE_DATE_FORMAT.format(repayment.getDueDate().toDate()));
            cell.add("￥ " + repayment.getAmountPrincipal());
            cell.add("￥ " + repayment.getAmountInterest());
            cell.add("￥ " + fee);
            cell.add("￥ " + repayment.getAmountPrincipal().add(repayment.getAmountInterest()).add(new BigDecimal(fee)));
            cells.add(cell);
        }
        List<String> cell = new ArrayList();
        cell.add("合计");
        cell.add("");
        cell.add("￥ " + String.valueOf(loanRequest.getAmount()));
        cell.add("￥ " + String.format("%1$.2f", new Object[]{Float.valueOf(loanRequest.getRate() * loanRequest.getAmount() / 10000.0F)}));
        cell.add("￥ " + String.format("%1$.2f", new Object[]{Double.valueOf(totalFee)}));
        cell.add("￥ " + String.format("%1$.2f", new Object[]{Double.valueOf(totalFee + loanRequest.getAmount() + loanRequest.getRate() * loanRequest.getAmount() / 10000.0F)}));
        cells.add(cell);

        PDFUtils.Table table = new PDFUtils.Table("附件：还款计划表", cells);

        table.setWeights(new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F});

        return table;
    }

    public ContractSeal generatePersonalSeal(Client client, User user, int page, int x, int y) {
        this.appBean.checkClientCode(client.getCode());
        return ContractSeal.generatePersonContractSeal(user, page, x, y);
    }

    private void generateRequestProviderLegalPerson(Map<String, Object> values, String legalPersonId) {
        if (StringUtils.isEmpty(legalPersonId)) {
            return;
        }
        User user = this.userService.findByUserId(this.appBean.getClientCode(), legalPersonId);
        if (user != null) {
            values.put("requestProviderLegalPerson", user);
        }
    }

    private void generateGuaranteeLegalPerson(Map<String, Object> values, String legalPersonId) {
        if (StringUtils.isEmpty(legalPersonId)) {
            return;
        }
        User user = this.userService.findByUserId(this.appBean.getClientCode(), legalPersonId);
        if (user != null) {
            values.put("guaranteeLegalPerson", user);
        }
    }

    protected void expandInvest(Invest invest) {
        User investor = invest.getUser();
        if (investor == null) {
            String userId = invest.getUserId();
            investor = this.userService.findByUserId(this.appBean.getClientCode(), userId);
            invest.setUser(investor);
        }
    }

    public void generateLoanContract(Client client, Invest invest, Loan loan, List<Repayment> repayments, FeeConfig feeConfig, String templateId, Map<String, Object> values, List<ContractSeal> seals, Claim claim) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());
        if (!validInvest(invest)) {
            this.logger.debug("invalid invest {}, status {}, will not generate contract.", invest.getId(), invest.getStatus());
            return;
        }
        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, invest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Invest already exist.[investId={}]", invest.getId());
        }
        this.logger.info("prepare to apply personal seal");
        System.out.println("prepare to apply personal seal");

        expandInvest(invest);

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

        values = generateValue(values, client, loan, invest, feeConfig, contract);
        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, invest, repayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);

        fields.extendValues = generateExcendValue(client, claim);
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

            outWithSeal = PDFUtils.insertSeal(this.appBean.getClientCode(), out, seals, this.appBean.getContractSealConfig());
        } catch (IOException | DocumentException ex) {
            this.logger.error("Contract generated content failed! [exception={}]", ex);
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

    public void generateLoanContract(Client client, Invest invest, Loan loan, List<Repayment> repayments, FeeConfig feeConfig, String templateId, Claim claim) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, invest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Invest already exist.[investId={}]", invest.getId());
        }
        expandInvest(invest);

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

        Map<String, Object> values = generateValue(null, client, loan, invest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, invest, repayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);

        fields.extendValues = generateExcendValue(client, claim);
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
        try {
            byte[] out = PDFUtils.claimTemplateToPdf(fields, template.getContent(), this.appBean.getWatermark());
            contract.setContent(out);
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    public void generateLoanContract(Client client, Invest invest, Loan loan, List<Repayment> repayments, FeeConfig feeConfig, String templateId, Map<String, Object> values, Claim claim) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());
        if (!validInvest(invest)) {
            this.logger.debug("invalid invest {}, status {}, will not generate contract.", invest.getId(), invest.getStatus());
            return;
        }
        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, invest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Invest already exist.[investId={}]", invest.getId());
        }
        expandInvest(invest);

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

        values = generateValue(values, client, loan, invest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, invest, repayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);

        fields.extendValues = generateExcendValue(client, claim);
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
        try {
            byte[] out = PDFUtils.claimTemplateToPdf(fields, template.getContent(), this.appBean.getWatermark());
            contract.setContent(out);
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    public void generateLoanContract(Client client, Invest currentInvest, List<Invest> investList, Loan loan, List<LoanRepayment> repayments, FeeConfig feeConfig, String templateId, Claim claim) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());
        if (!validInvest(currentInvest)) {
            this.logger.debug("invalid invest {}, status {}, will not generate contract.", currentInvest.getId(), currentInvest.getStatus());
            return;
        }
        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.INVEST, currentInvest.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Loan already exist.[loanId={}]", loan.getId());
        }
        expandInvest(currentInvest);
        for (Invest invest : investList) {
            expandInvest(invest);
        }
        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s与%s的借款合同_%s_%s", new Object[]{loan.getLoanRequest().getUser().getName(), currentInvest.getUser().getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.INVEST, currentInvest.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);

        Map<String, Object> values = generateValue(null, client, loan, currentInvest, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, currentInvest.getUser().getId(), loan, repayments, filterInvest(investList), feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);

        fields.extendValues = generateExcendValue(client, claim);
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
        try {
            byte[] out = PDFUtils.templateToPdfForInvestor(fields, template.getContent(), this.appBean.getWatermark());
            contract.setContent(out);
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        Map<ContractParty, User> userRelated = new HashMap();
        userRelated.put(ContractParty.FIRST, currentInvest.getUser());
        userRelated.put(ContractParty.SECOND, loan.getLoanRequest().getUser());
        contract.setUserRelated(userRelated);

        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    public void generateLoanContract(Client client, List<Invest> investList, Loan loan, List<LoanRepayment> repayments, FeeConfig feeConfig, String templateId, Claim claim) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.LOAN, loan.getId()), ContractType.LOAN);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("Contract for Loan already exist.[loanId={}]", loan.getId());
        }
        for (Invest invest : investList) {
            expandInvest(invest);
        }
        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName(String.format("%s的借款合同_%s_%s", new Object[]{loan.getLoanRequest().getUser().getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date())}));

        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.LOAN, loan.getId()));
        contract.setTimeCreated(new Date());
        contract.setType(ContractType.LOAN);

        Map<String, Object> values = generateValue(null, client, loan, investList, repayments, feeConfig, contract);

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, repayments, filterInvest(investList), feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);

        fields.extendValues = generateExcendValue(client, claim);
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
        try {
            byte[] out = PDFUtils.templateToPdfForObligator(template.getType() == null ? ContractType.LOAN : template.getType(), fields, template.getContent(), this.appBean.getWatermark());
            contract.setContent(out);
        } catch (IOException | DocumentException ex) {
            this.logger.error("Can't fullfil the template!", ex);
        }
        Map<ContractParty, User> userRelated = new HashMap();
        userRelated.put(ContractParty.SECOND, loan.getLoanRequest().getUser());
        contract.setUserRelated(userRelated);

        this.contractDAO.addNew(contract);

        this.logger.info("Contract generated.[contractId={}][contractName={}][time={}]", new Object[]{contract.getId(), contract.getName(), Long.valueOf(sw.getTime())});
    }

    public Contract getBrokerageContract(String clientCode, RealmEntity contractEntity, boolean withContent) {
        return getContractByEntityAndType(clientCode, contractEntity, withContent, ContractType.BROKERAGE);
    }

    public Contract getLoanClientContract(String clientCode, RealmEntity contractEntity, boolean withContent) {
        return getContractByEntityAndType(clientCode, contractEntity, withContent, ContractType.LOAN_CLIENT);
    }

    public Contract getAdvanceRepayContract(String clientCode, RealmEntity contractEntity, boolean withContent) {
        return getContractByEntityAndType(clientCode, contractEntity, withContent, ContractType.ADVANCE_REPAY);
    }

    public Contract getContractByEntityAndType(String clientCode, RealmEntity contractEntity, boolean withContent, ContractType contractType) {
        this.appBean.checkClientCode(clientCode);
        Contract result = null;
        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(clientCode, contractEntity, contractType);
        if ((results != null) && (!results.isEmpty())) {
            com.creditcloud.contract.entities.Contract entity = (com.creditcloud.contract.entities.Contract) results.get(0);

            this.logger.debug("getContract {} time created: {}", entity.getId(), entity.getTimeCreated());

            result = new Contract();
            result.setId(entity.getId());
            result.setClient(this.appBean.getClient());
            result.setName(entity.getName());
            result.setType(entity.getContractType());
            result.setEntity(DTOUtils.getRealmEntity(entity.getRealmEntity()));
            result.setTimeCreated(entity.getTimeCreated());
            Map<ContractParty, User> users = new HashMap();
            for (Map.Entry<ContractParty, String> entry : entity.getUserIdRelated().entrySet()) {
                users.put(entry.getKey(), this.userService.findByUserId(this.appBean.getClientCode(), (String) entry.getValue()));
            }
            result.setUserRelated(users);
            if (withContent) {
                result.setContent(entity.getContent());
            }
            if (results.size() > 1) {
                this.logger.warn("Multiple Contract found for RealmEntity.[RealmEntity={}]", contractEntity);
            }
        }
        return result;
    }

    @Asynchronous
    public void generateBrokerageContract(Client client, Loan loan, List<LoanRepayment> loanRepayments, FeeConfig feeConfig, String templateId, List<ContractSeal> seals) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.appBean.checkClientCode(client.getCode());

        List<com.creditcloud.contract.entities.Contract> results = this.contractDAO.findByEntityAndType(client.getCode(), new RealmEntity(Realm.LOAN, loan.getId()), ContractType.BROKERAGE);
        if ((results != null) && (!results.isEmpty())) {
            this.logger.warn("BrokerageContract for Loan already exist.[LoanId={}]", loan.getId());
        }
        Contract contract = new Contract();

        contract.setId(UUID.randomUUID().toString());
        contract.setName("居间服务协议");

        contract.setType(ContractType.BROKERAGE);
        contract.setClient(client);
        contract.setEntity(new RealmEntity(Realm.LOAN, loan.getId()));
        contract.setTimeCreated(new Date());

        Map<ContractParty, User> userRelated = new HashMap();

        userRelated.put(ContractParty.SECOND, loan.getLoanRequest().getUser());
        contract.setUserRelated(userRelated);

        Map<String, Object> values = generateValue(null, client, loan, null, feeConfig, contract);

        contract.setName("居间服务协议");

        PDFUtils.Fields fields = PDFUtils.convertToPdfField(contract.getId(), client, loan, loanRepayments, feeConfig, this.appBean.getClientConfig(), loan.getTimeSettled() != null ? loan.getTimeSettled() : new Date(), values);
        ContractTemplate template;
        if ((templateId != null) && (this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, false) != null)) {
            template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateId, true);
            this.logger.debug("Specific template used.[templateId={}]", templateId);
        } else {
            Tag templateTag = this.tagService.refer(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, loan.getLoanRequest().getId()), Realm.CONTRACTTEMPLATE);
            if ((templateTag == null) || (this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), false) == null)) {
                template = this.contractTemplateService.getDefault(this.appBean.getClientCode(), ContractType.BROKERAGE);
                this.logger.debug("Default template used.");
            } else {
                template = this.contractTemplateService.getById(this.appBean.getClientCode(), templateTag.getName(), true);
                this.logger.debug("Assigned tempalte loaded.[templateId={}]", template.getId());
            }
        }
        byte[] out = null;
        byte[] outWithSeal = null;
        try {
            out = PDFUtils.templateToPdfForObligator(template.getType() == null ? ContractType.LOAN : template.getType(), fields, template.getContent(), this.appBean.getWatermark());

            outWithSeal = PDFUtils.insertSeal(this.appBean.getClientCode(), out, seals, this.appBean.getContractSealConfig());
        } catch (IOException | DocumentException ex) {
            this.logger.error("Contract generated content failed! [exception={}]", ex);
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
}
