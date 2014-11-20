/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email.template;

import com.creditcloud.email.types.ActivationEmail;
import com.creditcloud.email.types.AuthenticationEmail;
import com.creditcloud.email.types.Email;
import com.creditcloud.email.types.RegistrationEmail;
import com.creditcloud.model.client.Client;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rooseek
 */
public final class EmailTemplate {

    private Configuration configuration;

    private String templateDirectory;

    private org.slf4j.Logger logger;

    private static final String HTTP = "http://";

    private static final String HTTPS = "https://";

    private static final String SEPERATOR = "/";

    public EmailTemplate() throws IOException {
        configuration = new Configuration();
        templateDirectory = this.getClass().getResource("").getPath();

        configuration.setDirectoryForTemplateLoading(new File(templateDirectory));
        configuration.setObjectWrapper(new DefaultObjectWrapper());

        logger = org.slf4j.LoggerFactory.getLogger(EmailTemplate.class);
    }

    public String getContent(Client client, Email mail) {
        Writer out = null;

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("com.creditcloud.email.template.names");
            String ftlName = bundle.getString(mail.getType().getKey());
            Template template = configuration.getTemplate(ftlName);
            Map<String, String> root = getFreeMakerMap(client, mail);


            out = new StringWriter();
            template.process(root, out);

            out.flush();
            return out.toString();
        } catch (IOException | TemplateException ex) {
            logger.error("Fail to get content from freemaker. [client={}][mail={}]", client, mail, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    logger.error("Fail to close StringWriter.", ex);
                }
            }
        }

        return StringUtils.EMPTY;
    }

    private Map<String, String> getFreeMakerMap(Client client, Email mail) {
        switch (mail.getType()) {
            case CONFIRM_CREDITMARKET_REGISTRATION:
                return getRegistrationMap(client, (RegistrationEmail) mail);
            case CONFIRM_CREDITMARKET_ACTIVATION:
                return getActivationMap(client, (ActivationEmail) mail);
            case CONFIRM_CREDITMARKET_AUTHENTICATION:
                return getAuthenticationMap(client, (AuthenticationEmail) mail);
            default:
                logger.error("Invalid EmailType. [client={}][mail={}].", client, mail);
        }

        return null;
    }

    private Map<String, String> getRegistrationMap(Client client, RegistrationEmail mail) {
        String scheme = client.isSecure() ? HTTPS : HTTP;
        
        Map<String, String> root = new HashMap<>();

        root.put("username", mail.getPersonal());

        String url = client.getUrl();
        url = urlTrim(url);

        String homeurl = scheme.concat(url);
        root.put("homeurl", homeurl);

        String investurl = url.concat("/invest/list");
        investurl = scheme.concat(investurl);
        root.put("investurl", investurl);

        String accounturl = url.concat("/account");
        accounturl = scheme.concat(accounturl);
        root.put("accounturl", accounturl);

        url = urlSuffix(url);
        root.put("helpemail", "info@" + url);
        root.put("supportemail", "support@" + url);

        root.put("company", client.getName());
        root.put("shortName", client.getShortName());

        return root;
    }

    private Map<String, String> getActivationMap(Client client, ActivationEmail mail) {
        String scheme = client.isSecure() ? HTTPS : HTTP;
        
        Map<String, String> root = new HashMap<>();

        root.put("username", mail.getPersonal());

        String url = client.getUrl();
        url = urlTrim(url);

        String homeurl = scheme.concat(url);
        root.put("homeurl", homeurl);

        root.put("company", client.getName());
        root.put("shortName", client.getShortName());

        String activeurl = scheme.concat(url)
                .concat("/register/activateEmail?code=")
                .concat(mail.getConfirmCode())
                .concat("&email=")
                .concat(mail.getAddress());
        root.put("activationurl", activeurl);

        url = urlSuffix(url);
        root.put("helpmail", "help@" + url);

        return root;
    }

    private Map<String, String> getAuthenticationMap(Client client, AuthenticationEmail mail) {
        String scheme = client.isSecure() ? HTTPS : HTTP;
        
        Map<String, String> root = new HashMap<>();

        root.put("username", mail.getPersonal());

        String url = client.getUrl();
        url = urlTrim(url);

        String homeurl = scheme.concat(url);
        root.put("homeurl", homeurl);

        root.put("company", client.getName());
        root.put("shortName", client.getShortName());

        String activeurl = scheme.concat(url)
                .concat("/account/authenticateEmail?code=")
                .concat(mail.getConfirmCode())
                .concat("&email=")
                .concat(mail.getAddress());
        root.put("activationurl", activeurl);

        url = urlSuffix(url);
        root.put("helpmail", "help@" + url);

        return root;
    }

    //TODO considering http:// and https://
    /**
     * remove the last “/" from url
     *
     * @param url
     * @return
     */
    private String urlTrim(String url) {
        return StringUtils.endsWith(url, SEPERATOR) ? StringUtils.substring(url, 0, StringUtils.lastIndexOf(url, SEPERATOR)) : url;
    }

    /**
     * remove server name "demo.", "www." or "admin." from url
     *
     * @param url
     * @return
     */
    private String urlSuffix(String url) {
        String temp = urlTrim(url);
        return StringUtils.substring(temp, temp.indexOf(".") + 1, StringUtils.length(url));
    }
}
