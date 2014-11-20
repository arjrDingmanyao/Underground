/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author rooseek
 */
public class EmailUtils {

    /**
     * get confirm code for user email on registration
     * 
     * @param clientCode: clientCode
     * @param emailAddress: user email address
     * @return 
     */
    public static String getConfirmCode(String clientCode, String emailAddress) {
        String str = clientCode.concat(emailAddress).concat("" + System.currentTimeMillis());
        return DigestUtils.md5Hex(str);
    }
}
