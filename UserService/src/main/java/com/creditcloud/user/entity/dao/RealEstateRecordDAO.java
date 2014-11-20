/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.user.entity.record.RealEstateRecord;
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
public class RealEstateRecordDAO extends AbstractDAO<RealEstateRecord> {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public RealEstateRecordDAO() {
        super(RealEstateRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<RealEstateRecord> listByEstate(String estateId) {
        return getEntityManager()
                .createNamedQuery("RealEstateRecord.listByEstate", RealEstateRecord.class)
                .setParameter("estateId", estateId)
                .getResultList();
    }
}
