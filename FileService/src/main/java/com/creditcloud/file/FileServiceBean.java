/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.file;

import com.creditcloud.common.store.FileStore;
import com.creditcloud.common.store.UpYunFile;
import com.creditcloud.common.utils.FileUtils;
import com.creditcloud.file.api.FileService;
import com.creditcloud.file.entity.dao.FileInfoDAO;
import com.creditcloud.model.constant.FileConstant;
import com.creditcloud.model.criteria.PageInfo;
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
public class FileServiceBean implements FileService {
    
    @Inject
    Logger logger;
    
    @Inject
    @UpYunFile
    FileStore fileStore;
    
    @EJB
    FileInfoDAO fileDAO;
    
    @Override
    public FileInfo upload(String clientCode, RealmEntity owner, String fileName, String filePath) {
        String storeName = FileUtils.hash(clientCode, owner, fileName);
        boolean storeResult = fileStore.store(clientCode,
                                              owner.getRealm(),
                                              storeName,
                                              filePath);
        if (storeResult) {
            com.creditcloud.file.entity.FileInfo file = fileDAO.getByOwnerAndName(clientCode, owner, fileName);
            if (file != null) {
                //file already exist ,just update upload time
                file.setTimeUpload(new Date());
                fileDAO.edit(file);
                logger.debug("file already exist,overwrite.[clientCode={}][owner={}][fileName={}]", clientCode, owner, fileName);
            } else {
                //file not exist ,upload to store persistence
                fileDAO.create(new com.creditcloud.file.entity.FileInfo(clientCode,
                                                                        com.creditcloud.common.utils.DTOUtils.convertRealmEntity(owner),
                                                                        fileName));
                logger.debug("upload file successful.[clientCode={}][owner={}][fileName={}][filePath={}]", clientCode, owner, fileName, filePath);
            }
            return download(clientCode, owner, fileName);
        }
        logger.debug("upload file failed.[clientCode={}][owner={}][fileName={}][filePath={}]", clientCode, owner, fileName, filePath);
        return new FileInfo(fileName, FileConstant.FILE_NOT_FOUND_URI);
    }
    
    @Override
    public boolean addNew(String clientCode, RealmEntity owner, String fileName) {
        com.creditcloud.file.entity.FileInfo file = fileDAO.getByOwnerAndName(clientCode, owner, fileName);
        if (file != null) {
            //file already exist ,just update upload time
            file.setTimeUpload(new Date());
            fileDAO.edit(file);
            logger.warn("file already exist,overwrite.[clientCode={}][owner={}][fileName={}]", clientCode, owner, fileName);
        } else {
            //file not exist ,upload to store persistence
            file = new com.creditcloud.file.entity.FileInfo(clientCode,
                                                            com.creditcloud.common.utils.DTOUtils.convertRealmEntity(owner),
                                                            fileName);
            fileDAO.create(file);
            logger.debug("add new file successful.[clientCode={}][owner={}][fileName={}]", clientCode, owner, fileName);
        }
        return true;
    }
    
    @Override
    public FileInfo download(String clientCode, RealmEntity owner, String fileName) {
        if (fileDAO.checkExist(clientCode, owner, fileName)) {
            String uri = fileStore.getURI(clientCode, owner.getRealm(), FileUtils.hash(clientCode, owner, fileName));
            return new FileInfo(fileName, uri);
        }
        
        return new FileInfo(fileName, FileConstant.FILE_NOT_FOUND_URI);
    }
    
    @Override
    public boolean delete(String clientCode, RealmEntity owner, String fileName) {
        /*
         * 只要数据库中删除了记录，不管存储中有没有删除都算成功
         */
        fileDAO.deleteByOwnerAndName(clientCode, owner, fileName);
        logger.debug("delete file by owner and name successful.[clientCode={}][owner={}][fileName={}]", clientCode, owner, fileName);
        return fileStore.delete(clientCode,
                                owner.getRealm(),
                                FileUtils.hash(clientCode, owner, fileName));
    }
    
    @Override
    public boolean delete(String clientCode, RealmEntity owner) {
        /*
         * 只要数据库中删除了记录，不管存储中有没有删除都算成功
         */
        for (com.creditcloud.file.entity.FileInfo info : fileDAO.listByOwner(clientCode, owner, PageInfo.ALL).getResults()) {
            fileStore.delete(clientCode,
                             owner.getRealm(),
                             FileUtils.hash(clientCode, owner, info.getName()));
        }
        fileDAO.deleteByOwner(clientCode, owner);
        logger.debug("delete file by owner successful.[clientCode={}][owner={}]", clientCode, owner.toString());
        return true;
    }
    
    @Override
    public PagedResult<FileInfo> list(String clientCode, RealmEntity owner, PageInfo info) {
        PagedResult<com.creditcloud.file.entity.FileInfo> files = fileDAO.listByOwner(clientCode, owner, info);
        List<FileInfo> result = new ArrayList<>(files.getResults().size());
        for (com.creditcloud.file.entity.FileInfo file : files.getResults()) {
            String uri = fileStore.getURI(clientCode,
                                          owner.getRealm(),
                                          FileUtils.hash(clientCode, owner, file.getName()));
            result.add(new FileInfo(file.getName(), uri));
        }
        return new PagedResult<>(result, files.getTotalSize());
    }
}
