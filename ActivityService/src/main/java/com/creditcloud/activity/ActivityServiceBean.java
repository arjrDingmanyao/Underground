/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.activity;

import com.creditcloud.activity.api.ActivityService;
import com.creditcloud.activity.entities.dao.ActivityDAO;
import com.creditcloud.activity.local.ApplicationBean;
import com.creditcloud.activity.utils.DTOUtils;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.misc.RealmEntity;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Asynchronous;
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
public class ActivityServiceBean implements ActivityService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    ActivityDAO activityDAO;

    @Asynchronous
    @Override
    public void addActivity(String clientCode, Activity activity) {
        appBean.checkClientCode(clientCode);
        com.creditcloud.activity.entities.Activity result = new com.creditcloud.activity.entities.Activity(DTOUtils.convertRealmEntity(activity.getPerformer()),
                                                                                                           activity.getType(),
                                                                                                           DTOUtils.convertRealmEntity(activity.getTarget()),
                                                                                                           activity.getDescription(),
                                                                                                           activity.getContent());
        activityDAO.create(result);
    }

    @Override
    public com.creditcloud.activity.Activity getById(String clientCode, String activityId) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getActivityDTO(activityDAO.find(activityId));
    }

    @Override
    public PagedResult<Activity> listByTarget(String clientCode, RealmEntity target, PageInfo pageInfo) {
        appBean.checkClientCode(clientCode);
        PagedResult<com.creditcloud.activity.entities.Activity> activities = activityDAO.listByTarge(DTOUtils.convertRealmEntity(target), pageInfo);
        List<com.creditcloud.activity.Activity> result = new ArrayList<>(activities.getResults().size());
        for (com.creditcloud.activity.entities.Activity activity : activities.getResults()) {
            result.add(DTOUtils.getActivityDTO(activity));
        }

        return new PagedResult<>(result, activities.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.activity.Activity> listByPerformer(String clientCode, RealmEntity performer, PageInfo pageInfo) {
        appBean.checkClientCode(clientCode);
        PagedResult<com.creditcloud.activity.entities.Activity> activities = activityDAO.listByPerformer(DTOUtils.convertRealmEntity(performer), pageInfo);
        List<com.creditcloud.activity.Activity> result = new ArrayList<>(activities.getResults().size());
        for (com.creditcloud.activity.entities.Activity activity : activities.getResults()) {
            result.add(DTOUtils.getActivityDTO(activity));
        }

        return new PagedResult<>(result, activities.getTotalSize());
    }
}
