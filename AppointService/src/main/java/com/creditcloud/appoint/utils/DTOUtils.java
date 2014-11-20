/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint.utils;

import com.creditcloud.appoint.entities.AppointAgent;
import com.creditcloud.appoint.entities.AppointRequest;
import com.creditcloud.appoint.entities.AppointUser;
import com.creditcloud.appoint.entities.Appointment;

/**
 *
 * @author rooseek
 */
public class DTOUtils {

    /**
     * handle Appointment
     *
     * @param appointment
     * @return
     */
    public static com.creditcloud.appoint.model.Appointment getAppointment(Appointment appointment) {
        com.creditcloud.appoint.model.Appointment result = null;
        if (appointment != null) {
            result = new com.creditcloud.appoint.model.Appointment(appointment.getId(),
                                                                   appointment.getClientCode(),
                                                                   appointment.getTitle(),
                                                                   appointment.getStatus(),
                                                                   appointment.getQuota(),
                                                                   appointment.getAmount(),
                                                                   appointment.getCount(),
                                                                   com.creditcloud.common.utils.DTOUtils.getInvestRule(appointment.getInvestRule()),
                                                                   appointment.getDescription(),
                                                                   appointment.getTimeOpened(),
                                                                   appointment.getTimeOut(),
                                                                   appointment.getTimeFinished());
        }
        return result;
    }
    
    public static Appointment convertAppointment(com.creditcloud.appoint.model.Appointment appointment) {
        Appointment result = null;
        if (appointment != null) {
            result = new Appointment(appointment.getTitle(),
                                     appointment.getStatus(),
                                     appointment.getQuota(),
                                     appointment.getAmount(),
                                     appointment.getCount(),
                                     com.creditcloud.common.utils.DTOUtils.convertInvestRule(appointment.getInvestRule()),
                                     appointment.getDescription(),
                                     appointment.getTimeOpened(),
                                     appointment.getTimeOut(),
                                     appointment.getTimeFinished());
            result.setId(appointment.getId());
            result.setClientCode(appointment.getClientCode());
        }
        return result;
    }

    /**
     * handle AppointRequest
     *
     * @param request
     * @return
     */
    public static com.creditcloud.appoint.model.AppointRequest getAppointRequest(AppointRequest request) {
        com.creditcloud.appoint.model.AppointRequest result = null;
        if (request != null) {
            result = new com.creditcloud.appoint.model.AppointRequest(request.getId(),
                                                                      request.getUserId(),
                                                                      request.getAppointment().getId(),
                                                                      request.getStatus(),
                                                                      request.getAmount(),
                                                                      request.getBranchId());
            result.setTimeRecorded(request.getTimeRecorded());
        }
        return result;
    }
    
    public static AppointRequest convertAppointRequest(com.creditcloud.appoint.model.AppointRequest request, Appointment appointment) {
        AppointRequest result = null;
        if (request != null) {
            result = new AppointRequest(appointment,
                                        request.getUserId(),
                                        request.getStatus(),
                                        request.getAmount(),
                                        request.getBranchId());
            result.setId(request.getId());
        }
        return result;
    }

    /**
     * handle AppointAgent
     */
    public static com.creditcloud.appoint.model.AppointAgent getAppointAgent(AppointAgent agent) {
        com.creditcloud.appoint.model.AppointAgent result = null;
        if (agent != null) {
            result = new com.creditcloud.appoint.model.AppointAgent(agent.getUserId(),
                                                                    agent.getDescription(),
                                                                    agent.getQuota(),
                                                                    agent.getAmount(),
                                                                    agent.getCount());
        }
        return result;
    }
    
    public static AppointAgent convertAppointAgent(com.creditcloud.appoint.model.AppointAgent agent) {
        AppointAgent result = null;
        if (agent != null) {
            result = new AppointAgent(agent.getUserId(),
                                      agent.getDescription(),
                                      agent.getQuota(),
                                      agent.getAmount(),
                                      agent.getCount());
        }
        return result;
    }

    /**
     * handle AppointUser
     *
     * @param user
     * @return
     */
    public static com.creditcloud.appoint.model.AppointUser getAppointUser(AppointUser user) {
        com.creditcloud.appoint.model.AppointUser result = null;
        if (user != null) {
            result = new com.creditcloud.appoint.model.AppointUser(user.getId(),
                                                                   user.getName(),
                                                                   user.getIdNumber(),
                                                                   user.getBranch());
            result.setBranchId(user.getBranchId());
            result.setTimeRecorded(user.getTimeRecorded());
        }
        return result;
    }
    
    public static AppointUser convertAppointUser(com.creditcloud.appoint.model.AppointUser user) {
        AppointUser result = null;
        if (user != null) {
            result = new AppointUser(user.getName(),
                                     user.getIdNumber(),
                                     user.getBranch());
            result.setBranchId(user.getBranchId());
            result.setId(user.getId());
        }
        return result;
    }
}
