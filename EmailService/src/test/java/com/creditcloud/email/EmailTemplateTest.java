/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email;

import com.creditcloud.email.types.Email;
import com.creditcloud.email.types.RegistrationEmail;
import com.creditcloud.email.template.EmailTemplate;
import com.creditcloud.email.types.ActivationEmail;
import com.creditcloud.model.client.Client;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author rooseek
 */
public class EmailTemplateTest {

    @Ignore
    @Test
    public void testRegistration() throws IOException {
        Email info = new RegistrationEmail("noreplay@creditcloud.com", "noreplay");
        EmailTemplate template = new EmailTemplate();
        Client client = new Client();
        client.setUrl("www.creditcloud.com");
        client.setName("云中信");
        System.out.println(template.getContent(client, info));
    }

    @Ignore
    @Test
    public void testActivation() throws IOException {
        Email info = new ActivationEmail("noreplay@creditcloud.com", "noreplay", "dafaf345235235fsfsfsdfs3");
        EmailTemplate template = new EmailTemplate();
        Client client = new Client();
        client.setUrl("www.creditcloud.com");
        System.out.println(template.getContent(client, info));
    }
}
