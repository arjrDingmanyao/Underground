package com.creditcloud.payment.entities.dao;

import com.creditcloud.common.entities.dao.AbstractReconciliationDAO;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.payment.entities.reconciliation.CreditAssignReconciliation;
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
public class AssignReconciliationDAO
        extends AbstractReconciliationDAO<CreditAssignReconciliation> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "PaymentPU")
    EntityManager em;

    public AssignReconciliationDAO() {
        super(CreditAssignReconciliation.class);
    }

    protected EntityManager getEntityManager() {
        return this.em;
    }

    public void addAll(List<CreditAssignReconciliation> reconciliationList) {
        for (CreditAssignReconciliation reconciliation : reconciliationList) {
            add(reconciliation);
        }
    }

    public void add(CreditAssignReconciliation reconciliation) {
        CreditAssignReconciliation result = getByOrderId(reconciliation.getOrdId());
        if (result == null) {
            create(reconciliation);
        }
    }

    public CreditAssignReconciliation getByOrderId(String orderId) {
        try {
            return (CreditAssignReconciliation) getEntityManager().createNamedQuery("CreditAssignReconciliation.getByOrderId", CreditAssignReconciliation.class).setParameter("OrdId", orderId).getSingleResult();
        } catch (NoResultException ex) {
            this.logger.debug("credit assign reconciliation not found.[orderId={}]", orderId);
        }
        return null;
    }

    public int countByOrderDate(LocalDate from, LocalDate to) {
        Long result = (Long) getEntityManager().createNamedQuery("CreditAssignReconciliation.countByOrdDate", Long.class).setParameter("from", from).setParameter("to", to).getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    public PagedResult<CreditAssignReconciliation> listByOrderDate(LocalDate from, LocalDate to, PageInfo pageInfo) {
        List<CreditAssignReconciliation> result = getEntityManager().createNamedQuery("CreditAssignReconciliation.listByOrdDate", CreditAssignReconciliation.class).setParameter("from", from).setParameter("to", to).setFirstResult(pageInfo.getOffset()).setMaxResults(pageInfo.getSize()).getResultList();

        int totalSize = countByOrderDate(from, to);
        return new PagedResult(result, totalSize);
    }

    public void update(CreditAssignReconciliation reconciliation) {
        edit(reconciliation);
    }
}
