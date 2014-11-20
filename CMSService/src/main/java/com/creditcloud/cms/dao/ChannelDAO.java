/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.cms.dao;

import com.creditcloud.cms.enums.Category;
import com.creditcloud.cms.entities.Channel;
import com.creditcloud.common.entities.dao.AbstractDAO;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class ChannelDAO extends AbstractDAO<Channel> {
    
    @Inject
    Logger logger;
    
    @PersistenceContext(unitName = "NewsPU")
    private EntityManager em;
    
    public ChannelDAO() {
        super(Channel.class);
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 保存栏目
     *
     * @param channel
     * @return
     */
    public Channel save(Channel channel) {
        if (channel.getId() == null || find(channel.getId()) == null) {
            return create(channel);
        }
        Channel result = find(channel.getId());
        result.setCategory(channel.getCategory());
        result.setName(channel.getName());
        result.setDescription(channel.getDescription());
        edit(result);
        return result;
    }

    /**
     * 根据分类查找栏目
     *
     * @param category
     * @return
     */
    public List<Channel> listByCategory(Category category) {
        return getEntityManager()
                .createNamedQuery("Channel.listByCategory", Channel.class)
                .setParameter("category", category)
                .getResultList();
    }
}
