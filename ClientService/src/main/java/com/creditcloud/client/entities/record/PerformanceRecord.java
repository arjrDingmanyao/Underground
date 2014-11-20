/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.client.entities.record;

import com.creditcloud.common.entities.RecordScopeEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author rooseek
 */
//@Entity
@Table(name = "TB_PERFORMANCE_RECORD")
public class PerformanceRecord extends RecordScopeEntity {

    public PerformanceRecord() {
    }
    
}
