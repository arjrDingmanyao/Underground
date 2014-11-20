/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint.entities;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.model.constraints.IdNumber;
import com.creditcloud.model.constraints.RealName;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@NoArgsConstructor
//@Entity
@Table(name = "TB_APPOINT_USER", uniqueConstraints = {
    //身份证手机号唯一
    @UniqueConstraint(columnNames = {"IDNUMBER"})})
@NamedQueries({
    /**
     * list query
     */
    @NamedQuery(name = "AppointUser.listAll", query = "select au from AppointUser au ORDER BY au.timeRecorded"),
    @NamedQuery(name = "AppointUser.listByBranch",
                query = "select au from AppointUser au where au.branch in :branchList order by au.timeRecorded"),
    /**
     * count query
     */
    @NamedQuery(name = "AppointUser.countByBranch",
                query = "select count(au) from AppointUser au where au.branch in :branchList"),
    @NamedQuery(name = "AppointUser.countEachByBranch",
                query = "select au.branch as branch, count(au) from AppointUser au group by branch "),
    /**
     * get query
     */
    @NamedQuery(name = "AppointUser.findByIdNumber",
                query = "select au from AppointUser au where au.idNumber = :idNumber")
})
public class AppointUser extends RecordScopeEntity {

    @RealName
    @Column(nullable = false)
    private String name;

    @IdNumber
    @Column(name = "IDNUMBER", nullable = false)
    private String idNumber;

    /**
     * 所属机构中文全称
     */
    @Column(nullable = false)
    private String branch;

    @Column(nullable = true)
    private String branchId;

    public AppointUser(String name, String idNumber, String branch) {
        this.name = name;
        this.idNumber = idNumber;
        this.branch = branch;
    }
}
