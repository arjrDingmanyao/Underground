/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.message;

import com.creditcloud.config.LoanRepayPush;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.message.api.PushMessageService;
import com.creditcloud.message.local.ApplicationBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;

/**
 * 消息推送
 *
 * @author Administrator
 */
@Stateless
@Remote
public class PushMessageServiceBean implements PushMessageService {

    public static final int BUFFER_SIZE = 1024 * 8;
    public static final String CONTENT_TYPE_XML = "text/xml;charset=GBK";
    public static final String CONTENT_TYPE_HTML = "text/html;charset=GBK";
    public static final String CONTENT_TYPE_PLAIN = "text/plain;charset=GBK";
    public static final String CONTENT_TYPE_JSON = "text/x-json;charset=GBK";
    public static final String CONTENT_TYPE_XML_UTF_8 = "text/xml;charset=UTF-8";
    public static final String CONTENT_TYPE_HTML_UTF_8 = "text/html;charset=UTF-8";
    public static final String CONTENT_TYPE_PLAIN_UTF_8 = "text/plain;charset=UTF-8";
    public static final String CONTENT_TYPE_JSON_UTF_8 = "text/x-json;charset=UTF-8";
    public static final String ENCODING_GBK = "GBK";
    public static final String ENCODING_UTF_8 = "UTF-8";

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    ConfigManager configManager;

    /**
     * 还款到账消息推送
     *
     * @param title
     * @param content
     * @param userIds
     */
    public void repayPush(String title, String content, List<String> userIds) {
	logger.debug("PushMessageServiceBean repayPush");
	LoanRepayPush push = configManager.getClientConfig().getLoanRepayPush();
	String url = push.getUrl();
	String result = "";
	JSONObject jsonObject = new JSONObject();
	jsonObject.put("pushType", "2");
	jsonObject.put("pushContent", "还款到账!");
	JSONArray pushGroupArray = new JSONArray();
	//循环用户的ID
	for (String userId : userIds) {
	    JSONObject objectPush = new JSONObject();
	    objectPush.put("userIdentifier", userId);
	    pushGroupArray.add(objectPush);
	}
	logger.debug("PushMessageServiceBean repayPush User count :{}", userIds.size());
	jsonObject.put("pushGroup", pushGroupArray);
	try {
	    result = send(url, jsonObject.toString(), CONTENT_TYPE_JSON_UTF_8);
	} catch (Exception e) {
	    logger.error("还款到账消息推送：\n{}", e);
	}
	logger.debug("还款到账消息推送：{}", result);
    }

    /**
     * 消息推送 发送
     */
    public static String send(String strUrl, String content,
	    String contentType) throws Exception {
	String returnStr = "";
	InputStreamReader in = null;
	BufferedReader bin = null;
	URL url = new URL(strUrl);
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setDoInput(true);
	connection.setDoOutput(true);
	connection.setUseCaches(false);
	if (contentType != null) {
	    // 设置content-type头部指示指定URL已编码数据的窗体MIME类型
	    connection.setRequestProperty("content-type", contentType);
	} else {
	    //给定默认值否则服务端获取不到内容
	    connection.setRequestProperty("content-type", CONTENT_TYPE_HTML_UTF_8);
	}
	connection.setConnectTimeout(10000);
	connection.setReadTimeout(60000);
	connection.setRequestMethod("POST");
	// conn.setRequestProperty("accept", "*/*");
	connection.connect();
	if (content != null) {
	    // 将数据流发给客户端，默认GBK编码
	    byte[] data = content.getBytes(ENCODING_UTF_8);
	    GZIPOutputStream out = new GZIPOutputStream(connection.getOutputStream());
	    // 发送数据
	    out.write(data, 0, data.length);
	    out.flush();
	    out.finish();
	    out.close();
	} else {
	    connection.getOutputStream().close();
	}
	// 用GBK编码接收流
	GZIPInputStream gin = new GZIPInputStream(connection.getInputStream());
	in = new InputStreamReader(gin, ENCODING_UTF_8);
	bin = new BufferedReader(in);
	char[] b = new char[BUFFER_SIZE];
	int read = 0;
	StringBuffer s = new StringBuffer();
	while ((read = bin.read(b)) != -1) {
	    s.append(b, 0, read);
	}
	returnStr = s.toString();
	bin.close();
	in.close();
	gin.close();
	connection.disconnect();
	return returnStr;
    }

}
