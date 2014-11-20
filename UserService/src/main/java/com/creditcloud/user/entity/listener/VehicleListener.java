/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.listener;

import com.creditcloud.user.entity.Vehicle;
import com.creditcloud.user.entity.record.VehicleRecord;
import java.util.Date;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

/**
 *
 * @author rooseek
 */
public class VehicleListener extends DescriptorEventAdapter {

    @Override
    public void preUpdate(DescriptorEvent event) {
        Vehicle original = (Vehicle) event.getOriginalObject();
        Vehicle current = (Vehicle) event.getObject();

        if (!current.equals(original)) {
            VehicleRecord record = new VehicleRecord(current,
                                                     current.getModel(),
                                                     current.getType(),
                                                     current.getVehicleLicense(),
                                                     current.getPlateNumber(),
                                                     current.getYearOfPurchase(),
                                                     current.getPriceOfPurchase(),
                                                     current.getEstimatedValue(),
                                                     current.getDescription(),
                                                     current.getLastModifiedBy(),
                                                     current.getSource());
            current.getChangeRecord().add(record);
            current.setTimeLastUpdated(new Date());
        }
    }
}
