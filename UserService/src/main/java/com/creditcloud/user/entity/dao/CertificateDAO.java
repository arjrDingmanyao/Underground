/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.model.enums.user.credit.CertificateType;
import com.creditcloud.user.entity.Certificate;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class CertificateDAO extends AbstractDAO<Certificate> {

    @EJB
    UserCreditDAO creditDAO;

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public CertificateDAO() {
        super(Certificate.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Certificate findByUserAndType(String userId, CertificateType type) {
        try {
            return getEntityManager()
                    .createNamedQuery("Certificate.findByUserAndType", Certificate.class)
                    .setParameter("userId", userId)
                    .setParameter("type", type)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public List<Certificate> listByUser(String userId) {
        return getEntityManager()
                .createNamedQuery("Certificate.listByUser", Certificate.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
