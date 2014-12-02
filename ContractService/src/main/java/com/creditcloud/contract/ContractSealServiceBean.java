package com.creditcloud.contract;

import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.contract.api.ContractSealService;
import com.creditcloud.contract.dao.ContractSealDAO;
import com.creditcloud.contract.dao.ContractTemplateDAO;
import com.creditcloud.contract.dao.SealDAO;
import com.creditcloud.contract.entities.ContractTemplate;
import com.creditcloud.contract.local.ApplicationBean;
import com.creditcloud.contract.utils.DTOUtils;
import com.creditcloud.model.criteria.CriteriaInfo;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.misc.ContractSealType;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.user.User;
import com.creditcloud.model.user.corporation.CorporationUser;
import com.creditcloud.user.api.CorporationUserService;
import com.creditcloud.user.api.UserService;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Remote
@Stateless
public class ContractSealServiceBean
  implements ContractSealService
{
  private static final Logger log = LoggerFactory.getLogger(ContractSealServiceBean.class);
  @EJB
  SealDAO sealDAO;
  @EJB
  ContractSealDAO contractSealDAO;
  @EJB
  ContractTemplateDAO templateDAO;
  @EJB
  UserService userService;
  @EJB
  CorporationUserService corporationUserService;
  @EJB
  ApplicationBean appBean;
  
  public ContractSeal createContractSeal(String clientCode, int page, int x, int y, String sealId, String templateId)
  {
    this.appBean.checkClientCode(clientCode);
    
    ContractTemplate template = this.templateDAO.getById(templateId, clientCode);
    if (template == null)
    {
      log.warn("contract template {} not found from database", templateId);
      return null;
    }
    com.creditcloud.contract.entities.Seal seal = (com.creditcloud.contract.entities.Seal)this.sealDAO.find(sealId);
    if (seal == null)
    {
      log.warn("contract seal {} not found from database", sealId);
      return null;
    }
    com.creditcloud.contract.entities.ContractSeal contractSeal = this.contractSealDAO.create(page, x, y, sealId, new RealmEntity(Realm.CONTRACTTEMPLATE, templateId));
    return DTOUtils.toModel(contractSeal, seal);
  }
  
  public Seal createSeal(String clientCode, String code, ContractSealType type, String content, String entityId)
  {
    this.appBean.checkClientCode(clientCode);
    
    com.creditcloud.contract.entities.Seal seal = null;
    if (type == ContractSealType.ENTERPRISE)
    {
      CorporationUser user = this.corporationUserService.getById(clientCode, entityId);
      if (user == null)
      {
        log.warn("createSeal corporationUser {} not found", user);
        return null;
      }
      seal = this.sealDAO.create(code, type, content, new RealmEntity(Realm.CORPORATIONUSER, entityId));
    }
    else if (type == ContractSealType.PERSONAL)
    {
      User user = this.userService.findByUserId(clientCode, entityId);
      if (user == null)
      {
        log.warn("createSeal user {} not found", user);
        return null;
      }
      seal = this.sealDAO.create(code, type, content, new RealmEntity(Realm.USER, entityId));
    }
    return DTOUtils.toModel(seal);
  }
  
  public ContractSeal getById(String clientCode, String contractSealId, boolean withSeal)
  {
    this.appBean.checkClientCode(clientCode);
    
    com.creditcloud.contract.entities.ContractSeal contractSeal = (com.creditcloud.contract.entities.ContractSeal)this.contractSealDAO.find(contractSealId);
    if (contractSeal == null)
    {
      log.warn("ContractSeal getById {} not found", contractSealId);
      return null;
    }
    com.creditcloud.contract.entities.Seal seal = null;
    if (withSeal) {
      seal = (com.creditcloud.contract.entities.Seal)this.sealDAO.find(contractSeal.getSealId());
    }
    if (seal == null) {
      log.warn("ContractSeal getSealById {} not found", seal);
    }
    return DTOUtils.toModel(contractSeal, seal);
  }
  
  public PagedResult<ContractSeal> list(PageInfo pageInfo, boolean withSeal)
  {
    PagedResult<com.creditcloud.contract.entities.ContractSeal> result = this.contractSealDAO.findAll(new CriteriaInfo(null, pageInfo, null));
    
    List<com.creditcloud.contract.entities.ContractSeal> entities = result.getResults();
    if (entities == null) {
      entities = new ArrayList();
    }
    List<ContractSeal> models = new ArrayList();
    for (com.creditcloud.contract.entities.ContractSeal entity : entities)
    {
      com.creditcloud.contract.entities.Seal seal = null;
      if (withSeal) {
        seal = (com.creditcloud.contract.entities.Seal)this.sealDAO.find(entity.getSealId());
      }
      models.add(DTOUtils.toModel(entity, seal));
    }
    return new PagedResult(models, this.contractSealDAO.count());
  }
  
  public PagedResult<Seal> listSeal(PageInfo pageInfo)
  {
    PagedResult<com.creditcloud.contract.entities.Seal> result = this.sealDAO.findAll(new CriteriaInfo(null, pageInfo, null));
    
    List<com.creditcloud.contract.entities.Seal> entities = result.getResults();
    if (entities == null) {
      entities = new ArrayList();
    }
    List<Seal> models = new ArrayList();
    for (com.creditcloud.contract.entities.Seal entity : entities) {
      models.add(DTOUtils.toModel(entity));
    }
    return new PagedResult(models, this.sealDAO.count());
  }
}
