package com.creditcloud.contract.entities;

import com.creditcloud.common.entities.ClientScopeEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="TB_CONTRACT_SEAL")
public class ContractSeal
  extends ClientScopeEntity
{
  @Column(nullable=false)
  private int page;
  @Column(nullable=false)
  private int x;
  @Column(nullable=false)
  private int y;
  @Column(nullable=false)
  private String sealId;
  @Column(nullable=false)
  private RealmEntity contractTemplateEntity;
  
  public ContractSeal() {}
  
  public ContractSeal(int page, int x, int y, String sealId, RealmEntity contractTemplateEntity)
  {
    this.page = page;
    this.x = x;
    this.y = y;
    this.sealId = sealId;
    this.contractTemplateEntity = contractTemplateEntity;
  }
  
  public int getPage()
  {
    return this.page;
  }
  
  public void setPage(int page)
  {
    this.page = page;
  }
  
  public int getX()
  {
    return this.x;
  }
  
  public void setX(int x)
  {
    this.x = x;
  }
  
  public int getY()
  {
    return this.y;
  }
  
  public void setY(int y)
  {
    this.y = y;
  }
  
  public String getSealId()
  {
    return this.sealId;
  }
  
  public void setSealId(String sealId)
  {
    this.sealId = sealId;
  }
  
  public RealmEntity getContractTemplateEntity()
  {
    return this.contractTemplateEntity;
  }
  
  public void setContractTemplateEntity(RealmEntity contractTemplateEntity)
  {
    this.contractTemplateEntity = contractTemplateEntity;
  }
}
