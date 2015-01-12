package com.creditcloud.payment.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.payment.entities.PaymentAccount;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

@LocalBean
@Stateless
public class PaymentAccountDAO
        extends AbstractDAO<PaymentAccount> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "PaymentPU")
    EntityManager em;

    public PaymentAccountDAO() {
        super(PaymentAccount.class);
    }

    protected EntityManager getEntityManager() {
        return this.em;
    }

    public PaymentAccount getByUserId(String clientCode, String userId) {
        PaymentAccount result = null;
        try {
            result = (PaymentAccount) this.em.createNamedQuery("PaymentAccount.getByUserId", PaymentAccount.class).setParameter("clientCode", clientCode).setParameter("userId", userId).getSingleResult();
        } catch (NoResultException ex) {
            this.logger.warn("PaymentAccount not exist for User: {} in Client: {}", userId, clientCode);
        }
        return result;
    }
}
