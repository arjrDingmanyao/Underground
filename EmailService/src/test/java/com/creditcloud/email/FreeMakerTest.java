/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author rooseek
 */
public class FreeMakerTest {

    @Ignore
    @Test
    public void test() throws IOException, TemplateException {
        Configuration configuration = new Configuration();
        configuration.setDirectoryForTemplateLoading(new File("/Users/rooseek/Work/GitHub/Underground/EmailService/src/main/resources/com/creditcloud/email/test/"));
        configuration.setObjectWrapper(new DefaultObjectWrapper());


        System.out.println(FreeMakerTest.class.getResource("").getPath());

        Template template = configuration.getTemplate("demo.ftl");
        template.setEncoding("UTF-8");

        Map root = new HashMap();
        root.put("username", "中文");
        try (Writer out = new OutputStreamWriter(System.out)) {
            template.process(root, out);
            out.flush();
        }
    }
}
