package com.creditcloud.contract.local;

import com.creditcloud.claim.model.Claim;
import com.creditcloud.config.Fee;
import com.creditcloud.config.FeeConfig;
import com.creditcloud.config.enums.FeePeriod;
import com.creditcloud.config.utils.FeeUtils;
import com.creditcloud.contract.Contract;
import com.creditcloud.contract.ContractSeal;
import com.creditcloud.contract.utils.PDFUtils;
import com.creditcloud.fund.api.FundAccountService;
import com.creditcloud.fund.model.FundAccount;
import com.creditcloud.model.client.Client;
import com.creditcloud.model.constant.TimeConstant;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.loan.InvestStatus;
import com.creditcloud.model.loan.Duration;
import com.creditcloud.model.loan.Invest;
import com.creditcloud.model.loan.Loan;
import com.creditcloud.model.loan.LoanFee;
import com.creditcloud.model.loan.LoanRepayment;
import com.creditcloud.model.misc.RealmEntity;
import com.creditcloud.model.user.User;
import com.creditcloud.model.user.corporation.CorporationInfo;
import com.creditcloud.model.user.corporation.CorporationUser;
import com.creditcloud.model.user.info.UserInfo;
import com.creditcloud.service.ManagerLoanService;
import com.creditcloud.tag.api.TagService;
import com.creditcloud.tag.model.Tag;
import com.creditcloud.user.api.CorporationUserService;
import com.creditcloud.user.api.UserInfoService;
import com.creditcloud.user.api.UserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@LocalBean
@Stateless
public class ContractExtendBean
{
  private static final String LOANREQUEST_TITLE_TEMPLATE = "%s与%s的借款合同_%s_%s";
  private static final String LOAN_TITLE_TEMPLATE = "%s与%s的借款合同_%s_%s";
  private static final String LOAN_TITLE_TEMPLATE_FOR_MORTGAGEE = "%s的借款合同_%s_%s";
  @Inject
  Logger logger;
  @EJB
  ApplicationBean appBean;
  @EJB
  UserService userService;
  @EJB
  TagService tagService;
  @EJB
  FundAccountService fundAccountService;
  @EJB
  CorporationUserService corporationUserService;
  @EJB
  TagBridge tagBridge;
  @EJB
  UserInfoService userInfoService;
  @EJB
  ManagerLoanService loanService;
  
  public boolean validInvest(Invest invest)
  {
    return (invest.getStatus().equals(InvestStatus.FROZEN)) || (invest.getStatus().equals(InvestStatus.FINISHED)) || (invest.getStatus().equals(InvestStatus.SETTLED));
  }
  
  public void expandInvest(Invest invest)
  {
    User investor = invest.getUser();
    if (investor == null)
    {
      String userId = invest.getUserId();
      investor = this.userService.findByUserId(this.appBean.getClientCode(), userId);
      invest.setUser(investor);
    }
  }
  
  public List<Invest> filterInvest(List<Invest> investList)
  {
    CollectionUtils.filter(investList, new Predicate()
    {
      public boolean evaluate(Object object)
      {
        Invest invest = (Invest)object;
        return (invest.getStatus().equals(InvestStatus.FROZEN)) || (invest.getStatus().equals(InvestStatus.FINISHED)) || (invest.getStatus().equals(InvestStatus.SETTLED));
      }
    });
    return investList;
  }
  
