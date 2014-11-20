/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user;

import com.creditcloud.file.api.FileService;
import com.creditcloud.image.api.ImageService;
import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.user.credit.CertificateType;
import com.creditcloud.model.enums.user.credit.ProofContentType;
import com.creditcloud.model.enums.user.credit.ProofType;
import com.creditcloud.model.misc.RealmEntity;
import com.creditcloud.user.api.UserCreditService;
import com.creditcloud.user.entity.Certificate;
import com.creditcloud.user.entity.record.CertificateRecord;
import com.creditcloud.user.entity.Proof;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.UserCredit;
import com.creditcloud.user.entity.dao.CertificateDAO;
import com.creditcloud.user.entity.dao.CertificateRecordDAO;
import com.creditcloud.user.entity.dao.ProofDAO;
import com.creditcloud.user.entity.dao.RealEstateDAO;
import com.creditcloud.user.entity.dao.UserCreditDAO;
import com.creditcloud.user.entity.dao.UserDAO;
import com.creditcloud.user.entity.dao.VehicleDAO;
import com.creditcloud.user.local.ApplicationBean;
import com.creditcloud.user.local.CreditLocalBean;
import com.creditcloud.user.utils.DTOUtils;
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
public class UserCreditServiceBean implements UserCreditService {

    @EJB
    ApplicationBean appBean;

    @Inject
    Logger logger;

    @EJB
    UserDAO userDAO;

    @EJB
    UserCreditDAO creditDAO;

    @EJB
    ProofDAO proofDAO;

    @EJB
    RealEstateDAO estateDAO;

    @EJB
    VehicleDAO vehicleDAO;

    @EJB
    CertificateDAO certificateDAO;

    @EJB
    CertificateRecordDAO recordDAO;

    @EJB
    CreditLocalBean creditBean;

    @EJB
    ImageService imageService;

    @EJB
    FileService fileService;

    @Override
    public com.creditcloud.model.user.credit.UserCredit getUserCreditByUserId(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        User user = userDAO.find(userId);
        if (user == null) {
            logger.warn("User not exist when get UserCredit.[userId={}]", userId);
            return null;
        }
        UserCredit userCredit = creditDAO.find(userId);
        if (userCredit == null) {
            logger.debug("Create UserCredit for User.[userId={}]", userId);
            userCredit = creditDAO.addNew(user);
        }
        return DTOUtils.getUserCredit(userCredit);
    }

    @Override
    public com.creditcloud.model.user.credit.Proof addProof(String clientCode, String userId, com.creditcloud.model.user.credit.Proof proof) {
        appBean.checkClientCode(clientCode);
        logger.info("add proof for user.[clientCode={}][userId={}][owner={}]", clientCode, userId, proof.getOwner());
        Certificate certificate = creditBean.getCertificateByUserAndType(userId, proof.getProofType().getCertificateType());
        Proof result = proofDAO.addOrUpdate(DTOUtils.convertProof(proof, certificate));

        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
        return DTOUtils.getProof(result);
    }

    @Override
    public List<com.creditcloud.model.user.credit.Proof> listProofByUserAndType(String clientCode, String userId, List<CertificateType> certificateTypes, List<ProofContentType> contentTypes) {
        appBean.checkClientCode(clientCode);
        List<Proof> proofs = proofDAO.listByUserAndType(userId, certificateTypes, contentTypes);
        List<com.creditcloud.model.user.credit.Proof> result = new ArrayList<>(proofs.size());
        for (Proof proof : proofs) {
            result.add(DTOUtils.getProof(proof));
        }

        return result;
    }

    @Override
    public List<com.creditcloud.model.user.credit.Proof> listProofByLoanRequestAndType(String clientCode, String userId, String requestId, ProofContentType... types) {
        appBean.checkClientCode(clientCode);
        List<Proof> proofs = proofDAO.listByUserAndOwner(userId,
                                                         new com.creditcloud.common.entities.embedded.RealmEntity(Realm.LOANREQUEST, requestId),
                                                         types);
        List<com.creditcloud.model.user.credit.Proof> result = new ArrayList<>(proofs.size());
        for (Proof proof : proofs) {
            result.add(DTOUtils.getProof(proof));
        }

        return result;
    }

    @Override
    public List<com.creditcloud.model.user.credit.Proof> listProofByEntityAndType(String clientCode, String userId, RealmEntity entity, ProofContentType... types) {
        appBean.checkClientCode(clientCode);
        List<Proof> proofs = proofDAO.listByUserAndOwner(userId, com.creditcloud.common.utils.DTOUtils.convertRealmEntity(entity), types);
        List<com.creditcloud.model.user.credit.Proof> result = new ArrayList<>(proofs.size());
        for (Proof proof : proofs) {
            result.add(DTOUtils.getProof(proof));
        }

        return result;
    }

    @Override
    public List<com.creditcloud.model.user.credit.Certificate> listCertificateByUser(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        List<Certificate> certificates = certificateDAO.listByUser(userId);
        List<com.creditcloud.model.user.credit.Certificate> result = new ArrayList<>(certificates.size());
        for (Certificate certificate : certificates) {
            result.add(DTOUtils.getCertificate(certificate));
        }

        return result;
    }

