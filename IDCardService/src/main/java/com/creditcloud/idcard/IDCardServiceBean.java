/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.idcard;

import com.creditcloud.central.CheckID;
import com.creditcloud.central.IDVerifier;
import com.creditcloud.idcard.api.IDCardService;
import com.creditcloud.model.constant.IdNumberConstant;
import com.creditcloud.model.constant.ImageConstant;
import com.creditcloud.model.misc.CheckIDResult;
import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.ws.WebServiceRef;
import org.slf4j.Logger;

/**
 *
 * @author sobranie
 */
@Remote
@Stateless
public class IDCardServiceBean implements IDCardService {

    @Inject
    Logger logger;

    @WebServiceRef(wsdlLocation = "http://www.creditcloud.com/IDVerifier/CheckID?wsdl")
    private IDVerifier service;

    private CheckID checkID;

    @PostConstruct
    void init() {
        checkID = service.getCheckIDPort();
    }

    @Override
    public CheckIDResult check(String clientCode, String idNumber, String name) {
        long startTime = System.currentTimeMillis();
        logger.debug("Check called by {} with {} {}", clientCode, idNumber, name);
        CheckIDResult result = new CheckIDResult();
        if (IdNumberConstant.DEFAULT_IDNUMBER.equalsIgnoreCase(idNumber)) {
            logger.debug("Skip check of DEFAULT_IDNUMBER which is definitely not exist.");
            result.setPicture(ImageConstant.DEFAULT_MALE_AVATAR);
            result.setIdNumber(idNumber);
            result.setName(name);
            result.setResultType(CheckIDResult.ResultType.UNMATCH_IDNUMBER);
            result.setErrorMessage("管理员用户ID不检查");
        } else {
            result.fromJsonString(checkID.check(clientCode, idNumber, name));
            logger.debug("Check returned in {}ms with ResultType: {}", System.currentTimeMillis() - startTime, result.getResultType().toString());
        }
        return result;
    }

    @Override
    public boolean quickCheck(String clientCode, String idNumber, String name) {
        long startTime = System.currentTimeMillis();
        logger.debug("QuickCheck called by {} with {} {}", clientCode, idNumber, name);
        boolean result;
        if (IdNumberConstant.DEFAULT_IDNUMBER.equalsIgnoreCase(idNumber)) {
            result = false;
        } else {
            result = checkID.quickCheck(clientCode, idNumber, name);
        }
        logger.debug("QuickCheck returned in {}ms with result: {}", System.currentTimeMillis() - startTime, result);
        return result;
    }
}