  public Map<String, Object> generateValue(Map<String, Object> values, Client client, Loan loan, Invest invest, FeeConfig feeConfig, Contract contract)
  {
    if (values == null) {
      values = new HashMap();
    }
    generationDuration(loan, values);
    values.put("loanRequest", loan.getLoanRequest());
    

    User user = loan.getLoanRequest().getUser();
    List<FundAccount> accounts = this.fundAccountService.listAccountByUser(client.getCode(), user.getId());
    for (FundAccount account : accounts) {
      if (account.isDefaultAccount())
      {
        values.put("obligatorFundAccount", account);
        break;
      }
    }
    List<Tag> keyValueTags = this.tagService.listTagByRealm(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, loan.getLoanRequest().getId()), Realm.KEYVALUE);
    for (Tag tag : keyValueTags)
    {
      String[] split = tag.getName().split(":");
      if (split.length == 2) {
        values.put(split[0], split[1]);
      } else {
        this.logger.warn("Invalid KeyValue tag name.[name={}]", tag.getName());
      }
    }
    if ((loan.getLoanRequest().getGuaranteeEntity() != null) && (!StringUtils.isEmpty(loan.getLoanRequest().getGuaranteeEntity().getEntityId())))
    {
      String corpId = loan.getLoanRequest().getGuaranteeEntity().getEntityId();
      CorporationUser corp = this.corporationUserService.getById(this.appBean.getClientCode(), corpId);
      CorporationInfo info = this.corporationUserService.getInfoById(this.appBean.getClientCode(), corpId);
      if (corp != null)
      {
        values.put("guaranteeCorporationUser", corp);
        generateGuaranteeLegalPerson(values, corp.getLegalPersonId());
      }
      if (info != null) {
        values.put("guaranteeCorporationInfo", info);
      }
    }
    if ((loan.getLoanRequest().getRequestProvider() != null) && (!StringUtils.isEmpty(loan.getLoanRequest().getRequestProvider().getEntityId())))
    {
      String corpId = loan.getLoanRequest().getRequestProvider().getEntityId();
      CorporationUser corp = this.corporationUserService.getById(this.appBean.getClientCode(), corpId);
      CorporationInfo info = this.corporationUserService.getInfoById(this.appBean.getClientCode(), corpId);
      if (corp != null)
      {
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
    if (!StringUtils.isEmpty(userId))
    {
      User shadowBorrower = this.userService.findByUserId(this.appBean.getClientCode(), userId);
      if (shadowBorrower == null)
      {
        this.logger.warn("shadowBorrower null");
      }
      else
      {
        contract.setName(String.format("%s与%s的借款合同_%s_%s", new Object[] { invest.getUser().getName(), shadowBorrower.getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date()) }));
        



        values.put("shadowBorrower", shadowBorrower);
      }
    }
    return generateFeeToValue(values, loan, invest, feeConfig);
  }
  
  private void generationDuration(Loan loan, Map<String, Object> values)
  {
    Duration duration = loan.getLoanRequest().getDuration();
    if (duration != null)
    {
      StringBuilder loanDuration = new StringBuilder();
      if (duration.getYears() > 0) {
        loanDuration.append(duration.getYears()).append("年");
      }
      if (duration.getMonths() > 0) {
        loanDuration.append(duration.getMonths()).append("个月");
      }
      if (duration.getDays() > 0) {
        loanDuration.append(duration.getDays()).append("天");
      }
      values.put("loanDuration", loanDuration.toString());
    }
  }
  
  private void generateGuaranteeLegalPerson(Map<String, Object> values, String legalPersonId)
  {
    if (StringUtils.isEmpty(legalPersonId)) {
      return;
    }
    User user = this.userService.findByUserId(this.appBean.getClientCode(), legalPersonId);
    if (user != null) {
      values.put("guaranteeLegalPerson", user);
    }
  }
  
  private void generateRequestProviderLegalPerson(Map<String, Object> values, String legalPersonId)
  {
    if (StringUtils.isEmpty(legalPersonId)) {
      return;
    }
    User user = this.userService.findByUserId(this.appBean.getClientCode(), legalPersonId);
    if (user != null) {
      values.put("requestProviderLegalPerson", user);
    }
  }
  
