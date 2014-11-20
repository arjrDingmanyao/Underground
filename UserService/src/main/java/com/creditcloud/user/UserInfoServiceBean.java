/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user;

import com.creditcloud.model.user.info.CareerInfo;
import com.creditcloud.model.ContactInfo;
import com.creditcloud.model.user.info.FinanceInfo;
import com.creditcloud.model.PersonalInfo;
import com.creditcloud.model.SocialInfo;
import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.user.info.UserInfo;
import com.creditcloud.user.api.UserInfoService;
import com.creditcloud.user.entity.dao.CareerInfoRecordDAO;
import com.creditcloud.user.entity.dao.PersonalInfoRecordDAO;
import com.creditcloud.user.entity.dao.ShippingAddressDAO;
import com.creditcloud.user.entity.dao.UserDAO;
import com.creditcloud.user.entity.dao.UserInfoDAO;
import com.creditcloud.user.entity.record.CareerInfoRecord;
import com.creditcloud.user.entity.record.PersonalInfoRecord;
import com.creditcloud.user.local.ApplicationBean;
import com.creditcloud.user.model.ShippingAddress;
import com.creditcloud.user.utils.DTOUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class UserInfoServiceBean implements UserInfoService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    UserInfoDAO infoDAO;

    @EJB
    CareerInfoRecordDAO careerRecordDAO;

    @EJB
    PersonalInfoRecordDAO personalRecordDAO;

    @EJB
    UserDAO userDAO;

    @EJB
    ShippingAddressDAO addressDAO;

    @Override
    public UserInfo getUserInfoByUserId(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        com.creditcloud.user.entity.User user = userDAO.find(userId);
        if (user == null) {
            logger.warn("User not exist when get UserInfo.[userId={}]", userId);
            return null;
        }
        com.creditcloud.user.entity.UserInfo userInfo = infoDAO.find(userId);
        if (userInfo == null) {
            logger.debug("Create UserInfo for User.[userId={}]", userId);
            userInfo = infoDAO.addNew(user);
        }
        return DTOUtils.getUserInfo(userInfo, user);
    }

    @Override
    public com.creditcloud.model.user.info.UserInfo getUserInfoByIdNumber(String clientCode, String idNumber) {
        appBean.checkClientCode(clientCode);
        com.creditcloud.user.entity.User user = userDAO.findByIdNumber(idNumber);
        if (user == null) {
            logger.warn("User not exist when get UserInfo.[idNumber={}]", idNumber);
            return null;
        }
        com.creditcloud.user.entity.UserInfo userInfo = infoDAO.findByIdNumber(idNumber);
        if (userInfo == null) {
            logger.debug("Create UserInfo for User.[userId={}]", user.getId());
            userInfo = infoDAO.addNew(user);
        }
        return DTOUtils.getUserInfo(userInfo, user);
    }

    @Override
    public boolean updatePersonalInfo(String clientCode, String userId, PersonalInfo info) {
        appBean.checkClientCode(clientCode);
        infoDAO.update(userId, info);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public boolean updateCareerInfo(String clientCode, String userId, CareerInfo info) {
        appBean.checkClientCode(clientCode);
        infoDAO.update(userId, info);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public boolean updateFinanceInfo(String clientCode, String userId, FinanceInfo info) {
        appBean.checkClientCode(clientCode);
        infoDAO.update(userId, info);
        return true;
    }

    @Override
    public boolean updateContactInfo(String clientCode, String userId, ContactInfo info) {
        appBean.checkClientCode(clientCode);
        infoDAO.update(userId, info);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public boolean updateSocialInfo(String clientCode, String userId, SocialInfo info) {
        appBean.checkClientCode(clientCode);
        infoDAO.update(userId, info);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public boolean validateLoanRequest(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        return infoDAO.validateLoanRequest(userId);
    }

    @Override
    public boolean updateUserInfo(String clientCode, UserInfo info) {
        appBean.checkClientCode(clientCode);
        infoDAO.edit(DTOUtils.convertUserInfo(info, userDAO.find(info.getUserId())));
        // delete cache
        appBean.deleteCache(info.getUserId(), CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public boolean updatePersonalInfo(String clientCode,
                                      String userId,
                                      PersonalInfo info,
                                      String employeeId,
                                      BigDecimal longitude,
                                      BigDecimal latitude) {
        appBean.checkClientCode(clientCode);
        infoDAO.update(userId, info);
        PersonalInfoRecord record = new PersonalInfoRecord(userDAO.find(userId),
                                                           info == null ? null : (info.getPlace() == null ? null : info.getPlace().getCurrentAddress()),
                                                           employeeId,
                                                           Source.MOBILE,
                                                           longitude,
                                                           latitude);
        personalRecordDAO.create(record);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public boolean updateCareerInfo(String clientCode,
                                    String userId,
                                    CareerInfo info,
                                    String employeeId,
                                    BigDecimal longitude,
                                    BigDecimal latitude) {
        appBean.checkClientCode(clientCode);
        infoDAO.update(userId, info);
        CareerInfoRecord record = new CareerInfoRecord(userDAO.find(userId),
                                                       info == null ? null : (info.getCompany() == null ? null : info.getCompany().getAddress()),
                                                       employeeId,
                                                       Source.MOBILE,
                                                       longitude,
                                                       latitude);
        careerRecordDAO.create(record);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public ShippingAddress addAddress(String clientCode, ShippingAddress address) {
        appBean.checkClientCode(clientCode);
        
        // delete cache
        appBean.deleteCache(address.getUserId(), CacheConstant.KEY_PREFIX_USER_INFO);
        
        return DTOUtils.getShippingAddress(addressDAO.addNew(address));
    }

    @Override
    public ShippingAddress updateAddress(String clientCode, ShippingAddress address) {
        appBean.checkClientCode(clientCode);
        
        // delete cache
        appBean.deleteCache(address.getUserId(), CacheConstant.KEY_PREFIX_USER_INFO);
        
        return DTOUtils.getShippingAddress(addressDAO.update(address));
    }

    @Override
    public ShippingAddress getAddressById(String clientCode, String addressId) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getShippingAddress(addressDAO.find(addressId));
    }

    @Override
    public ShippingAddress getDefaultAddress(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getShippingAddress(addressDAO.getDefault(userId));
    }

    @Override
    public List<ShippingAddress> listAddressByUser(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        List<com.creditcloud.user.entity.ShippingAddress> addresses = addressDAO.listByUser(userId);
        List<ShippingAddress> result = new ArrayList<>(addresses.size());
        for (com.creditcloud.user.entity.ShippingAddress shippingAddress : addresses) {
            result.add(DTOUtils.getShippingAddress(shippingAddress));
        }
        return result;
    }

    @Override
    public boolean markDefaultAddress(String clientCode, String userId, String addressId) {
        appBean.checkClientCode(clientCode);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
        return addressDAO.markDefault(userId, addressId);
    }
}
