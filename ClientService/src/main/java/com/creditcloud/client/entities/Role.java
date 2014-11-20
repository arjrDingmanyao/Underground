/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities;

import com.creditcloud.common.entities.ClientScopeEntity;
import com.creditcloud.model.enums.client.Privilege;
import java.util.Collection;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * A group
 *
 * @author sobranie
 */
@Entity
@Table(name = "TB_ROLE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ClientCode", "NAME"})
})
public class Role extends ClientScopeEntity {

    /**
     * Name of this role
     */
    @Column(name = "NAME",
            nullable = false)
    private String name;
    
    /**
     * 员工组说明
     */
    private String description;

    /**
     * The members having this role granted
     */
    @ManyToMany(mappedBy = "roles")
    private Collection<Employee> members;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "RF_PRIVILEGE")
    @Enumerated(EnumType.STRING)
    private Collection<Privilege> privileges;

    public Role() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Employee> getMembers() {
        return members;
    }

    public void setMembers(Collection<Employee> members) {
        this.members = members;
    }

    public Collection<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Collection<Privilege> privileges) {
        this.privileges = privileges;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
