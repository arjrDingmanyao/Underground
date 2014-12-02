package com.creditcloud.contract.entities;

import com.creditcloud.common.entities.ClientScopeEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.model.enums.misc.ContractSealType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="TB_SEAL")
public class Seal
  extends ClientScopeEntity
{
  private String code;
  @Enumerated(EnumType.STRING)
  @Column(name="TYPE", nullable=false)
  private ContractSealType type;
  @Lob
  private String content;
  @Column(nullable=false)
  private RealmEntity entity;
  
  public Seal() {}
  
  public Seal(String code, ContractSealType type, String content, RealmEntity entity)
  {
    this.code = code;
    this.type = type;
    this.content = content;
    this.entity = entity;
  }
  
  public String getCode()
  {
    return this.code;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public ContractSealType getType()
  {
    return this.type;
  }
  
  public void setType(ContractSealType type)
  {
    this.type = type;
  }
  
  public String getContent()
  {
    return this.content;
  }
  
  public void setContent(String content)
  {
    this.content = content;
  }
  
  public RealmEntity getEntity()
  {
    return this.entity;
  }
  
  public void setEntity(RealmEntity entity)
  {
    this.entity = entity;
  }
}
