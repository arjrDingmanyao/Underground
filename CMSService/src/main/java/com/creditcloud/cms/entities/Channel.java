/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.cms.entities;

import com.creditcloud.cms.enums.Category;
import com.creditcloud.common.entities.RecordScopeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "TB_CMS_CHANNEL")
@NamedQueries({
    @NamedQuery(name = "Channel.listByCategory",
                query = "SELECT c from Channel c where c.category= :category order by c.timeRecorded desc")})
@Data
@NoArgsConstructor
public class Channel extends RecordScopeEntity {

    /**
     * 频道名
     */
    @Column(nullable = false)
    private String name;

    /**
     * 频道类别
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = true)
    private String description;

    public Channel(String name, Category category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }
}
