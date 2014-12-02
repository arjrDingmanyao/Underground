package com.creditcloud.contract.local;

import com.creditcloud.client.api.ClientService;
import com.creditcloud.common.bean.AbstractClientApplicationBean;
import com.creditcloud.config.ClientConfig;
import com.creditcloud.config.ClientFeatures;
import com.creditcloud.config.ContractSealConfig;
import com.creditcloud.config.Features;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.contract.ContractType;
import com.creditcloud.contract.dao.ContractTemplateDAO;
import com.creditcloud.contract.entities.ContractTemplate;
import com.creditcloud.model.exception.ClientCodeNotMatchException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

@Singleton
@LocalBean
public class ApplicationBean
  extends AbstractClientApplicationBean
{
  private static final String LOCAL_CONTRACT_PATH = "/var/CreditCloud/Contract/";
  @Inject
  Logger logger;
  @EJB
  ConfigManager configManager;
  @EJB
  ClientService clientService;
  @EJB
  ContractTemplateDAO contractTemplateDAO;
  private Features features;
  private ClientFeatures clientFeatures;
  private byte[] template;
  private byte[] brokerageTemplate;
  private byte[] watermark;
  
  public byte[] getTemplate()
  {
    return this.template;
  }
  
  public byte[] getBrokerageTemplate()
  {
    return this.brokerageTemplate;
  }
  
  public byte[] getWatermark()
  {
    return this.watermark;
  }
  
  @PostConstruct
  void init()
  {
    ClientConfig clientConfig = this.configManager.getClientConfig();
    this.features = clientConfig.getFeatures();
    this.clientFeatures = clientConfig.getClientFeatures();
    this.client = this.clientService.getClient(clientConfig.getCode());
    
    File loanTemplate = new File("/var/CreditCloud/Contract/loan.pdf");
    File brokerageTemplateF = new File("/var/CreditCloud/Contract/brokerage.pdf");
    File watermarkFile = new File("/var/CreditCloud/Contract/watermark.png");
    try
    {
      InputStream tempateIn = new FileInputStream(loanTemplate);Throwable localThrowable4 = null;
      try
      {
        InputStream brokerageTempateIn = new FileInputStream(brokerageTemplateF);Throwable localThrowable5 = null;
        try
        {
          InputStream watermarkIn = new FileInputStream(watermarkFile);Throwable localThrowable6 = null;
          try
          {
            this.template = IOUtils.toByteArray(tempateIn);
            this.brokerageTemplate = IOUtils.toByteArray(brokerageTempateIn);
            
            this.watermark = IOUtils.toByteArray(watermarkIn);
            if (this.contractTemplateDAO.getDefaultByType(this.client.getCode(), ContractType.LOAN) == null)
            {
              ContractTemplate ct = new ContractTemplate();
              ct.setName("默认借款合同模板");
              ct.setContent(this.template);
              ct.setClientCode(this.client.getCode());
              ct.setTimeCreated(new Date());
              ct.setDefault(true);
              ct.setType(ContractType.LOAN);
              this.contractTemplateDAO.create(ct);
              this.logger.info("Default template saved.");
            }
            if ((this.contractTemplateDAO.getDefaultByType(this.client.getCode(), ContractType.BROKERAGE) == null) && (this.clientFeatures.isEnableBrokerage()))
            {
              ContractTemplate ct = new ContractTemplate();
              ct.setName("默认居间合同模板");
              ct.setContent(this.brokerageTemplate);
              ct.setClientCode(this.client.getCode());
              ct.setType(ContractType.BROKERAGE);
              ct.setTimeCreated(new Date());
              ct.setDefault(true);
              this.contractTemplateDAO.create(ct);
              this.logger.info("Default BROKERAGE template saved.");
            }
            this.logger.info("ContractService loaded.");
          }
          catch (Throwable localThrowable1)
          {
            localThrowable6 = localThrowable1;throw localThrowable1;
          }
          finally {}
        }
        catch (Throwable localThrowable2)
        {
          localThrowable5 = localThrowable2;throw localThrowable2;
        }
        finally {}
      }
      catch (Throwable localThrowable3)
      {
        localThrowable4 = localThrowable3;throw localThrowable3;
      }
      finally
      {
        if (tempateIn != null) {
          if (localThrowable4 != null) {
            try
            {
              tempateIn.close();
            }
            catch (Throwable x2)
            {
              localThrowable4.addSuppressed(x2);
            }
          } else {
            tempateIn.close();
          }
        }
      }
    }
    catch (IOException ex)
    {
      this.logger.error("Can't read loan.pdf or watermark.png", ex);
    }
    this.logger.info("ApplicationBean in ConstractService initialized.[clientCode={}]", getClientCode());
  }
  
  public void checkClientCode(String clientCode)
  {
    if (!isValid(clientCode))
    {
      String cause = String.format("The incoming clientcode do not match the local client.[incoming=%s][local=%s]", new Object[] { clientCode, getClientCode() });
      this.logger.warn(cause);
      throw new ClientCodeNotMatchException(cause);
    }
  }
  
  public boolean isEnableShadowLoan()
  {
    return this.features.isEnableShadowLoan();
  }
  
  public ClientConfig getClientConfig()
  {
    return this.configManager.getClientConfig();
  }
  
  public ContractSealConfig getContractSealConfig()
  {
    return this.configManager.getContractSealConfig();
  }
}
