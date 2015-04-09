/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.sms.entities;

import com.creditcloud.common.entities.UUIDEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短信发送记录-日志
 *
 * @author Administrator
 */
@Entity
@Table(name = "TB_SMS_MESSAGE")
@Data
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "SMSMessage.countByDaterangeOrSecrch",
	    query = "select count(O) from SMSMessage O where O.sentTime between :from and :to and (O.receiver like :receiver or O.content like :content ) order by O.sentTime DESC"),
    @NamedQuery(name = "SMSMessage.listByDaterangeOrSecrch",
	    query = "select O from SMSMessage O where O.sentTime between :from and :to and (O.receiver like :receiver or O.content like :content ) order by O.sentTime DESC")
})
public class SMSMessage extends UUIDEntity {

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String receiver;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SENT_TIME", nullable = false)
    private Date sentTime;

    public SMSMessage(String content, String receiver, Date sentTime) {
	this.content = content;
	this.receiver = receiver;
	this.sentTime = sentTime;
    }

}
