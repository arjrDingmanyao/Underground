/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.listener;

import com.creditcloud.user.entity.UserCredit;
import com.creditcloud.user.entity.record.UserCreditRecord;
import java.util.Date;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

/**
 *
 * @author rooseek
 */
public class UserCreditListener extends DescriptorEventAdapter {

    @Override
    public void preUpdate(DescriptorEvent event) {
        UserCredit original = (UserCredit) event.getOriginalObject();
        UserCredit current = (UserCredit) event.getObject();

        if (!current.equals(original)) {
            UserCreditRecord record = new UserCreditRecord(current,
                                                           current.getCreditRank(),
                                                           current.getAssessment(),
                                                           current.getCreditLimit(),
                                                           current.getCreditAvailable(),
                                                           current.getLastModifiedBy());

            current.getChangeRecord().add(record);
            current.setTimeLastUpdated(new Date());
        }
    }
}
