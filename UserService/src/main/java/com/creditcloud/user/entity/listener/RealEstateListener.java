/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.listener;

import com.creditcloud.user.entity.RealEstate;
import com.creditcloud.user.entity.record.RealEstateRecord;
import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

/**
 * act as the listener handle for meaningful changes in RealEstate
 *
 * @author rooseek
 */
public class RealEstateListener extends DescriptorEventAdapter {

    @Override
    public void preUpdate(DescriptorEvent event) {
        RealEstate original = (RealEstate) event.getOriginalObject();
        RealEstate current = (RealEstate) event.getObject();

        if (!EqualsBuilder.reflectionEquals(original, current, true)) {
            RealEstateRecord record = new RealEstateRecord(current,
                                                           current.getType(),
                                                           current.getLocation(),
                                                           current.getArea(),
                                                           current.isLoan(),
                                                           current.getEstimatedValue(),
                                                           current.getDescription(),
                                                           current.getLastModifiedBy(),
                                                           current.getSource(),
                                                           current.getLongitude(),
                                                           current.getLatitude());
            current.getChangeRecord().add(record);
            current.setTimeLastUpdated(new Date());
        }
    }
}
