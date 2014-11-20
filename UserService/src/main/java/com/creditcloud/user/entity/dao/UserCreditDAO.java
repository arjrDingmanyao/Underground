/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.ElementCount;
import com.creditcloud.model.enums.user.credit.CreditRank;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.UserCredit;
import com.creditcloud.user.entity.embedded.Assessment;
import com.creditcloud.user.utils.DTOUtils;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class UserCreditDAO extends AbstractDAO<UserCredit> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public UserCreditDAO() {
        super(UserCredit.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 根据User新建UserCredit.
     *
     * @param user
     * @return
     */
    public UserCredit addNew(User user) {
        UserCredit credit = new UserCredit(user,
                                           CreditRank.HR,
                                           new Assessment(0),
                                           0,
                                           0,
                                           "SYSTEM");
        try {
            getValidatorWrapper().tryValidate(credit);
            return create(credit);
        } catch (InvalidException ex) {
            logger.warn("user credit {} is not valid!", credit.toString(), ex);
        } catch (Exception ex) {
            logger.warn("Add new user credit failed!!!\n{}", credit.toString(), ex);
        }
        return null;
    }

    public void update(com.creditcloud.model.user.credit.UserCredit credit) {
        UserCredit result = find(credit.getUserId());
        if (result != null) {
            result.setAssessment(DTOUtils.convertAssessment(credit.getAssessment()));
            result.setCreditAvailable(credit.getCreditAvailable());
            result.setCreditLimit(credit.getCreditLimit());
            result.setCreditRank(credit.getCreditRank());
            result.setLastModifiedBy(credit.getLastModifiedBy());
        }
    }

    /**
     * 按信用评级统计用户
     *
     * @return
     */
    public List<ElementCount<CreditRank>> countEachByRank() {
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("UserCredit.countEachByRank")
                .getResultList();
        List<ElementCount<CreditRank>> result = new ArrayList<>(objects.size());
        for (Object[] object : objects) {
            CreditRank rank = (CreditRank) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(rank, count == null ? 0 : count.intValue()));
        }

        return result;
    }
}
