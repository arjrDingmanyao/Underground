/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.image.local;

import com.creditcloud.common.store.ImageStore;
import com.creditcloud.common.store.UpYunImage;
import com.creditcloud.image.entity.dao.ImageDAO;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class ImageLocalBean {

    /**
     * 图片存在与否以数据库为准,ImageStore只用来存取图片本身
     */
    @Inject
    @UpYunImage
    ImageStore imageStore;

    @EJB
    ImageDAO imageDAO;

    private void clear() {
        //TODO
        //周期性清理数据库中已删除，但ImageStore上没有删除的图片,一切以数据库为准
    }
}
