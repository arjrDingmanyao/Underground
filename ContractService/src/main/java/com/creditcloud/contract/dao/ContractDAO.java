package com.creditcloud.contract.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.utils.DTOUtils;
import com.creditcloud.contract.ContractParty;
import com.creditcloud.contract.ContractType;
import com.creditcloud.model.misc.RealmEntity;
import com.creditcloud.model.user.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@LocalBean
public class ContractDAO
  extends AbstractDAO<com.creditcloud.contract.entities.Contract>
{
  @PersistenceContext(unitName="ContractPU")
  private EntityManager em;
  
  public ContractDAO()
  {
    super(com.creditcloud.contract.entities.Contract.class);
  }
  
  protected EntityManager getEntityManager()
  {
    return this.em;
  }
  
  public List<com.creditcloud.contract.entities.Contract> findByEntityAndType(String clientCode, RealmEntity realmEntity, ContractType contractType)
  {
    return getEntityManager().createNamedQuery("Contract.listByEntityAndType", com.creditcloud.contract.entities.Contract.class).setParameter("clientCode", clientCode).setParameter("realmEntity", DTOUtils.convertRealmEntity(realmEntity)).setParameter("contractType", contractType).getResultList();
  }
  
  public com.creditcloud.contract.entities.Contract addNew(com.creditcloud.contract.Contract contract)
  {
    com.creditcloud.contract.entities.Contract entity = new com.creditcloud.contract.entities.Contract();
    entity.setId(contract.getId());
    entity.setName(contract.getName());
    entity.setClientCode(contract.getClient().getCode());
    entity.setContractType(contract.getType());
    entity.setRealmEntity(DTOUtils.convertRealmEntity(contract.getEntity()));
    entity.setTimeCreated(contract.getTimeCreated());
    Map<ContractParty, String> users = new HashMap();
    for (Map.Entry<ContractParty, User> entry : contract.getUserRelated().entrySet()) {
      users.put(entry.getKey(), ((User)entry.getValue()).getId());
    }
    entity.setUserIdRelated(users);
    entity.setContent(contract.getContent());
    return (com.creditcloud.contract.entities.Contract)create(entity);
  }
}
