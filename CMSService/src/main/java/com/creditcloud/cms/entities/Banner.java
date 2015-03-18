/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.cms.entities;

import com.creditcloud.common.entities.RecordScopeEntity;
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
 *
 * @author rooseek
 */
@Entity
@Table(name = "TB_CMS_BANNER")
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "Banner.listByName",
                query = "select a from Banner a where a.name like :name order by a.status desc, a.number asc, a.createTime desc"),
    @NamedQuery(name = "Banner.listAll",
                query = "select a from Banner a order by a.status desc, a.number asc, a.createTime desc"),
     @NamedQuery(name = "Banner.listActive",
                query = "select a from Banner a where a.status = :status order by a.number asc, a.createTime desc"),
    /**
     * count
     */
    @NamedQuery(name = "Banner.countByName",
                query = "select count(a) from Banner a where a.name = :name"),
    @NamedQuery(name = "Banner.countAll",
                query = "select count(a) from Banner a"),
    @NamedQuery(name = "Banner.countActive",
                query = "select count(a) from Banner a where a.status = 1"),
    /*
     * get
     */
    @NamedQuery(name = "Banner.getById",
                query = "select a from Banner a where a.id = :id"),
    /**
     * delete
     */
    @NamedQuery(name = "Banner.deleteById",
                query = "delete from Banner a where a.id = :id")

})
@Data
@NoArgsConstructor
public class Banner extends RecordScopeEntity {

    /**
     * BANNER名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * BANNER顺序号
     */
    @Column(nullable = false)
    private int number;

    /**
     * BANNER状态
     */
    @Column(nullable = false)
    private int status;

    /**
     * BANNER链接
     */
    @Column(nullable = true)
    private String url;

    /**
     * BANNER图片地址
     */
    @Column(nullable = false)
    private String imgUrl;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date createTime;
    
    /**
    * 创建时间
    */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date updateTime;
    
    /**
     * 创建人
     */
    @Column(nullable = true)
    private String author;
    
    /**
     * 更新人
     */
    @Column(nullable = true)
    private String updateBy;
    

    public Banner(String name,
                  int number,
                  int status,
                  String url,
                  String imgUrl,
                  Date createTime,
                  Date updateTime,
                  String author,
                  String updateBy
                ) {
       this.name = name;
       this.number = number;
       this.status = status;
       this.url = url;
       this.imgUrl = imgUrl;
       this.createTime = createTime;
       this.updateTime = updateTime;
       this.author = author;
       this.updateBy = updateBy;
    }
}
