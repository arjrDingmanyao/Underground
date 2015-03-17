/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.cms.utils;

import com.creditcloud.cms.entities.Article;
import com.creditcloud.cms.entities.Banner;
import com.creditcloud.cms.entities.Channel;

/**
 *
 * @author rooseek
 */
public class DTOUtils {

    /**
     * handle article
     *
     * @param article
     * @return
     */
    public static com.creditcloud.cms.model.Article getArticleDTO(Article article) {
        com.creditcloud.cms.model.Article result = null;
        if (article != null) {
            result = new com.creditcloud.cms.model.Article(article.getId(),
                                                           article.getChannel().getId(),
                                                           article.getTitle(),
                                                           article.getContent(),
                                                           article.getCategory(),
                                                           article.isHasImage(),
                                                           article.isPriority(),
                                                           article.getNewsId(),
                                                           article.getUrl(),
                                                           article.getPubDate(),
                                                           article.getMedia(),
                                                           article.getAuthor(),
                                                           article.getSummary(),
                                                           article.getMiniImg());
            result.setBgColor(article.getBgColor());
            result.setTimeRecorded(article.getTimeRecorded());
        }
        return result;
    }

    /**
     * handle channel
     *
     * @param channel
     * @return
     */
    public static com.creditcloud.cms.model.Channel getChannelDTO(Channel channel) {
        com.creditcloud.cms.model.Channel result = null;
        if (channel != null) {
            result = new com.creditcloud.cms.model.Channel(channel.getId(),
                                                           channel.getCategory(),
                                                           channel.getName(),
                                                           channel.getDescription());
            result.setTimeRecorded(channel.getTimeRecorded());
        }
        return result;
    }

    /**
     * 从model模型转换到Entity模型
     *
     * @param article
     * @param channel
     * @return
     */
    public static Article convertArticleDTO(com.creditcloud.cms.model.Article article, Channel channel) {
        Article result = null;
        if (article != null) {
            result = new Article(channel,
                                 article.getTitle(),
                                 article.getCategory(),
                                 article.getContent(),
                                 article.isHasImage(),
                                 article.isPriority(),
                                 article.getNewsId(),
                                 article.getUrl(),
                                 article.getPubDate(),
                                 article.getAuthor(),
                                 article.getMedia(),
                                 article.getBgColor(),
                                 article.getSummary(),
                                 article.getMiniImg());
            result.setId(article.getId());
        }
        return result;
    }

    /**
     * 将Channel从model模型转化到entity模型
     *
     * @param channel
     * @return
     */
    public static Channel convertChannelDTO(com.creditcloud.cms.model.Channel channel) {
        Channel result = null;
        if (channel != null) {
            result = new Channel(channel.getName(),
                                 channel.getCategory(),
                                 channel.getDescrption());
            result.setId(channel.getId());
        }
        return result;
    }
    
    
    public static com.creditcloud.cms.model.Banner getBannerDTO(Banner banner) {
        com.creditcloud.cms.model.Banner result = null;
        if (banner != null) {
            result = new com.creditcloud.cms.model.Banner(banner.getId(),
                                                          banner.getName(),
                                                          banner.getNumber(),
                                                          banner.getStatus(),
                                                          banner.getUrl(),
                                                          banner.getImgUrl(),
                                                          banner.getCreateTime(),
                                                          banner.getUpdateTime(),
                                                          banner.getAuthor(),
                                                          banner.getUpdateBy());
            result.setTimeRecorded(banner.getTimeRecorded());
        }
        return result;
    }
    
    
    /**
    * 从model模型转换到Entity模型
    *
    * @param banner
    * @return
    */
    public static Banner convertBannerDTO(com.creditcloud.cms.model.Banner banner) {
        Banner result = null;
        if (banner != null) {
            result = new Banner(banner.getName(),
                                banner.getNumber(),
                                banner.getStatus(),
                                banner.getUrl(),
                                banner.getImgUrl(),
                                banner.getCreateTime(),
                                banner.getUpdateTime(),
                                banner.getAuthor(),
                                banner.getUpdateBy());
            result.setId(banner.getId());
        }
        return result;
    }
}
