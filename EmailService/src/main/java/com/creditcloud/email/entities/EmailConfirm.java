/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.email.entities;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.email.EmailConstants;
import com.creditcloud.model.constraints.ClientCode;
import com.creditcloud.model.constraints.EmailAddress;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.eclipse.persistence.annotations.Index;

/**
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_EMAIL_CONFIRM")
@NamedQueries({
    @NamedQuery(name = "EmailConfirm.findByEmailAndCode",
                query = "SELECT ec FROM EmailConfirm ec WHERE ec.clientCode = :clientCode AND ec.emailAddress = :emailAddress AND ec.confirmCode = :confirmCode"),
    @NamedQuery(name = "EmailConfirm.findByClient",
                query = "SELECT ec FROM EmailConfirm ec WHERE ec.clientCode = :clientCode"),
    @NamedQuery(name = "EmailConfirm.findByEmail",
                query = "SELECT ec FROM EmailConfirm ec WHERE ec.clientCode = :clientCode AND ec.emailAddress = :emailAddress"),
})
public class EmailConfirm extends UUIDEntity {

    /**
     * client for this user
     */
    @ClientCode
    private String clientCode;

    /**
     * The email address
     */
    @Index
    @EmailAddress
    private String emailAddress;

    /**
     * The confirm code for this email address
     */
    @Column(unique = true, nullable = false)
    private String confirmCode;

    /**
     * whether this email is activated or not
     */
    @Column(nullable = false)
    private boolean activated;

    /**
     * created timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdDate;

    /**
     * timestamp when this email is activated
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date activatedDate;

    /**
     * timestamp when this confirm will expire
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date expiredDate;
    
    @Index
    private String userId;

    public EmailConfirm() {
    }

    public EmailConfirm(String clientCode, String emailAddress, String confirmCode, String userId) {
        this(clientCode, emailAddress, confirmCode, false, new Date(System.currentTimeMillis()), null, new Date(System.currentTimeMillis() + EmailConstants.CONFIRM_INTERVAL), userId);
    }

    public EmailConfirm(String clientCode,
                        String emailAddress,
                        String confirmCode,
                        boolean activated,
                        Date createdDate,
                        Date activatedDate,
                        Date expiredDate,
                        String userId) {
        this.clientCode = clientCode;
        this.emailAddress = emailAddress;
        this.confirmCode = confirmCode;
        this.activated = activated;
        this.createdDate = createdDate;
        this.activatedDate = activatedDate;
        this.expiredDate = expiredDate;
        this.userId = userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getConfirmCode() {
        return confirmCode;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setConfirmCode(String confirmCode) {
        this.confirmCode = confirmCode;
    }

    public boolean isActivated() {
        return activated;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getActivatedDate() {
        return activatedDate;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setActivatedDate(Date activatedDate) {
        this.activatedDate = activatedDate;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
