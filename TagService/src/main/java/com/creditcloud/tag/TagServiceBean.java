/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.tag;

import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.tag.api.TagService;
import com.creditcloud.tag.entities.Tag;
import com.creditcloud.tag.entities.TagEntity;
import com.creditcloud.tag.entities.dao.TagDAO;
import com.creditcloud.tag.entities.dao.TagEntityDAO;
import com.creditcloud.tag.local.ApplicationBean;
import com.creditcloud.tag.utils.DTOUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author rooseek
 */
@Slf4j
@Remote
@Stateless
public class TagServiceBean implements TagService {

    @EJB
    ApplicationBean appBean;

    @EJB
    TagDAO tagDAO;

    @EJB
    TagEntityDAO entityDAO;

    @Override
    public com.creditcloud.tag.model.Tag saveTag(String clientCode, com.creditcloud.tag.model.Tag tag) {
        appBean.checkClientCode(clientCode);
        Tag result = tagDAO.getByName(tag.getRealm(), tag.getName());
        if (result != null) {
            //有只更新description和alias
            if (tag.getDescription() != null) {
                result.setDescription(tag.getDescription());
            }
            if (tag.getAlias() != null) {
                result.setAlias(tag.getAlias());
            }
            tagDAO.edit(result);
        } else {
            //没有则添加
            result = new Tag(tag.getRealm(), tag.getName(), tag.getAlias(), tag.getDescription());
            tagDAO.create(result);
        }
        return DTOUtils.getTagDTO(result);
    }

    @Override
    public void tag(String clientCode, com.creditcloud.model.misc.RealmEntity entity, boolean overwrite, Pair<Realm, String>... tags) {
        appBean.checkClientCode(clientCode);
        List<com.creditcloud.tag.model.Tag> tagList = new ArrayList<>(tags.length);
        for (Pair<Realm, String> pair : tags) {
            tagList.add(new com.creditcloud.tag.model.Tag(pair.getLeft(), pair.getRight(), null, null));
        }
        tag(clientCode, entity, overwrite, tagList);
    }

    @Override
    public void tag(String clientCode, com.creditcloud.model.misc.RealmEntity entity, boolean overwrite, com.creditcloud.tag.model.Tag... tags) {
        tag(clientCode, entity, overwrite, Arrays.asList(tags));
    }

    @Override
    public void tag(String clientCode, com.creditcloud.model.misc.RealmEntity entity, boolean overwrite, List<com.creditcloud.tag.model.Tag> tags) {
        appBean.checkClientCode(clientCode);
        List<Tag> tagList = new ArrayList<>();
        for (com.creditcloud.tag.model.Tag result : tags) {
            Tag tag = tagDAO.getByName(result.getRealm(), result.getName());
            if (tag == null) {
                //没有则添加
                tag = tagDAO.create(new Tag(result.getRealm(), result.getName(), result.getAlias(), result.getDescription()));
            } else {
                //有只更新description和alias
                if (result.getDescription() != null) {
                    tag.setDescription(result.getDescription());
                }
                if (result.getAlias() != null) {
                    tag.setAlias(result.getAlias());
                }
            }
            tagList.add(tag);
        }
        TagEntity tagEntity = entityDAO.getByRealmEntity(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(entity));
        if (tagEntity == null) {
            tagEntity = entityDAO.create(new TagEntity(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(entity), Collections.EMPTY_LIST));
        }

        if (overwrite) {
            //直接覆盖
            tagEntity.setTags(tagList);
        } else {
            //添加
            Collection<Tag> oldTags = tagEntity.getTags();
            if (oldTags == null) {
                tagEntity.setTags(tagList);
            } else {
                Set<Tag> newTags = new HashSet<>();
                newTags.addAll(oldTags);
                newTags.addAll(tagList);
                tagEntity.setTags(newTags);
            }
        }
        entityDAO.edit(tagEntity);
    }

    @Override
    public com.creditcloud.tag.model.TagEntity getTagEntity(String clientCode, com.creditcloud.model.misc.RealmEntity entity) {
        appBean.checkClientCode(clientCode);
        TagEntity tagEntity = entityDAO.getByRealmEntity(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(entity));
        return DTOUtils.getTagEntityDTO(tagEntity);
    }

    @Override
    public int countByTag(String clientCode, com.creditcloud.tag.model.Tag tag) {
        appBean.checkClientCode(clientCode);
        Tag result = tagDAO.getByName(tag.getRealm(), tag.getName());
        if (result == null) {
            return 0;
        }
        return entityDAO.countByTag(result);
    }

    @Override
    public int countByTagInRealm(String clientCode, com.creditcloud.tag.model.Tag tag, Realm realm) {
        appBean.checkClientCode(clientCode);
        appBean.checkClientCode(clientCode);
        Tag result = tagDAO.getByName(tag.getRealm(), tag.getName());
        if (result == null) {
            return 0;
        }
        return entityDAO.countByTagAndRealm(realm, result);
    }

