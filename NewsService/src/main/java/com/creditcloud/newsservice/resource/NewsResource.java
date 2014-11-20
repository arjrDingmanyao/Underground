/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.newsservice.resource;

/**
 *
 * @author elastix
 */
import com.creditcloud.common.rest.BaseResource;
import com.creditcloud.newsservice.dao.CategoryDAO;
import com.creditcloud.newsservice.dao.NewsDAO;
import com.creditcloud.newsservice.model.Category;
import com.creditcloud.newsservice.model.News;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/news")
public class NewsResource extends BaseResource {

    Logger logger = LoggerFactory.getLogger(NewsResource.class);

    @EJB
    CategoryDAO categoryDAO;

    @EJB
    NewsDAO newsDAO;

    @POST
    @Path("/create")
    public Boolean create(@Context HttpServletRequest request,
                          @FormParam("title") String title,
                          @FormParam("newId") String newsId,
                          @FormParam("url") String url,
                          @FormParam("author") String author,
                          @FormParam("snap") String snap,
                          @FormParam("media") String media,
                          @FormParam("pubdate") String pubdate,
                          @FormParam("categoryID") String categoryID) {
        String[] categoryIDArr = request.getParameterValues(categoryID);
        List<Category> categoryList = new ArrayList<Category>();
        try {
            News news = new News();
            news.setAuthor(author);
            news.setMedia(media);
            news.setPubdate(pubdate);
            news.setNewsId(newsId);
            news.setSnap(snap);
            news.setTitle(title);
            news.setUrl(url);
            news.setTimeCreated(new Date(System.currentTimeMillis() - 1000));
            for (int i = 0; i < categoryIDArr.length; i++) {
                categoryList.add(categoryDAO.find(categoryIDArr[i]));
            }
            news.setCategories(categoryList);
            news.setManualAdded(true);
            newsDAO.create(news);
            return true;
        } catch (Exception ex) {
            logger.error("Can't create news. {}", ex);
        }
        return false;
    }

    @POST
    @Path("/update")
    public Boolean update(@Context HttpServletRequest request,
                          @FormParam("ID") String ID,
                          @FormParam("title") String title,
                          @FormParam("newId") String newsId,
                          @FormParam("url") String url,
                          @FormParam("author") String author,
                          @FormParam("snap") String snap,
                          @FormParam("media") String media,
                          @FormParam("pubdate") String pubdate,
                          @FormParam("isTop") Boolean isTop,
                          @FormParam("isFiltered") Boolean isFiltered,
                          @FormParam("isMunualAdded") Boolean isManualAdded) {

        try {
            News news = null;
            if (ID != null) {
                news = newsDAO.find(ID);
            } else {
                return false;
            }
            if (author != null) {
                news.setAuthor(author);
            }
            if (media != null) {
                news.setMedia(media);
            }
            if (pubdate != null) {
                news.setPubdate(pubdate);
            }
            if (newsId != null) {
                news.setNewsId(newsId);
            }
            if (snap != null) {
                news.setSnap(snap);
            }
            if (title != null) {
                news.setTitle(title);
            }
            if (url != null) {
                news.setUrl(url);
            }
            if (isTop != null) {
                news.setTop(isTop);
            }
            if (isFiltered != null) {
                news.setFiltered(isFiltered);
            }
            if (isManualAdded != null) {
                news.setManualAdded(isFiltered);
            }
            news.setTimeModified(new Date(System.currentTimeMillis() - 1000));
            newsDAO.edit(news);
            return true;
        } catch (Exception ex) {
            logger.error("Can't create news. {}", ex);
        }
        return false;
    }

    /*
     **添加类别
     */
    @POST
    @Path("/add")
    public Boolean add(@Context HttpServletRequest request,
                       @FormParam("newsID") String newsID,
                       @FormParam("name") String name) {
        String[] categoryArr = request.getParameterValues(name);
        List<Category> categoryList = new ArrayList<Category>();
        try {
            News news = null;
            news = newsDAO.find(newsID);
            categoryList = news.getCategories();
            for (int i = 0; i < categoryArr.length; i++) {
                categoryList.add(categoryDAO.getByName(categoryArr[i]));
            }
            news.setCategories(categoryList);
            news.setTimeModified(new Date(System.currentTimeMillis() - 1000));
            newsDAO.edit(news);
            return true;
        } catch (Exception ex) {
            logger.error("Can't update news. {}", ex);
        }
        return false;
    }

    /*
     **删除类别
     */
    @POST
    @Path("/delete")
    public Boolean delete(@Context HttpServletRequest request,
                          @FormParam("newsID") String newsID,
                          @FormParam("name") String name) {
        String[] categoryArr = request.getParameterValues(name);
        List<Category> categoryList = new ArrayList<Category>();
        try {
            News news = null;
            news = newsDAO.find(newsID);
            categoryList = news.getCategories();
            for (int i = 0; i < categoryArr.length; i++) {
                if (categoryList.contains(categoryDAO.getByName(categoryArr[i]))) {
                    categoryList.add(categoryDAO.getByName(categoryArr[i]));
                }
            }
            news.setCategories(categoryList);
            news.setTimeModified(new Date(System.currentTimeMillis() - 1000));
            newsDAO.edit(news);
            return true;
        } catch (Exception ex) {
            logger.error("Can't update news. {}", ex);
        }
        return false;
    }

    /*
     **查找所有存在的新闻
     */
    @GET
    @Path("/all")
    public List<News> findAll(@Context HttpServletRequest request) {
        logger.error("Can't update news. {}", newsDAO);
        return newsDAO.findAll();
    }

    /*
     **可以输入多个keyword,返回可以符合查询条件的所有新闻
     */
    @GET
    @Path("/keyword")
    public List<News> check(@Context HttpServletRequest request) {
        String[] keywordArr = request.getParameterValues("keyword");
        List<News> result = new ArrayList<News>();
        for (int i = 0; i < keywordArr.length; i++) {
            List<News> listPerKeyword = newsDAO.findNewsByKeyword(keywordArr[i]);
            List<News> listPerKeywordMedia = newsDAO.getNewsByMedia(keywordArr[i]);
            if (!listPerKeyword.isEmpty()) {
                result.addAll(listPerKeyword);
            }
            if (!listPerKeywordMedia.isEmpty()) {
                result.addAll(listPerKeywordMedia);
            }
        }
        return result;
    }
}
