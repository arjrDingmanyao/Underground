/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.TimeScopeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Index;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@NamedQueries({
    @NamedQuery(name = "ShippingAddress.listByUser",
                query = "select usa from ShippingAddress usa where usa.userId = :userId order by usa.defaultAddress desc, usa.timeLastUpdated desc"),
    @NamedQuery(name = "ShippingAddress.markDefault",
                query = "update ShippingAddress usa set usa.defaultAddress = (case when (usa.id = :addressId) then TRUE else FALSE end) where usa.userId = :userId"),
    @NamedQuery(name = "ShippingAddress.getDefault",
                query = "select usa from ShippingAddress usa where usa.userId = :userId and usa.defaultAddress = TRUE ORDER BY usa.timeLastUpdated desc")
})
@Table(name = "TB_USER_SHIPPING_ADDRESS")
public class ShippingAddress extends TimeScopeEntity {

    @Index
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String realName;

    /**
     * 13810002000 or 010-61006200
     */
    @Column(nullable = false)
    private String contact;

    private String email;

    /**
     * 北京西城区二环到三环西直门外大街金贸中心A座1627室</p>
     * 全称或json结构化
     *
     */
    private String detail;

    private boolean defaultAddress;

    //公司地址，老家地址
    private String alias;

    public ShippingAddress(String userId,
                           String realName,
                           String contact,
                           String email,
                           String detail,
                           boolean defaultAddress,
                           String alias) {
        this.userId = userId;
        this.realName = realName;
        this.contact = contact;
        this.email = email;
        this.detail = detail;
        this.defaultAddress = defaultAddress;
        this.alias = alias;
    }
}
