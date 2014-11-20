/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.user.entity.record.PersonalInfoRecord;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class PersonalInfoRecordDAO extends AbstractDAO<PersonalInfoRecord> {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public PersonalInfoRecordDAO() {
        super(PersonalInfoRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
