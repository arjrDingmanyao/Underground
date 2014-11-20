/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.tag.utils;

import com.creditcloud.tag.entities.Tag;
import com.creditcloud.tag.entities.TagEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rooseek
 */
public class DTOUtils {

    /**
     * handle Tag
     *
     * @param tag
     * @return
     */
    public static com.creditcloud.tag.model.Tag getTagDTO(Tag tag) {
        com.creditcloud.tag.model.Tag result = null;
        if (tag != null) {
            result = new com.creditcloud.tag.model.Tag(tag.getRealm(), tag.getName(), tag.getAlias(), tag.getDescription());
        }
        return result;
    }

    /**
     * handle TagEntity
     *
     * @param entity
     * @return
     */
    public static com.creditcloud.tag.model.TagEntity getTagEntityDTO(TagEntity entity) {
        com.creditcloud.tag.model.TagEntity result = null;
        if (entity != null) {
            List< com.creditcloud.tag.model.Tag> list = new ArrayList<>();
            for (Tag tag : entity.getTags()) {
                list.add(getTagDTO(tag));
            }
            result = new com.creditcloud.tag.model.TagEntity(entity.getId(),
                                                             com.creditcloud.common.utils.DTOUtils.getRealmEntity(entity.getEntity()),
                                                             list,
                                                             entity.getTimeRecorded());
        }
        return result;
    }
}
