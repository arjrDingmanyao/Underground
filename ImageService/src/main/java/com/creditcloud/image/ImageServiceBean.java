/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.image;

import com.creditcloud.common.store.ImageStore;
import com.creditcloud.common.store.UpYunImage;
import com.creditcloud.common.utils.ImageUtils;
import com.creditcloud.image.api.ImageService;
import com.creditcloud.image.entity.Image;
import com.creditcloud.image.entity.dao.ImageDAO;
import com.creditcloud.model.constant.ImageConstant;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.ImageSize;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.misc.RealmEntity;
import java.util.ArrayList;
import java.util.Date;
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
public class ImageServiceBean implements ImageService {

    @Inject
    Logger logger;

    /**
     * 不需要存储新的图片名，随时生成，减轻数据库压力 图片存在与否以数据库为准,ImageStore只用来存取图片本身
     */
    @Inject
    @UpYunImage
    ImageStore imageStore;

    @EJB
    ImageDAO imageDAO;

    @Override
    public com.creditcloud.image.Image upload(String clientCode, RealmEntity owner, String imageName, String imagePath) {
        String storeName = ImageUtils.hash(clientCode, owner, imageName);
        boolean storeResult = imageStore.store(clientCode,
                                               owner.getRealm(),
                                               storeName,
                                               imagePath);
        if (storeResult) {
            Image image = imageDAO.getByOwnerAndName(clientCode, owner, imageName);
            if (image != null) {
                //image already exist ,just update upload time
                image.setTimeUpload(new Date());
                imageDAO.edit(image);
                logger.debug("image already exist,overwrite.[clientCode={}][owner={}][imageName={}]", clientCode, owner, imageName);
            } else {
                //image not exist ,upload to store persistence
                imageDAO.create(new com.creditcloud.image.entity.Image(clientCode,
                                                                       com.creditcloud.common.utils.DTOUtils.convertRealmEntity(owner),
                                                                       imageName));
                logger.debug("upload image successful.[clientCode={}][owner={}][imageName={}][imagePath={}]", clientCode, owner, imageName, imagePath);
            }
            return download(clientCode, owner, imageName, ImageSize.ORIGINAL, false);
        }
        logger.debug("upload image failed.[clientCode={}][owner={}][imageName={}][imagePath={}]", clientCode, owner, imageName, imagePath);
        return new com.creditcloud.image.Image(imageName, ImageConstant.IMAGE_NOT_FOUND_URI);
    }

    @Override
    public boolean addNew(String clientCode, RealmEntity owner, String imageName) {
        Image image = imageDAO.getByOwnerAndName(clientCode, owner, imageName);
        if (image != null) {
            //image already exist ,just update upload time
            image.setTimeUpload(new Date());
            imageDAO.edit(image);
            logger.warn("image already exist,overwrite.[clientCode={}][owner={}][imageName={}]", clientCode, owner, imageName);
        } else {
            //image not exist ,upload to store persistence
            image = new Image(clientCode,
                              com.creditcloud.common.utils.DTOUtils.convertRealmEntity(owner),
                              imageName);
            imageDAO.create(image);
            logger.debug("add new image successful.[clientCode={}][owner={}][imageName={}]", clientCode, owner, imageName);
        }
        return true;
    }

    @Override
    public com.creditcloud.image.Image download(String clientCode, RealmEntity owner, String imageName, ImageSize size, boolean watermark) {
        if (imageDAO.checkExist(clientCode, owner, imageName)) {
            String uri = imageStore.getURI(clientCode,
                                           owner.getRealm(),
                                           ImageUtils.hash(clientCode, owner, imageName),
                                           size,
                                           watermark);
            return new com.creditcloud.image.Image(imageName, uri);
        }

        return new com.creditcloud.image.Image(imageName, ImageConstant.IMAGE_NOT_FOUND_URI);
    }

    @Override
    public boolean delete(String clientCode, RealmEntity owner, String imageName) {
        /*
         * 只要数据库中删除了记录，不管存储中有没有删除都算成功
         */
        imageDAO.deleteByOwnerAndName(clientCode, owner, imageName);
        logger.debug("delete image by owner and name successful.[clientCode={}][owner={}][imageName={}]", clientCode, owner, imageName);
        return imageStore.delete(clientCode,
                                 owner.getRealm(),
                                 ImageUtils.hash(clientCode, owner, imageName));
    }

    @Override
    public boolean delete(String clientCode, RealmEntity owner) {
        /*
         * 只要数据库中删除了记录，不管存储中有没有删除都算成功
         */
        for (Image info : imageDAO.listByOwner(clientCode, owner, PageInfo.ALL).getResults()) {
            imageStore.delete(clientCode,
                              owner.getRealm(),
                              ImageUtils.hash(clientCode, owner, info.getName()));
        }
        imageDAO.deleteByOwner(clientCode, owner);
        logger.debug("delete image by owner successful.[clientCode={}][owner={}]", clientCode, owner.toString());
        return true;
    }

    @Override
    public PagedResult<com.creditcloud.image.Image> list(String clientCode,
                                                         RealmEntity owner,
                                                         PageInfo info,
                                                         ImageSize size,
                                                         boolean watermark) {
        PagedResult<Image> images = imageDAO.listByOwner(clientCode, owner, info);
        List<com.creditcloud.image.Image> result = new ArrayList<>(images.getResults().size());
        for (Image image : images.getResults()) {
            String uri = imageStore.getURI(clientCode,
                                           owner.getRealm(),
                                           ImageUtils.hash(clientCode, owner, image.getName()),
                                           size,
                                           watermark);
            result.add(new com.creditcloud.image.Image(image.getName(), uri));
        }
        return new PagedResult<>(result, images.getTotalSize());
    }

    public PagedResult<com.creditcloud.image.Image> list(String clientCode,
                                                         RealmEntity owner,
                                                         PageInfo info,
                                                         ImageSize size) {
        return list(clientCode, owner, info, size, false);
    }

    public PagedResult<com.creditcloud.image.Image> list(String clientCode, RealmEntity owner, PageInfo info) {
        return list(clientCode, owner, info, ImageSize.ORIGINAL);
    }
}
