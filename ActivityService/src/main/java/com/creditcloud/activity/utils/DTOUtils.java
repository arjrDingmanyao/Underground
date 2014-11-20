/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.activity.utils;

import com.creditcloud.activity.entities.Activity;

/**
 *
 * @author rooseek
 */
public class DTOUtils extends com.creditcloud.common.utils.DTOUtils {
    
    public static com.creditcloud.activity.Activity getActivityDTO(Activity activity) {
        com.creditcloud.activity.Activity result = null;
        if (activity != null) {
            result = new com.creditcloud.activity.Activity(activity.getId(),
                                                           getRealmEntity(activity.getPerformer()),
                                                           activity.getType(),
                                                           getRealmEntity(activity.getTarget()),
                                                           activity.getDescription(),
                                                           activity.getContent());
            result.setTimeRecorded(activity.getTimeRecorded());
        }
        return result;
    }
}
