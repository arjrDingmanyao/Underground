/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email.sendcloud;

import com.sohu.sendcloud.Message;
import com.sohu.sendcloud.SendCloud;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author rooseek
 */
public class SendCloudTest {

    @Ignore
    @Test
    public void testSimpleSendMail() throws Exception {
        Message message = new Message("min.chen@creditcloud.com", "云中信");
        // 正文， 使用html形式，或者纯文本形式
        message.setBody("欢迎使用SendCloud"); // html

        // 添加to, cc, bcc replyto
        message.setSubject("SendCloud测试邮件");
        List<String> addressList = new ArrayList<>();
        addressList.add("hao.chen@creditcloud.com");
        message.addRecipients(addressList);

        // 添加附件
        //message.addAttachment("txt", "/Users/rooseek/Downloads/邦信员工信息表.csv");

        // 组装消息发送邮件
        // 不同于登录SendCloud站点的帐号，您需要登录后台创建发信域名，获得对应发信域名下的帐号和密码才可以进行邮件的发送。
        SendCloud sendCloud = new SendCloud("postmaster@creditcloud.sendcloud.org", "FzjnJ83o");
        sendCloud.setMessage(message);
        //sendCloud.setDebug(true); //设置调试, 可以看到java mail的调试信息
        sendCloud.send();

        // 获取emailId列表
        System.out.println(sendCloud.getEmailIdList());
    }
}
