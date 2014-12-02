package com.creditcloud.contract.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.contract.ContractType;
import com.creditcloud.contract.entities.ContractTemplate;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class ContractTemplateDAO
  extends AbstractDAO<ContractTemplate>
{
  @Inject
  Logger logger;
  @PersistenceContext(unitName="ContractPU")
  private EntityManager em;
  
  public ContractTemplateDAO()
  {
    super(ContractTemplate.class);
  }
  
  protected EntityManager getEntityManager()
  {
    return this.em;
  }
  
  public PagedResult<ContractTemplate> listByClient(String clientCode, PageInfo info)
  {
    Query query = getEntityManager().createNamedQuery("ContractTemplate.listByClient", ContractTemplate.class).setParameter("clientCode", clientCode);
    

    query.setFirstResult(info.getOffset());
    query.setMaxResults(info.getSize());
    
    List<ContractTemplate> templates = query.getResultList();
    int totalSize = countByClient(clientCode);
    
    return new PagedResult(templates, totalSize);
  }
  
  public int countByClient(String clientCode)
  {
    Long result = (Long)getEntityManager().createNamedQuery("ContractTemplate.countByClient", Long.class).setParameter("clientCode", clientCode).getSingleResult();
    



    return result == null ? 0 : result.intValue();
  }
  
  public int countByClientAndType(String clientCode, ContractType... types)
  {
    if ((types == null) || (types.length == 0)) {
      return 0;
    }
    Long result = (Long)getEntityManager().createNamedQuery("ContractTemplate.countByClientAndType", Long.class).setParameter("clientCode", clientCode).setParameter("typeList", Arrays.asList(types)).getSingleResult();
    




    return result == null ? 0 : result.intValue();
  }
  
  public List<ContractTemplate> listByType(String clientCode, ContractType... types)
  {
    if ((types == null) || (types.length == 0)) {
      return Collections.EMPTY_LIST;
    }
    List<ContractTemplate> result = getEntityManager().createNamedQuery("ContractTemplate.listByType", ContractTemplate.class).setParameter("clientCode", clientCode).setParameter("typeList", Arrays.asList(types)).getResultList();
    



    return result;
  }
  
  public ContractTemplate getById(String id, String clientCode)
  {
    ContractTemplate result = null;
    try
    {
      result = (ContractTemplate)this.em.createNamedQuery("ContractTemplate.getById", ContractTemplate.class).setParameter("id", id).setParameter("clientCode", clientCode).getSingleResult();
    }
    catch (NoResultException ex)
    {
      this.logger.debug("No result found for contractTemplate with id {}", id, ex);
    }
    return result;
  }
  
  public void deleteById(String id, String clientCode)
  {
    if (getById(id, clientCode) != null) {
      removeById(id);
    } else {
      this.logger.warn("ContractTemplate is not available.[contractTemplateId={}]", id);
    }
  }
  
  public ContractTemplate getDefault(String clientCode)
  {
    ContractTemplate result = null;
    try
    {
      result = (ContractTemplate)this.em.createNamedQuery("ContractTemplate.getDefault", ContractTemplate.class).setParameter("clientCode", clientCode).getSingleResult();
    }
    catch (NoResultException ex)
    {
      this.logger.debug("No result found for default contractTemplate");
    }
    return result;
  }
  
  public ContractTemplate getDefaultByType(String clientCode, ContractType type)
  {
    ContractTemplate result = null;
    try
    {
      result = (ContractTemplate)this.em.createNamedQuery("ContractTemplate.getDefaultByType", ContractTemplate.class).setParameter("clientCode", clientCode).setParameter("type", type).getSingleResult();
    }
    catch (NoResultException ex)
    {
      this.logger.debug("No result found for default contractTemplate");
    }
    return result;
  }
  
  public int countByType(String clientCode, ContractType... types)
  {
    if ((types == null) || (types.length == 0)) {
      return 0;
    }
    Long result = (Long)getEntityManager().createNamedQuery("ContractTemplate.countByClientAndType", Long.class).setParameter("clientCode", clientCode).setParameter("typeList", Arrays.asList(types)).getSingleResult();
    




    return result == null ? 0 : result.intValue();
  }
  
  public void setDefault(String clientCode, String id, ContractType type)
  {
    getEntityManager().createNamedQuery("ContractTemplate.setDefault").setParameter("clientCode", clientCode).setParameter("id", id).setParameter("type", type).executeUpdate();
  }
}
