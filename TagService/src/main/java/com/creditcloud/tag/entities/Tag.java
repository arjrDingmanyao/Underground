/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.tag.entities;

import com.creditcloud.common.entities.RecordScopeEntity;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.tag.constants.TagConstant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author rooseek
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"description", "alias"}, callSuper = false)
@Table(name = "TB_TAG", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"realm", "name"})})
@NamedQueries({
    /**
     * get query
     */
    @NamedQuery(name = "Tag.getByName",
                query = "select t from Tag t where t.realm = :realm and t.name = :name"),
    /**
     * list query
     */
    @NamedQuery(name = "Tag.listByRealm",
                query = "select t from Tag t where t.realm = :realm order by t.timeRecorded"),
    @NamedQuery(name = "Tag.listAll",
                query = "select t from Tag t order by t.timeRecorded"),
    /**
     * count query
     */
    @NamedQuery(name = "Tag.countByRealm",
                query = "select count(t) from Tag t where t.realm = :realm")
})
public class Tag extends RecordScopeEntity {

    /**
     * 如果是Realm.STRING表示name中仅仅是字符串，不是系统中的实体
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Realm realm;

    @Size(max = TagConstant.MAX_TAG_NAME)
    @Column(nullable = false)
    private String name;

    @Size(max = TagConstant.MAX_TAG_NAME)
    @Column(nullable = true)
    private String alias;

    @Size(max = TagConstant.MAX_TAG_DESCRIPTION)
    @Column(nullable = true)
    private String description;

}
