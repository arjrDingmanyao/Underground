/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.wealthproduct.entities;

import com.creditcloud.common.entities.BaseEntity;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_WEALTHPRODUCT_FEE")
@NamedQueries({})
public class ProductFee extends BaseEntity {

    @Id
    private String productId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "ID")
    private WealthProduct product;

    @Column(nullable = true,
            precision = 15,
            scale = 2)
    private BigDecimal purchaseFee;

    @Column(nullable = true,
            precision = 15,
            scale = 2)
    private BigDecimal manageFee;

    @Column(nullable = true,
            precision = 15,
            scale = 2)
    private BigDecimal redeemFee;

    @Column(nullable = true,
            precision = 15,
            scale = 2)
    private BigDecimal advanceRedeemFee;
}
