/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.cms;

import com.creditcloud.cms.api.CMSService;
import com.creditcloud.cms.enums.Category;
import com.creditcloud.cms.dao.ArticleDAO;
import com.creditcloud.cms.dao.BannerDAO;
import com.creditcloud.cms.dao.ChannelDAO;
import com.creditcloud.cms.entities.Article;
import com.creditcloud.cms.entities.Banner;
import com.creditcloud.cms.entities.Channel;
import com.creditcloud.cms.local.ApplicationBean;
import com.creditcloud.cms.utils.DTOUtils;
import com.creditcloud.common.bean.BaseBean;
import com.creditcloud.model.criteria.CriteriaInfo;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class CMSServiceBean extends BaseBean implements CMSService {

    @EJB
    ApplicationBean appBean;

    @Inject
    Logger logger;

    @EJB
    ArticleDAO articleDAO;

    @EJB
    ChannelDAO channelDAO;
    
    @EJB
    BannerDAO bannerDAO;

    //****************************************关于栏目的实现*******************************************
    @Override
    public List<com.creditcloud.cms.model.Channel> listAllChannel(String clientCode) {
        appBean.checkClientCode(clientCode);
        List<Channel> channels = channelDAO.findAll();
        List<com.creditcloud.cms.model.Channel> result = new ArrayList<>(channels.size());
        for (Channel channel : channels) {
            result.add(DTOUtils.getChannelDTO(channel));
        }
        return result;
    }

    @Override
    public com.creditcloud.cms.model.Channel saveChannel(String clientCode, com.creditcloud.cms.model.Channel channel) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getChannelDTO(channelDAO.save(DTOUtils.convertChannelDTO(channel)));
    }

    /**
     * 根据主键删除栏目
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteChannelById(String clientCode, String id) {
        appBean.checkClientCode(clientCode);
        articleDAO.deleteByChannel(id);
        channelDAO.removeById(id);
        return channelDAO.find(id) == null;
    }

    @Override
    public PagedResult<com.creditcloud.cms.model.Channel> listChannel(String clientCode, CriteriaInfo info) {
        appBean.checkClientCode(clientCode);
        PagedResult<Channel> channels = channelDAO.list(info);
        List<com.creditcloud.cms.model.Channel> result = new ArrayList<>(channels.getResults().size());
        for (Channel channel : channels.getResults()) {
            result.add(DTOUtils.getChannelDTO(channel));
        }
        return new PagedResult<>(result, channels.getTotalSize());
    }

    /**
     * 根据主键获取一个栏目
     *
     * @param id
     * @return
     */
    @Override
    public com.creditcloud.cms.model.Channel getChannelById(String clientCode, String id) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getChannelDTO(channelDAO.find(id));
    }

    /**
     * 根据分类获取栏目
     *
     * @param category
     * @return
     */
    @Override
    public List<com.creditcloud.cms.model.Channel> listChannelByCategory(String clientCode, Category category) {
        appBean.checkClientCode(clientCode);
        List<Channel> channels = channelDAO.listByCategory(category);
        List<com.creditcloud.cms.model.Channel> result = new ArrayList<>(channels.size());
        for (Channel channel : channels) {
            result.add(DTOUtils.getChannelDTO(channel));
        }
        return result;
    }

    //******************************************关于文章的实现*********************************************
    /**
     * 根据分类获取文章列表
     *
     * @param category
     * @param info
     * @return
     */
    @Override
    public PagedResult<com.creditcloud.cms.model.Article> listArticleByCategory(String clientCode, Category category, PageInfo info) {
        appBean.checkClientCode(clientCode);
        List<Article> articles = articleDAO.listByCategory(category, info).getResults();
        List<com.creditcloud.cms.model.Article> result = new ArrayList<>(articles.size());
        for (Article article : articles) {
            result.add(DTOUtils.getArticleDTO(article));
        }
        return new PagedResult<>(result, articleDAO.countByCategory(category));
    }

    @Override
    public PagedResult<com.creditcloud.cms.model.Article> listArticleByChannel(String clientCode, String channelId, PageInfo info) {
        appBean.checkClientCode(clientCode);
        PagedResult<Article> articles = articleDAO.listByChannel(channelId, info);
        List<com.creditcloud.cms.model.Article> result = new ArrayList<>(articles.getResults().size());
        for (Article article : articles.getResults()) {
            result.add(DTOUtils.getArticleDTO(article));
        }
        return new PagedResult<>(result, articleDAO.countByChannel(channelId));
    }

    @Override
    public PagedResult<com.creditcloud.cms.model.Article> listArticle(String clientCode, CriteriaInfo info) {
        appBean.checkClientCode(clientCode);
        PagedResult<Article> articles = articleDAO.list(info);
        List<com.creditcloud.cms.model.Article> result = new ArrayList<>(articles.getResults().size());
        for (Article article : articles.getResults()) {
            result.add(DTOUtils.getArticleDTO(article));
        }
        return new PagedResult<>(result, articles.getTotalSize());
    }

    /**
     * 根据文章ID获取文章
     *
     * @param id
     * @return
     */
    @Override
    public com.creditcloud.cms.model.Article getArticleById(String clientCode, String id) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getArticleDTO(articleDAO.find(id));
    }

    @Override
    public boolean deleteArticleById(String clientCode, String id) {
        appBean.checkClientCode(clientCode);
        articleDAO.removeById(id);
        return articleDAO.find(id) == null;
    }

    @Override
    public com.creditcloud.cms.model.Article saveArticle(String clientCode, com.creditcloud.cms.model.Article article) {
        appBean.checkClientCode(clientCode);
        Channel channel = channelDAO.find(article.getChannelId());
        if (channel != null) {
            article.setCategory(channel.getCategory());
            Article result = articleDAO.save(DTOUtils.convertArticleDTO(article, channel));
//            if (result != null) {
//                if (article.getUrl() == null) {
//                    //生成默认url
//                    String url = appBean.getClient().getUrl().concat(CMSConstant.DEFAULT_ARTICLE_URL).concat("/").concat(result.getId());
//                    result.setUrl(url);
//                }
//            }
            return DTOUtils.getArticleDTO(result);
        }
        logger.debug("fail to find channel {} to save article.", article.getChannelId());
        return null;
    }
    /************************************************BANNER相关********************************************************/
     /**
     * 根据文章Id获取BANNER
     *
     * @param clientCode
     * @param id
     * @return
     */
    @Override
    public com.creditcloud.cms.model.Banner getBannerById(String clientCode, String id){
        appBean.checkClientCode(clientCode);
        return DTOUtils.getBannerDTO(bannerDAO.find(id));
    }

    /**
     * list by criteria
     *
     * @param clientCode
     * @param pageInfo
     * @return
     */
    @Override
    public PagedResult<com.creditcloud.cms.model.Banner> listBanner(String clientCode, PageInfo pageInfo){
        appBean.checkClientCode(clientCode);
        PagedResult<Banner> banners = bannerDAO.listAll(pageInfo);
        List<com.creditcloud.cms.model.Banner> result = new ArrayList<>(banners.getResults().size());
        for (Banner banner : banners.getResults()) {
            result.add(DTOUtils.getBannerDTO(banner));
        }
        return new PagedResult<>(result, banners.getTotalSize());
    }

    /**
     * 通过姓名列出BANNER
     *
     * @param clientCode
     * @param name
     * @param pageInfo
     * @return
     */
    @Override
    public PagedResult<com.creditcloud.cms.model.Banner> listBannerByName(String clientCode, String name, PageInfo pageInfo){
        appBean.checkClientCode(clientCode);
        PagedResult<Banner> banners = bannerDAO.listByName(name, pageInfo);
        List<com.creditcloud.cms.model.Banner> result = new ArrayList<>(banners.getResults().size());
        for (Banner banner : banners.getResults()) {
            result.add(DTOUtils.getBannerDTO(banner));
        }
        return new PagedResult<>(result, bannerDAO.countByName(name));
    }
    
    /**
     * list by criteria
     *
     * @param clientCode
     * @param pageInfo
     * @return
     */
    @Override
    public PagedResult<com.creditcloud.cms.model.Banner> listBannerActive(String clientCode, PageInfo pageInfo){
        appBean.checkClientCode(clientCode);
        PagedResult<Banner> banners = bannerDAO.listActive(pageInfo);
        List<com.creditcloud.cms.model.Banner> result = new ArrayList<>(banners.getResults().size());
        for (Banner banner : banners.getResults()) {
            result.add(DTOUtils.getBannerDTO(banner));
        }
        return new PagedResult<>(result, bannerDAO.countActive());
    }

     /**
     * 根据主键删除一个BANNER
     *
     * @param clientCode
     * @param id
     * @return
     */
    @Override
    public boolean deleteBannerById(String clientCode, String id){
        appBean.checkClientCode(clientCode);
        bannerDAO.removeById(id);
        return articleDAO.find(id) == null;
    }

    /**
     * 创建或更新文章
     * <p>
     * 更新成功之后返回更新成功的文章
     *
     * @param clientCode
     * @param banner
     * @return
     */
    public com.creditcloud.cms.model.Banner saveBanner(String clientCode, com.creditcloud.cms.model.Banner banner){
        appBean.checkClientCode(clientCode);
        return DTOUtils.getBannerDTO(bannerDAO.save(DTOUtils.convertBannerDTO(banner)));
    }
}
