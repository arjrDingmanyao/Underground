/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.newsservice.resource;

import com.creditcloud.common.rest.BaseResource;
import com.creditcloud.newsservice.model.News;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/")
public class HomeResource extends BaseResource {

    @GET
    public Response index() {
        return Response.ok("Hello").build();
    }

    @POST
    public boolean login(@Context HttpServletRequest request,
                         @BeanParam News news
    ) {
        return false;
    }
}
