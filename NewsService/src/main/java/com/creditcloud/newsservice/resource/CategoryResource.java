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

@Path("/category")
public class CategoryResource extends BaseResource {
    
    Logger logger = LoggerFactory.getLogger(CategoryResource.class);

    @EJB
    CategoryDAO categoryDAO;

    @EJB
    NewsDAO newsDAO;

    @POST
    @Path("/create")
    public Boolean create(@Context HttpServletRequest request,
                          @FormParam("newsID") String newsID,
                          @FormParam("name") String name) {
        String[] newsIDArr = request.getParameterValues(newsID);
        List<News> newsList = new ArrayList<News>();
        try {
            Category category = new Category();
            if (categoryDAO.getByName(name) == null) {
                category.setName(name);
            } else {
                return false;
            }
            category.setTimeCreated(new Date(System.currentTimeMillis() - 1000));
            for (int i = 0; i < newsIDArr.length; i++) {
                newsList.add(newsDAO.find(newsIDArr[i]));
            }
            category.setNews(newsList);
            categoryDAO.create(category);
            return true;
        } catch (Exception ex) {
            logger.error("Can't create category. {}", ex);
        }
        return false;
    }

    /*
     **添加新闻
     */
    @POST
    @Path("/addNews")
    public Boolean addNews(@Context HttpServletRequest request,
                           @FormParam("newsID") String newsID,
                           @FormParam("name") String name) {
        String[] addNewsIDArr = request.getParameterValues(newsID);
        List<News> newsList = new ArrayList<News>();
        try {
            Category category = null;
            category = categoryDAO.getByName(name);
            newsList = category.getNews();
            for (int i = 0; i < addNewsIDArr.length; i++) {
                newsList.add(newsDAO.find(addNewsIDArr[i]));
            }
            category.setNews(newsList);
            category.setTimeModified(new Date(System.currentTimeMillis() - 1000));
            categoryDAO.edit(category);
            return true;
        } catch (Exception ex) {
            logger.error("Can't update category. {}", ex);
        }
        return false;
 }

    /*
     **删除新闻
     */
    @POST
    @Path("/deleteNews")
    public Boolean deleteNews(@Context HttpServletRequest request,
                              @FormParam("newsID") String newsID,
                              @FormParam("name") String name) {
        String[] addNewsIDArr = request.getParameterValues(newsID);
        List<News> newsList = new ArrayList<News>();

        try {
            Category category = null;
            category = categoryDAO.getByName(name);
            newsList = category.getNews();
            for (int i = 0; i < addNewsIDArr.length; i++) {
                if (newsList.contains(newsDAO.find(addNewsIDArr[i]))) {
                    newsList.remove(newsDAO.find(addNewsIDArr[i]));
                }
            }
            category.setNews(newsList);
            category.setTimeModified(new Date(System.currentTimeMillis() - 1000));
            categoryDAO.edit(category);
            return true;

        } catch (Exception ex) {
            logger.error("Can't update category. {}", ex);
        }
        return false;

    }


    /*
     **删除类别
     */
    @POST
    @Path("/delete")
    public void delete(@Context HttpServletRequest request,
                       @FormParam("name") String name
    ) {

        categoryDAO.delete(name);
    }

    /*
     **删除类别
     */
    @POST
    @Path("/update")
    public void update(@Context HttpServletRequest request,
                       @FormParam("name") String name
    ) {

        categoryDAO.update(name);
    }

    /*
     **根据用户码查找选过的关键字
     */
    @GET
    @Path("/name")
    public Category check(@Context HttpServletRequest request) {

        String name = request.getParameter("name");

        return categoryDAO.getByName(name);

    }

    /*
     **查找所有存在的关键字
     */
    @GET
    @Path("/all")
    public List<Category> findAll(@Context HttpServletRequest request) {

        return categoryDAO.findAll();

    }

}
