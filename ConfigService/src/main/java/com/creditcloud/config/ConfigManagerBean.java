package com.creditcloud.config;

import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.config.wealthproduct.WealthProductConfig;
import com.creditcloud.model.enums.loan.LoanRequestType;
import com.creditcloud.model.qualifier.Local;
import java.io.IOException;
import java.io.Reader;
import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;

@Remote
@Startup
@Singleton
public class ConfigManagerBean
	implements ConfigManager {

    @Inject
    Logger logger;

    @Inject
    @Local
    ConfigLoader loader;

    private JAXBContext context;

    private Unmarshaller unmarshaller;

    public ClientConfig getClientConfig() {
	return (ClientConfig) loadConfig(ClientConfig.class);
    }

    public SMSConfig getSMSConfig() {
	return (SMSConfig) loadConfig(SMSConfig.class);
    }

    public EmailConfig getEmailConfig() {
	return (EmailConfig) loadConfig(EmailConfig.class);
    }

    public UpYunConfig getUpYunConfig() {
	return (UpYunConfig) loadConfig(UpYunConfig.class);
    }

    public DeviceManagerConfig getDeviceManagerConfig() {
	return (DeviceManagerConfig) loadConfig(DeviceManagerConfig.class);
    }

    public PaymentConfig getPaymentConfig() {
	return (PaymentConfig) loadConfig(PaymentConfig.class);
    }

    public CreditManagerConfig getCreditManagerConfig() {
	return (CreditManagerConfig) loadConfig(CreditManagerConfig.class);
    }

    public FeeConfig getFeeConfig() {
	return (FeeConfig) loadConfig(FeeConfig.class);
    }

    public CertificateConfig getCertificateConfig() {
	return (CertificateConfig) loadConfig(CertificateConfig.class);
    }

    public AllWinConfig getAllWinConfig() {
	return (AllWinConfig) loadConfig(AllWinConfig.class);
    }

    public GuaranteeConfig getGuaranteeConfig() {
	return (GuaranteeConfig) loadConfig(GuaranteeConfig.class);
    }

    public FeeConfig getFeeConfig(LoanRequestType type) {
	if (type != null) {
	    GuaranteeConfig guaranteeConfig = getGuaranteeConfig();
	    if ((guaranteeConfig == null) || (guaranteeConfig.getFeeConfig() == null)) {
		return getFeeConfig();
	    }
	    return guaranteeConfig.getFeeConfig();
	}
	this.logger.debug("FeeConfig for LoanRequestType {} not found, use default FeeConfig.", type == null ? "null" : type);
	return getFeeConfig();
    }

    public RewardConfig getRewardConfig() {
	return (RewardConfig) loadConfig(RewardConfig.class);
    }

    public FuiouConfig getFuiouConfig() {
	return (FuiouConfig) loadConfig(FuiouConfig.class);
    }

    public UmpConfig getUmpConfig() {
	return (UmpConfig) loadConfig(UmpConfig.class);
    }

    public WealthProductConfig getWealthProductConfig() {
	return (WealthProductConfig) loadConfig(WealthProductConfig.class);
    }

    @PostConstruct
    void init() {
	try {
	    this.context = JAXBContext.newInstance(new Class[]{ClientConfig.class, SMSConfig.class, EmailConfig.class, UpYunConfig.class, DeviceManagerConfig.class, PaymentConfig.class, CreditManagerConfig.class, FeeConfig.class, CertificateConfig.class, AllWinConfig.class, GuaranteeConfig.class, RewardConfig.class, FuiouConfig.class, UmpConfig.class, RedisConfig.class, SentinelConfig.class, ContractSealConfig.class, CacheConfig.class,CarInsuranceConfig.class});

	    this.unmarshaller = this.context.createUnmarshaller();
	} catch (Exception ex) {
	    this.logger.error("Error init JAXB env", ex);
	}
    }

    private BaseConfig loadConfig(Class clazz) {
	BaseConfig result = null;
	String className = clazz.getName();
	try {
	    String configName = (String) clazz.getField("CONFIG_NAME").get(null);
	    try {
		Reader reader = this.loader.loadConfig(configName);
		Throwable localThrowable2 = null;
		try {
		    result = (BaseConfig) this.unmarshaller.unmarshal(reader);
		    result.setLastUpdate(System.currentTimeMillis());
		    result.setLastModified(this.loader.getLastModified(configName));
		    this.logger.debug(className + " loaded as config.\n" + result.toString());
		} catch (Throwable localThrowable1) {
		    localThrowable2 = localThrowable1;
		    throw localThrowable1;
		} finally {
		    if (reader != null) {
			if (localThrowable2 != null) {
			    try {
				reader.close();
			    } catch (Throwable x2) {
				localThrowable2.addSuppressed(x2);
			    }
			} else {
			    reader.close();
			}
		    }
		}
	    } catch (IOException ex) {
		this.logger.error("Cannot get Reader for " + className, ex);
	    } catch (JAXBException ex) {
		this.logger.error("Cannot unmarshall the " + className + ", check config content.", ex);
	    }
	} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
	    this.logger.error("Config class " + className + " may not have CONFIG_NAME as public static field.", ex);
	}
	return result;
    }

    public RedisConfig getRedisConfig() {
	return (RedisConfig) loadConfig(RedisConfig.class);
    }

    public SentinelConfig getSentinelConfig() {
	return (SentinelConfig) loadConfig(SentinelConfig.class);
    }

    public CacheConfig getCacheConfig() {
	return (CacheConfig) loadConfig(CacheConfig.class);
    }

    public ContractSealConfig getContractSealConfig() {
	return (ContractSealConfig) loadConfig(ContractSealConfig.class);
    }

    public CarInsuranceConfig getCarInsuranceConfig() {
	return (CarInsuranceConfig) loadConfig(CarInsuranceConfig.class);
    }
}
