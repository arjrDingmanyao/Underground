/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email;

import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author rooseek
 */
public class ConfirmCodeTest {
    
    @Ignore
    @Test
    public void testMD5() throws NoSuchAlgorithmException {
        System.out.println(MD5("noreplay@creditcloud.com"));
    }

    private String MD5(String emailAddress) throws NoSuchAlgorithmException {
        return DigestUtils.md5Hex(emailAddress);
    }
    
}
