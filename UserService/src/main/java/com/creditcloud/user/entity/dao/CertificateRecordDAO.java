/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.user.entity.record.CertificateRecord;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class CertificateRecordDAO extends AbstractDAO<CertificateRecord> {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public CertificateRecordDAO() {
        super(CertificateRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<CertificateRecord> listByCertificate(String certificateId) {
        return getEntityManager()
                .createNamedQuery("CertificateRecord.listByCertificateId", CertificateRecord.class)
                .setParameter("certificateId", certificateId)
                .getResultList();
    }
}
