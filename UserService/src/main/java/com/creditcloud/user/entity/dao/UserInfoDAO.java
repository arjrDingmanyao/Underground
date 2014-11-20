/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.constraints.validator.ChineseIdNumber;
import com.creditcloud.model.validation.group.LoanRequestCheck;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.UserInfo;
import com.creditcloud.user.entity.embedded.CareerInfo;
import com.creditcloud.common.entities.embedded.info.ContactInfo;
import com.creditcloud.user.entity.embedded.FinanceInfo;
import com.creditcloud.common.entities.embedded.info.PersonalInfo;
import com.creditcloud.common.entities.embedded.info.SocialInfo;
import com.creditcloud.user.local.ApplicationBean;
import com.creditcloud.user.utils.DTOUtils;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class UserInfoDAO extends AbstractDAO<UserInfo> {

    @EJB
    ApplicationBean appBean;

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public UserInfoDAO() {
        super(UserInfo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 根据User创建相应的UserInfo条目并返回
     *
     * @param user
     * @return
     */
    public UserInfo addNew(User user) {
        PersonalInfo personal = new PersonalInfo(user.getIdNumber() == null ? true : ChineseIdNumber.isMale(user.getIdNumber()),
                                                 null,
                                                 user.getIdNumber() == null ? null : ChineseIdNumber.getDateOfBirth(user.getIdNumber()),
                                                 null,
                                                 false,
                                                 null,
                                                 null,
                                                 null);
        UserInfo info = new UserInfo(user,
                                     personal,
                                     null,
                                     null,
                                     null,
                                     null);

        try {
            getValidatorWrapper().tryValidate(info);
            return create(info);
        } catch (InvalidException ex) {
            logger.warn("user information {} is not valid!", info.toString(), ex);
        } catch (Exception ex) {
            logger.warn("Add new user information failed!!!\n{}", info.toString(), ex);
        }
        return null;
    }

    public UserInfo findByIdNumber(String IdNumber) {
        try {
            return getEntityManager()
                    .createNamedQuery("UserInfo.findInfoByIdNumber", UserInfo.class)
                    .setParameter("idNumber", IdNumber)
                    .getSingleResult();
        } catch (NoResultException nre) {
            logger.debug("no user info found for IdNumber:{}", IdNumber);
        }

        return null;
    }

    /**
     * validate an user info for LoanRequestCheck
     *
     * @param userId
     * @return
     */
    public boolean validateLoanRequest(String userId) {
        UserInfo info = find(userId);
        try {
            getValidatorWrapper().tryValidate(info, LoanRequestCheck.class);
            return true;
        } catch (InvalidException iex) {
            logger.warn("user info is not valid for LoanCheck.[userInfo={}]", info.toString(), iex);
        }
        return false;
    }

    public void update(UserInfo info) {
        UserInfo oldUser = find(info.getUserId());
        if (oldUser != null) {
            edit(info);
        } else {
            //do nothing
            //persist is preferable than merge in EJB, call addUser if you want to insert
            logger.warn("Fail to update user info, user not exist.[user={}]", info);

        }
    }

    /**
     * update PersonalInfo of UserInfo
     *
     * @param userId
     * @param personal
     */
    public void update(String userId, com.creditcloud.model.PersonalInfo personal) {
        UserInfo info = find(userId);
        if (info != null) {
            PersonalInfo result = com.creditcloud.common.utils.DTOUtils.convertPersonalInfo(personal);
            info.setPersonal(result);
            edit(info);
        } else {
            logger.warn("update failed, user info not exist.[clientCode={}][userId={}]", appBean.getClientCode(), userId);
        }
    }

    /**
     * update FinanceInfo of UserInfo
     *
     * @param userId
     * @param finance
     */
    public void update(String userId, com.creditcloud.model.user.info.FinanceInfo finance) {
        UserInfo info = find(userId);
        if (info != null) {
            FinanceInfo result = DTOUtils.convertFinanceInfo(finance);
            info.setFinance(result);
            edit(info);
        } else {
            logger.warn("update failed, user info not exist.[clientCode={}][userId={}]", appBean.getClientCode(), userId);
        }
    }

    /**
     * update CareerInfo of UserInfo
     *
     * @param userId
     * @param career
     */
    public void update(String userId, com.creditcloud.model.user.info.CareerInfo career) {
        UserInfo info = find(userId);
        if (info != null) {
            CareerInfo result = DTOUtils.convertCareerInfo(career);
            info.setCareer(result);
            edit(info);
        } else {
            logger.warn("update failed, user info not exist.[clientCode={}][userId={}]", appBean.getClientCode(), userId);
        }
    }

    /**
     * update ContactInfo of UserInfo
     *
     * @param userId
     * @param contact
     */
    public void update(String userId, com.creditcloud.model.ContactInfo contact) {
        UserInfo info = find(userId);
        if (info != null) {
            ContactInfo result = com.creditcloud.common.utils.DTOUtils.convertContactInfo(contact);
            info.setContact(result);
            edit(info);
        } else {
            logger.warn("update failed, user info not exist.[clientCode={}][userId={}]", appBean.getClientCode(), userId);
        }
    }

    /**
     * update SocialInfo of UserInfo
     *
     * @param userId
     * @param social
     */
    public void update(String userId, com.creditcloud.model.SocialInfo social) {
        UserInfo info = find(userId);
        if (info != null) {
            SocialInfo result = com.creditcloud.common.utils.DTOUtils.convertSocialInfo(social);
            info.setSocial(result);
            edit(info);
        } else {
            logger.warn("update failed, user info not exist.[clientCode={}][userId={}]", appBean.getClientCode(), userId);
        }
    }
}
