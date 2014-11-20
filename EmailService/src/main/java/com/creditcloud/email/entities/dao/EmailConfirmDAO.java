/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email.entities.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.email.entities.EmailConfirm;
import com.creditcloud.email.types.ConfirmResult;
import com.creditcloud.model.client.Client;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class EmailConfirmDAO extends AbstractDAO<EmailConfirm> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "ConfirmPU")
    private EntityManager em;

    public EmailConfirmDAO() {
        super(EmailConfirm.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 从数据库验证记录
     * 
     * @param client
     * @param emailAddress
     * @param confirmCode
     * @param userId 为null时不参与验证
     * @return 
     */
    public ConfirmResult confirmEmail(final Client client,
                                      final String emailAddress,
                                      final String confirmCode,
                                      final String userId) {
        EmailConfirm confirm = findByEmailAndCode(client, emailAddress, confirmCode);

        if (confirm == null) {
            return ConfirmResult.UNSUCCESSFUL;
        }

        if (confirm.isActivated()) {
            if (StringUtils.isBlank(userId)) {
                return ConfirmResult.AUTHENTICATED;
            } else {
                if (userId.equalsIgnoreCase(confirm.getUserId())) {
                    return ConfirmResult.AUTHENTICATED;
                } else {
                    return ConfirmResult.UNSUCCESSFUL;
                }
            }
        }

        if (confirm.getExpiredDate().getTime() < System.currentTimeMillis()) {
            return ConfirmResult.EXPIRED;
        }

        activateEmail(client, emailAddress);

        return ConfirmResult.SUCCESSFUL;
    }

    public EmailConfirm findByEmailAndCode(final Client client, final String emailAddress, final String confirmCode) {
        EmailConfirm confirm = null;
        try {
            confirm = (EmailConfirm) getEntityManager().createNamedQuery("EmailConfirm.findByEmailAndCode")
                    .setParameter("clientCode", client.getCode())
                    .setParameter("emailAddress", emailAddress)
                    .setParameter("confirmCode", confirmCode)
                    .getSingleResult();
        } catch (NoResultException nre) {
            logger.error("No confirm result found.[client={}][email={}][code={}]", client.toString(), emailAddress, confirmCode);
        }
        return confirm;
    }

    public List<EmailConfirm> findByClient(final Client client) {
        return getEntityManager().createNamedQuery("EmailConfirm.findByClient", EmailConfirm.class)
                .setParameter("clientCode", client.getCode())
                .getResultList();
    }

    public List<EmailConfirm> findByEmail(final Client client, final String emailAddress) {
        return getEntityManager().createNamedQuery("EmailConfirm.findByEmail", EmailConfirm.class)
                .setParameter("clientCode", client.getCode())
                .setParameter("emailAddress", emailAddress)
                .getResultList();
    }

    public boolean addNew(final Client client,
                          final String emailAddress,
                          final String confirmCode,
                          final String userId) {
        boolean result = false;
        try {
            validator.validate(client);
            EmailConfirm ec = new EmailConfirm(client.getCode(), emailAddress, confirmCode, userId);
            create(ec);
            result = true;
        } catch (Exception ex) {
            logger.warn("Add new EmailConfirm failed.[emailAddress={}][confirmCode={}]", emailAddress, confirmCode, ex);
        }
        return result;
    }

    public void activateEmail(final Client client, final String emailAddress) {
        long activatedTime = System.currentTimeMillis();
        for (EmailConfirm emailConfirm : findByEmail(client, emailAddress)) {
            emailConfirm.setActivated(true);
            emailConfirm.setActivatedDate(new Date(activatedTime));
            edit(emailConfirm);
        }
    }

    public boolean isActivated(final Client client, final String emailAddress) {
        List<EmailConfirm> confirmList = findByEmail(client, emailAddress);
        for (EmailConfirm confirm : confirmList) {
            if (confirm.isActivated()) {
                return true;
            }
        }

        return false;
    }
    
    public boolean isActivated(final Client client, final String emailAddress, final String userId) {
        List<EmailConfirm> confirmList = findByEmail(client, emailAddress);
        for (EmailConfirm confirm : confirmList) {
            if (confirm.isActivated() && userId.equalsIgnoreCase(confirm.getUserId())) {
                return true;
            }
        }
        return false;
    }
}
