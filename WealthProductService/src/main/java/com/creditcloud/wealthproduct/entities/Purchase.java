/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.wealthproduct.entities;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.wealthproduct.enums.PurchaseStatus;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_WEALTHPRODUCT_PURCHASE")
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "Purchase.listByProduct",
                query = "select p from Purchase p where  p.product.id = :productId and p.status in :statusList order by p.submitTime DESC"),
    @NamedQuery(name = "Purchase.listByUserAndProduct",
                query = "select p from Purchase p where p.userId = :userId and p.product.id = :productId and p.status in :statusList order by p.submitTime DESC"),
    /**
     * count
     */
    @NamedQuery(name = "Purchase.countByProduct",
                query = "select count(p) from Purchase p where p.product.id = :productId and p.status in :statusList"),
    @NamedQuery(name = "Purchase.countByUserAndProdct",
                query = "select count(p) from Purchase p where p.userId = :userId and p.product.id = :productId and p.status in :statusList"),
    /**
     * sum
     */
    @NamedQuery(name = "Purchase.sumByProduct",
                query = "select sum(p.amount) from Purchase p where p.product.id = :productId and p.status in :statusList")
})
public class Purchase extends UUIDEntity {

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private WealthProduct product;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submitTime;

    public Purchase(WealthProduct product,
                    String userId,
                    int amount,
                    PurchaseStatus status,
                    Date submitTime) {
        this.product = product;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.submitTime = submitTime;
    }
}
