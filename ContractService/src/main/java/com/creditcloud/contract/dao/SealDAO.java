package com.creditcloud.contract.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.contract.entities.Seal;
import com.creditcloud.model.enums.misc.ContractSealType;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@LocalBean
public class SealDAO
  extends AbstractDAO<Seal>
{
  @PersistenceContext(unitName="ContractPU")
  private EntityManager em;
  
  public SealDAO()
  {
    super(Seal.class);
  }
  
  protected EntityManager getEntityManager()
  {
    return this.em;
  }
  
  public Seal create(String code, ContractSealType type, String content, RealmEntity entity)
  {
    Seal seal = new Seal(code, ContractSealType.PERSONAL, content, entity);
    return (Seal)create(seal);
  }
}
