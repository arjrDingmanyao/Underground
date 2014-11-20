/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.cms.entities;

import com.creditcloud.cms.enums.Category;
import com.creditcloud.common.entities.RecordScopeEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "TB_CMS_ARTICLE")
@NamedQueries({
    /**
     * list
     */
    @NamedQuery(name = "Article.listByChannel",
                query = "select a from Article a where a.channel.id = :id order by a.priority desc, a.timeRecorded desc"),
    @NamedQuery(name = "Article.listByCategory",
                query = "select a from Article a where a.category = :category order by a.priority desc, a.timeRecorded desc"),
    /**
     * count
     */
    @NamedQuery(name = "Article.countByChannel",
                query = "select count(a) from Article a where a.channel.id = :id"),
    @NamedQuery(name = "Article.countByCategory",
                query = "select count(a) from Article a where a.category = :category"),
    /*
     * get
     */
    @NamedQuery(name = "Article.getByTitle",
                query = "select a from Article a where a.title = :title"),
    /**
     * delete
     */
    @NamedQuery(name = "Article.deleteByChannel",
                query = "delete from Article a where a.channel.id = :id")

})
@Data
@NoArgsConstructor
public class Article extends RecordScopeEntity {

    @ManyToOne
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;

    /**
     * 新闻标题
     */
    @Column(nullable = false)
    private String title;

    /**
     * 频道类别
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    /**
     * 新闻内容
     */
    @Lob
    @Column(nullable = true)
    private String content;

    /**
     * 是否含有图片
     */
    @Column(nullable = true)
    private boolean hasImage;

    /**
     * 是否优先显示
     */
    @Column(nullable = true)
    private boolean priority;

    /**
     * 新闻id
     */
    @Column(nullable = true)
    private String newsId;

    /**
     * 新闻url
     */
    @Column(nullable = true)
    private String url;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date pubDate;

    @Column(nullable = true)
    private String author;

    @Column(nullable = true)
    private String media;
    
    private String bgColor;

    public Article(Channel channel,
                   String title,
                   Category category,
                   String content,
                   boolean hasImage,
                   boolean priority,
                   String newsId,
                   String url,
                   Date pubDate,
                   String author,
                   String media,
                   String bgColor) {
        this.channel = channel;
        this.title = title;
        this.category = category;
        this.content = content;
        this.hasImage = hasImage;
        this.priority = priority;
        this.newsId = newsId;
        this.url = url;
        this.pubDate = pubDate;
        this.author = author;
        this.media = media;
        this.bgColor = bgColor;
    }
}
