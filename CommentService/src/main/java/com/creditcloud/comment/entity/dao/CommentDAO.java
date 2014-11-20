/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.comment.entity.dao;

import com.creditcloud.comment.CommentStatus;
import com.creditcloud.comment.entity.Comment;
import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.utils.DTOUtils;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.misc.RealmEntity;
import java.util.Arrays;
import java.util.Collections;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ValidationException;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class CommentDAO extends AbstractDAO<Comment> {

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "CommentPU")
    private EntityManager em;

    public CommentDAO() {
        super(Comment.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * add new comment
     *
     * @param comment
     * @return
     */
    public Comment addNew(com.creditcloud.comment.Comment comment) {
        try {
            getValidatorWrapper().tryValidate(comment);
            Comment result = create(new Comment(comment.getClientCode(),
                                                comment.getParentId(),
                                                com.creditcloud.common.utils.DTOUtils.convertRealmEntity(comment.getOwner()),
                                                comment.getContent(),
                                                comment.getRealm(),
                                                comment.getSender(),
                                                comment.getReceiver(),
                                                comment.getStatus()));
            logger.debug("Comment added as: \n{}", result);
            return result;
        } catch (InvalidException ex) {
            logger.warn("Comment {} is not valid!", comment.toString(), ex);
        } catch (IllegalArgumentException | ValidationException ex) {
            logger.warn("Exception happend when add new Comment as:\n{}", comment, ex);
        }
        return null;
    }

    /**
     * count comments by owner
     *
     * @param owner
     * @param status
     * @return
     */
    public int countByOwner(RealmEntity owner, CommentStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Comment.countByOwner", Long.class)
                .setParameter("owner", DTOUtils.convertRealmEntity(owner))
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * list comment by owner
     *
     * @param owner
     * @param pageInfo
     * @param status
     * @return
     */
    public PagedResult<Comment> listByOwner(RealmEntity owner, PageInfo pageInfo, CommentStatus... status) {
        if (status == null || status.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        Query query = getEntityManager()
                .createNamedQuery("Comment.listByOwner", Comment.class)
                .setParameter("owner", DTOUtils.convertRealmEntity(owner))
                .setParameter("statusList", Arrays.asList(status));
        query.setFirstResult(pageInfo.getOffset());
        query.setMaxResults(pageInfo.getSize());

        int totalSize = countByOwner(owner, status);
        return new PagedResult<>(query.getResultList(), totalSize);
    }

    /**
     * count comments by receiver
     *
     * @param receiver
     * @param status
     * @return
     */
    public int countByReceiver(RealmEntity receiver, CommentStatus... status) {
        if (status == null || status.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("Comment.countByReceiver", Long.class)
                .setParameter("realm", receiver.getRealm())
                .setParameter("receiver", receiver.getEntityId())
                .setParameter("statusList", Arrays.asList(status))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * list comments by receiver
     *
     * @param receiver
     * @param pageInfo
     * @param status
     * @return
     */
    public PagedResult<Comment> listByReceiver(RealmEntity receiver, PageInfo pageInfo, CommentStatus... status) {
        if (status == null || status.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        Query query = getEntityManager()
                .createNamedQuery("Comment.listByReceiver", Comment.class)
                .setParameter("realm", receiver.getRealm())
                .setParameter("receiver", receiver.getEntityId())
                .setParameter("statusList", Arrays.asList(status));
        query.setFirstResult(pageInfo.getOffset());
        query.setMaxResults(pageInfo.getSize());

        int totalSize = countByReceiver(receiver, status);
        return new PagedResult<>(query.getResultList(), totalSize);
    }

    /**
     * mark status for comments
     *
     * @param status
     * @param commentIds
     * @return
     */
    public boolean markStatus(CommentStatus status, String... commentIds) {
        if (commentIds == null || commentIds.length == 0) {
            return true;
        }
        getEntityManager()
                .createNamedQuery("Comment.markStatus")
                .setParameter("status", status)
                .setParameter("commentIds", Arrays.asList(commentIds))
                .executeUpdate();
        return true;
    }
}
