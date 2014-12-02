package com.creditcloud.contract.entities;

import com.creditcloud.common.entities.ClientScopeEntity;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.contract.ContractParty;
import com.creditcloud.contract.ContractType;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="TB_CONTRACT")
@NamedQueries({@javax.persistence.NamedQuery(name="Contract.listByEntity", query="SELECT c FROM Contract c where c.clientCode = :clientCode and c.realmEntity = :realmEntity order by c.timeCreated desc"), @javax.persistence.NamedQuery(name="Contract.listByEntityAndType", query="SELECT c FROM Contract c where c.clientCode = :clientCode and c.realmEntity = :realmEntity and c.contractType = :contractType order by c.timeCreated desc")})
public class Contract
  extends ClientScopeEntity
{
  @NotNull
  @Column(nullable=false)
  private String name;
  @Column(nullable=false)
  @Enumerated(EnumType.STRING)
  private ContractType contractType;
  @Column(nullable=false)
  private RealmEntity realmEntity;
  @ElementCollection
  @CollectionTable(name="RF_CONTRACT_USER")
  @MapKeyEnumerated(EnumType.STRING)
  @MapKeyColumn(name="CONTRACT_PARTY")
  @Column(name="USERID")
  private Map<ContractParty, String> userIdRelated;
  @Column(nullable=false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date timeCreated;
  @Basic(fetch=FetchType.LAZY, optional=false)
  @Lob
  private byte[] content;
  
  public int hashCode()
  {
    int PRIME = 31;int result = 1;Object $name = getName();result = result * 31 + ($name == null ? 0 : $name.hashCode());Object $contractType = getContractType();result = result * 31 + ($contractType == null ? 0 : $contractType.hashCode());Object $realmEntity = getRealmEntity();result = result * 31 + ($realmEntity == null ? 0 : $realmEntity.hashCode());Object $userIdRelated = getUserIdRelated();result = result * 31 + ($userIdRelated == null ? 0 : $userIdRelated.hashCode());Object $timeCreated = getTimeCreated();result = result * 31 + ($timeCreated == null ? 0 : $timeCreated.hashCode());result = result * 31 + Arrays.hashCode(getContent());return result;
  }
  
  public boolean canEqual(Object other)
  {
    return other instanceof Contract;
  }
  
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Contract)) {
      return false;
    }
    Contract other = (Contract)o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$name = getName();Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
      return false;
    }
    Object this$contractType = getContractType();Object other$contractType = other.getContractType();
    if (this$contractType == null ? other$contractType != null : !this$contractType.equals(other$contractType)) {
      return false;
    }
    Object this$realmEntity = getRealmEntity();Object other$realmEntity = other.getRealmEntity();
    if (this$realmEntity == null ? other$realmEntity != null : !this$realmEntity.equals(other$realmEntity)) {
      return false;
    }
    Object this$userIdRelated = getUserIdRelated();Object other$userIdRelated = other.getUserIdRelated();
    if (this$userIdRelated == null ? other$userIdRelated != null : !this$userIdRelated.equals(other$userIdRelated)) {
      return false;
    }
    Object this$timeCreated = getTimeCreated();Object other$timeCreated = other.getTimeCreated();
    if (this$timeCreated == null ? other$timeCreated != null : !this$timeCreated.equals(other$timeCreated)) {
      return false;
    }
    return Arrays.equals(getContent(), other.getContent());
  }
  
  public String toString()
  {
    return "Contract(name=" + getName() + ", contractType=" + getContractType() + ", realmEntity=" + getRealmEntity() + ", userIdRelated=" + getUserIdRelated() + ", timeCreated=" + getTimeCreated() + ", content=" + Arrays.toString(getContent()) + ")";
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setContractType(ContractType contractType)
  {
    this.contractType = contractType;
  }
  
  public ContractType getContractType()
  {
    return this.contractType;
  }
  
  public RealmEntity getRealmEntity()
  {
    return this.realmEntity;
  }
  
  public void setRealmEntity(RealmEntity realmEntity)
  {
    this.realmEntity = realmEntity;
  }
  
  public Map<ContractParty, String> getUserIdRelated()
  {
    return this.userIdRelated;
  }
  
  public void setUserIdRelated(Map<ContractParty, String> userIdRelated)
  {
    this.userIdRelated = userIdRelated;
  }
  
  public Date getTimeCreated()
  {
    return this.timeCreated;
  }
  
  public void setTimeCreated(Date timeCreated)
  {
    this.timeCreated = timeCreated;
  }
  
  public void setContent(byte[] content)
  {
    this.content = content;
  }
  
  public byte[] getContent()
  {
    return this.content;
  }
}
