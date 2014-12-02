package com.creditcloud.contract.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.contract.entities.ContractSeal;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class ContractSealDAO
  extends AbstractDAO<ContractSeal>
{
  private static final Logger log = LoggerFactory.getLogger(ContractSealDAO.class);
  @PersistenceContext(unitName="ContractPU")
  private EntityManager em;
  
  public ContractSealDAO()
  {
    super(ContractSeal.class);
  }
  
  protected EntityManager getEntityManager()
  {
    return this.em;
  }
  
  public ContractSeal create(int page, int x, int y, String sealId, RealmEntity contractTemplateEntity)
  {
    ContractSeal contractSeal = new ContractSeal(page, x, y, sealId, contractTemplateEntity);
    return (ContractSeal)create(contractSeal);
  }
}
