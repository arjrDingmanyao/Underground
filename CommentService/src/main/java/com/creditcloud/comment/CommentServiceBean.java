/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.comment;

import com.creditcloud.comment.api.CommentService;
import com.creditcloud.comment.entity.Comment;
import com.creditcloud.comment.entity.dao.CommentDAO;
import com.creditcloud.comment.local.ApplicationBean;
import com.creditcloud.comment.utils.DTOUtils;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.misc.RealmEntity;
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
public class CommentServiceBean implements CommentService {

    @Inject
    Logger logger;

    @EJB
    CommentDAO commentDAO;

    @EJB
    ApplicationBean appBean;

    @Override
    public com.creditcloud.comment.Comment add(String clientCode, com.creditcloud.comment.Comment comment) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getCommentDTO(commentDAO.addNew(comment));
    }

    @Override
    public PagedResult<com.creditcloud.comment.Comment> listByOwner(String clientCode, RealmEntity owner, PageInfo pageInfo, CommentStatus... status) {
        PagedResult<Comment> comments = commentDAO.listByOwner(owner, pageInfo, status);
        List<com.creditcloud.comment.Comment> result = new ArrayList<>(comments.getResults().size());
        for (Comment comment : comments.getResults()) {
            result.add(DTOUtils.getCommentDTO(comment));
        }
        return new PagedResult<>(result, comments.getTotalSize());
    }

    @Override
    public int countByOwner(String clientCode, RealmEntity owner, CommentStatus... status) {
        return commentDAO.countByOwner(owner, status);
    }

    @Override
    public PagedResult<com.creditcloud.comment.Comment> listByReceiver(String clientCode, RealmEntity receiver, PageInfo pageInfo, CommentStatus... status) {
        PagedResult<Comment> comments = commentDAO.listByReceiver(receiver, pageInfo, status);
        List<com.creditcloud.comment.Comment> result = new ArrayList<>(comments.getResults().size());
        for (Comment comment : comments.getResults()) {
            result.add(DTOUtils.getCommentDTO(comment));
        }
        return new PagedResult<>(result, comments.getTotalSize());

    }

    @Override
    public int countByReceiver(String clientCode, RealmEntity receiver, CommentStatus... status) {
        return commentDAO.countByReceiver(receiver, status);
    }

    @Override
    public boolean markStatus(String clientCode, CommentStatus status, String... commentIds) {
        return commentDAO.markStatus(status, commentIds);
    }
}
