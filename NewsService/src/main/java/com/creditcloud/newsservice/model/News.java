/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.newsservice.model;

/**
 *
 * @author elastix
 */
import com.creditcloud.common.entities.UUIDEntity;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Past;
import lombok.Data;

@Entity
@Table(name = "TB_NEWS")
@NamedQueries({
    @NamedQuery(name = "News.findAll", query = "SELECT n FROM News n order by n.timeCreated desc"),
    @NamedQuery(name = "News.findByMedia", query = "SELECT n from News n WHERE n.media = :media order by n.timeCreated desc"),
    @NamedQuery(name = "News.getByKeyword",
                query = "select n from News n where (n.snap like :keyWord or n.title like :keyWord) order by n.timeCreated desc"),
    @NamedQuery(name = "News.getByCategory",
                query = " select distinct n from News n inner join n.categories c where c.name = :names"),
   
//select car from Car car inner join car.dealerships dealership where dealership in :dealerships

})
@Data
public class News extends UUIDEntity {

    /**
     * 新闻标题
     */
    @Column(nullable = true)
    private String title;

    /**
     * 新闻标识id
     */
    @Column(nullable = true)
    private String newsId;

    /**
     * 原文链接
     */
    @Column(nullable = true)
    @Lob
    @Basic
    private String url;

    /**
     * 发布时间
     */
    @Column(nullable = true)
    private String pubdate;

    /**
     * 发布媒体
     */
    @Column(nullable = true)
    private String media;

    /**
     * 作者
     */
    @Column(nullable = true)
    private String author;

    /**
     * 内容摘要
     */
    @Column(nullable = true)
    @Lob
    @Basic
    private String snap;

    /*
     **建立时间
     */
    @Past
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date timeCreated;

    /*
     **修改时间
     */
    @Past
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date timeModified;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "NEWS_CATEGORY",
               joinColumns = @JoinColumn(name = "NEWS_ID", nullable = true),
               inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID", nullable = true))
    private List<Category> categories;

    /*
     * 是否置顶
     */
    private boolean isTop;

    /*
     * 是否不予显示
     */
    private boolean isFiltered;
    
     /*
     * 是否不予显示
     */
    private boolean isManualAdded;

}