    @Override
    public PagedResult<com.creditcloud.model.misc.RealmEntity> listByTag(String clientCode, com.creditcloud.tag.model.Tag modelTag, PageInfo pageInfo) {
        appBean.checkClientCode(clientCode);
        Tag tag = tagDAO.getByName(modelTag.getRealm(), modelTag.getName());
        if (tag != null) {
            PagedResult<RealmEntity> tagEntities = entityDAO.listByTag(tag, pageInfo);
            List<com.creditcloud.model.misc.RealmEntity> result = new ArrayList<>(tagEntities.getResults().size());
            for (RealmEntity entity : tagEntities.getResults()) {
                result.add(com.creditcloud.common.utils.DTOUtils.getRealmEntity(entity));
            }
            return new PagedResult<>(result, tagEntities.getTotalSize());
        }
        return new PagedResult<>(Collections.EMPTY_LIST, 0);
    }

    @Override
    public boolean checkTagExist(String clientCode, com.creditcloud.tag.model.Tag tag, com.creditcloud.model.misc.RealmEntity entity) {
        appBean.checkClientCode(clientCode);
        Tag result = tagDAO.getByName(tag.getRealm(), tag.getName());
        if (result == null) {
            return false;
        }
        return entityDAO.checkTagExist(result, com.creditcloud.common.utils.DTOUtils.convertRealmEntity(entity));
    }

