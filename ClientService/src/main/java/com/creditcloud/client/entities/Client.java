/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities;

import com.creditcloud.common.entities.UUIDEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Client is an actual entity (company) using this System.
 *
 * @author sobranie
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_CLIENT")
@NamedQueries({
    @NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c"),
    @NamedQuery(name = "Client.findByCode", query = "SELECT c from Client c WHERE c.code = :code")
})
public class Client extends UUIDEntity {

    /**
     * The name of this client
     */
    @Column(unique = true, nullable = false)
    private String name;
    
    /**
     * 简称
     */
    @Column(unique = true, nullable = false)
    private String shortName;
    
    /**
     * 网站标题
     */
    @Column(nullable = false)
    private String title;
    
    /**
     * 系统发送重要通知的手机号
     */
    @Column(nullable = false)
    private String mobile;
    
    /**
     * 系统支持邮件地址
     */
    @Column(nullable = false)
    private String supportEmail;
    
    /**
     * 支持/客服电话
     */
    @Column(nullable = true)
    private String supportPhone;
    
    /**
     * 内部邮件特征字符串，邮件域名中包含creditcloud的即为CRCD的内部客户.
     * 
     * 多个字符串用逗号分割
     */
    @Column(nullable = true)
    private String internalEmailIndicators;

    /**
     * The code for this client to identify and mask the real name if necessary
     * 4 char
     */
    @Column(unique = true, nullable = false, length = 4)
    private String code;

    /**
     * The URL of this client, mostly the entry of this client's admin site
     */
    @Column(nullable = false)
    private String url;
    
    @Column(nullable = true)
    private boolean secure;
    
    @Column(nullable = true)
    private String logo;

}
