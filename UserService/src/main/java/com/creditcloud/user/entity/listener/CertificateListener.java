/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.listener;

import com.creditcloud.user.entity.Certificate;
import com.creditcloud.user.entity.record.CertificateRecord;
import java.util.Date;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

/**
 * act as the listener handler for meaningful changes in Certificate
 *
 * @author rooseek
 */
public class CertificateListener extends DescriptorEventAdapter {

    @Override
    public void preUpdate(DescriptorEvent event) {
        Certificate original = (Certificate) event.getOriginalObject();
        Certificate current = (Certificate) event.getObject();

        if (!original.equals(current)) {
            CertificateRecord record = new CertificateRecord(current,
                                                             current.getStatus(),
                                                             current.getAuditor(),
                                                             current.getAuditInfo(),
                                                             current.getAssessment());
            current.getChangeRecord().add(record);
            current.setTimeLastUpdated(new Date());
        }
    }
}
