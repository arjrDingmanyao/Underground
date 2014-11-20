/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity;

import com.creditcloud.common.entities.TimeScopeEntity;
import com.creditcloud.model.GPSCoordinates;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.enums.user.info.EstateType;
import com.creditcloud.user.entity.record.RealEstateRecord;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
@Entity
//@EntityListeners(RealEstateListener.class)
@Table(name = "TB_REAL_ESTATE")
@NamedQueries({
    @NamedQuery(name = "RealEstate.listByUser",
                query = "select re from RealEstate re where re.user.id = :userId order by re.area")
})
public class RealEstate extends TimeScopeEntity implements GPSCoordinates {

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstateType type;

    /**
     * 地址
     */
    @Column(nullable = false)
    private String location;

    /**
     * 建筑面积
     */
    @Column(nullable = false)
    private double area;

    /**
     * 是否有房贷
     */
    @Column(nullable = false)
    private boolean loan;

    /**
     * 现估值
     */
    @Column(nullable = false)
    private int estimatedValue;

    @Column(nullable = true)
    private String description;

    @Column(precision = 12, scale = 8)
    private BigDecimal longitude;

    @Column(precision = 12, scale = 8)
    private BigDecimal latitude;

    /**
     * 最后更改信息的员工，如果是用户本人更改则为空
     */
    @Column(nullable = false)
    private String lastModifiedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source;

    @OneToMany(mappedBy = "estate",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Collection<RealEstateRecord> changeRecord;

    public RealEstate(User user,
                      EstateType type,
                      String location,
                      double area,
                      boolean loan,
                      int estimatedValue,
                      String description,
                      BigDecimal longitude,
                      BigDecimal latitude,
                      String lastModifiedBy,
                      Source source) {
        this.user = user;
        this.type = type;
        this.location = location;
        this.area = area;
        this.loan = loan;
        this.estimatedValue = estimatedValue;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.lastModifiedBy = lastModifiedBy;
        this.source = source;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(type)
                .append(location)
                .append(area)
                .append(loan)
                .append(estimatedValue)
                .append(description)
                .append(longitude)
                .append(latitude).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final RealEstate other = (RealEstate) obj;

        return new EqualsBuilder()
                .append(type, other.type)
                .append(location, other.location)
                .append(area, other.area)
                .append(loan, other.loan)
                .append(estimatedValue, other.estimatedValue)
                .append(description, other.description)
                .append(longitude, other.longitude)
                .append(latitude, other.latitude).isEquals();
    }
}
