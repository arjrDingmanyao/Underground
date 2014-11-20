/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.corporation.utils;

import com.creditcloud.corporation.entities.Factoring;
import com.creditcloud.corporation.entities.FinanceCorporation;

/**
 *
 * @author rooseek
 */
public class DTOUtils {

    /**
     * handle Factoring
     *
     * @param factoring
     * @return
     */
    public static com.creditcloud.corporation.factoring.Factoring getFactoring(Factoring factoring) {
        com.creditcloud.corporation.factoring.Factoring result = null;
        if (factoring != null) {
            result = new com.creditcloud.corporation.factoring.Factoring(factoring.getId(),
                                                                         factoring.getFactoringCorporation(),
                                                                         factoring.getCentralBankRegisterNo(),
                                                                         factoring.getType(),
                                                                         factoring.getReceivables(),
                                                                         factoring.getFinanceCorporation(),
                                                                         factoring.getDebtDescription(),
                                                                         factoring.getDescription(),
                                                                         factoring.getAntiDescription(),
                                                                         factoring.getHistoryDescription());
            result.setTimeCreated(factoring.getTimeCreated());
            result.setTimeLastUpdated(factoring.getTimeLastUpdated());
        }
        return result;
    }

    public static Factoring convertFactoring(com.creditcloud.corporation.factoring.Factoring factoring) {
        Factoring result = null;
        if (factoring != null) {
            result = new Factoring(factoring.getId(),
                                   factoring.getFactoringCorporation(),
                                   factoring.getCentralBankRegisterNo(),
                                   factoring.getType(),
                                   factoring.getReceivables(),
                                   factoring.getFinanceCorporation(),
                                   factoring.getDebtDescription(),
                                   factoring.getDescription(),
                                   factoring.getAntiDescription(),
                                   factoring.getHistoryDescription());
        }
        return result;
    }

    /**
     * handle FinanceCorporation
     *
     * @param corporation
     * @return
     */
    public static com.creditcloud.corporation.factoring.FinanceCorporation getFinanceCorporation(FinanceCorporation corporation) {
        com.creditcloud.corporation.factoring.FinanceCorporation result = null;
        if (corporation != null) {
            result = new com.creditcloud.corporation.factoring.FinanceCorporation(corporation.getId(),
                                                                                  corporation.getName(),
                                                                                  corporation.getShortName(),
                                                                                  corporation.getOrgCode(),
                                                                                  corporation.getBusiCode(),
                                                                                  corporation.getTaxCode(),
                                                                                  corporation.getDescription(),
                                                                                  corporation.getBankLicense(),
                                                                                  corporation.getFactoringCorporation());
            result.setTimeCreated(corporation.getTimeCreated());
            result.setTimeLastUpdated(corporation.getTimeLastUpdated());
        }
        return result;
    }

    public static FinanceCorporation convertFinanceCorporation(com.creditcloud.corporation.factoring.FinanceCorporation corporation) {
        FinanceCorporation result = null;
        if (corporation != null) {
            result = new FinanceCorporation(corporation.getName(),
                                            corporation.getShortName(),
                                            corporation.getOrgCode(),
                                            corporation.getBusiCode(),
                                            corporation.getTaxCode(),
                                            corporation.getDescription(),
                                            corporation.getBankLicense(),
                                            corporation.getFactoringCorporation());
            result.setId(corporation.getId());
        }
        return result;
    }
}
