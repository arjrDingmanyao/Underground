package com.creditcloud.contract.utils;

import com.creditcloud.claim.model.RepaymentPlan;
import com.creditcloud.config.ClientConfig;
import com.creditcloud.config.ContractSealConfig;
import com.creditcloud.config.FeeConfig;
import com.creditcloud.contract.ContractParty;
import com.creditcloud.contract.ContractSeal;
import com.creditcloud.contract.ContractType;
import com.creditcloud.contract.Seal;
import com.creditcloud.fund.model.FundAccount;
import com.creditcloud.model.PersonalInfo;
import com.creditcloud.model.PlaceInfo;
import com.creditcloud.model.enums.loan.RepaymentMethod;
import com.creditcloud.model.enums.misc.ContractSealType;
import com.creditcloud.model.loan.CreditAssign;
import com.creditcloud.model.loan.Duration;
import com.creditcloud.model.loan.Invest;
import com.creditcloud.model.loan.Loan;
import com.creditcloud.model.loan.LoanRepayment;
import com.creditcloud.model.loan.LoanRequest;
import com.creditcloud.model.loan.Repayment;
import com.creditcloud.model.user.User;
import com.creditcloud.model.user.asset.Vehicle;
import com.creditcloud.model.user.corporation.CorporationInfo;
import com.creditcloud.model.user.corporation.CorporationUser;
import com.creditcloud.model.user.fund.BankAccount;
import com.creditcloud.model.user.info.CareerInfo;
import com.creditcloud.model.user.info.UserInfo;
import com.esa2000.SealParser;
import com.esa2000.Shell;
import com.esa2000.pdfseal.unit.CommonUtil;
import com.esa2000.pdfseal.unit.PFXSealDownloader;
import com.esa2000.pdfseal.unit.ShellExtendForSubCerter;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import java.awt.FontFormatException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFUtils
{
  public static Logger logger = LoggerFactory.getLogger(PDFUtils.class);
  public static BaseFont sFont;
  public static java.awt.Font sPrivateSealFont;
  
  static
  {
    try
    {
      sFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", false);
      sPrivateSealFont = java.awt.Font.createFont(0, new File("SIMSUN.TTC")).deriveFont(1, 12.0F);
    }
    catch (FontFormatException|DocumentException|IOException e) {}
  }
  
  public static class Table
  {
    public String title;
    public List<List<String>> values = new CopyOnWriteArrayList();
    public float[] weights;
    
    public Table(String title, List<List<String>> values)
    {
      this.title = title;
      this.values = values;
    }
    
    public void setWeights(float[] weights)
    {
      this.weights = new float[weights.length];
      
      float totalNumber = 0.0F;
      for (float weight : weights) {
        totalNumber += weight;
      }
      int i = 0;
      for (int n = weights.length; i < n; i++) {
        this.weights[i] = (weights[i] / totalNumber);
      }
    }
  }
  
  public static class Fields
  {
    public String serial = "";
    public String companyName = "";
    public String companyAddress = "";
    public String legalRepresentative = "";
    public String debtorBankAccountName = "";
    public String debtorBankAccountBranch = "";
    public String debtorBankAccountNumber = "";
    public String agreementNo = "";
    public String CJRName = "";
    public String CJRloginName = "";
    public String CJRIdNumber = "000000000000000000";
    public String JKRName = "";
    public String JKRloginName = "";
    public String JKRIdNumber = "";
    public String JKRIdNumberPrivacy = "";
    public String amount = "";
    public String amountUpper = "";
    public String loanPurpose = "";
    public String loanRate = "";
    public String loanDate = "";
    public String endDate = "";
    public String cxrDate = "";
    public String repayMethod = "";
    public String repayMethodOrdinal = "";
    public String repayDate = "";
    public String repayAmount = "";
    public String repayAmountMonthly = "";
    public String advancedRepayFee = "";
    public String fxbzjRate = "";
    public String yzhglfRate = "";
    public String loanManageFee = "";
    public String loanManageFeeAmount = "";
    public String zxRate = "";
    public String zxRateAmount = "";
    public String riskFee = "";
    public String riskFeeAmount = "";
    public String totalServiceRate = "";
    public String jzhglfRate = "";
    public String yqRate = "";
    public String yqfxRate = "";
    public String jklxRate = "";
    public String daysToBreach = "";
    public String loanVisitFee = "";
    public String withdrawFee = "";
    public String loanRiskFee = "";
    public String loanRiskFeeAmount = "";
    public String repaymentNo = "";
    public String zqr = this.CJRName;
    public String zwr = this.JKRName;
    public String fr = "";
    public String titleName = "";
    public String name = "";
    public String url = "";
    public String signDate = "";
    public List<Repayment> repaymentMonthlyListForInvestor = new CopyOnWriteArrayList();
    public List<LoanRepayment> repaymentMonthlyList = new CopyOnWriteArrayList();
    public List<LoanRepayment> repaymentMonthlyListForObligator = new CopyOnWriteArrayList();
    public List<PDFUtils.Investor> investorList = new CopyOnWriteArrayList();
    public Map<String, Object> values = new ConcurrentHashMap();
    public Map<String, String> extendValues = new ConcurrentHashMap();
    public List<RepaymentPlan> RepaymentPlanList = new CopyOnWriteArrayList();
    
    //增加债权转让pdf字段
    public String SRRName;
    public String SRRIdNumber;
    public String SRRloginName;
    public String originalAmount;
    public String assignAmount;
    public String assignFeeAmount;
    public String assignDate;
    public String assignPeriod;
    public String assignDateFrom;
    public String assignDateTo;
  }
  
  public static Fields convertToPdfFieldForAssign(String no, com.creditcloud.model.client.Client legal, Loan loan, Invest originalInvest,Invest invest, List<Repayment> repaymentList, FeeConfig feeConfig, ClientConfig clientConfig, Date signDate, Map<String, Object> values, CreditAssign creditAssign)
  {
    User creditor = invest.getUser();
    User debtor = loan.getLoanRequest().getUser();
    
    Fields fields = new Fields();
    fields.values = values;
    if (values != null)
    {
      fields.values = values;
      fields.values.put("loanBidAmount", Integer.valueOf(loan.getBidAmount()));
    }
    String serial = getSerial(clientConfig, loan);
    if ((serial != null) && (!serial.isEmpty())) {
      fields.serial = serial;
    }
    fields.CJRName = originalInvest.getUser().getName();
    fields.CJRloginName = originalInvest.getUser().getLoginName();
    fields.CJRIdNumber = originalInvest.getUser().getIdNumber();
    
    fields.SRRName = creditor.getName();
    fields.SRRloginName = creditor.getLoginName();
    fields.SRRIdNumber = creditor.getIdNumber();
    
    fields.JKRName = debtor.getName();
    fields.JKRloginName = debtor.getLoginName();
    fields.JKRIdNumber = debtor.getIdNumber();
    fields.JKRIdNumberPrivacy = new StringBuffer(debtor.getIdNumber()).replace(6, 14, "********").toString();
    fields.zwr = debtor.getName();
    
    
    fields.amount = ("人民币" + loan.getAmount() + "元整");
    fields.amountUpper = (toChineseCurrency(invest.getAmount()) + "整");
    
    fields.loanPurpose = loan.getLoanRequest().getPurpose().getKey();
    fields.loanRate = (originalInvest.getRate() / 100.0F + "%");
    Date timeFinished = signDate;
    Duration duration = originalInvest.getDuration();
    String date = toPdfDateString(timeFinished);
    fields.loanDate = date.substring(date.indexOf("月") + 1);
    Calendar c = Calendar.getInstance();
    c.setTime(timeFinished);
    c.add(5, duration.getDays());
    c.add(1, duration.getYears());
    c.add(2, duration.getMonths());
    fields.endDate = toPdfDateString(c.getTime());
    fields.cxrDate = toPdfDateString(timeFinished);
    fields.repayDate = "见附件还款详情";
    fields.repayMethod = loan.getMethod().getKey();
    fields.repayMethodOrdinal = String.valueOf(loan.getMethod().ordinal() + 1);
    fields.repayAmount = (loan.getMethod() == RepaymentMethod.EqualInstallment ? "人民币" + ((Repayment)repaymentList.get(0)).getAmount() + "元" : "见附件还款详情");
    fields.repayAmountMonthly = "见附件还款详情及账户管理费比例";
    int month;
    if(loan.getDuration().getYears() != 0){
        month = loan.getDuration().getYears() * 12;
    }else if(loan.getDuration().getMonths() != 0){
        month = loan.getDuration().getMonths();
    }else{
        month = loan.getDuration().getDays()/30;
    }
    fields.repaymentNo = month + "";
    fields.signDate = toPdfDateString(signDate);
    fields.agreementNo = (legal.getCode() + no.substring(0, 8).toUpperCase());
    fields.fr = legal.getName();
    fields.name = legal.getShortName();
    fields.titleName = legal.getShortName();
    fields.url = legal.getUrl();
    //还款计划
    fields.repaymentMonthlyListForInvestor = repaymentList;
    if (values.containsKey("loanDuration")) {
      fields.extendValues.put("loanDuration", (String)values.get("loanDuration"));
    }
    //增加转让信息
    fields.originalAmount = ("人民币" + invest.getAmount().toString() + "元");
    fields.assignAmount = ("人民币" + invest.getCreditAssignDealAmount().toString() + "元");
    fields.assignFeeAmount = ("人民币" + creditAssign.getFee().toString() + "元");
    fields.assignDate = toPdfDateString(new Date());
    fields.assignPeriod = repaymentList.size() + "";
    Duration assignDuration = invest.getDuration();
    c.clear();
    c.setTime(timeFinished);
    c.add(5, assignDuration.getDays());
    c.add(1, assignDuration.getYears());
    c.add(2, assignDuration.getMonths());
    fields.assignDateTo = toPdfDateString(c.getTime());
    fields.assignDateFrom = toPdfDateString(timeFinished);
    return fields;
  }
  
  public static Fields convertToPdfField(String no, com.creditcloud.model.client.Client legal, Loan loan, Invest invest, List<Repayment> repaymentList, FeeConfig feeConfig, ClientConfig clientConfig, Date signDate, Map<String, Object> values)
  {
    User creditor = invest.getUser();
    User debtor = loan.getLoanRequest().getUser();
    
    Fields fields = new Fields();
    fields.values = values;
    if (values != null)
    {
      fields.values = values;
      fields.values.put("loanBidAmount", Integer.valueOf(loan.getBidAmount()));
    }
    String serial = getSerial(clientConfig, loan);
    if ((serial != null) && (!serial.isEmpty())) {
      fields.serial = serial;
    }
    if (feeConfig.getInvestInterestFee() != null) {
      fields.jzhglfRate = ("" + feeConfig.getInvestInterestFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanInterestFee() != null) {
      fields.yzhglfRate = ("" + feeConfig.getLoanInterestFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanManageFee() != null) {
      fields.loanManageFee = ("" + feeConfig.getLoanManageFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanGuaranteeFee() != null) {
      fields.fxbzjRate = ("" + feeConfig.getLoanGuaranteeFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanServiceFee() != null) {
      fields.zxRate = ("" + feeConfig.getLoanServiceFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanGuaranteeFee() != null) {
      fields.totalServiceRate = ("" + feeConfig.getLoanGuaranteeFee().getRate().add(feeConfig.getLoanServiceFee().getRate()).movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanOverdueFee() != null) {
      fields.yqRate = ("" + feeConfig.getLoanOverdueFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanPenaltyFee() != null) {
      fields.yqfxRate = ("" + feeConfig.getLoanPenaltyFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanManageFee() != null) {
      fields.jklxRate = ("" + feeConfig.getLoanManageFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    fields.daysToBreach = ("" + String.valueOf(feeConfig.getDaysToBreach()));
    if (feeConfig.getLoanVisitFee() != null) {
      fields.loanVisitFee = ("" + feeConfig.getLoanVisitFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getWithdrawFee() != null) {
      fields.withdrawFee = ("" + feeConfig.getWithdrawFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanRiskFee() != null) {
      fields.loanRiskFee = feeConfig.getLoanRiskFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    }
    fields.CJRName = creditor.getName();
    fields.CJRloginName = creditor.getLoginName();
    fields.CJRIdNumber = creditor.getIdNumber();
    fields.zqr = creditor.getName();
    
    fields.JKRName = debtor.getName();
    fields.JKRloginName = debtor.getLoginName();
    fields.JKRIdNumber = debtor.getIdNumber();
    fields.JKRIdNumberPrivacy = new StringBuffer(debtor.getIdNumber()).replace(6, 14, "********").toString();
    fields.zwr = debtor.getName();
    
    fields.companyName = ((String)values.get("companyName"));
    fields.companyAddress = ((String)values.get("companyAddress"));
    fields.legalRepresentative = debtor.getName();
    
    fields.amount = ("人民币" + invest.getAmount() + "元整");
    fields.amountUpper = (toChineseCurrency(invest.getAmount()) + "整");
    
    fields.loanPurpose = loan.getLoanRequest().getPurpose().getKey();
    fields.loanRate = (invest.getRate() / 100.0F + "%");
    if (values.containsKey("obligatorFundAccount"))
    {
      BankAccount account = ((FundAccount)values.get("obligatorFundAccount")).getAccount();
      
      fields.debtorBankAccountName = account.getName();
      
      fields.debtorBankAccountNumber = account.getAccount();
      
      String branch = account.getBranch();
      if ((branch != null) && (!branch.isEmpty())) {
        fields.debtorBankAccountBranch = account.getBranch();
      } else {
        fields.debtorBankAccountBranch = account.getBank().getKey();
      }
    }
    Date timeFinished = signDate;
    Duration duration = invest.getDuration();
    fields.loanDate = toPdfDateString(timeFinished);
    

    Calendar c = Calendar.getInstance();
    c.setTime(timeFinished);
    c.add(5, duration.getDays());
    c.add(1, duration.getYears());
    c.add(2, duration.getMonths());
    fields.endDate = toPdfDateString(c.getTime());
    

    fields.cxrDate = toPdfDateString(timeFinished);
    

    fields.repayDate = "见附件还款详情";
    

    fields.repayMethod = loan.getMethod().getKey();
    fields.repayMethodOrdinal = String.valueOf(loan.getMethod().ordinal() + 1);
    fields.repayAmount = (loan.getMethod() == RepaymentMethod.EqualInstallment ? "人民币" + ((Repayment)repaymentList.get(0)).getAmount() + "元" : "见附件还款详情");
    fields.repayAmountMonthly = "见附件还款详情及账户管理费比例";
    

    fields.repaymentNo = ("共" + repaymentList.size() + "期");
    

    fields.signDate = toPdfDateString(signDate);
    

    fields.agreementNo = (legal.getCode() + no.substring(0, 8).toUpperCase());
    

    fields.fr = legal.getName();
    fields.name = legal.getShortName();
    fields.titleName = legal.getShortName();
    fields.url = legal.getUrl();
    










    fields.repaymentMonthlyListForInvestor = repaymentList;
    if (values.containsKey("loanDuration")) {
      fields.extendValues.put("loanDuration", (String)values.get("loanDuration"));
    }
    return fields;
  }
  
  public static byte[] templateToPdf(Fields fields, byte[] template, byte[] watermark)
    throws IOException, DocumentException
  {
    PdfReader reader = new PdfReader(template);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] pdf = resetFields(bos, reader, fields);
    
    ByteArrayInputStream bis = new ByteArrayInputStream(pdf);
    
    pdf = insertTableToPdf(bis, reader, fields);
    
    bis = new ByteArrayInputStream(pdf);
    if (watermark != null) {
      pdf = insertWaterMark(bis, reader, watermark);
    }
    return pdf;
  }
  
  public static byte[] claimTemplateToPdf(Fields fields, byte[] template, byte[] watermark)
    throws IOException, DocumentException
  {
    PdfReader reader = new PdfReader(template);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] pdf = resetFields(bos, reader, fields);
    
    ByteArrayInputStream bis = new ByteArrayInputStream(pdf);
    
    pdf = insertTableToClaimPdf(bis, reader, fields);
    
    bis = new ByteArrayInputStream(pdf);
    if (watermark != null) {
      pdf = insertWaterMark(bis, reader, watermark);
    }
    return pdf;
  }
  
  public static synchronized byte[] insertSeal(String clientCode, byte[] input, List<ContractSeal> contractSeals, ContractSealConfig contractSealConfig)
    throws FileNotFoundException, IOException
  {
    for (ContractSeal contractSeal : contractSeals) {
      input = insertSeal(clientCode, input, contractSeal, contractSealConfig);
    }
    return input;
  }
  
  public static byte[] insertSeal(String clientCode, byte[] input, ContractSeal contractSeal, ContractSealConfig contractSealConfig)
    throws FileNotFoundException, IOException
  {
    Seal seal = contractSeal.getSeal();
    String src = UUID.randomUUID().toString();
    String dst = UUID.randomUUID().toString();
    byte[] output = null;
    
    logger.info("insert seal init: {}", contractSeal);
    System.out.println("insert seal init");
    try
    {
      FileOutputStream fos = new FileOutputStream(src);Throwable localThrowable2 = null;
      try
      {
        fos.write(input);
      }
      catch (Throwable localThrowable1)
      {
        localThrowable2 = localThrowable1;throw localThrowable1;
      }
      finally
      {
        if (fos != null) {
          if (localThrowable2 != null) {
            try
            {
              fos.close();
            }
            catch (Throwable x2)
            {
              localThrowable2.addSuppressed(x2);
            }
          } else {
            fos.close();
          }
        }
      }
    }
    catch (Exception e)
    {
      logger.info("FileOutputStream [exception={}]", e);
      return input;
    }
    if (seal.getType() == ContractSealType.PERSONAL)
    {
      if (StringUtils.isEmpty(seal.getCode()))
      {
        applyPersonalSeal(clientCode, contractSeal, contractSealConfig);
        if (StringUtils.isEmpty(seal.getCode())) {
          return input;
        }
      }
      logger.info("Contract contractSealConfig {}, [contractSeal={}]", contractSealConfig, contractSeal);
      
      ShellExtendForSubCerter shellExtend = new ShellExtendForSubCerter();
      
      shellExtend.setCharsetName("UTF-8");
      shellExtend.setFont(sPrivateSealFont);
      
      int result = shellExtend.initCert(contractSealConfig.getApiPDFAddPersonalSeal(), seal.getCode());
      if (result == 0)
      {
        byte[] pfxBytes = shellExtend.getPfxBytes();
        

        byte[] imageBytes = shellExtend.getImageBytes();
        

        String password = shellExtend.getPassword();
        

        Shell shell = new Shell();
        



        shell.init(src, dst, "", true);
        


        String pfxPath = UUID.randomUUID().toString();
        
        String imgPath = UUID.randomUUID().toString() + ".bmp";
        try
        {
          new File(pfxPath).createNewFile();
          new File(imgPath).createNewFile();
        }
        catch (IOException e)
        {
          logger.info("create new file failed exception:{}", e);
        }
        CommonUtil.writeBytesToFile(pfxBytes, pfxPath);
        CommonUtil.writeBytesToFile(imageBytes, imgPath);
        
        shell.addSealByBmp(pfxPath, imgPath, password, contractSeal.getX(), contractSeal.getY(), contractSeal.getPage());
        shell.close();
        
        delete(pfxPath);
        delete(imgPath);
        logger.info("private contract seal successed {}", contractSeal);
        System.out.println("private contract seal successed");
      }
      else
      {
        logger.info("get contract seal code failed，wrong code：{}", Integer.valueOf(result));
        System.out.println("get contract seal code failed，wrong code:" + result);
      }
    }
    else
    {
      String sealCode = CommonUtil.base64EncodeString(seal.getCode());
      if ((seal.getContent() != null) && (seal.getContent().length > 0))
      {
        Shell shell = new Shell();
        SealParser sealParser = new SealParser();
        seal.setContent(seal.getContent());
        sealParser.parser(new ByteArrayInputStream(seal.getContent()));
        shell.init(src, dst, sealParser, true);
        shell.addSealOpen(contractSeal.getX(), contractSeal.getY(), contractSeal.getPage());
        shell.close();
      }
      else
      {
        String sealContent = PFXSealDownloader.downloadPfxSealFromEsa2012(contractSealConfig.getApiPDFAddSeal(), sealCode, 1);
        if (StringUtils.isEmpty(sealContent))
        {
          logger.warn("public contract seal content null: {}, contractSealConfig: {}", contractSealConfig, contractSeal);
          return input;
        }
        if ((!sealContent.equals("1")) || (!sealContent.equals("2")))
        {
          Shell shell = new Shell();
          SealParser sealParser = new SealParser();
          seal.setContent(sealContent.getBytes());
          sealParser.parser(new ByteArrayInputStream(seal.getContent()));
          shell.init(src, dst, sealParser, true);
          shell.addSealOpen(contractSeal.getX(), contractSeal.getY(), contractSeal.getPage());
          shell.close();
          
          logger.info("public contract seal successed: {}", sealContent);
        }
        else if (sealContent.equals("2"))
        {
          insertSeal(clientCode, input, contractSeal, contractSealConfig);
          
          logger.info("public contract seal 2");
        }
        else
        {
          output = input;
          logger.info("public contract seal failed [sealContent={}]", sealContent);
          System.out.println("public contract seal failed sealContent=" + sealContent);
        }
      }
    }
    if ((output == null) || (output.length == 0)) {
      output = IOUtils.toByteArray(new FileInputStream(dst));
    }
    if ((output == null) || (output.length == 0)) {
      output = input;
    }
    delete(src);
    delete(dst);
    
    logger.info("insert seal finished: {}", contractSeal);
    System.out.println("insert seal finished");
    return output;
  }
  
  private static void delete(String fileName)
  {
    File f = new File(fileName);
    if (!f.exists()) {
      logger.warn("Delete: no such file or directory: " + fileName);
    }
    if (!f.canWrite()) {
      logger.warn("Delete: write protected: " + fileName);
    }
    if (f.isDirectory())
    {
      String[] files = f.list();
      if (files.length > 0) {
        logger.warn("Delete: directory not empty: " + fileName);
      }
    }
    boolean success = f.delete();
    if (!success) {
      logger.warn("Delete: deletion failed");
    }
  }
  
  public static byte[] insertWriting(byte[] input, byte[] writing, int page, float percentX, float percentY)
    throws IOException, DocumentException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PdfReader reader = new PdfReader(input);
    PdfStamper stamper = new PdfStamper(reader, bos);
    
    Image image = Image.getInstance(writing);
    PdfContentByte under = stamper.getUnderContent(page);
    Rectangle rectangle = reader.getPageSize(page);
    float h = rectangle.getHeight();
    float w = rectangle.getHeight();
    image.setAbsolutePosition(percentX * w, percentY * h);
    image.scalePercent(50.0F);
    under.addImage(image);
    stamper.close();
    
    byte[] ouput = bos.toByteArray();
    bos.close();
    return ouput;
  }
  
  public static Fields convertToPdfField(String no, com.creditcloud.model.client.Client legal, String userId, Loan loan, List<LoanRepayment> repaymentList, List<Invest> investList, FeeConfig feeConfig, ClientConfig clientConfig, Date signDate, Map<String, Object> values)
  {
    Fields fields = new Fields();
    fields.values = values;
    if (values != null)
    {
      fields.values = values;
      fields.values.put("loanBidAmount", Integer.valueOf(loan.getBidAmount()));
    }
    String serial = getSerial(clientConfig, loan);
    if ((serial != null) && (!serial.isEmpty())) {
      fields.serial = serial;
    }
    User debtor = loan.getLoanRequest().getUser();
    
    fields.amount = ("人民币" + loan.getAmount() + "元整");
    fields.amountUpper = (toChineseCurrency(Integer.valueOf(loan.getAmount())) + "整");
    
    convertFeeConfigToPdfField(new BigDecimal(loan.getAmount()), fields, feeConfig);
    
    fields.JKRName = debtor.getName();
    fields.JKRloginName = debtor.getLoginName();
    fields.JKRIdNumber = debtor.getIdNumber();
    fields.JKRIdNumberPrivacy = new StringBuffer(debtor.getIdNumber()).replace(6, 14, "********").toString();
    
    fields.zwr = debtor.getName();
    
    fields.companyName = ((String)values.get("companyName"));
    fields.companyAddress = ((String)values.get("companyAddress"));
    fields.legalRepresentative = debtor.getName();
    
    fields.loanPurpose = loan.getLoanRequest().getPurpose().getKey();
    fields.loanRate = (loan.getRate() / 100.0F + "%");
    if (values.containsKey("obligatorFundAccount"))
    {
      BankAccount account = ((FundAccount)values.get("obligatorFundAccount")).getAccount();
      
      fields.debtorBankAccountName = account.getName();
      
      fields.debtorBankAccountNumber = account.getAccount();
      
      String branch = account.getBranch();
      if ((branch != null) && (!branch.isEmpty())) {
        fields.debtorBankAccountBranch = account.getBranch();
      } else {
        fields.debtorBankAccountBranch = account.getBank().getKey();
      }
    }
    Date timeFinished = signDate;
    Duration duration = loan.getDuration();
    fields.loanDate = toPdfDateString(timeFinished);
    

    Calendar c = Calendar.getInstance();
    c.setTime(timeFinished);
    c.add(5, duration.getDays());
    c.add(1, duration.getYears());
    c.add(2, duration.getMonths());
    fields.endDate = toPdfDateString(c.getTime());
    

    fields.cxrDate = toPdfDateString(timeFinished);
    

    fields.repayDate = "见附件还款详情";
    

    fields.repayMethod = loan.getMethod().getKey();
    fields.repayMethodOrdinal = String.valueOf(loan.getMethod().ordinal() + 1);
    fields.repayAmount = (loan.getMethod() == RepaymentMethod.EqualInstallment ? "人民币" + ((LoanRepayment)repaymentList.get(0)).getRepayAmount() + "元" : "见附件还款详情");
    fields.repayAmountMonthly = "见附件还款详情及账户管理费比例";
    

    fields.repaymentNo = ("共" + repaymentList.size() + "期");
    

    fields.signDate = toPdfDateString(signDate);
    

    fields.agreementNo = (legal.getCode() + no.substring(0, 8).toUpperCase());
    

    fields.fr = legal.getName();
    fields.name = legal.getShortName();
    fields.titleName = legal.getShortName();
    fields.url = legal.getUrl();
    


    int i = 0;
    










    fields.repaymentMonthlyList = repaymentList;
    fields.repaymentMonthlyListForObligator = repaymentList;
    for (Invest inv : investList)
    {
      Investor investor = new Investor();
      User user = inv.getUser();
      investor.amount = ("￥ " + inv.getAmount());
      if (user.getId().contentEquals(userId))
      {
        investor.loginName = user.getLoginName();
        investor.name = user.getName();
        investor.idNumberPrivacy = user.getIdNumber();
      }
      else
      {
        investor.loginName = new StringBuffer(user.getLoginName()).replace(1, user.getLoginName().length() - 1, "****").toString();
        investor.name = new StringBuffer(user.getName()).replace(1, user.getName().length() - 1, "*").toString();
        investor.idNumberPrivacy = new StringBuffer(user.getIdNumber()).replace(6, 14, "********").toString();
      }
      fields.investorList.add(investor);
    }
    if (values.containsKey("loanDuration")) {
      fields.extendValues.put("loanDuration", (String)values.get("loanDuration"));
    }
    return fields;
  }
  
  public static Fields convertToPdfField(String no, com.creditcloud.model.client.Client legal, Loan loan, List<LoanRepayment> repaymentList, List<Invest> investList, FeeConfig feeConfig, ClientConfig clientConfig, Date signDate, Map<String, Object> values)
  {
    Fields fields = new Fields();
    if (values != null)
    {
      fields.values = values;
      fields.values.put("loanBidAmount", Integer.valueOf(loan.getBidAmount()));
    }
    String serial = getSerial(clientConfig, loan);
    if ((serial != null) && (!serial.isEmpty())) {
      fields.serial = serial;
    }
    User debtor = loan.getLoanRequest().getUser();
    
    fields.amount = ("人民币" + loan.getAmount() + "元整");
    fields.amountUpper = (toChineseCurrency(Integer.valueOf(loan.getAmount())) + "整");
    
    convertFeeConfigToPdfField(new BigDecimal(loan.getAmount()), fields, feeConfig);
    
    fields.JKRName = debtor.getName();
    fields.JKRloginName = debtor.getLoginName();
    fields.JKRIdNumber = debtor.getIdNumber();
    fields.JKRIdNumberPrivacy = new StringBuffer(debtor.getIdNumber()).replace(6, 14, "********").toString();
    
    fields.zwr = debtor.getName();
    
    fields.companyName = ((String)values.get("companyName"));
    fields.companyAddress = ((String)values.get("companyAddress"));
    fields.legalRepresentative = debtor.getName();
    
    fields.loanPurpose = loan.getLoanRequest().getPurpose().getKey();
    fields.loanRate = (loan.getRate() / 100.0F + "%");
    if (values.containsKey("obligatorFundAccount"))
    {
      BankAccount account = ((FundAccount)values.get("obligatorFundAccount")).getAccount();
      
      fields.debtorBankAccountName = account.getName();
      
      fields.debtorBankAccountNumber = account.getAccount();
      
      String branch = account.getBranch();
      if ((branch != null) && (!branch.isEmpty())) {
        fields.debtorBankAccountBranch = account.getBranch();
      } else {
        fields.debtorBankAccountBranch = account.getBank().getKey();
      }
    }
    Date timeFinished = signDate;
    Duration duration = loan.getDuration();
    fields.loanDate = toPdfDateString(timeFinished);
    

    Calendar c = Calendar.getInstance();
    c.setTime(timeFinished);
    c.add(5, duration.getDays());
    c.add(1, duration.getYears());
    c.add(2, duration.getMonths());
    fields.endDate = toPdfDateString(c.getTime());
    

    fields.cxrDate = toPdfDateString(timeFinished);
    

    fields.repayDate = "见附件还款详情";
    

    fields.repayMethod = loan.getMethod().getKey();
    fields.repayMethodOrdinal = String.valueOf(loan.getMethod().ordinal() + 1);
    fields.repayAmount = (loan.getMethod() == RepaymentMethod.EqualInstallment ? "人民币" + ((LoanRepayment)repaymentList.get(0)).getRepayAmount() + "元" : "见附件还款详情");
    fields.repayAmountMonthly = "见附件还款详情及账户管理费比例";
    

    fields.repaymentNo = ("共" + repaymentList.size() + "期");
    

    fields.signDate = toPdfDateString(signDate);
    

    fields.agreementNo = (legal.getCode() + no.substring(0, 8).toUpperCase());
    

    fields.fr = legal.getName();
    fields.name = legal.getShortName();
    fields.titleName = legal.getShortName();
    fields.url = legal.getUrl();
    













    fields.repaymentMonthlyList = repaymentList;
    fields.repaymentMonthlyListForObligator = repaymentList;
    for (Invest inv : investList)
    {
      Investor investor = new Investor();
      User user = inv.getUser();
      investor.amount = ("人民币" + inv.getAmount() + "元");
      
      investor.loginName = user.getLoginName();
      investor.name = user.getName();
      if (clientConfig.getCode().equalsIgnoreCase("FENG")) {
        investor.idNumberPrivacy = user.getIdNumber();
      } else {
        investor.idNumberPrivacy = toStyleIdNumber(user.getIdNumber());
      }
      fields.investorList.add(investor);
    }
    if (values.containsKey("loanDuration")) {
      fields.extendValues.put("loanDuration", (String)values.get("loanDuration"));
    }
    return fields;
  }
  
  private static void convertFeeConfigToPdfField(BigDecimal amount, Fields fields, FeeConfig feeConfig)
  {
    fields.jzhglfRate = feeConfig.getInvestInterestFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    
    fields.yzhglfRate = feeConfig.getLoanInterestFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    

    BigDecimal loanManageFee = feeConfig.getLoanManageFee().getRate();
    fields.loanManageFee = loanManageFee.movePointRight(2).stripTrailingZeros().toPlainString();
    fields.loanManageFeeAmount = loanManageFee.multiply(amount).stripTrailingZeros().toPlainString();
    

    BigDecimal riskFee = feeConfig.getLoanGuaranteeFee().getRate();
    fields.riskFee = riskFee.movePointRight(2).stripTrailingZeros().toPlainString();
    fields.riskFeeAmount = riskFee.multiply(amount).stripTrailingZeros().toPlainString();
    

    fields.fxbzjRate = feeConfig.getLoanGuaranteeFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    

    BigDecimal loanServiceFee = feeConfig.getLoanServiceFee().getRate();
    fields.zxRate = loanServiceFee.movePointRight(2).stripTrailingZeros().toPlainString();
    fields.zxRateAmount = loanServiceFee.multiply(amount).stripTrailingZeros().toPlainString();
    

    fields.totalServiceRate = feeConfig.getLoanGuaranteeFee().getRate().add(feeConfig.getLoanServiceFee().getRate()).movePointRight(2).stripTrailingZeros().toPlainString();
    
    fields.yqRate = feeConfig.getLoanOverdueFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    
    fields.yqfxRate = feeConfig.getLoanPenaltyFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    
    fields.jklxRate = feeConfig.getLoanManageFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    
    fields.daysToBreach = String.valueOf(feeConfig.getDaysToBreach());
    
    fields.loanVisitFee = feeConfig.getLoanVisitFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    
    fields.withdrawFee = feeConfig.getWithdrawFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    if (feeConfig.getLoanRiskFee() != null)
    {
      BigDecimal loanRiskFee = feeConfig.getLoanRiskFee().getRate();
      fields.loanRiskFee = loanRiskFee.movePointRight(2).stripTrailingZeros().toPlainString();
      fields.loanRiskFeeAmount = loanRiskFee.multiply(amount).stripTrailingZeros().toPlainString();
    }
  }
  
  public static byte[] templateToPdfForInvestor(Fields fields, byte[] template, byte[] watermark)
    throws IOException, DocumentException
  {
    PdfReader reader = new PdfReader(template);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] pdf = resetFields(bos, reader, fields);
    
    ByteArrayInputStream bis = new ByteArrayInputStream(pdf);
    
    pdf = insertTableToPdfForInvestor(bis, reader, fields);
    
    bis = new ByteArrayInputStream(pdf);
    if (watermark != null) {
      pdf = insertWaterMark(bis, reader, watermark);
    }
    return pdf;
  }
  
  public static byte[] templateToPdfForObligator(Fields fields, byte[] template, byte[] watermark)
    throws IOException, DocumentException
  {
    PdfReader reader = new PdfReader(template);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] pdf = resetFields(bos, reader, fields);
    
    ByteArrayInputStream bis = new ByteArrayInputStream(pdf);
    
    pdf = insertTableToPdfForObligator(bis, reader, fields);
    
    bis = new ByteArrayInputStream(pdf);
    if (watermark != null) {
      pdf = insertWaterMark(bis, reader, watermark);
    }
    return pdf;
  }
  
  public static byte[] templateToPdfForObligator(ContractType type, Fields fields, byte[] template, byte[] watermark)
    throws IOException, DocumentException
  {
    logger.debug(">>>>>> templateToPdfForObligator called");
    PdfReader reader = new PdfReader(template);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] pdf = resetFields(bos, reader, fields);
    
    ByteArrayInputStream bis = new ByteArrayInputStream(pdf);
    
    pdf = insertTableToPdfForObligator(type, bis, reader, fields);
    
    bis = new ByteArrayInputStream(pdf);
    if (watermark != null) {
      pdf = insertWaterMark(bis, reader, watermark);
    }
    return pdf;
  }
  
  public static String toPdfDateString(Date date)
  {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
    String dateString = formatter.format(date);
    return dateString;
  }
  
  public static String toChineseCurrency(Object o)
  {
    if ((o instanceof Number))
    {
      String s = new DecimalFormat("#.00").format(o);
      s = s.replaceAll("\\.", "");
      char[] digit = { 38646, '壹', 36144, '叁', 32902, '伍', 38470, '柒', '捌', '玖' };
      
      String unit = "仟佰拾兆仟佰拾亿仟佰拾万仟佰拾元角分";
      int l = unit.length();
      StringBuffer sb = new StringBuffer(unit);
      for (int i = s.length() - 1; i >= 0; i--) {
        sb = sb.insert(l - s.length() + i, digit[(s.charAt(i) - '0')]);
      }
      s = sb.substring(l - s.length(), l + s.length());
      s = s.replaceAll("零[拾佰仟]", "零").replaceAll("零{2,}", "零").replaceAll("零([兆万元])", "$1").replaceAll("零[角分]", "");
      


      return s;
    }
    throw new NumberFormatException();
  }
  
  private static byte[] insertTableToPdf(ByteArrayInputStream bis, PdfReader reader, Fields fields)
    throws DocumentException, IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4);
    PdfWriter writer = PdfWriter.getInstance(document, bos);
    document.open();
    PdfContentByte cb = writer.getDirectContent();
    

    reader = new PdfReader(bis);
    

    int n = reader.getNumberOfPages();
    for (int i = 0; i < n; i++)
    {
      PdfImportedPage page = writer.getImportedPage(reader, i + 1);
      
      document.newPage();
      cb.addTemplate(page, 0.0F, 0.0F);
    }
    document.newPage();
    
    insertTitleToDocument(document, "还款详情表", 20, 1);
    
    document.add(addRepayTableForInvestor(fields));
    document.close();
    
    byte[] byteArray = bos.toByteArray();
    bos.close();
    return byteArray;
  }
  
  private static byte[] insertWaterMark(ByteArrayInputStream bis, PdfReader reader, byte[] watermark)
    throws DocumentException, IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    reader = new PdfReader(bis);
    PdfStamper stamper = new PdfStamper(reader, bos);
    int i = 0;
    for (int sum = reader.getNumberOfPages(); i < sum; i++) {
      waterMark(reader, stamper, watermark, i + 1);
    }
    stamper.close();
    
    byte[] ouput = bos.toByteArray();
    bos.close();
    return ouput;
  }
  
  private static byte[] insertTableToPdfForInvestor(ByteArrayInputStream bis, PdfReader reader, Fields fields)
    throws DocumentException, IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4);
    PdfWriter writer = PdfWriter.getInstance(document, bos);
    document.open();
    PdfContentByte cb = writer.getDirectContent();
    

    reader = new PdfReader(bis);
    

    int n = reader.getNumberOfPages();
    for (int i = 0; i < n; i++)
    {
      PdfImportedPage page = writer.getImportedPage(reader, i + 1);
      
      document.newPage();
      cb.addTemplate(page, 0.0F, 0.0F);
    }
    document.newPage();
    
    insertTitleToDocument(document, "附件1：出借人列表", 12, 1);
    
    document.add(addInvestorsTableForObligator(fields));
    
    insertTitleToDocument(document, "附件2：还款计划表", 12, 1);
    
    document.add(addRepayTableForInvestor(fields));
    
    document.close();
    
    byte[] byteArray = bos.toByteArray();
    bos.close();
    return byteArray;
  }
  
  private static byte[] insertTableToPdfForObligator(ByteArrayInputStream bis, PdfReader reader, Fields fields)
    throws DocumentException, IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4);
    PdfWriter writer = PdfWriter.getInstance(document, bos);
    document.open();
    PdfContentByte cb = writer.getDirectContent();
    

    reader = new PdfReader(bis);
    

    int n = reader.getNumberOfPages();
    for (int i = 0; i < n; i++)
    {
      PdfImportedPage page = writer.getImportedPage(reader, i + 1);
      
      document.newPage();
      cb.addTemplate(page, 0.0F, 0.0F);
    }
    document.newPage();
    
    insertTitleToDocument(document, "附件1：出借人列表", 12, 1);
    
    document.add(addInvestorsTableForObligator(fields));
    
    insertTitleToDocument(document, "附件2：还款计划表", 12, 1);
    
    document.add(addRepayTableForObligator(fields));
    
    document.close();
    
    byte[] byteArray = bos.toByteArray();
    bos.close();
    return byteArray;
  }
  
  private static byte[] insertTableToClaimPdf(ByteArrayInputStream bis, PdfReader reader, Fields fields)
    throws DocumentException, IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4);
    PdfWriter writer = PdfWriter.getInstance(document, bos);
    document.open();
    PdfContentByte cb = writer.getDirectContent();
    

    reader = new PdfReader(bis);
    

    int n = reader.getNumberOfPages();
    for (int i = 0; i < n; i++)
    {
      PdfImportedPage page = writer.getImportedPage(reader, i + 1);
      
      document.newPage();
      cb.addTemplate(page, 0.0F, 0.0F);
    }
    document.newPage();
    
