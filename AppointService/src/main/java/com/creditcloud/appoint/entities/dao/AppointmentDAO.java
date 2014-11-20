/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.appoint.entities.Appointment;
import com.creditcloud.appoint.enums.AppointmentStatus;
import java.util.Arrays;
import java.util.Collections;
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
public class AppointmentDAO extends AbstractDAO<Appointment> {

    @PersistenceContext(unitName = "AppointPU")
    private EntityManager em;

    public AppointmentDAO() {
        super(Appointment.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Appointment> listByStatus(AppointmentStatus... status) {
        if (status == null || status.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("Appointment.listByStatus", Appointment.class)
                .setParameter("statusList", Arrays.asList(status))
                .getResultList();
    }
}