  private Map<String, Object> generateFeeToValue(Map<String, Object> values, Loan loan, Invest invest, FeeConfig feeConfig)
  {
    LoanFee actualFee = this.loanService.getLoanFee(this.appBean.getClientCode(), loan.getLoanRequest().getId());
    if (actualFee != null) {
      values.put("loanActualFee", actualFee);
    }
    Fee manageFee = feeConfig.getLoanManageFee();
    if (manageFee != null) {
      values.put("loanManageFee", manageFee);
    }
    return values;
  }
  
  public Map<String, Object> generateValue(Map<String, Object> values, Client client, Loan loan, List<Invest> invests, List<LoanRepayment> repayments, FeeConfig feeConfig, Contract contract)
  {
    if (values == null) {
      values = new HashMap();
    }
    generationDuration(loan, values);
    values.put("loanRequest", loan.getLoanRequest());
    

    User user = loan.getLoanRequest().getUser();
    List<FundAccount> accounts = this.fundAccountService.listAccountByUser(client.getCode(), user.getId());
    for (FundAccount account : accounts) {
      if (account.isDefaultAccount())
      {
        values.put("obligatorFundAccount", account);
        break;
      }
    }
    List<Tag> keyValueTags = this.tagService.listTagByRealm(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, loan.getLoanRequest().getId()), Realm.KEYVALUE);
    for (Tag tag : keyValueTags)
    {
      String[] split = tag.getName().split(":");
      if (split.length == 2) {
        values.put(split[0], split[1]);
      } else {
        this.logger.warn("Invalid KeyValue tag name.[name={}]", tag.getName());
      }
    }
    UserInfo info = this.userInfoService.getUserInfoByUserId(this.appBean.getClientCode(), user.getId());
    if (info != null) {
      values.put("userInfo", info);
    }
    String userId = this.tagBridge.getShadowBorrower(loan.getLoanRequest().getId());
    if (!StringUtils.isEmpty(userId))
    {
      User shadowBorrower = this.userService.findByUserId(this.appBean.getClientCode(), userId);
      if (shadowBorrower == null)
      {
        this.logger.warn("shadowBorrower null");
      }
      else
      {
        contract.setName(String.format("%s的借款合同_%s_%s", new Object[] { shadowBorrower.getName(), loan.getTitle(), TimeConstant.SIMPLE_CHINESE_DATE_FORMAT.format(new Date()) }));
        


        values.put("shadowBorrower", shadowBorrower);
      }
    }
    return generateFeeToValue(values, loan, invests, repayments, feeConfig);
  }
  
  private Map<String, Object> generateFeeToValue(Map<String, Object> values, Loan loan, List<Invest> invests, List<LoanRepayment> repayments, FeeConfig feeConfig)
  {
    LoanFee actualFee = this.loanService.getLoanFee(this.appBean.getClientCode(), loan.getLoanRequest().getId());
    if (actualFee != null) {
      values.put("loanActualFee", actualFee);
    }
    Fee manageFee = feeConfig.getLoanManageFee();
    if (manageFee != null)
    {
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
  
  public ContractSeal generatePersonalSeal(Client client, User user, int page, int x, int y)
  {
    this.appBean.checkClientCode(client.getCode());
    return ContractSeal.generatePersonContractSeal(user, page, x, y);
  }
  
  public Map<String, String> generateExcendValue(Client client, Claim claim)
  {
    Map<String, String> extendvalues = new HashMap();
    if (null != claim)
    {
      CorporationInfo corporationInfo = this.corporationUserService.getInfoById(client.getCode(), claim.getUserId());
      extendvalues.put("JKRCompanyName", claim.getCorporationUser().getName());
      extendvalues.put("JKROrgCode", claim.getCorporationUser().getOrgCode());
      extendvalues.put("JKRAddress", null == corporationInfo.getAddress() ? "" : corporationInfo.getAddress());
      String JKRLegalRepresentative = claim.getCorporationUser().getUser().getName();
      if (StringUtils.isNotBlank(claim.getCorporationUser().getLegalPersonId()))
      {
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
}
