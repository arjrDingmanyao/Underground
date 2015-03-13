/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.sms;

/**
 *
 * @author rooseek
 */
import com.creditcloud.config.CID;
import com.creditcloud.config.SMSConfig;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.client.Client;
import static com.creditcloud.sms.SMSType.CONFIRM_CREDITMARKET_AUTHENTICATE_MOBILE;
import static com.creditcloud.sms.SMSType.CONFIRM_CREDITMARKET_CHANGE_LOGIN_PASSWORD;
import static com.creditcloud.sms.SMSType.CONFIRM_CREDITMARKET_REGISTER;
import static com.creditcloud.sms.SMSType.CREDITMANAGER_MESSAGE;
import static com.creditcloud.sms.SMSType.CREDITMANAGER_RESET_PASSWORD;
import static com.creditcloud.sms.SMSType.CREDITMARKET_RESET_PASSWORD;
import static com.creditcloud.sms.SMSType.NOTIFICATION_CREDITMANAGER_EMPLOYEE_CREATED;
import static com.creditcloud.sms.SMSType.NOTIFICATION_CREDITMARKET_USER_CREATED;
import static com.creditcloud.sms.SMSType.NOTIFICATION_LOANREQUEST_STATUS;
import static com.creditcloud.sms.SMSType.NOTIFICATION_LOAN_STATUS;
import com.creditcloud.sms.api.SMSService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class SMSServiceBean implements SMSService {

    @Inject
    Logger logger;

    @EJB
    ConfigManager configManager;

    /**
     * SMS gateway config
     */
    private SMSConfig config;

    @PostConstruct
    void init() {
	config = configManager.getSMSConfig();
    }

    @Asynchronous
    @Override
    public void sendMessage(Client client, SMSType type, String mobile, String... contents) {

	//微米发送短信只需要发送数字验证码，平台自动绑定模版
	if ("weimi".equalsIgnoreCase(config.getPlatform())) {
	    sendSMS(mobile, type, client, contents);
	} else if ("zucp".equalsIgnoreCase(config.getPlatform())) {
	    ResourceBundle resourceBundle = ResourceBundle.getBundle("com.creditcloud.sms.messages", client.getLocale());
	    String contentTemplate = resourceBundle.getString(type.getKey());
	    switch (type) {
		case CONFIRM_CREDITMARKET_REGISTER:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CONFIRM_CREDITMARKET_CHANGE_LOGIN_PASSWORD:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CREDITMARKET_RESET_PASSWORD:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CONFIRM_CREDITMARKET_AUTHENTICATE_MOBILE:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CREDITMANAGER_MESSAGE:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CREDITMANAGER_RESET_PASSWORD:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case NOTIFICATION_CREDITMANAGER_EMPLOYEE_CREATED:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2]));
		    break;
		case NOTIFICATION_CREDITMARKET_USER_CREATED:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2]));
		    break;
		case NOTIFICATION_LOANREQUEST_STATUS:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2]));
		    break;
		case NOTIFICATION_LOAN_STATUS:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2]));
		    break;
		case NOTIFICATION_LOAN_REPAY:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2], client.getShortName(), contents[3]));
		    break;
		case NOTIFICATION_LOAN_CLEARED:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, contents[0], contents[1]));
		    break;
		case NOTIFICATION_WITHDRAW_APPLY:
		    sendSMSViaZucp(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0], contents[1], contents[2]));
		    break;

		default:
		//do nothing
	    }
	} else {
	    ResourceBundle resourceBundle = ResourceBundle.getBundle("com.creditcloud.sms.messages", client.getLocale());
	    String contentTemplate = resourceBundle.getString(type.getKey());
	    //企信港发送短信，需要在发送内容处加上签名，否则无法发送。
	    if ("qixingang".equals(config.getPlatform())) {
		contentTemplate = "【安润金融】" + contentTemplate;
	    }
	    switch (type) {
		case CONFIRM_CREDITMARKET_REGISTER:
		    sendSMS(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CONFIRM_CREDITMARKET_CHANGE_LOGIN_PASSWORD:
		    sendSMS(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CREDITMARKET_RESET_PASSWORD:
		    sendSMS(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CONFIRM_CREDITMARKET_AUTHENTICATE_MOBILE:
		    sendSMS(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CREDITMANAGER_MESSAGE:
		    sendSMS(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case CREDITMANAGER_RESET_PASSWORD:
		    sendSMS(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0]));
		    break;
		case NOTIFICATION_CREDITMANAGER_EMPLOYEE_CREATED:
		    sendSMS(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2]));
		    break;
		case NOTIFICATION_CREDITMARKET_USER_CREATED:
		    sendSMS(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2]));
		    break;
		case NOTIFICATION_LOANREQUEST_STATUS:
		    sendSMS(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2]));
		    break;
		case NOTIFICATION_LOAN_STATUS:
		    sendSMS(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2]));
		    break;
		case NOTIFICATION_LOAN_REPAY:
		    sendSMS(mobile, type, client, String.format(contentTemplate, contents[0], contents[1], contents[2], client.getShortName(), contents[3]));
		    break;
		case NOTIFICATION_LOAN_CLEARED:
		    sendSMS(mobile, type, client, String.format(contentTemplate, contents[0], contents[1]));
		    break;
		case NOTIFICATION_WITHDRAW_APPLY:
		    sendSMS(mobile, type, client, String.format(contentTemplate, client.getShortName(), contents[0], contents[1], contents[2]));
		    break;
		default:
		//do nothing
	    }
	}

    }

    /**
     *
     * @param destination Phone number
     * @param type smsType
     * @param content Actual content sent to user as sms
     */
    private void sendSMS(String destination, SMSType type, Client client, String... content) {
	HttpPost post = null;
	try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
	    logger.debug("Sending messge. [destination={}][content={}]", destination, content);
	    HttpClient httpClient = new DefaultHttpClient();
	    post = new HttpPost(config.getUrl());
	    post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(initnameValuePairs(destination, type, client, content), "UTF-8");
	    post.setEntity(formEntity);
	    HttpResponse resp = httpClient.execute(post);
	    //记录短信平台返回
	    resp.getEntity().writeTo(baos);
	    logger.debug("Message sent. [responseCode={}][response={}][destination={}][content={}]",
		    resp.getStatusLine().getStatusCode(),
		    baos.toString(),
		    destination,
		    content);
	} catch (UnsupportedEncodingException ex) {
	    logger.info("Exception when sending short message", ex);
	} catch (ClientProtocolException ex) {
	    logger.info("Exception when sending short message", ex);
	} catch (IOException ex) {
	    logger.info("Exception when sending short message", ex);
	} finally {
	    if (post != null) {
		post.releaseConnection();
	    }
	}
    }

    private void sendSMSViaZucp(String destination, SMSType type, Client client, String content) {

	HttpPost post = null;
	try (ByteArrayOutputStream inputBaos = new ByteArrayOutputStream(); ByteArrayOutputStream outputBaos = new ByteArrayOutputStream()) {

	    logger.debug("Sending message. [destination={}][content={}]", destination, content);

	    HttpClient httpClient = new DefaultHttpClient();
	    post = new HttpPost(config.getUrl());
	    post.addHeader("Content-Type", "text/xml; charset=UTF-8");
	    inputBaos.write(initZucpValueString(destination, content).getBytes("UTF-8"));
	    post.setEntity(new ByteArrayEntity(inputBaos.toByteArray()));
	    HttpResponse resp = httpClient.execute(post);

	    //记录短信平台返回
	    resp.getEntity().writeTo(outputBaos);
	    Pattern pattern = Pattern.compile("<mtResult>(.*)</mtResult>");
	    Matcher matcher = pattern.matcher(outputBaos.toString());
	    if (matcher.find()) {
		logger.debug("Message sent. [responseCode={}][response={}][destination={}][content={}]",
			resp.getStatusLine().getStatusCode(),
			matcher.group(1),
			destination,
			content);
	    }
	} catch (UnsupportedEncodingException ex) {
	    logger.info("Exception when sending short message", ex);
	} catch (ClientProtocolException ex) {
	    logger.info("Exception when sending short message", ex);
	} catch (IOException ex) {
	    logger.info("Exception when sending short message", ex);
	} finally {
	    if (post != null) {
		post.releaseConnection();
	    }
	}
    }

    private CopyOnWriteArrayList<NameValuePair> initnameValuePairs(String destination, SMSType type, Client client, String... content) {

	String platForm = config.getPlatform();

	//platform 为空默认使用 webchinese
	if (null == platForm || "".equals(platForm)) {
	    return initWebChineseValuePairs(destination, content);
	}
	//根据配置文件中的内容调用不同的接口
	switch (platForm) {
	    case "monternet":
		return initMonternetValuePairs(destination, content);
	    case "webchinese":
		return initWebChineseValuePairs(destination, content);
	    case "weimi":
		return initWeimiValuePairs(destination, type, client, content);
	    case "c123":
		return initC123ValuePairs(destination, content);
	    case "qixingang":
		return initQiXinGangValuePairs(destination, content);
	    //默认使用webchinese
	    default:
		return initWebChineseValuePairs(destination, content);

	}
    }

    // 配置 webChinese 参数
    private CopyOnWriteArrayList<NameValuePair> initWebChineseValuePairs(String destination, String... content) {
	CopyOnWriteArrayList<NameValuePair> nameValuePairs = new CopyOnWriteArrayList<>();
	nameValuePairs.add(new BasicNameValuePair("Uid", config.getUid()));
	nameValuePairs.add(new BasicNameValuePair("Key", config.getKey()));
	nameValuePairs.add(new BasicNameValuePair("smsMob", destination));
	nameValuePairs.add(new BasicNameValuePair("smsText", content[0]));
	return nameValuePairs;
    }

    // 配置 qixingang 参数
    private CopyOnWriteArrayList<NameValuePair> initQiXinGangValuePairs(String destination, String... content) {
	CopyOnWriteArrayList<NameValuePair> nameValuePairs = new CopyOnWriteArrayList<>();
	nameValuePairs.add(new BasicNameValuePair("account", config.getUid()));
	nameValuePairs.add(new BasicNameValuePair("pswd", config.getKey()));
	nameValuePairs.add(new BasicNameValuePair("mobile", destination));
	nameValuePairs.add(new BasicNameValuePair("msg", content[0]));
	nameValuePairs.add(new BasicNameValuePair("needstatus", String.valueOf(false)));
	nameValuePairs.add(new BasicNameValuePair("extno", "771"));
	nameValuePairs.add(new BasicNameValuePair("product", "38909401"));
	return nameValuePairs;
    }

    // 配置 weimi 参数
    private CopyOnWriteArrayList<NameValuePair> initWeimiValuePairs(String destination, SMSType type, Client client, String... content) {
	CID cid = config.getCid();

	CopyOnWriteArrayList<NameValuePair> nameValuePairs = new CopyOnWriteArrayList<>();
	nameValuePairs.add(new BasicNameValuePair("uid", config.getUid()));
	nameValuePairs.add(new BasicNameValuePair("pas", config.getKey()));
	nameValuePairs.add(new BasicNameValuePair("mob", destination));

	switch (type) {
	    case CONFIRM_CREDITMARKET_REGISTER:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getRegister()));
		nameValuePairs.add(new BasicNameValuePair("p1", client.getShortName()));
		nameValuePairs.add(new BasicNameValuePair("p2", content[0]));
		break;
	    case CONFIRM_CREDITMARKET_CHANGE_LOGIN_PASSWORD:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getChangeLoginPassword()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		break;
	    case CREDITMARKET_RESET_PASSWORD:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getResetPassword()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		break;
	    case CONFIRM_CREDITMARKET_AUTHENTICATE_MOBILE:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getAuthenticateMobile()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		break;
	    case CREDITMANAGER_MESSAGE:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getMessage()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		break;

	    case CREDITMANAGER_RESET_PASSWORD:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getManageResetPassword()));
		nameValuePairs.add(new BasicNameValuePair("p1", client.getShortName()));
		nameValuePairs.add(new BasicNameValuePair("p2", content[0]));
		break;

	    case NOTIFICATION_CREDITMANAGER_EMPLOYEE_CREATED:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getEmployeeCreated()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[2]));
		break;

	    case NOTIFICATION_CREDITMARKET_USER_CREATED:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getUserCreated()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[2]));
		break;

	    case NOTIFICATION_LOANREQUEST_STATUS:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getLoanRequestStatus()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[2]));
		break;

	    case NOTIFICATION_LOAN_STATUS:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getLoanStatus()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[2]));
		break;

	    case NOTIFICATION_LOAN_REPAY:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getLoanRepay()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[2]));
		nameValuePairs.add(new BasicNameValuePair("p4", client.getShortName()));
		nameValuePairs.add(new BasicNameValuePair("p5", content[3]));
		break;

	    case NOTIFICATION_LOAN_CLEARED:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getLoanCleared()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		break;

	    case NOTIFICATION_WITHDRAW_APPLY:
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getWithdrawApply()));
		nameValuePairs.add(new BasicNameValuePair("p1", client.getShortName()));
		nameValuePairs.add(new BasicNameValuePair("p2", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p4", content[2]));
		break;
	    case NOTIFICATION_AUTO_REPAYMENT_NOTICE:
		logger.debug("SMSTye NOTIFICATION_AUTO_REPAYMENT_NOTICE");
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getRepaymentNotice()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[2]));
		nameValuePairs.add(new BasicNameValuePair("p4", content[3]));
		break;
	    case NOTIFICATION_AUTO_REPAYMENT_NOTICE_NEW:
		logger.debug("SMSTye NOTIFICATION_AUTO_REPAYMENT_NOTICE_NEW");
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getRepaymentNoticeNew()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[2]));
		nameValuePairs.add(new BasicNameValuePair("p4", content[3]));
		break;
	    case NOTIFICATION_AUTO_LOANS_SUCCESS_NOTICE:
		logger.debug("SMSTye NOTIFICATION_AUTO_LOANS_SUCCESS_NOTICE");
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getAutoLoansSuccessNotice()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[2]));
		break;
	    case NOTIFICATION_AUTO_LOANS_FAILURE_NOTICE:
		logger.debug("SMSTye NOTIFICATION_AUTO_LOANS_FAILURE_NOTICE");
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getAutoLoansFailureNotice()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		break;
	    case NOTIFICATION_LOANS_SETTLE_NOTICE:
		logger.debug("SMSTye NOTIFICATION_LOANS_SETTLE_NOTICE");
		nameValuePairs.add(new BasicNameValuePair("cid", cid.getLoansSettleNotice()));
		nameValuePairs.add(new BasicNameValuePair("p1", content[0]));
		nameValuePairs.add(new BasicNameValuePair("p2", content[1]));
		nameValuePairs.add(new BasicNameValuePair("p3", content[2]));
		break;
	    default:
		logger.debug("SMSTye not set");
		break;
	}
	return nameValuePairs;
    }

    // 配置 monternet 参数
    private CopyOnWriteArrayList<NameValuePair> initMonternetValuePairs(String destination, String... content) {
	CopyOnWriteArrayList<NameValuePair> nameValuePairs = new CopyOnWriteArrayList<>();
	nameValuePairs.add(new BasicNameValuePair("userId", config.getUid()));
	nameValuePairs.add(new BasicNameValuePair("password", config.getKey()));
	nameValuePairs.add(new BasicNameValuePair("pszMobis", destination));
	nameValuePairs.add(new BasicNameValuePair("pszMsg", content[0]));
	nameValuePairs.add(new BasicNameValuePair("iMobiCount", "1"));
	nameValuePairs.add(new BasicNameValuePair("pszSubPort", "*"));
	return nameValuePairs;
    }

    // 配置 c123 参数
    private CopyOnWriteArrayList<NameValuePair> initC123ValuePairs(String destination, String... content) {
	CopyOnWriteArrayList<NameValuePair> nameValuePairs = new CopyOnWriteArrayList<>();
	nameValuePairs.add(new BasicNameValuePair("action", "sendOnce"));
	nameValuePairs.add(new BasicNameValuePair("ac", config.getUid()));
	nameValuePairs.add(new BasicNameValuePair("authkey", config.getKey()));
	nameValuePairs.add(new BasicNameValuePair("m", destination));
	nameValuePairs.add(new BasicNameValuePair("c", content[0]));
	nameValuePairs.add(new BasicNameValuePair("cgid", config.getCgid()));
	return nameValuePairs;
    }

    // 配置zucp.net参数
    private synchronized String initZucpValueString(String destination, String content) {
	StringBuilder soapRequestBuilder = new StringBuilder();
	soapRequestBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><mt xmlns=\"http://tempuri.org/\"><sn>");
	soapRequestBuilder.append(config.getUid());
	soapRequestBuilder.append("</sn><pwd>");
	soapRequestBuilder.append(DigestUtils.md5Hex(config.getUid() + config.getKey()).toUpperCase());
	soapRequestBuilder.append("</pwd><mobile>");
	soapRequestBuilder.append(destination);
	soapRequestBuilder.append("</mobile><content>");
	soapRequestBuilder.append(content);
	soapRequestBuilder.append(config.getSignature());
	soapRequestBuilder.append("</content><ext></ext><stime></stime><rrid></rrid></mt></soap:Body></soap:Envelope>");
	return soapRequestBuilder.toString();
    }
}
