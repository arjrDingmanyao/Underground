package com.creditcloud.contract;

import com.creditcloud.contract.api.ContractTemplateService;
import com.creditcloud.contract.dao.ContractTemplateDAO;
import com.creditcloud.contract.local.ApplicationBean;
import com.creditcloud.contract.utils.DTOUtils;
import com.creditcloud.model.criteria.PageInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.slf4j.Logger;

@Remote
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.WRITE)
public class ContractTemplateServiceBean
  implements ContractTemplateService
{
  @Inject
  Logger logger;
  @EJB
  ApplicationBean appBean;
  @EJB
  ContractTemplateDAO contractTemplateDAO;
  private volatile boolean dirty = true;
  private Map<String, ContractTemplate> templateCache = new HashMap();
  
  @PostConstruct
  void init()
  {
    updateTemplateCache();
  }
  
  private void updateTemplateCache()
  {
    this.logger.debug("update contract template cache.");
    this.templateCache.clear();
    for (com.creditcloud.contract.entities.ContractTemplate template : this.contractTemplateDAO.listByClient(this.appBean.getClientCode(), PageInfo.ALL).getResults()) {
      this.templateCache.put(template.getId(), DTOUtils.toModel(template, true));
    }
    this.dirty = false;
  }
  
  @Lock(LockType.READ)
  public List<ContractTemplate> listAllTemplates(String clientCode)
  {
    long startTime = System.currentTimeMillis();
    this.appBean.checkClientCode(clientCode);
    this.logger.debug("listAllTemplates called by client {}", clientCode);
    if (this.dirty) {
      updateTemplateCache();
    }
    this.logger.debug("ListAllTemplates done in {}ms, return {} templates", Long.valueOf(System.currentTimeMillis() - startTime), Integer.valueOf(this.templateCache.size()));
    return new ArrayList(this.templateCache.values());
  }
  
  public void deleteById(String clientCode, String contractId)
  {
    this.contractTemplateDAO.deleteById(contractId, clientCode);
    this.dirty = true;
  }
  
  public ContractTemplate create(String clientCode, String name, ContractType type, boolean isDefault, byte[] content)
  {
    this.appBean.checkClientCode(clientCode);
    com.creditcloud.contract.entities.ContractTemplate template = new com.creditcloud.contract.entities.ContractTemplate();
    template.setName(name);
    template.setType(type == null ? ContractType.LOAN : type);
    template.setContent(content);
    template.setClientCode(clientCode);
    Date date = new Date();
    template.setTimeCreated(date);
    template = (com.creditcloud.contract.entities.ContractTemplate)this.contractTemplateDAO.create(template);
    if ((this.contractTemplateDAO.count() == 1) && (!template.isDefault()))
    {
      template.setDefault(true);
      this.contractTemplateDAO.edit(template);
    }
    this.dirty = true;
    return DTOUtils.toModel(template, true);
  }
  
  @Lock(LockType.READ)
  public ContractTemplate getById(String clientCode, String id, boolean fetchContent)
  {
    this.appBean.checkClientCode(clientCode);
    com.creditcloud.contract.entities.ContractTemplate template = this.contractTemplateDAO.getById(id, clientCode);
    if (template == null)
    {
      this.logger.warn("contract template {} not found!", id);
      return null;
    }
    if (fetchContent) {
      template.getContent();
    }
    return DTOUtils.toModel(template, fetchContent);
  }
  
  @Lock(LockType.READ)
  public ContractTemplate getDefault(String clientCode, ContractType type)
  {
    this.appBean.checkClientCode(clientCode);
    if (type == null) {
      type = ContractType.LOAN;
    }
    com.creditcloud.contract.entities.ContractTemplate template = this.contractTemplateDAO.getDefaultByType(clientCode, type);
    if (template == null)
    {
      this.logger.warn("default contract template not found!");
      if (this.contractTemplateDAO.countByType(clientCode, new ContractType[] { type }) > 0)
      {
        template = (com.creditcloud.contract.entities.ContractTemplate)this.contractTemplateDAO.listByType(clientCode, new ContractType[] { type }).get(0);
        template.setDefault(true);
        this.contractTemplateDAO.setDefault(clientCode, template.getId(), type);
      }
      else
      {
        return null;
      }
    }
    template.getContent();
    return DTOUtils.getContractTemplateDTO(template, true);
  }
  
  public void setDefault(String clientCode, String id, ContractType type)
  {
    ContractTemplate template = getById(clientCode, id, false);
    if (template != null) {
      this.contractTemplateDAO.setDefault(clientCode, id, type);
    } else {
      this.logger.warn("contract template {} not found!", id);
    }
    this.dirty = true;
  }
  
  public void renameById(String clientCode, String id, String rename)
  {
    this.appBean.checkClientCode(clientCode);
    com.creditcloud.contract.entities.ContractTemplate template = this.contractTemplateDAO.getById(id, clientCode);
    if (template == null)
    {
      this.logger.warn("contract template not found with id: {}", id);
      return;
    }
    if ((rename == null) || (rename.isEmpty()))
    {
      this.logger.warn("contract template name can not empty");
      return;
    }
    template.setName(rename);
    this.contractTemplateDAO.edit(template);
    this.dirty = true;
  }
  
  @Lock(LockType.READ)
  public List<ContractTemplate> listByType(String clientCode, ContractType... type)
  {
    this.appBean.checkClientCode(clientCode);
    List<com.creditcloud.contract.entities.ContractTemplate> templates = this.contractTemplateDAO.listByType(clientCode, type);
    List<ContractTemplate> result = new ArrayList(templates.size());
    for (com.creditcloud.contract.entities.ContractTemplate template : templates) {
      result.add(DTOUtils.toModel(template, true));
    }
    return result;
  }
}
