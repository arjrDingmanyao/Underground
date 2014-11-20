/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.model.ElementCount;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.enums.user.credit.CertificateType;
import com.creditcloud.model.enums.user.credit.ProofContentType;
import com.creditcloud.model.enums.user.credit.ProofType;
import com.creditcloud.user.entity.Proof;
import com.creditcloud.user.utils.DTOUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class ProofDAO extends AbstractDAO<Proof> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public ProofDAO() {
        super(Proof.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Proof按照userId/contentType/content保证唯一性，如果有重复就覆盖之前的
     *
     * @param proof
     * @return
     */
    public Proof addOrUpdate(Proof proof) {
        //TODO for compatibility with legacy system
        Proof original = proof.getOwner() == null
                ? getByUserAndOwnerAndContent(proof.getCertificate().getCredit().getUserId(),
                                              proof.getProofType(),
                                              proof.getContentType(),
                                              proof.getContent())
                : getByUserAndOwnerAndContent(proof.getCertificate().getCredit().getUserId(),
                                              proof.getOwner(),
                                              proof.getProofType(),
                                              proof.getContentType(),
                                              proof.getContent());
        if (original == null) {
            //not exist,create new one
            return create(proof);
        }
        //overwrite original proof
        original.setContent(proof.getContent());
        original.setContentType(proof.getContentType());
        original.setDescription(proof.getDescription());
        original.setLatitude(proof.getLatitude());
        original.setLongitude(proof.getLongitude());
        original.setMosaic(proof.isMosaic());
        original.setProofType(proof.getProofType());
        original.setEmployee(proof.getEmployee());
        original.setOwner(proof.getOwner());
        original.setSource(proof.getSource());
        original.setSubmitTime(proof.getSubmitTime());
        original.setCertificate(proof.getCertificate());
        edit(original);

        return find(original.getId());
    }

    /**
     * list by user and certificate type
     *
     * @param userId
     * @param certificateTypes
     * @param contentTypes
     * @return
     */
    public List<Proof> listByUserAndType(String userId, List<CertificateType> certificateTypes, List<ProofContentType> contentTypes) {
        if (contentTypes == null || contentTypes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        if (certificateTypes == null || certificateTypes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("Proof.listByUserAndType", Proof.class)
                .setParameter("userId", userId)
                .setParameter("certificateTypes", certificateTypes)
                .setParameter("contentTypes", contentTypes)
                .getResultList();
    }

    /**
     *
     * @param userId
     * @param owner
     * @param types
     * @return
     */
    public List<Proof> listByUserAndOwner(String userId, RealmEntity owner, ProofContentType... types) {
        if (types == null || types.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return getEntityManager()
                .createNamedQuery("Proof.listByUserAndOwnerAndType", Proof.class)
                .setParameter("userId", userId)
                .setParameter("owner", owner)
                .setParameter("typeList", Arrays.asList(types))
                .getResultList();
    }

    public boolean checkExist(String userId, RealmEntity owner, ProofType proofType, ProofContentType contentType, String content) {
        return getByUserAndOwnerAndContent(userId, owner, proofType, contentType, content) != null;
    }

    /**
     * get by user/contentType/content
     *
     * @param userId
     * @param owner
     * @param proofType
     * @param contentType
     * @param content
     * @return
     */
    public Proof getByUserAndOwnerAndContent(String userId, RealmEntity owner, ProofType proofType, ProofContentType contentType, String content) {
        try {
            return getEntityManager()
                    .createNamedQuery("Proof.getByUserAndOwnerAndContent", Proof.class)
                    .setParameter("userId", userId)
                    .setParameter("owner", owner)
                    .setParameter("proofType", proofType)
                    .setParameter("contentType", contentType)
                    .setParameter("content", content)
                    .getSingleResult();
        } catch (NoResultException nre) {
            //do nothing
        }
        return null;
    }

    /**
     * //TODO for compatibility with legacy system
     *
     * @param userId
     * @param proofType
     * @param contentType
     * @param content
     * @return
     */
    private Proof getByUserAndOwnerAndContent(String userId, ProofType proofType, ProofContentType contentType, String content) {
        try {
            return getEntityManager()
                    .createNamedQuery("Proof.getByUserAndOwnerAndContent2", Proof.class)
                    .setParameter("userId", userId)
                    .setParameter("owner", new RealmEntity(Realm.USER, userId))
                    .setParameter("proofType", proofType)
                    .setParameter("contentType", contentType)
                    .setParameter("content", content)
                    .getSingleResult();
        } catch (NoResultException nre) {
            //do nothing
        }
        return null;
    }

    /**
     * count proof by source
     *
     * @return
     */
    public List<ElementCount<Source>> countEachBySource() {

        List<Object[]> objects = getEntityManager()
                .createNamedQuery("Proof.countEachBySource")
                .getResultList();
        List<ElementCount<Source>> result = new ArrayList<>(objects.size());
        for (Object[] object : objects) {
            Source source = (Source) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(source, count == null ? 0 : count.intValue()));
        }

        return result;
    }

    /**
     * count proof by certificate type
     *
     * @param source
     * @return
     */
    public List<ElementCount<CertificateType>> countEachByCertificateType(Source... source) {
        if (source == null || source.length == 0) {
            return Collections.EMPTY_LIST;
        }

        List<Object[]> objects = getEntityManager()
                .createNamedQuery("Proof.countEachByCertificateType")
                .setParameter("sourceList", Arrays.asList(source))
                .getResultList();
        List<ElementCount<CertificateType>> result = new ArrayList<>(objects.size());
        for (Object[] object : objects) {
            CertificateType type = (CertificateType) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(type, count == null ? 0 : count.intValue()));
        }

        return result;
    }

    /**
     * count proof by proof type
     *
     * @param source
     * @return
     */
    public List<ElementCount<ProofType>> countEachByProofType(Source... source) {
        if (source == null || source.length == 0) {
            return Collections.EMPTY_LIST;
        }

        List<Object[]> objects = getEntityManager()
                .createNamedQuery("Proof.countEachByProofType")
                .setParameter("sourceList", Arrays.asList(source))
                .getResultList();
        List<ElementCount<ProofType>> result = new ArrayList<>(objects.size());
        for (Object[] object : objects) {
            ProofType type = (ProofType) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(type, count == null ? 0 : count.intValue()));
        }

        return result;
    }

    /**
     * count proof by employee
     *
     * @param source
     * @return
     */
    public List<ElementCount<String>> countEachByEmployee(Source... source) {
        if (source == null || source.length == 0) {
            return Collections.EMPTY_LIST;
        }

        List<Object[]> objects = getEntityManager()
                .createNamedQuery("Proof.countEachByEmployee")
                .setParameter("sourceList", Arrays.asList(source))
                .getResultList();
        List<ElementCount<String>> result = new ArrayList<>(objects.size());
        for (Object[] object : objects) {
            String employee = (String) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(employee, count == null ? 0 : count.intValue()));
        }

        return result;
    }

    public void markAsCover(String userId, RealmEntity owner, String proofId) {
        getEntityManager()
                .createNamedQuery("Proof.markAsCover")
                .setParameter("userId", userId)
                .setParameter("owner", owner)
                .setParameter("id", proofId)
                .executeUpdate();
    }

    public List<Proof> listCover(String userId, RealmEntity owner) {
        return getEntityManager()
                .createNamedQuery("Proof.listCover", Proof.class)
                .setParameter("userId", userId)
                .setParameter("owner", owner)
                .getResultList();
    }
}