    @Override
    public PagedResult<com.creditcloud.tag.model.Tag> listAllTagByRealm(String clientCode, Realm realm, PageInfo pageInfo) {
        appBean.checkClientCode(clientCode);
        PagedResult<Tag> tags = tagDAO.listByRealm(realm, pageInfo);
        List<com.creditcloud.tag.model.Tag> result = new ArrayList<>(tags.getResults().size());
        for (Tag tag : tags.getResults()) {
            result.add(DTOUtils.getTagDTO(tag));
        }
        return new PagedResult<>(result, tags.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.tag.model.Tag> listAllTags(String clientCode, PageInfo pageInfo) {
        appBean.checkClientCode(clientCode);
        PagedResult<Tag> tags = tagDAO.listAll(pageInfo);
        List<com.creditcloud.tag.model.Tag> result = new ArrayList<>(tags.getResults().size());
        for (Tag tag : tags.getResults()) {
            result.add(DTOUtils.getTagDTO(tag));
        }
        return new PagedResult<>(result, tags.getTotalSize());
    }

    @Override
    public com.creditcloud.tag.model.Tag getTag(String clientCode, Realm realm, String tagName) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getTagDTO(tagDAO.getByName(realm, tagName));
    }

    @Override
    public boolean checkCommonTags(String clientCode, List<com.creditcloud.model.misc.RealmEntity> entityList, com.creditcloud.tag.model.Tag tag) {
        appBean.checkClientCode(clientCode);
        if (entityList == null || entityList.isEmpty() || tag == null) {
            log.warn("both entityList and tag can not be null for checkCommonTags");
            return false;
        }
        List<String> ids = new ArrayList<>();
        for (com.creditcloud.model.misc.RealmEntity entity : entityList) {
            TagEntity tagEntity = entityDAO.getByRealmEntity(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(entity));
            if (tagEntity != null) {
                ids.add(tagEntity.getId());
            } else {
                log.warn("TagEntity for RealmEntity {} not exist for checkCommonTags.", entity);
                return false;
            }
        }
        Tag entityTag = tagDAO.getByName(tag.getRealm(), tag.getName());
        if (entityTag == null) {
            log.warn("can not find common tag {}.", tag);
            return false;
        }

        return entityDAO.checkCommonTag(ids, entityTag);
    }

    @Override
    public PagedResult<com.creditcloud.model.misc.RealmEntity> listByTagAndRealm(String clientCode, com.creditcloud.tag.model.Tag modelTag, Realm realm, PageInfo pageInfo) {
        appBean.checkClientCode(clientCode);
        Tag tag = tagDAO.getByName(modelTag.getRealm(), modelTag.getName());
        if (tag != null) {
            PagedResult<RealmEntity> tagEntities = entityDAO.listByTagAndRealm(realm, tag, pageInfo);
            List<com.creditcloud.model.misc.RealmEntity> result = new ArrayList<>(tagEntities.getResults().size());
            for (RealmEntity entity : tagEntities.getResults()) {
                result.add(com.creditcloud.common.utils.DTOUtils.getRealmEntity(entity));
            }
            return new PagedResult<>(result, tagEntities.getTotalSize());
        }
        return new PagedResult<>(Collections.EMPTY_LIST, 0);
    }

    @Override
    public List<com.creditcloud.tag.model.Tag> listTagByRealm(String clientCode, com.creditcloud.model.misc.RealmEntity entity, Realm realm) {
        appBean.checkClientCode(clientCode);
        List<Tag> tags = entityDAO.listTagByRealm(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(entity), realm);
        List<com.creditcloud.tag.model.Tag> result = new ArrayList<>(tags.size());
        for (Tag tag : tags) {
            result.add(DTOUtils.getTagDTO(tag));
        }
        return result;
    }

    @Override
    public void link(String clientCode,
                     com.creditcloud.model.misc.RealmEntity source,
                     com.creditcloud.model.misc.RealmEntity target,
                     boolean reverse,
                     boolean replaceLink,
                     boolean replaceReverseLink,
                     String alias,
                     String description) {
        appBean.checkClientCode(clientCode);
        long startTime = System.currentTimeMillis();
        //create or update link tag(s) first
        com.creditcloud.tag.model.Tag linkTag = new com.creditcloud.tag.model.Tag(target.getRealm(), target.getEntityId(), alias, description);
        com.creditcloud.tag.model.Tag reverseLinkTag = new com.creditcloud.tag.model.Tag(source.getRealm(), source.getEntityId(), alias, description);
        saveTag(clientCode, linkTag);
        if (reverse) {
            saveTag(clientCode, reverseLinkTag);
        }
        //remove current link on demand
        if (replaceLink) {
            unlinkAny(clientCode, source, target.getRealm());
        }
        if (replaceReverseLink) {
            unlinkAny(clientCode, target, source.getRealm());
        }
        //make the link
        tag(clientCode, source, false, linkTag);
        if (reverse) {
            tag(clientCode, target, false, reverseLinkTag);
        }
        log.debug("Linked target to source.[source={}][target={}][reverse={}][replaceLink={}][replaceReverseLink][duration={}]",
                  source,
                  target,
                  reverse,
                  replaceLink,
                  replaceReverseLink,
                  System.currentTimeMillis() - startTime);
    }

    @Override
    public void unlinkAny(String clientCode, com.creditcloud.model.misc.RealmEntity entity, Realm realm) {
        appBean.checkClientCode(clientCode);
        List<Tag> tags = entityDAO.listTagByRealm(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(entity), realm);
        TagEntity tagEntity = entityDAO.getByRealmEntity(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(entity));
        if (tags != null && !tags.isEmpty()) {
            tagEntity.getTags().removeAll(tags);
            entityDAO.edit(tagEntity);
        }
    }

    @Override
    public void unlink(String clientCode, com.creditcloud.model.misc.RealmEntity source, com.creditcloud.model.misc.RealmEntity target, boolean unlinkReverse) {
        appBean.checkClientCode(clientCode);
        TagEntity tagEntity = entityDAO.getByRealmEntity(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(source));
        Tag linkTag = null;
        for (Tag tag : tagEntity.getTags()) {
            if (tag.getRealm().equals(target.getRealm()) && tag.getName().equalsIgnoreCase(target.getEntityId())) {
                linkTag = tag;
                break;
            }
        }
        if (linkTag != null) {
            tagEntity.getTags().remove(linkTag);
            entityDAO.edit(tagEntity);
            log.debug("Unlinked.[source={}][target={}]", source, target);
        }
        if (unlinkReverse) {
            tagEntity = entityDAO.getByRealmEntity(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(target));
            linkTag = null;
            for (Tag tag : tagEntity.getTags()) {
                if (tag.getRealm().equals(source.getRealm()) && tag.getName().equalsIgnoreCase(source.getEntityId())) {
                    linkTag = tag;
                    break;
                }
            }
            if (linkTag != null) {
                tagEntity.getTags().remove(linkTag);
                entityDAO.edit(tagEntity);
                log.debug("Unlinked reverse.[source={}][target={}]", source, target);
            }
        }
    }

    @Override
    public com.creditcloud.tag.model.Tag refer(String clientCode, com.creditcloud.model.misc.RealmEntity source, Realm realm) {
        appBean.checkClientCode(clientCode);
        Tag result = null;
        List<Tag> tags = entityDAO.listTagByRealm(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(source), realm);
        if (tags == null || tags.isEmpty()) {
            log.debug("No referrence found.[source={}][realm={}]", source, realm);
            return null;
        }
        for (Tag tag : tags) {
            if (tag.getRealm().equals(realm)) {
                //返回最后一个对应的tag，即最后被标记上的
                if (result == null) {
                    result = tag;
                } else {
                    result = tag;
                    log.warn("Multiple referrence found.[source={}][realm={}][tag={}]", source, realm, tag);
                }
            }
        }
        return DTOUtils.getTagDTO(result);
    }
}
