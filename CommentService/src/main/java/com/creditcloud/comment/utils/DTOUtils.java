/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.comment.utils;

import com.creditcloud.comment.entity.Comment;

/**
 *
 * @author rooseek
 */
public class DTOUtils {

    /**
     * handle Comment
     *
     * @param comment
     * @return
     */
    public static com.creditcloud.comment.Comment getCommentDTO(Comment comment) {
        com.creditcloud.comment.Comment result = null;
        if (comment != null) {
            result = new com.creditcloud.comment.Comment(comment.getClientCode(),
                                                         comment.getId(),
                                                         comment.getParentId(),
                                                         com.creditcloud.common.utils.DTOUtils.getRealmEntity(comment.getOwner()),
                                                         comment.getContent(),
                                                         comment.getRealm(),
                                                         comment.getSender(),
                                                         comment.getReceiver(),
                                                         comment.getStatus());
            result.setTimeRecorded(comment.getTimeRecorded());
        }
        return result;
    }

    public static Comment convertCommentDTO(com.creditcloud.comment.Comment comment) {
        Comment result = null;
        if (comment != null) {
            result = new Comment(comment.getClientCode(),
                                 comment.getParentId(),
                                 com.creditcloud.common.utils.DTOUtils.convertRealmEntity(comment.getOwner()),
                                 comment.getContent(),
                                 comment.getRealm(),
                                 comment.getSender(),
                                 comment.getReceiver(),
                                 comment.getStatus());
            result.setId(comment.getId());
        }
        return result;
    }
}
