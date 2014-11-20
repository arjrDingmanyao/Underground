/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.local;

import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.user.entity.SocialUser;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.dao.SocialUserDAO;
import com.creditcloud.user.entity.dao.UserAuthenticateDAO;
import com.creditcloud.user.entity.dao.UserCreditDAO;
import com.creditcloud.user.entity.dao.UserDAO;
import com.creditcloud.user.entity.dao.UserInfoDAO;
import com.creditcloud.user.entity.record.UserRecord;
import com.creditcloud.user.entity.record.dao.UserRecordDAO;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * cooperation work between user entities
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class UserLocalBean {

    @Inject
    Logger logger;

    @EJB
    UserDAO userDAO;

    @EJB
    UserInfoDAO infoDAO;

    @EJB
    UserCreditDAO creditDAO;

    @EJB
    UserAuthenticateDAO authenticateDAO;

    @EJB
    UserRecordDAO recordDAO;

    @EJB
    SocialUserDAO socialUserDAO;

    @EJB
    ApplicationBean appBean;
    
    /**
     * create a user as well as related entities
     *
     * @param user
     * @return
     */
    public User createUser(User user) {
        if(user.getIdNumber() != null){
            user.setIdNumber(user.getIdNumber().toUpperCase());
        }
        User result = userDAO.addNew(user);
        if (result != null) {
            logger.debug("User added as:\n{}", result);
            infoDAO.addNew(result);
            creditDAO.addNew(result);
            authenticateDAO.addNew(result);

            //add user create record
            UserRecord record = new UserRecord(result,
                                               result.getName(),
                                               result.getLoginName(),
                                               result.getIdNumber(),
                                               result.getMobile(),
                                               result.getEmail(),
                                               result.getLastModifiedBy());
            recordDAO.create(record);
            
            // delete cache
            appBean.deleteCache(user.getId(), CacheConstant.KEY_PREFIX_USER_ACCOUNT);
        
        } else {
            logger.warn("Failed to create user!\n{}", user);
        }
        return result;
    }

    /**
     * 生成SocialUser记录
     *
     * @param socialUser
     * @return
     */
    public SocialUser createSocialUser(SocialUser socialUser) {
        return socialUserDAO.create(socialUser);
    }
}
