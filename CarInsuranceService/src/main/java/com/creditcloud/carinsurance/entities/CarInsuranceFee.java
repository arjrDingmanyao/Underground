package com.creditcloud.carinsurance.entities;

import com.creditcloud.common.entities.UUIDEntity;
import com.creditcloud.carinsurance.model.enums.CarInsuranceStatus;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ 车险分期 手续费
 * @ author Administrator
 */
@Data
@NoArgsConstructor
@Table(name = "TB_CAR_INSURANCE_FEE")
@Entity
@NamedQueries({
    @NamedQuery(name = "CarInsuranceFee.findByInSuranceNumAndCurrentPeriod", query = "select O from CarInsuranceFee O where O.carInsuranceRepayment.carInsurance.insuranceNum=:insuranceNum and O.carInsuranceRepayment.currentPeriod=:currentPeriod order by O.carInsuranceRepayment.currentPeriod asc")
})
public class CarInsuranceFee extends UUIDEntity {

    /**
     * @ 跟分期费用关联的 还款计划
     * @ 还款计划 没一条记录 对应一条分期手续费
     */
    @OneToOne
    @JoinColumn(name = "CAR_INSURANCE_REPAYMENT_ID", nullable = false)
    private CarInsuranceRepayment carInsuranceRepayment;

    //手续费
    @Min(0)
    @Column(nullable = false,
            precision = 15,
            scale = 2)
    private BigDecimal feeAmount;

    //偿还状态
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarInsuranceStatus status;

    /**
     * 构造方法
     * @param carInsuranceRepayment
     * @param feeAmount
     * @param status 
     */
    public CarInsuranceFee(
            CarInsuranceRepayment carInsuranceRepayment,
            BigDecimal feeAmount,
            CarInsuranceStatus status) {
        this.carInsuranceRepayment = carInsuranceRepayment;
        this.feeAmount = feeAmount;
        this.status = status;
    }
}