    @Override
    public List<com.creditcloud.model.user.credit.CertificateRecord> listRecordByCertificate(String clientCode, String certificateId) {
        appBean.checkClientCode(clientCode);
        List<CertificateRecord> records = recordDAO.listByCertificate(certificateId);
        List<com.creditcloud.model.user.credit.CertificateRecord> result = new ArrayList<>(records.size());
        for (CertificateRecord record : records) {
            result.add(DTOUtils.getCertificateRecord(record));
        }
        return result;
    }

    @Override
    public boolean updateCertificate(String clientCode, com.creditcloud.model.user.credit.Certificate certificate) {
        appBean.checkClientCode(clientCode);
        Certificate entityCertificate = DTOUtils.convertCertificate(certificate, creditDAO.find(certificate.getUserId()));
        //certificateDAO.edit(entityCertificate);
        creditBean.updateCertificate(entityCertificate);
        return true;
    }

    @Override
    public com.creditcloud.model.user.credit.Certificate getCertificateByUserByType(String clientCode, String userId, CertificateType type) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getCertificate(creditBean.getCertificateByUserAndType(userId, type));
    }

    @Override
    public com.creditcloud.model.user.credit.Certificate getCertificateById(String clientCode, String certificateId) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getCertificate(certificateDAO.find(certificateId));
    }

    /**
     * TODO 目前认为一个image/file只对应到一个proof，所以同时删除对应认证材料。未来可能一个材料对应多个认证，那么就不能删除原材料
     *
     * @param clientCode
     * @param proofId
     * @return
     */
    @Override
    public boolean deleteProof(String clientCode, String proofId) {
        appBean.checkClientCode(clientCode);

        Proof proof = proofDAO.find(proofId);
        if (proof == null) {
            //can not find such proof, return false
            logger.warn("proof with id {} not found.", proofId);
            return false;
        }

        switch (proof.getContentType()) {
            case IMAGE:
                // delete image for this proof
                imageService.delete(appBean.getClientCode(),
                                    com.creditcloud.common.utils.DTOUtils.getRealmEntity(proof.getOwner()),
                                    proof.getContent());
                break;
            case DOCUMENT:
                //delete file for this proof
                fileService.delete(appBean.getClientCode(),
                                   com.creditcloud.common.utils.DTOUtils.getRealmEntity(proof.getOwner()),
                                   proof.getContent());
                break;
            default:
            //do nothing
        }

        String userId = proof.getCertificate().getCredit().getUserId();
        //now let's delete this proof 
        proofDAO.remove(proof);
        logger.debug("proof {} deleted.", proofId);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
        
        return true;
    }

    @Override
    public com.creditcloud.model.user.credit.Proof getProofByUserAndContent(String clientCode,
                                                                            String userId,
                                                                            RealmEntity owner,
                                                                            ProofType proofType,
                                                                            ProofContentType contentType,
                                                                            String content) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getProof(proofDAO.getByUserAndOwnerAndContent(userId,
                                                                      com.creditcloud.common.utils.DTOUtils.convertRealmEntity(owner),
                                                                      proofType, contentType, content));
    }

    @Override
    public boolean deleteProofByUserAndContent(String clientCode,
                                               String userId,
                                               RealmEntity owner,
                                               ProofType proofType,
                                               ProofContentType contentType,
                                               String content) {
        appBean.checkClientCode(clientCode);
        Proof proof = proofDAO.getByUserAndOwnerAndContent(userId,
                                                           com.creditcloud.common.utils.DTOUtils.convertRealmEntity(owner),
                                                           proofType, contentType, content);
        if (proof == null) {
            //not exist
            return true;
        }
        
        return deleteProof(clientCode, proof.getId());
    }

    @Override
    public boolean updateUserCredit(String clientCode, com.creditcloud.model.user.credit.UserCredit credit) {
        appBean.checkClientCode(clientCode);
        creditDAO.update(credit);
        
        // delete cache
        appBean.deleteCache(credit.getUserId(), CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public void markProofAsCover(String clientCode, String userId, RealmEntity owner, String proofId) {
        appBean.checkClientCode(clientCode);
        logger.debug("mark proof {} as cover for owner {}.", proofId, owner);
        proofDAO.markAsCover(userId, com.creditcloud.common.utils.DTOUtils.convertRealmEntity(owner), proofId);
        
        // delete cache
        appBean.deleteCache(userId, CacheConstant.KEY_PREFIX_USER_INFO);
    }

    @Override
    public List<com.creditcloud.model.user.credit.Proof> getCoverProof(String clientCode, String userId, RealmEntity owner) {
        appBean.checkClientCode(clientCode);
        List<Proof> proofs = proofDAO.listCover(userId, com.creditcloud.common.utils.DTOUtils.convertRealmEntity(owner));
        List<com.creditcloud.model.user.credit.Proof> result = new ArrayList<>(proofs.size());
        for (Proof proof : proofs) {
            result.add(DTOUtils.getProof(proof));
        }
        return result;
    }
}
