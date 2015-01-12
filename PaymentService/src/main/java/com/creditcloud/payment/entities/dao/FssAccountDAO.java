package com.creditcloud.payment.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.payment.entities.FssAccount;
import com.creditcloud.payment.model.FssStats;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.joda.time.LocalDate;
import org.slf4j.Logger;

@LocalBean
@Stateless
public class FssAccountDAO
        extends AbstractDAO<FssAccount> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "PaymentPU")
    EntityManager em;

    public FssAccountDAO() {
        super(FssAccount.class);
    }

    protected EntityManager getEntityManager() {
        return this.em;
    }

    public FssAccount getByUserId(String clientCode, String userId) {
        FssAccount result = null;
        try {
            result = (FssAccount) this.em.createNamedQuery("FssAccount.getByUserId", FssAccount.class).setParameter("clientCode", clientCode).setParameter("userId", userId).getSingleResult();
        } catch (NoResultException ex) {
            this.logger.warn("FssAccount not exist.[clientCode={}][userId={}]", clientCode, userId);
        }
        return result;
    }

    public boolean updateAsset(String clientCode, String userId, BigDecimal totalAsset, BigDecimal totalProfit) {
        int result = 0;
        try {
            result = this.em.createNamedQuery("FssAccount.updateAsset").setParameter("clientCode", clientCode).setParameter("userId", userId).setParameter("totalAsset", totalAsset).setParameter("totalProfit", totalProfit).executeUpdate();

            this.logger.debug("FssAccount updated.[clientCode={}][userId={}][totalAsset={}][totalProfit={}]", new Object[]{clientCode, userId, totalAsset, totalProfit});
        } catch (Exception ex) {
            this.logger.error("Error happened when update FssAccount.[clientCode={}][userId={}][totalAsset={}][totalProfit={}]", new Object[]{clientCode, userId, totalAsset, totalProfit, ex});
        }
        return result > 0;
    }

    public FssAccount newFssAccount(String clientCode, String accountId, String userId, BigDecimal totalAsset, BigDecimal totalProfit) {
        FssAccount fa = new FssAccount();
        fa.setAccountId(accountId);
        fa.setClientCode(clientCode);
        fa.setUserId(userId);
        fa.setBalance(totalAsset);
        fa.setTotalProfit(totalProfit);
        fa.setTotalDeposit(BigDecimal.ZERO);
        fa.setTotalWithdraw(BigDecimal.ZERO);
        fa.setTimeCreated(new Date());
        fa.setTimeUpdated(new Date());
        return (FssAccount) this.em.merge(fa);
    }

    public List<String> outOfDateFssAccounts(String clientCode) {
        List<String> result = new ArrayList();
        try {
            result = this.em.createNamedQuery("FssAccount.outOfDateFssAccounts", String.class).setParameter("clientCode", clientCode).setParameter("timeUpdate", LocalDate.now().toDateTimeAtStartOfDay().toDate()).getResultList();
        } catch (Exception ex) {
            this.logger.error("Error happend when listing the out-of-date FssAccount.", ex);
        }
        return result;
    }

    public FssStats fssStats(String clientCode) {
        FssStats result = new FssStats();
        try {
            Object[] results = (Object[]) this.em.createNamedQuery("FssAccount.fssStats", Object.class).setParameter("clientCode", clientCode).getSingleResult();

            result.setTotalAsset((BigDecimal) results[0]);
            result.setTotalProfit((BigDecimal) results[1]);
            result.setTotalDeposit((BigDecimal) results[2]);
            result.setTotalWithdraw((BigDecimal) results[3]);
        } catch (Exception ex) {
            this.logger.error("Error happend when stats all FssAccounts.", ex);
        }
        return result;
    }
}
