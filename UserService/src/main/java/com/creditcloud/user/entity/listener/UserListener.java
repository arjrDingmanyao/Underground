/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.listener;

import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.record.UserRecord;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

/**
 *
 * @author rooseek
 */
public class UserListener extends DescriptorEventAdapter {

    @Override
    public void preUpdate(DescriptorEvent event) {
        User original = (User) event.getOriginalObject();
        User current = (User) event.getObject();
        if (!current.equals(original)) {
            UserRecord record = new UserRecord(current,
                                               current.getName(),
                                               current.getLoginName(),
                                               current.getIdNumber(),
                                               current.getMobile(),
                                               current.getEmail(),
                                               current.getLastModifiedBy());

            current.getChangeRecord().add(record);
        }
    }
}
