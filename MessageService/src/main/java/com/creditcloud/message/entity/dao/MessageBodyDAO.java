/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.message.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.message.entity.MessageBody;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.message.MessageType;
import com.creditcloud.model.misc.PagedResult;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rooseek
 */
@Stateless
@LocalBean
public class MessageBodyDAO extends AbstractDAO<MessageBody> {

    @PersistenceContext(unitName = "MessagePU")
    private EntityManager em;

    public MessageBodyDAO() {
        super(MessageBody.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int countByRealmAndType(List<Realm> realmList, List<MessageType> typeList) {
        Long result = getEntityManager()
                .createNamedQuery("MessageBody.countByRealmAndType", Long.class)
                .setParameter("realmList", realmList)
                .setParameter("typeList", typeList)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<MessageBody> listByRealmAndType(PageInfo pageInfo, List<Realm> realmList, List<MessageType> typeList) {
        List<MessageBody> result = getEntityManager()
                .createNamedQuery("MessageBody.listByRealmAndType", MessageBody.class)
                .setParameter("realmList", realmList)
                .setParameter("typeList", typeList)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countByRealmAndType(realmList, typeList));
    }
}
