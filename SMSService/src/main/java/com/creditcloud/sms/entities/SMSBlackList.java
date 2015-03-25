/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.sms.entities;

import com.creditcloud.common.entities.UUIDEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_SMS_BLACKLIST")
@NamedQueries({
    /**
     * list
     */
//    @NamedQuery(name = "SMSBlackList.listByName",
//                query = "select a from Banner a where a.name like :name order by a.status desc, a.number asc, a.createTime desc"),
    /**
     * count
     */
    @NamedQuery(name = "SMSBlackList.countAll",
                query = "select count(a) from SMSBlackList a"),
    /*
     * get
     */
    @NamedQuery(name = "SMSBlackList.getById",
                query = "select a from SMSBlackList a where a.id = :id"),
    /**
     * delete
     */
    @NamedQuery(name = "SMSBlackList.deleteById",
                query = "delete from SMSBlackList a where a.id = :id")

})
@Data
@NoArgsConstructor
public class SMSBlackList extends UUIDEntity{

    /**
     * BANNER名称
     */
    @Column(nullable = false)
    private String number;

    public SMSBlackList(String number) {
       this.number = number;
    }
}