//    insertTitleToDocument(document, "附件1：原始债权还款计划列表", 12, 1);
//    
//    document.add(addClaimRepayPlanTable(fields));
    
    insertTitleToDocument(document, "附件1：本协议标的转让后本金还款计划列表", 12, 1);
    document.add(addRepayTableForInvestor(fields));
    
    document.close();
    
    byte[] byteArray = bos.toByteArray();
    bos.close();
    return byteArray;
  }
  
  private static void insertTitleToDocument(Document document, String title, int textSize, int alignment)
    throws DocumentException, IOException
  {
    BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", false);
    

    com.itextpdf.text.Font font = new com.itextpdf.text.Font(baseFont);
    float fontSize = font.getSize();
    font.setSize(textSize);
    Paragraph preface = new Paragraph(title, font);
    preface.setAlignment(alignment);
    document.add(preface);
    
    font.setSize(fontSize);
    preface = new Paragraph("", font);
    document.add(preface);
  }
  
  private static byte[] resetFields(ByteArrayOutputStream bos, PdfReader reader, Fields fields)
    throws IOException, DocumentException
  {
    PdfStamper ps = new PdfStamper(reader, bos);
    BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", false);
    

    AcroFields s = ps.getAcroFields();
    

    s.setFieldProperty("debtorBankAccountName", "textfont", bf, null);
    
    s.setField("debtorBankAccountName", fields.debtorBankAccountName);
    

    s.setFieldProperty("debtorBankAccountBranch", "textfont", bf, null);
    
    s.setField("debtorBankAccountBranch", fields.debtorBankAccountBranch);
    

    s.setFieldProperty("debtorBankAccountNumber", "textfont", bf, null);
    
    s.setField("debtorBankAccountNumber", fields.debtorBankAccountNumber);
    if (!StringUtils.isEmpty(fields.serial))
    {
      s.setFieldProperty("serial", "textfont", bf, null);
      s.setField("serial", fields.serial);
    }
    else
    {
      s.setFieldProperty("serial", "textfont", bf, null);
      s.setField("serial", fields.agreementNo);
    }
    s.setFieldProperty("agreementNo", "textfont", bf, null);
    s.setField("agreementNo", fields.agreementNo);
    
    s.setFieldProperty("JKRName", "textfont", bf, null);
    s.setField("JKRName", fields.JKRName);
    
    s.setFieldProperty(contractPartyName(ContractParty.FIRST), "textfont", bf, null);
    s.setField(contractPartyName(ContractParty.FIRST), fields.JKRName);
    
    s.setFieldProperty("JKRNamePrivacy", "textfont", bf, null);
    s.setField("JKRNamePrivacy", new StringBuffer(fields.JKRName).replace(1, fields.JKRName.length(), StringUtils.repeat("*", fields.JKRloginName.length() - 1)).toString());
    
    s.setFieldProperty("JKRloginName", "textfont", bf, null);
    s.setField("JKRloginName", fields.JKRloginName);
    
    s.setFieldProperty(contractPartyLoginName(ContractParty.FIRST), "textfont", bf, null);
    s.setField(contractPartyLoginName(ContractParty.FIRST), fields.JKRloginName);
    
    s.setFieldProperty("JKRloginNamePrivacy", "textfont", bf, null);
    if (fields.JKRloginName.length() > 2) {
      s.setField("JKRloginNamePrivacy", new StringBuffer(fields.JKRloginName).replace(1, fields.JKRloginName.length() - 1, StringUtils.repeat("*", fields.JKRloginName.length() - 2)).toString());
    } else {
      s.setField("JKRloginNamePrivacy", fields.JKRloginName);
    }
    if (!StringUtils.isEmpty(fields.companyName))
    {
      s.setFieldProperty("companyName", "textfont", bf, null);
      s.setField("companyName", fields.companyName);
    }
    s.setFieldProperty("companyAddress", "textfont", bf, null);
    s.setField("companyAddress", fields.companyAddress);
    
    s.setFieldProperty("legalRepresentative", "textfont", bf, null);
    s.setField("legalRepresentative", fields.legalRepresentative);
    
    setCIdNumber(s, fields.CJRIdNumber);
    setJIdNumber(s, fields.JKRIdNumber);
    
    s.setFieldProperty("CJRIdNumber", "textfont", bf, null);
    s.setField("CJRIdNumber", fields.CJRIdNumber);
    
    s.setFieldProperty("JKRIdNumber", "textfont", bf, null);
    s.setField("JKRIdNumber", fields.JKRIdNumber);
    
    s.setFieldProperty(contractPartyCode(ContractParty.FIRST), "textfont", bf, null);
    s.setField(contractPartyCode(ContractParty.FIRST), fields.JKRIdNumber);
    
    s.setFieldProperty("JKRIdNumberPrivacy", "textfont", bf, null);
    s.setField("JKRIdNumberPrivacy", fields.JKRIdNumberPrivacy);
    
    s.setFieldProperty("CJRloginName", "textfont", bf, null);
    s.setField("CJRloginName", fields.CJRloginName);
    
    s.setFieldProperty("CJRName", "textfont", bf, null);
    s.setField("CJRName", fields.CJRName);
    
    s.setFieldProperty("amount", "textfont", bf, null);
    s.setField("amount", fields.amount);
    
    s.setFieldProperty("amountUpper", "textfont", bf, null);
    s.setField("amountUpper", fields.amountUpper);
    
    s.setFieldProperty("loanPurpose", "textfont", bf, null);
    s.setField("loanPurpose", fields.loanPurpose);
    
    s.setFieldProperty("loanRate", "textfont", bf, null);
    s.setField("loanRate", fields.loanRate);
    
    s.setFieldProperty("cxrDate", "textfont", bf, null);
    s.setField("cxrDate", fields.cxrDate);
    
    s.setFieldProperty("loanDate", "textfont", bf, null);
    s.setField("loanDate", fields.loanDate);
    
    s.setFieldProperty("endDate", "textfont", bf, null);
    s.setField("endDate", fields.endDate);
    
    s.setFieldProperty("repayDate", "textfont", bf, null);
    s.setField("repayDate", fields.repayDate);
    
    s.setFieldProperty("repayMethod", "textfont", bf, null);
    s.setField("repayMethod", fields.repayMethod);
    
    s.setFieldProperty("repayMethodOrdinal", "textfont", bf, null);
    s.setField("repayMethodOrdinal", fields.repayMethodOrdinal);
    
    s.setFieldProperty("repayAmount", "textfont", bf, null);
    s.setField("repayAmount", fields.repayAmount);
    
    s.setFieldProperty("repayAmountMonthly", "textfont", bf, null);
    s.setField("repayAmountMonthly", fields.repayAmountMonthly);
    
    s.setFieldProperty("repaymentNo", "textfont", bf, null);
    s.setField("repaymentNo", fields.repaymentNo);
    
    s.setFieldProperty("fxbzjRate", "textfont", bf, null);
    s.setField("fxbzjRate", fields.fxbzjRate);
    
    s.setFieldProperty("jzhglfRate", "textfont", bf, null);
    s.setField("jzhglfRate", fields.jzhglfRate);
    
    s.setFieldProperty("yzhglfRate", "textfont", bf, null);
    s.setField("yzhglfRate", fields.yzhglfRate);
    
    s.setFieldProperty("loanManageFee", "textfont", bf, null);
    s.setField("loanManageFee", fields.loanManageFee);
    
    s.setFieldProperty("loanManageFeeAmount", "textfont", bf, null);
    s.setField("loanManageFeeAmount", fields.loanManageFeeAmount);
    
    s.setFieldProperty("advancedRepayFee", "textfont", bf, null);
    s.setField("advancedRepayFee", fields.advancedRepayFee);
    
    s.setFieldProperty("zxRate", "textfont", bf, null);
    s.setField("zxRate", fields.zxRate);
    
    s.setFieldProperty("zxRateAmount", "textfont", bf, null);
    s.setField("zxRateAmount", fields.zxRateAmount);
    
    s.setFieldProperty("riskFee", "textfont", bf, null);
    s.setField("riskFee", fields.riskFee);
    
    s.setFieldProperty("riskFeeAmount", "textfont", bf, null);
    s.setField("riskFeeAmount", fields.riskFeeAmount);
    
    s.setFieldProperty("yqfxRate", "textfont", bf, null);
    s.setField("yqfxRate", fields.yqfxRate);
    
    s.setFieldProperty("yqRate", "textfont", bf, null);
    s.setField("yqRate", fields.yqRate);
    
    s.setFieldProperty("jklxRate", "textfont", bf, null);
    s.setField("jklxRate", fields.jklxRate);
    
    s.setFieldProperty("totalServiceRate", "textfont", bf, null);
    s.setField("totalServiceRate", fields.totalServiceRate);
    
    s.setFieldProperty("daysToBreach", "textfont", bf, null);
    s.setField("daysToBreach", fields.daysToBreach);
    
    s.setFieldProperty("loanVisitFee", "textfont", bf, null);
    s.setField("loanVisitFee", fields.loanVisitFee);
    
    s.setFieldProperty("withdrawFee", "textfont", bf, null);
    s.setField("withdrawFee", fields.withdrawFee);
    
    s.setFieldProperty("loanRiskFee", "textfont", bf, null);
    s.setField("loanRiskFee", fields.loanRiskFee);
    
    s.setFieldProperty("loanRiskFeeAmount", "textfont", bf, null);
    s.setField("loanRiskFeeAmount", fields.loanRiskFeeAmount);
    
    s.setFieldProperty("zqr", "textfont", bf, null);
    s.setField("zqr", fields.zqr);
    
    s.setFieldProperty("zwr", "textfont", bf, null);
    s.setField("zwr", fields.zwr);
    
    s.setFieldProperty("fr", "textfont", bf, null);
    s.setField("fr", fields.fr);
    
    s.setFieldProperty("Name", "textfont", bf, null);
    s.setField("Name", fields.name);
    
    s.setFieldProperty("titleName", "textfont", bf, null);
    s.setField("titleName", fields.titleName);
    
    s.setFieldProperty("url", "textfont", bf, null);
    s.setField("url", fields.url);
    
    s.setFieldProperty("signDate", "textfont", bf, null);
    s.setField("signDate", fields.signDate);
    
    //在域中增加受让人信息
    s.setFieldProperty("SRRloginName", "textfont", bf, null);
    s.setField("SRRloginName", fields.SRRloginName);
    
    s.setFieldProperty("SRRName", "textfont", bf, null);
    s.setField("SRRName", fields.SRRName);
    
    s.setFieldProperty("SRRIdNumber", "textfont", bf, null);
    s.setField("SRRIdNumber", fields.SRRIdNumber);
    
    s.setFieldProperty("originalAmount", "textfont", bf, null);
    s.setField("originalAmount", fields.originalAmount);
    
    s.setFieldProperty("assignAmount", "textfont", bf, null);
    s.setField("assignAmount", fields.assignAmount);
    
    s.setFieldProperty("assignFeeAmount", "textfont", bf, null);
    s.setField("assignFeeAmount", fields.assignFeeAmount);
    
    s.setFieldProperty("assignDate", "textfont", bf, null);
    s.setField("assignDate", fields.assignDate);
    
    s.setFieldProperty("assignPeriod", "textfont", bf, null);
    s.setField("assignPeriod", fields.assignPeriod);
    
    s.setFieldProperty("assignDateFrom", "textfont", bf, null);
    s.setField("assignDateFrom", fields.assignDateFrom);
    
    s.setFieldProperty("assignDateTo", "textfont", bf, null);
    s.setField("assignDateTo", fields.assignDateTo);

    Map<String, Object> values = fields.values;
    if (values != null)
    {
      if (values.containsKey("loanBidAmount"))
      {
        int loanBidAmount = ((Integer)values.get("loanBidAmount")).intValue();
        s.setFieldProperty("loanBidAmount", "textfont", bf, null);
        s.setField("loanBidAmount", String.format("人民币%d元整", new Object[] { Integer.valueOf(loanBidAmount) }));
      }
      if (values.containsKey("userInfo"))
      {
        UserInfo info = (UserInfo)values.get("userInfo");
        CareerInfo careerInfo = info.getCareer();
        logger.info("contract out careerInfo info: {}", careerInfo);
        if ((careerInfo != null) && (careerInfo.getCompany() != null) && (!StringUtils.isEmpty(careerInfo.getCompany().getName())))
        {
          String companyName = careerInfo.getCompany().getName();
          
          s.setFieldProperty("companyName", "textfont", bf, null);
          s.setField("companyName", companyName);
          logger.info("companName: {}", companyName);
          
          StringBuilder buffer = new StringBuilder(companyName);
          
          String companyNamePrivacy = companyName.length() > 6 ? buffer.replace(0, companyName.length() - 6, "********").toString() : "********";
          

          s.setFieldProperty("companyNamePrivacy", "textfont", bf, null);
          s.setField("companyNamePrivacy", companyNamePrivacy);
          logger.info("companyNamePrivacy: {}", companyNamePrivacy);
        }
        PersonalInfo personalInfo = info.getPersonal();
        if (personalInfo != null)
        {
          PlaceInfo placeInfo = personalInfo.getPlace();
          if ((placeInfo != null) && (!StringUtils.isEmpty(placeInfo.getCurrentAddress())))
          {
            s.setFieldProperty(contractPartyAddress(ContractParty.FIRST), "textfont", bf, null);
            s.setField(contractPartyAddress(ContractParty.FIRST), placeInfo.getCurrentAddress());
          }
        }
      }
      if (values.containsKey("shadowBorrower"))
      {
        User shadowBorrower = (User)values.get("shadowBorrower");
        s.setFieldProperty("shadowBorrowerName", "textfont", bf, null);
        s.setField("shadowBorrowerName", shadowBorrower.getName());
        
        s.setFieldProperty("shadowBorrowerIdNumber", "textfont", bf, null);
        s.setField("shadowBorrowerIdNumber", shadowBorrower.getIdNumber());
        
        s.setFieldProperty("shadowBorrowerLoginName", "textfont", bf, null);
        s.setField("shadowBorrowerLoginName", shadowBorrower.getLoginName());
      }
      if (values.containsKey("CJRAddress"))
      {
        String CJRAddress = (String)values.get("CJRAddress");
        s.setFieldProperty("CJRAddress", "textfont", bf, null);
        s.setField("CJRAddress", CJRAddress);
      }
      if (values.containsKey("JKRAddress"))
      {
        String JKRAddress = (String)values.get("JKRAddress");
        s.setFieldProperty("JKRAddress", "textfont", bf, null);
        s.setField("JKRAddress", JKRAddress);
      }
      if (values.containsKey("loanRequest"))
      {
        LoanRequest loanRequest = (LoanRequest)values.get("loanRequest");
        s.setFieldProperty("loanRequestAmount", "textfont", bf, null);
        s.setField("loanRequestAmount", "人民币" + String.valueOf(loanRequest.getAmount()) + "元整");
        s.setFieldProperty("loanRequestName", "textfont", bf, null);
        s.setField("loanRequestName", loanRequest.getTitle());
        if (!StringUtils.isEmpty(loanRequest.getDescription()))
        {
          s.setFieldProperty("loanRequestDescription", "textfont", bf, null);
          s.setField("loanRequestDescription", loanRequest.getDescription());
        }
        if (!StringUtils.isEmpty(loanRequest.getGuaranteeInfo()))
        {
          s.setFieldProperty("loanRequestGuaranteeInfo", "textfont", bf, null);
          s.setField("loanRequestGuaranteeInfo", loanRequest.getGuaranteeInfo());
        }
        if (!StringUtils.isEmpty(loanRequest.getRiskInfo()))
        {
          s.setFieldProperty("loanRequestRiskInfo", "textfont", bf, null);
          s.setField("loanRequestRiskInfo", loanRequest.getRiskInfo());
        }
        if (!StringUtils.isEmpty(loanRequest.getMortgageInfo()))
        {
          s.setFieldProperty("loanRequestMortgageInfo", "textfont", bf, null);
          s.setField("loanRequestMortgageInfo", loanRequest.getMortgageInfo());
        }
        if (!StringUtils.isEmpty(loanRequest.getTitle()))
        {
          s.setFieldProperty("loanRequestJKSerial", "textfont", bf, null);
          

          s.setField("loanRequestJKSerial", loanRequest.getTitle().substring(loanRequest.getTitle().indexOf("-") + 1));
          
          s.setFieldProperty("loanRequestDBSerial", "textfont", bf, null);
          

          s.setField("loanRequestDBSerial", loanRequest.getTitle().substring(loanRequest.getTitle().indexOf("-") + 1));
          
          s.setFieldProperty("loanRequestFWSerial", "textfont", bf, null);
          s.setField("loanRequestFWSerial", "FW-" + loanRequest.getTitle());
        }
      }
      if (values.containsKey("vehicle"))
      {
        Vehicle vehicle = (Vehicle)values.get("vehicle");
        if (!StringUtils.isEmpty(vehicle.getBrand()))
        {
          s.setFieldProperty("vehicleBrand", "textfont", bf, null);
          s.setField("vehicleBrand", vehicle.getBrand());
        }
        if (!StringUtils.isEmpty(vehicle.getModel()))
        {
          s.setFieldProperty("vehicleModel", "textfont", bf, null);
          s.setField("vehicleModel", vehicle.getModel());
        }
        s.setFieldProperty("vehicleYearOfPurchase", "textfont", bf, null);
        s.setField("vehicleYearOfPurchase", String.valueOf(vehicle.getYearOfPurchase()));
        
        s.setFieldProperty("vehicleMileage", "textfont", bf, null);
        s.setField("vehicleMileage", String.valueOf(vehicle.getMileage()));
        
        s.setFieldProperty("vehicleOperating", "textfont", bf, null);
        s.setField("vehicleOperating", String.valueOf(vehicle.isOperating() ? "运营" : "非运营"));
        
        s.setFieldProperty("vehicleEstimatedValue", "textfont", bf, null);
        s.setField("vehicleEstimatedValue", String.valueOf(vehicle.getEstimatedValue()));
      }
      if (values.containsKey("guaranteeCorporationUser"))
      {
        CorporationUser corporationUser = (CorporationUser)values.get("guaranteeCorporationUser");
        if (!StringUtils.isEmpty(corporationUser.getBusiCode()))
        {
          s.setFieldProperty("guaranteeCorporationBusiCode", "textfont", bf, null);
          s.setField("guaranteeCorporationBusiCode", corporationUser.getBusiCode());
        }
        if (!StringUtils.isEmpty(corporationUser.getCategory()))
        {
          s.setFieldProperty("guaranteeCorporationCategory", "textfont", bf, null);
          s.setField("guaranteeCorporationCategory", corporationUser.getCategory());
        }
        if (!StringUtils.isEmpty(corporationUser.getName()))
        {
          s.setFieldProperty("guaranteeCorporationName", "textfont", bf, null);
          s.setField("guaranteeCorporationName", corporationUser.getName());
        }
        if (!StringUtils.isEmpty(corporationUser.getOrgCode()))
        {
          s.setFieldProperty("guaranteeCorporationOrgCode", "textfont", bf, null);
          s.setField("guaranteeCorporationOrgCode", corporationUser.getOrgCode());
        }
        if (!StringUtils.isEmpty(corporationUser.getShortName()))
        {
          s.setFieldProperty("guaranteeCorporationShortName", "textfont", bf, null);
          s.setField("guaranteeCorporationShortName", corporationUser.getShortName());
        }
        if (!StringUtils.isEmpty(corporationUser.getTaxCode()))
        {
          s.setFieldProperty("guaranteeCorporationTaxCode", "textfont", bf, null);
          s.setField("guaranteeCorporationTaxCode", corporationUser.getTaxCode());
        }
        if (corporationUser.getType() != null)
        {
          s.setFieldProperty("guaranteeCorporationType", "textfont", bf, null);
          s.setField("guaranteeCorporationType", corporationUser.getType().getKey());
        }
        if (!StringUtils.isEmpty(corporationUser.getUserName()))
        {
          s.setFieldProperty("guaranteeCorporationUserName", "textfont", bf, null);
          s.setField("guaranteeCorporationUserName", corporationUser.getUserName());
        }
        if (!StringUtils.isEmpty(corporationUser.getLoginName()))
        {
          s.setFieldProperty("guaranteeCorporationUserLoginName", "textfont", bf, null);
          s.setField("guaranteeCorporationUserLoginName", corporationUser.getLoginName());
        }
        if (!StringUtils.isEmpty(corporationUser.getUserIdNumber()))
        {
          s.setFieldProperty("guaranteeCorporationUserIdNumber", "textfont", bf, null);
          s.setField("guaranteeCorporationUserIdNumber", corporationUser.getUserIdNumber());
          
          s.setFieldProperty("guaranteeCorporationUserIdNumberPrivacy", "textfont", bf, null);
          s.setField("guaranteeCorporationUserIdNumberPrivacy", toStyleIdNumber(corporationUser.getUserIdNumber()));
        }
        if (!StringUtils.isEmpty(corporationUser.getUserMobile()))
        {
          s.setFieldProperty("guaranteeCorporationUserMobile", "textfont", bf, null);
          s.setField("guaranteeCorporationUserMobile", corporationUser.getUserMobile());
        }
        if (!StringUtils.isEmpty(corporationUser.getUserEmail()))
        {
          s.setFieldProperty("guaranteeCorporationUserEmail", "textfont", bf, null);
          s.setField("guaranteeCorporationUserEmail", corporationUser.getUserEmail());
        }
      }
      if (values.containsKey("guaranteeCorporationInfo"))
      {
        CorporationInfo info = (CorporationInfo)values.get("guaranteeCorporationInfo");
        if (!StringUtils.isEmpty(info.getAddress()))
        {
          s.setFieldProperty("guaranteeCorporationAddress", "textfont", bf, null);
          s.setField("guaranteeCorporationAddress", info.getAddress());
        }
        if (!StringUtils.isEmpty(info.getBusinessScope()))
        {
          s.setFieldProperty("guaranteeCorporationBusinessScope", "textfont", bf, null);
          s.setField("guaranteeCorporationBusinessScope", info.getBusinessScope());
        }
        if (!StringUtils.isEmpty(info.getContactEmail()))
        {
          s.setFieldProperty("guaranteeCorporationContactEmail", "textfont", bf, null);
          s.setField("guaranteeCorporationContactEmail", info.getContactEmail());
        }
        if (!StringUtils.isEmpty(info.getContactPersion()))
        {
          s.setFieldProperty("guaranteeCorporationContactPerson", "textfont", bf, null);
          s.setField("guaranteeCorporationContactPerson", info.getContactPersion());
        }
        if (!StringUtils.isEmpty(info.getContactPhone()))
        {
          s.setFieldProperty("guaranteeCorporationContactPhone", "textfont", bf, null);
          s.setField("guaranteeCorporationContactPhone", info.getContactPhone());
        }
        if (!StringUtils.isEmpty(info.getDescription()))
        {
          s.setFieldProperty("guaranteeCorporationDescription", "textfont", bf, null);
          s.setField("guaranteeCorporationDescription", info.getDescription());
        }
        if (!StringUtils.isEmpty(info.getRegisteredLocation()))
        {
          s.setFieldProperty("guaranteeCorporationRegisteredLocation", "textfont", bf, null);
          s.setField("guaranteeCorporationRegisteredLocation", info.getRegisteredLocation());
        }
        if (!StringUtils.isEmpty(info.getUrl()))
        {
          s.setFieldProperty("guaranteeCorporationUrl", "textfont", bf, null);
          s.setField("guaranteeCorporationUrl", info.getUrl());
        }
        if (info.getRegisteredCapital() != null)
        {
          s.setFieldProperty("guaranteeCorporationRegisteredCapital", "textfont", bf, null);
          s.setField("guaranteeCorporationRegisteredCapital", String.valueOf(info.getRegisteredCapital().intValue()));
        }
        if (info.getTimeEstablished() != null)
        {
          s.setFieldProperty("guaranteeCorporationTimeEstablished", "textfont", bf, null);
          s.setField("guaranteeCorporationTimeEstablished", toPdfDateString(info.getTimeEstablished().toDate()));
        }
      }
      if (values.containsKey("guaranteeLegalPerson"))
      {
        User user = (User)values.get("guaranteeLegalPerson");
        if (!StringUtils.isEmpty(user.getEmail()))
        {
          s.setFieldProperty("guaranteeLegalPersonEmail", "textfont", bf, null);
          s.setField("guaranteeLegalPersonEmail", user.getEmail());
        }
        if (!StringUtils.isEmpty(user.getMobile()))
        {
          s.setFieldProperty("guaranteeLegalPersonMobile", "textfont", bf, null);
          s.setField("guaranteeLegalPersonMobile", user.getMobile());
        }
        if (!StringUtils.isEmpty(user.getIdNumber()))
        {
          s.setFieldProperty("guaranteeLegalPersonIdNumber", "textfont", bf, null);
          s.setField("guaranteeLegalPersonIdNumber", user.getIdNumber());
          
          s.setFieldProperty("guaranteeLegalPersonIdNumberPrivacy", "textfont", bf, null);
          s.setField("guaranteeLegalPersonIdNumberPrivacy", toStyleIdNumber(user.getIdNumber()));
        }
        if (!StringUtils.isEmpty(user.getLoginName()))
        {
          s.setFieldProperty("guaranteeLegalPersonLoginName", "textfont", bf, null);
          s.setField("guaranteeLegalPersonLoginName", user.getIdNumber());
        }
        if (!StringUtils.isEmpty(user.getName()))
        {
          s.setFieldProperty("guaranteeLegalPersonName", "textfont", bf, null);
          s.setField("guaranteeLegalPersonName", user.getName());
        }
      }
      if (values.containsKey("requestProviderCorporationUser"))
      {
        CorporationUser corporationUser = (CorporationUser)values.get("requestProviderCorporationUser");
        if (!StringUtils.isEmpty(corporationUser.getBusiCode()))
        {
          s.setFieldProperty("requestProviderCorporationBusiCode", "textfont", bf, null);
          s.setField("requestProviderCorporationBusiCode", corporationUser.getBusiCode());
        }
        if (!StringUtils.isEmpty(corporationUser.getCategory()))
        {
          s.setFieldProperty("requestProviderCorporationCategory", "textfont", bf, null);
          s.setField("requestProviderCorporationCategory", corporationUser.getCategory());
        }
        if (!StringUtils.isEmpty(corporationUser.getName()))
        {
          s.setFieldProperty("requestProviderCorporationName", "textfont", bf, null);
          s.setField("requestProviderCorporationName", corporationUser.getName());
          
          s.setFieldProperty(contractPartyName(ContractParty.FIRST), "textfont", bf, null);
          s.setField(contractPartyName(ContractParty.FIRST), corporationUser.getName());
        }
        if (!StringUtils.isEmpty(corporationUser.getOrgCode()))
        {
          s.setFieldProperty("requestProviderCorporationOrgCode", "textfont", bf, null);
          s.setField("requestProviderCorporationOrgCode", corporationUser.getOrgCode());
          
          s.setFieldProperty(contractPartyCode(ContractParty.FIRST), "textfont", bf, null);
          s.setField(contractPartyCode(ContractParty.FIRST), corporationUser.getOrgCode());
        }
        if (!StringUtils.isEmpty(corporationUser.getShortName()))
        {
          s.setFieldProperty("requestProviderCorporationShortName", "textfont", bf, null);
          s.setField("requestProviderCorporationShortName", corporationUser.getShortName());
        }
        if (!StringUtils.isEmpty(corporationUser.getTaxCode()))
        {
          s.setFieldProperty("requestProviderCorporationTaxCode", "textfont", bf, null);
          s.setField("requestProviderCorporationTaxCode", corporationUser.getTaxCode());
        }
        if (corporationUser.getType() != null)
        {
          s.setFieldProperty("requestProviderCorporationType", "textfont", bf, null);
          s.setField("requestProviderCorporationType", corporationUser.getType().getKey());
        }
        if (!StringUtils.isEmpty(corporationUser.getUserName()))
        {
          s.setFieldProperty("requestProviderCorporationUserName", "textfont", bf, null);
          s.setField("requestProviderCorporationUserName", corporationUser.getUserName());
        }
        if (!StringUtils.isEmpty(corporationUser.getLoginName()))
        {
          s.setFieldProperty("requestProviderCorporationUserLoginName", "textfont", bf, null);
          s.setField("requestProviderCorporationUserLoginName", corporationUser.getLoginName());
          
          s.setFieldProperty(contractPartyLoginName(ContractParty.FIRST), "textfont", bf, null);
          s.setField(contractPartyLoginName(ContractParty.FIRST), corporationUser.getLoginName());
        }
        if (!StringUtils.isEmpty(corporationUser.getUserIdNumber()))
        {
          s.setFieldProperty("requestProviderCorporationUserIdNumber", "textfont", bf, null);
          s.setField("requestProviderCorporationUserIdNumber", corporationUser.getUserIdNumber());
          
          s.setFieldProperty("requestProviderCorporationUserIdNumberPrivacy", "textfont", bf, null);
          s.setField("requestProviderCorporationUserIdNumberPrivacy", toStyleIdNumber(corporationUser.getUserIdNumber()));
        }
        if (!StringUtils.isEmpty(corporationUser.getUserMobile()))
        {
          s.setFieldProperty("requestProviderCorporationUserMobile", "textfont", bf, null);
          s.setField("requestProviderCorporationUserMobile", corporationUser.getUserMobile());
        }
        if (!StringUtils.isEmpty(corporationUser.getUserEmail()))
        {
          s.setFieldProperty("requestProviderCorporationUserEmail", "textfont", bf, null);
          s.setField("requestProviderCorporationUserEmail", corporationUser.getUserEmail());
        }
      }
      if (values.containsKey("requestProviderCorporationInfo"))
      {
        CorporationInfo info = (CorporationInfo)values.get("requestProviderCorporationInfo");
        if (!StringUtils.isEmpty(info.getAddress()))
        {
          s.setFieldProperty("requestProviderCorporationAddress", "textfont", bf, null);
          s.setField("requestProviderCorporationAddress", info.getAddress());
          
          s.setFieldProperty(contractPartyAddress(ContractParty.FIRST), "textfont", bf, null);
          s.setField(contractPartyAddress(ContractParty.FIRST), info.getAddress());
        }
        if (!StringUtils.isEmpty(info.getBusinessScope()))
        {
          s.setFieldProperty("requestProviderCorporationBusinessScope", "textfont", bf, null);
          s.setField("requestProviderCorporationBusinessScope", info.getBusinessScope());
        }
        if (!StringUtils.isEmpty(info.getContactEmail()))
        {
          s.setFieldProperty("requestProviderCorporationContactEmail", "textfont", bf, null);
          s.setField("requestProviderCorporationContactEmail", info.getContactEmail());
        }
        if (!StringUtils.isEmpty(info.getContactPersion()))
        {
          s.setFieldProperty("requestProviderCorporationContactPerson", "textfont", bf, null);
          s.setField("requestProviderCorporationContactPerson", info.getContactPersion());
        }
        if (!StringUtils.isEmpty(info.getContactPhone()))
        {
          s.setFieldProperty("requestProviderCorporationContactPhone", "textfont", bf, null);
          s.setField("requestProviderCorporationContactPhone", info.getContactPhone());
        }
        if (!StringUtils.isEmpty(info.getDescription()))
        {
          s.setFieldProperty("requestProviderCorporationDescription", "textfont", bf, null);
          s.setField("requestProviderCorporationDescription", info.getDescription());
        }
        if (!StringUtils.isEmpty(info.getRegisteredLocation()))
        {
          s.setFieldProperty("requestProviderCorporationRegisteredLocation", "textfont", bf, null);
          s.setField("requestProviderCorporationRegisteredLocation", info.getRegisteredLocation());
        }
        if (!StringUtils.isEmpty(info.getUrl()))
        {
          s.setFieldProperty("requestProviderCorporationUrl", "textfont", bf, null);
          s.setField("requestProviderCorporationUrl", info.getUrl());
        }
        if (info.getRegisteredCapital() != null)
        {
          s.setFieldProperty("requestProviderCorporationRegisteredCapital", "textfont", bf, null);
          s.setField("requestProviderCorporationRegisteredCapital", String.valueOf(info.getRegisteredCapital().intValue()));
        }
        if (info.getTimeEstablished() != null)
        {
          s.setFieldProperty("requestProviderCorporationTimeEstablished", "textfont", bf, null);
          s.setField("requestProviderCorporationTimeEstablished", toPdfDateString(info.getTimeEstablished().toDate()));
        }
      }
      if (values.containsKey("requestProviderLegalPerson"))
      {
        User user = (User)values.get("requestProviderLegalPerson");
        if (!StringUtils.isEmpty(user.getEmail()))
        {
          s.setFieldProperty("requestProviderLegalPersonEmail", "textfont", bf, null);
          s.setField("requestProviderLegalPersonEmail", user.getEmail());
        }
        if (!StringUtils.isEmpty(user.getMobile()))
        {
          s.setFieldProperty("requestProviderLegalPersonMobile", "textfont", bf, null);
          s.setField("requestProviderLegalPersonMobile", user.getMobile());
        }
        if (!StringUtils.isEmpty(user.getIdNumber()))
        {
          s.setFieldProperty("requestProviderLegalPersonIdNumber", "textfont", bf, null);
          s.setField("requestProviderLegalPersonIdNumber", user.getIdNumber());
          
          s.setFieldProperty("requestProviderLegalPersonIdNumberPrivacy", "textfont", bf, null);
          s.setField("requestProviderLegalPersonIdNumberPrivacy", toStyleIdNumber(user.getIdNumber()));
        }
        if (!StringUtils.isEmpty(user.getLoginName()))
        {
          s.setFieldProperty("requestProviderLegalPersonLoginName", "textfont", bf, null);
          s.setField("requestProviderLegalPersonLoginName", user.getIdNumber());
        }
        if (!StringUtils.isEmpty(user.getName()))
        {
          s.setFieldProperty("requestProviderLegalPersonName", "textfont", bf, null);
          s.setField("requestProviderLegalPersonName", user.getName());
        }
      }
    }
    Map<String, String> extendValues = fields.extendValues;
    if (extendValues != null) {
      for (String value : extendValues.keySet())
      {
        s.setFieldProperty(value, "textfont", bf, null);
        s.setField(value, (String)extendValues.get(value));
      }
    }
    ps.setFormFlattening(true);
    ps.close();
    
    byte[] byteArray = bos.toByteArray();
    bos.close();
    
    return byteArray;
  }
  
  private static void setCIdNumber(AcroFields acro, String CJRIdNumber)
    throws IOException, DocumentException
  {
    char[] c = CJRIdNumber.toCharArray();
    for (int i = 0; i < 18; i++) {
      acro.setField("C" + i, String.valueOf(c[i]));
    }
  }
  
  private static void setJIdNumber(AcroFields acro, String jKRIdNumber)
    throws IOException, DocumentException
  {
    char[] c = jKRIdNumber.toCharArray();
    for (int i = 0; i < 18; i++) {
      acro.setField("J" + i, String.valueOf(c[i]));
    }
  }
  
  public static Paragraph addInvestorsTableForObligator(Fields fields)
    throws DocumentException
  {
    Paragraph ph = new Paragraph();
    
    PdfPTable t = new PdfPTable(4);
    
    t.setSpacingBefore(25.0F);
    
    t.setSpacingAfter(25.0F);
    
    t.setWidths(new float[] { 0.2F, 0.2F, 0.35F, 0.25F });
    
    FontSelector selector = new FontSelector();
    selector.addFont(FontFactory.getFont("Times-Roman", 12.0F));
    
    selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", false));
    

    PdfPCell c1 = new PdfPCell(selector.process("用户名"));
    
    t.addCell(c1);
    
    PdfPCell c2 = new PdfPCell(selector.process("真实姓名"));
    
    t.addCell(c2);
    
    PdfPCell c3 = new PdfPCell(selector.process("身份证号码"));
    
    t.addCell(c3);
    
    PdfPCell c4 = new PdfPCell(selector.process("借出金额（元）"));
    
    t.addCell(c4);
    for (int i = 0; i < fields.investorList.size(); i++)
    {
      Investor user = (Investor)fields.investorList.get(i);
      
      t.addCell(selector.process(user.loginName));
      
      t.addCell(selector.process(user.name));
      
      t.addCell(selector.process(user.idNumberPrivacy));
      
      t.addCell(selector.process(user.amount));
    }
    ph.add(t);
    return ph;
  }
  
  private static Paragraph addRepayTableForInvestor(Fields fields)
    throws DocumentException
  {
    Paragraph ph = new Paragraph();
    
    PdfPTable t = new PdfPTable(5);
    
    t.setWidths(new float[] { 0.1F, 0.24F, 0.22F, 0.22F, 0.22F });
    
    t.setSpacingBefore(25.0F);
    
    t.setSpacingAfter(25.0F);
    
    FontSelector selector = new FontSelector();
    selector.addFont(FontFactory.getFont("Times-Roman", 12.0F));
    
    selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", false));
    

    PdfPCell c1 = new PdfPCell(selector.process("期数"));
    
    t.addCell(c1);
    
    PdfPCell c2 = new PdfPCell(selector.process("还款日期"));
    
    t.addCell(c2);
    
    PdfPCell c3 = new PdfPCell(selector.process("还款金额"));
    
    t.addCell(c3);
    
    PdfPCell c4 = new PdfPCell(selector.process("本金"));
    
    t.addCell(c4);
    
    PdfPCell c5 = new PdfPCell(selector.process("利息"));
    
    t.addCell(c5);
    
    int i = 0;
    if (!fields.repaymentMonthlyListForInvestor.isEmpty()) {
      for (Repayment repayment : fields.repaymentMonthlyListForInvestor)
      {
        t.addCell(selector.process(String.valueOf(++i)));
        
        t.addCell(selector.process(toPdfDateString(repayment.getDueDate().toDate())));
        
        t.addCell(selector.process("￥ " + repayment.getAmount()));
        
        t.addCell(selector.process("￥ " + repayment.getAmountPrincipal()));
        
        t.addCell(selector.process("￥ " + repayment.getAmountInterest()));
      }
    } else if (!fields.repayAmountMonthly.isEmpty()) {
      for (LoanRepayment repayment : fields.repaymentMonthlyList)
      {
        t.addCell(selector.process(String.valueOf(++i)));
        
        t.addCell(selector.process(toPdfDateString(repayment.getRepayment().getDueDate().toDate())));
        
        t.addCell(selector.process("￥ " + repayment.getRepayment().getAmount()));
        
        t.addCell(selector.process("￥ " + repayment.getRepayment().getAmountPrincipal()));
        
        t.addCell(selector.process("￥ " + repayment.getRepayment().getAmountInterest()));
      }
    }
    ph.add(t);
    return ph;
  }
  
  private static Paragraph addRepayTableForObligator(Fields fields)
    throws DocumentException
  {
    Paragraph ph = new Paragraph();
    
    PdfPTable t = new PdfPTable(6);
    
    t.setWidths(new float[] { 0.07F, 0.23F, 0.17F, 0.17F, 0.17F, 0.19F });
    
    t.setSpacingBefore(25.0F);
    
    t.setSpacingAfter(25.0F);
    
    FontSelector selector = new FontSelector();
    selector.addFont(FontFactory.getFont("Times-Roman", 12.0F));
    
    selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", false));
    

    PdfPCell c1 = new PdfPCell(selector.process("期数"));
    
    t.addCell(c1);
    
    PdfPCell c2 = new PdfPCell(selector.process("应还日期"));
    
    t.addCell(c2);
    
    PdfPCell c3 = new PdfPCell(selector.process("应还本金"));
    
    t.addCell(c3);
    
    PdfPCell c4 = new PdfPCell(selector.process("应还利息"));
    
    t.addCell(c4);
    
    PdfPCell c5 = new PdfPCell(selector.process("借款管理费"));
    
    t.addCell(c5);
    
    PdfPCell c6 = new PdfPCell(selector.process("还款总额"));
    t.addCell(c6);
    
    BigDecimal totalPrincipal = BigDecimal.ZERO;
    BigDecimal totalRepayAmount = BigDecimal.ZERO;
    BigDecimal totalInterest = BigDecimal.ZERO;
    BigDecimal totalManageAmount = BigDecimal.ZERO;
    




    BigDecimal loanManageFeeMonthly = BigDecimal.ZERO;
    if ((fields.values != null) && (fields.values.containsKey("loanManageFeeMonthly"))) {
      loanManageFeeMonthly = (BigDecimal)fields.values.get("loanManageFeeMonthly");
    }
    for (int i = 0; i < fields.repaymentMonthlyListForObligator.size(); i++)
    {
      LoanRepayment repayment = (LoanRepayment)fields.repaymentMonthlyListForObligator.get(i);
      
      t.addCell(selector.process(String.valueOf(i + 1)));
      
      t.addCell(selector.process(toPdfDateString(repayment.getRepayment().getDueDate().toDate())));
      
      BigDecimal amountPrincipal = repayment.getRepayment().getAmountPrincipal();
      t.addCell(selector.process("￥ " + amountPrincipal));
      totalPrincipal = totalPrincipal.add(amountPrincipal);
      
      BigDecimal amountInterest = repayment.getRepayment().getAmountInterest();
      t.addCell(selector.process("￥ " + amountInterest));
      totalInterest = totalInterest.add(amountInterest);
      
      BigDecimal totalAmount = repayment.getRepayment().getAmount();
      
      t.addCell(selector.process(String.format("￥ %.2f", new Object[] { Float.valueOf(loanManageFeeMonthly.floatValue()) })));
      totalManageAmount = totalManageAmount.add(loanManageFeeMonthly);
      
      totalAmount = totalAmount.add(loanManageFeeMonthly);
      totalRepayAmount = totalRepayAmount.add(totalAmount);
      t.addCell(selector.process("￥ " + totalAmount));
    }
    t.addCell(selector.process(" "));
    t.addCell(selector.process("总计"));
    t.addCell(selector.process(String.format("￥ %.2f", new Object[] { totalPrincipal })));
    t.addCell(selector.process(String.format("￥ %.2f", new Object[] { totalInterest })));
    t.addCell(selector.process(String.format("￥ %.2f", new Object[] { totalManageAmount })));
    t.addCell(selector.process(String.format("￥ %.2f", new Object[] { totalRepayAmount })));
    
    ph.add(t);
    
    return ph;
  }
  
  private static Paragraph addClaimRepayPlanTable(Fields fields)
    throws DocumentException
  {
    Paragraph ph = new Paragraph();
    PdfPTable t = new PdfPTable(3);
    t.setWidths(new float[] { 0.1F, 0.2F, 0.2F });
    t.setSpacingBefore(25.0F);
    t.setSpacingAfter(25.0F);
    
    FontSelector selector = new FontSelector();
    selector.addFont(FontFactory.getFont("Times-Roman", 12.0F));
    
    selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", false));
    

    PdfPCell c1 = new PdfPCell(selector.process("期数"));
    c1.setHorizontalAlignment(1);
    t.addCell(c1);
    
    PdfPCell c2 = new PdfPCell(selector.process("应还日期"));
    c2.setHorizontalAlignment(1);
    t.addCell(c2);
    
    PdfPCell c3 = new PdfPCell(selector.process("还款总额"));
    c3.setHorizontalAlignment(1);
    t.addCell(c3);
    for (RepaymentPlan repaymentPlan : fields.RepaymentPlanList)
    {
      t.addCell(selector.process(String.valueOf(repaymentPlan.getPeriod())));
      t.addCell(selector.process(toPdfDateString(repaymentPlan.getRepaymentDate())));
      BigDecimal amountPrincipal = repaymentPlan.getRepaymentAmount();
      t.addCell(selector.process("￥ " + amountPrincipal));
    }
    ph.add(t);
    return ph;
  }
  
  private static void intsertImage(PdfStamper stamper, byte[] writing, int page, int x, int y)
    throws BadElementException, MalformedURLException, IOException, DocumentException
  {
    Image image = Image.getInstance(writing);
    PdfContentByte under = stamper.getUnderContent(page);
    image.setAbsolutePosition(x, y);
    under.addImage(image);
  }
  
  private static void waterMark(PdfReader reader, PdfStamper stamper, byte[] watermark, int page)
    throws BadElementException, MalformedURLException, IOException, DocumentException
  {
    Image image = Image.getInstance(watermark);
    float imageWidth = image.getWidth();
    float imageHeight = image.getHeight();
    PdfContentByte under = stamper.getUnderContent(page);
    Rectangle rectangle = reader.getPageSize(page);
    float h = rectangle.getHeight();
    float w = rectangle.getHeight();
    
    float dh = (float)((h - imageHeight * 0.5D) / 2.0D);
    image.scalePercent(50.0F);
    image.setAbsolutePosition(0.0F, dh);
    under.addImage(image);
  }
  
  private static String getSerial(ClientConfig clientConfig, Loan loan)
  {
    String serial = loan.getLoanRequest().getSerial();
    if (StringUtils.isEmpty(serial)) {
      return null;
    }
    if ((clientConfig.getFeatures().isExtendFailedLoan()) && (loan.isAutoSplitted())) {
      return serial + "-" + loan.getOrdinal();
    }
    return serial;
  }
  
  public static byte[] output(Map<String, String> fields, List<Table> tables, byte[] input, byte[] writing, int page, float percentX, float percentY)
  {
    try
    {
      byte[] pdf = templateToPdf(fields, input);
      
      logger.info("pdf length {}", Integer.valueOf(pdf.length));
      
      pdf = insertTablesToPdf(pdf, tables);
      
      logger.info("pdf with table length {}", Integer.valueOf(pdf.length));
      if (writing == null) {
        return pdf;
      }
      return insertWriting(pdf, writing, page, percentX, percentY);
    }
    catch (DocumentException|IOException e)
    {
      logger.error("out put pdf exception {}", e);
    }
    return null;
  }
  
  private static byte[] templateToPdf(Map<String, String> values, byte[] input)
    throws IOException, DocumentException
  {
    PdfReader reader = new PdfReader(input);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PdfStamper ps = new PdfStamper(reader, bos);
    AcroFields s = ps.getAcroFields();
    Iterator<Map.Entry<String, String>> i = values.entrySet().iterator();
    while (i.hasNext())
    {
      Map.Entry<String, String> o = (Map.Entry)i.next();
      String key = (String)o.getKey();
      s.setFieldProperty(key, "textfont", sFont, null);
      s.setField(key, (String)o.getValue());
    }
    ps.setFormFlattening(true);
    ps.close();
    byte[] pdf = bos.toByteArray();
    return pdf;
  }
  
  private static byte[] insertTablesToPdf(byte[] input, List<Table> tables)
    throws IOException, DocumentException
  {
    PdfReader reader = new PdfReader(input);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4);
    PdfWriter writer = PdfWriter.getInstance(document, bos);
    document.open();
    PdfContentByte cb = writer.getDirectContent();
    

    int n = reader.getNumberOfPages();
    for (int i = 0; i < n; i++)
    {
      PdfImportedPage page = writer.getImportedPage(reader, i + 1);
      
      document.newPage();
      cb.addTemplate(page, 0.0F, 0.0F);
    }
    document.newPage();
    for (Table table : tables) {
      insertTableToPdf(document, table);
    }
    document.close();
    byte[] pdf = bos.toByteArray();
    bos.close();
    return pdf;
  }
  
  public static void insertTableToPdf(Document document, Table table)
    throws DocumentException, IOException
  {
    if (!StringUtils.isEmpty(table.title)) {
      insertTitleToDocument(document, table.title, 12, 1);
    }
    Paragraph ph = new Paragraph();
    
    PdfPTable t = new PdfPTable(table.weights);
    
    t.setSpacingBefore(25.0F);
    
    t.setSpacingAfter(25.0F);
    
    t.setWidths(table.weights);
    
    FontSelector selector = new FontSelector();
    selector.addFont(FontFactory.getFont("Times-Roman", 10.0F));
    
    selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", false));
    for (List<String> values : table.values) {
      for (String value : values)
      {
        PdfPCell cell = new PdfPCell(selector.process(value));
        t.addCell(cell);
      }
    }
    ph.add(t);
    
    document.add(ph);
  }
  
  private static ContractSeal applyPersonalSeal(String clientCode, ContractSeal contractSeal, ContractSealConfig contractSealConfig)
  {
    User user = contractSeal.getSeal().getUser();
    if (user == null) {
      return contractSeal;
    }
    ConcurrentHashMap<String, String> map = new ConcurrentHashMap();
    map.put("certType", "2");
    map.put("certModel", "1");
    map.put("organCode", "678");
    map.put("userName", base64EncodeString(user.getName(), "UTF-8"));
    map.put("userIdNum", user.getIdNumber());
    map.put("IdTypeCode", "1");
    map.put("userIdNo", user.getIdNumber());
    map.put("orgName", base64EncodeString("测试机构", "UTF-8"));
    if (!clientCode.equalsIgnoreCase("FENG"))
    {
      map.put("EngName", base64EncodeString(user.getName(), "UTF-8"));
      map.put("email", StringUtils.isEmpty(user.getEmail()) ? "empty" : user.getEmail());
      map.put("address", "北京");
      map.put("TelNo", StringUtils.isEmpty(user.getMobile()) ? "empty" : user.getMobile());
    }
    StringBuilder params = new StringBuilder();
    Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
    for (int i = 0; iter.hasNext(); i++)
    {
      Map.Entry<String, String> entry = (Map.Entry)iter.next();
      String key = (String)entry.getKey();
      String val = (String)entry.getValue();
      params.append(i == 0 ? "?" : "&").append(key).append("=").append(val);
    }
    javax.ws.rs.client.Client client = ClientBuilder.newClient();
    


    System.out.println("prepare apply personal seal url=" + contractSealConfig.getApiPDFApplyCert() + params);
    logger.info("prepare apply personal seal [url={}] [user={}]", contractSealConfig.getApiPDFApplyCert() + params, user);
    WebTarget target = client.target(contractSealConfig.getApiPDFApplyCert() + params);
    
    String result = (String)target.request(new String[] { "application/x-www-form-urlencoded" }).get(String.class);
    if ((result != null) && (!"".equals(result))) {
      result = result.trim();
    }
    logger.info("contract apply [url={}] [certid={}]", target.getUri(), result);
    System.out.println("contract apply url=" + contractSealConfig.getApiPDFApplyCert() + params + " certid=" + result);
    
    contractSeal.getSeal().setCode(result);
    return contractSeal;
  }
  
  public static String toStyleIdNumber(String idNumber)
  {
    return new StringBuffer(idNumber).replace(6, 14, "********").toString();
  }
  
  public static String contractPartyCode(ContractParty contractParty)
  {
    return String.format("%1$sCode", new Object[] { contractParty.name().toLowerCase() });
  }
  
  public static String contractPartyName(ContractParty contractParty)
  {
    return String.format("%1$sName", new Object[] { contractParty.name().toLowerCase() });
  }
  
  public static String contractPartyLoginName(ContractParty contractParty)
  {
    return String.format("%1$sLoginName", new Object[] { contractParty.name().toLowerCase() });
  }
  
  public static String contractPartyAddress(ContractParty contractParty)
  {
    return String.format("%1$sAddress", new Object[] { contractParty.name().toLowerCase() });
  }
  
  public static byte[] templateToPdfForInvestorExtend(Fields fields, byte[] template, byte[] watermark)
    throws IOException, DocumentException
  {
    PdfReader reader = new PdfReader(template);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] pdf = resetFields(bos, reader, fields);
    
    ByteArrayInputStream bis = new ByteArrayInputStream(pdf);
    

    Map<String, List<String>> map = new HashMap();
    List<String> titleInvestor = new ArrayList();
    
    titleInvestor.add("出借人明细");
    titleInvestor.add(String.format("(归属于编号为 %s 的《借款及担保合同》)", new Object[] { fields.agreementNo }));
    map.put("titleInvestors", titleInvestor);
    pdf = insertTableToPdfForInvestorExtend(bis, reader, fields, map);
    
    bis = new ByteArrayInputStream(pdf);
    if (watermark != null) {
      pdf = insertWaterMark(bis, reader, watermark);
    }
    return pdf;
  }
  
  private static byte[] insertTableToPdfForInvestorExtend(ByteArrayInputStream bis, PdfReader reader, Fields fields, Map<String, List<String>> titleMap)
    throws DocumentException, IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4);
    PdfWriter writer = PdfWriter.getInstance(document, bos);
    document.open();
    PdfContentByte cb = writer.getDirectContent();
    

    reader = new PdfReader(bis);
    

    int n = reader.getNumberOfPages();
    for (int i = 0; i < n; i++)
    {
      PdfImportedPage page = writer.getImportedPage(reader, i + 1);
      
      document.newPage();
      cb.addTemplate(page, 0.0F, 0.0F);
    }
    document.newPage();
    List<String> titleInvestors = (List)titleMap.get("titleInvestors");
    if (CollectionUtils.isNotEmpty(titleInvestors)) {
      for (String title : titleInvestors) {
        insertTitleToDocument(document, title, 12, 1);
      }
    } else {
      insertTitleToDocument(document, "附件1：出借人列表", 12, 1);
    }
    document.add(addInvestorsTableForObligatorExtend(fields));
    











    document.close();
    
    byte[] byteArray = bos.toByteArray();
    bos.close();
    return byteArray;
  }
  
  public static Paragraph addInvestorsTableForObligatorExtend(Fields fields)
    throws DocumentException
  {
    Paragraph ph = new Paragraph();
    
    PdfPTable t = new PdfPTable(4);
    
    t.setSpacingBefore(25.0F);
    
    t.setSpacingAfter(25.0F);
    
    t.setWidths(new float[] { 0.1F, 0.3F, 0.35F, 0.25F });
    
    FontSelector selector = new FontSelector();
    selector.addFont(FontFactory.getFont("Times-Roman", 12.0F));
    
    selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", false));
    

    PdfPCell c1 = new PdfPCell(selector.process("序号"));
    
    t.addCell(c1);
    
    PdfPCell c2 = new PdfPCell(selector.process("融资平台注册用户名"));
    
    t.addCell(c2);
    
    PdfPCell c3 = new PdfPCell(selector.process("绑定账号/流水号"));
    
    t.addCell(c3);
    
    PdfPCell c4 = new PdfPCell(selector.process("出借金额"));
    
    t.addCell(c4);
    for (int i = 0; i < fields.investorList.size(); i++)
    {
      Investor user = (Investor)fields.investorList.get(i);
      t.addCell(selector.process(String.valueOf(i + 1)));
      
      t.addCell(selector.process(user.loginName));
      
      t.addCell(selector.process(user.idNumberPrivacy));
      
      t.addCell(selector.process(user.amount));
    }
    ph.add(t);
    return ph;
  }
  
  public static Fields convertToPdfField(String no, com.creditcloud.model.client.Client client, Loan loan, List<LoanRepayment> repaymentList, FeeConfig feeConfig, ClientConfig clientConfig, Date signDate, Map<String, Object> values)
  {
    User debtor = loan.getLoanRequest().getUser();
    
    Fields fields = new Fields();
    fields.values = values;
    if (values != null)
    {
      fields.values = values;
      fields.values.put("loanBidAmount", Integer.valueOf(loan.getBidAmount()));
    }
    String serial = getSerial(clientConfig, loan);
    if ((serial != null) && (!serial.isEmpty())) {
      fields.serial = serial;
    } else {
      fields.serial = (client.getCode() + no.substring(0, 8).toUpperCase());
    }
    if (feeConfig.getInvestInterestFee() != null) {
      fields.jzhglfRate = ("" + feeConfig.getInvestInterestFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanInterestFee() != null) {
      fields.yzhglfRate = ("" + feeConfig.getLoanInterestFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanManageFee() != null) {
      fields.loanManageFee = ("" + feeConfig.getLoanManageFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanGuaranteeFee() != null) {
      fields.fxbzjRate = ("" + feeConfig.getLoanGuaranteeFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanServiceFee() != null) {
      fields.zxRate = ("" + feeConfig.getLoanServiceFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanGuaranteeFee() != null) {
      fields.totalServiceRate = ("" + feeConfig.getLoanGuaranteeFee().getRate().add(feeConfig.getLoanServiceFee().getRate()).movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanOverdueFee() != null) {
      fields.yqRate = ("" + feeConfig.getLoanOverdueFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanPenaltyFee() != null) {
      fields.yqfxRate = ("" + feeConfig.getLoanPenaltyFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanManageFee() != null) {
      fields.jklxRate = ("" + feeConfig.getLoanManageFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    fields.daysToBreach = ("" + String.valueOf(feeConfig.getDaysToBreach()));
    if (feeConfig.getLoanVisitFee() != null) {
      fields.loanVisitFee = ("" + feeConfig.getLoanVisitFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getWithdrawFee() != null) {
      fields.withdrawFee = ("" + feeConfig.getWithdrawFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString());
    }
    if (feeConfig.getLoanRiskFee() != null) {
      fields.loanRiskFee = feeConfig.getLoanRiskFee().getRate().movePointRight(2).stripTrailingZeros().toPlainString();
    }
    fields.JKRName = debtor.getName();
    fields.JKRloginName = debtor.getLoginName();
    fields.JKRIdNumber = debtor.getIdNumber();
    fields.JKRIdNumberPrivacy = new StringBuffer(debtor.getIdNumber()).replace(6, 14, "********").toString();
    fields.zwr = debtor.getName();
    
    fields.companyName = ((String)values.get("companyName"));
    fields.companyAddress = ((String)values.get("companyAddress"));
    fields.legalRepresentative = debtor.getName();
    

    fields.amount = ("人民币" + loan.getAmount() + "元整");
    fields.amountUpper = (toChineseCurrency(Integer.valueOf(loan.getAmount())) + "整");
    
    fields.loanPurpose = loan.getLoanRequest().getPurpose().getKey();
    fields.loanRate = (loan.getRate() / 100.0F + "%");
    
















    Date timeFinished = signDate;
    
    fields.loanDate = toPdfDateString(timeFinished);
    

    Calendar c = Calendar.getInstance();
    c.setTime(timeFinished);
    c.add(5, loan.getDuration().getDays());
    c.add(1, loan.getDuration().getYears());
    c.add(2, loan.getDuration().getMonths());
    fields.endDate = toPdfDateString(c.getTime());
    

    fields.cxrDate = toPdfDateString(timeFinished);
    

    fields.repayDate = "见附件还款详情";
    

    fields.repayMethod = loan.getMethod().getKey();
    fields.repayMethodOrdinal = String.valueOf(loan.getMethod().ordinal() + 1);
    



    fields.repaymentNo = ("共" + repaymentList.size() + "期");
    

    fields.signDate = toPdfDateString(signDate);
    



    fields.serial = ("FW-" + fields.serial);
    














    fields.repaymentMonthlyListForObligator = repaymentList;
    
    return fields;
  }
  
  private static byte[] insertTableToPdfForObligator(ContractType type, ByteArrayInputStream bis, PdfReader reader, Fields fields)
    throws DocumentException, IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4);
    PdfWriter writer = PdfWriter.getInstance(document, bos);
    document.open();
    PdfContentByte cb = writer.getDirectContent();
    

    reader = new PdfReader(bis);
    

    int n = reader.getNumberOfPages();
    for (int i = 0; i < n; i++)
    {
      PdfImportedPage page = writer.getImportedPage(reader, i + 1);
      
      document.newPage();
      cb.addTemplate(page, 0.0F, 0.0F);
    }
    int attachNo = 0;
    if (!type.equals(ContractType.BROKERAGE))
    {
      document.newPage();
      attachNo++;
      insertTitleToDocument(document, "附件" + attachNo + "：出借人列表", 12, 1);
      document.add(addInvestorsTableForObligator(fields));
    }
    document.newPage();
    attachNo++;
    insertTitleToDocument(document, "附件" + attachNo + "：还款计划表", 12, 1);
    document.add(addRepayTableForObligator(fields));
    
    document.close();
    
    byte[] byteArray = bos.toByteArray();
    bos.close();
    return byteArray;
  }
  
  public static String base64EncodeString(String value, String charSetName)
  {
    String encodeValue = null;
    try
    {
      if (value != null) {
        encodeValue = Base64.encodeBytes(value.getBytes(charSetName));
      }
    }
    catch (UnsupportedEncodingException e) {}
    return encodeValue;
  }
  
  public static class Investor
  {
    public String loginName;
    public String name;
    public String idNumberPrivacy;
    public String amount;
  }
  
  public static class RepaymentMonthly
  {
    public BigDecimal no;
    public BigDecimal principal;
    public BigDecimal repayDate;
    public BigDecimal interestAmount;
    public BigDecimal amount;
    public BigDecimal loanManageAmount;
  }
}
