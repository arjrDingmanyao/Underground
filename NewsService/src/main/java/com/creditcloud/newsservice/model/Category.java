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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Past;
import lombok.Data;

@Entity
@Table(name = "TB_CATEGORY")
@NamedQueries({
    @NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c order by c.timeCreated desc"),
    @NamedQuery(name = "Category.delete", query = " DELETE from Category c where c.name = :name "),
    @NamedQuery(name = "Category.update", query = " UPDATE Category c set c.name = :name "),
    @NamedQuery(name = "Category.findByName", query = "SELECT c from Category c WHERE c.name = :name order by c.timeCreated desc"),
    @NamedQuery(name = "Category.getByNews",
                query = " select distinct c from Category c inner join c.news n where n= :news"), //select car from Car car inner join car.dealerships dealership where dealership in :dealerships
})
@Data
public class Category extends UUIDEntity {

    /*
     * 类型名称
     */
    @Column(nullable = true)

    private String name;

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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "NEWS_CATEGORY",
               joinColumns = @JoinColumn(name = "CATEGORY_ID", nullable = true),
               inverseJoinColumns = @JoinColumn(name = "NEWS_ID", nullable = true))
    private List<News> news;

  

}
