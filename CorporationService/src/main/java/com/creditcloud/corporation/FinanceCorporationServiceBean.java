/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.corporation;

import com.creditcloud.corporation.api.FinanceCorporationService;
import com.creditcloud.corporation.dao.FinanceCorporationDAO;
import com.creditcloud.corporation.entities.FinanceCorporation;
import com.creditcloud.corporation.local.ApplicationBean;
import com.creditcloud.corporation.utils.DTOUtils;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class FinanceCorporationServiceBean implements FinanceCorporationService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    FinanceCorporationDAO financeCorporationDAO;

    @Override
    public com.creditcloud.corporation.factoring.FinanceCorporation save(String clientCode, com.creditcloud.corporation.factoring.FinanceCorporation financeCorporation) {
        appBean.checkClientCode(clientCode);
        if (financeCorporation == null) {
            logger.debug("financeCorporation shall not be null.");
            return null;
        }
        //not found, create new one
        if (financeCorporation.getId() == null || financeCorporationDAO.find(financeCorporation.getId()) == null) {
            FinanceCorporation entity = financeCorporationDAO.create(DTOUtils.convertFinanceCorporation(financeCorporation));
            com.creditcloud.corporation.factoring.FinanceCorporation result = DTOUtils.getFinanceCorporation(entity);
            logger.debug("create new finance corporation.\n {}", result);
            return result;
        }

        logger.debug("update finance corporation.\n{}", financeCorporation);
        FinanceCorporation result = financeCorporationDAO.find(financeCorporation.getId());
        result.setBankLicense(financeCorporation.getBankLicense());
        result.setBusiCode(financeCorporation.getBusiCode());
        result.setDescription(financeCorporation.getDescription());
        result.setFactoringCorporation(financeCorporation.getFactoringCorporation());
        result.setName(financeCorporation.getName());
        result.setOrgCode(financeCorporation.getOrgCode());
        result.setShortName(financeCorporation.getShortName());
        result.setTaxCode(financeCorporation.getTaxCode());
        result.setTimeLastUpdated(new Date());

        return DTOUtils.getFinanceCorporation(result);
    }

    @Override
    public com.creditcloud.corporation.factoring.FinanceCorporation getById(String clientCode, String id) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getFinanceCorporation(financeCorporationDAO.find(id));
    }

    @Override
    public PagedResult<com.creditcloud.corporation.factoring.FinanceCorporation> listByFactoringCorporation(String clientCode, String factoringCorporation, PageInfo info) {
        appBean.checkClientCode(clientCode);
        PagedResult<FinanceCorporation> corporations = financeCorporationDAO.listByFactoringCorporation(factoringCorporation, info);
        List<com.creditcloud.corporation.factoring.FinanceCorporation> result = new ArrayList<>(corporations.getResults().size());
        for (FinanceCorporation coporation : corporations.getResults()) {
            result.add(DTOUtils.getFinanceCorporation(coporation));
        }
        return new PagedResult<>(result, corporations.getTotalSize());
    }

    @Override
    public void delete(String clientCode, String financeCorporation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public com.creditcloud.corporation.factoring.FinanceCorporation getByBusiCode(String clientCode, String factoringCorporation, String busiCode) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getFinanceCorporation(financeCorporationDAO.getByBusiCode(factoringCorporation, busiCode));
    }

    @Override
    public com.creditcloud.corporation.factoring.FinanceCorporation getByTaxCode(String clientCode, String factoringCorporation, String taxCode) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getFinanceCorporation(financeCorporationDAO.getByTaxCode(factoringCorporation, taxCode));
    }

    @Override
    public com.creditcloud.corporation.factoring.FinanceCorporation getByOrgCode(String clientCode, String factoringCorporation, String orgCode) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getFinanceCorporation(financeCorporationDAO.getByOrgCode(factoringCorporation, orgCode));
    }

    @Override
    public com.creditcloud.corporation.factoring.FinanceCorporation getByBankLicense(String clientCode, String factoringCorporation, String bankLicense) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getFinanceCorporation(financeCorporationDAO.getByBankLicense(factoringCorporation, bankLicense));
    }

    @Override
    public com.creditcloud.corporation.factoring.FinanceCorporation getByName(String clientCode, String factoringCorporation, String name) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getFinanceCorporation(financeCorporationDAO.getByName(factoringCorporation, name));
    }

}
