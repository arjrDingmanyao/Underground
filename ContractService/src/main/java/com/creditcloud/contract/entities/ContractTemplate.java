package com.creditcloud.contract.entities;

import com.creditcloud.common.entities.ClientScopeEntity;
import com.creditcloud.contract.ContractType;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="TB_CONTRACT_TEMPLATE")
@NamedQueries({@javax.persistence.NamedQuery(name="ContractTemplate.listByClient", query="select ct from ContractTemplate ct where ct.clientCode = :clientCode order by ct.timeCreated"), @javax.persistence.NamedQuery(name="ContractTemplate.listByType", query="select ct from ContractTemplate ct where ct.clientCode = :clientCode and ct.type in :typeList order by ct.timeCreated desc"), @javax.persistence.NamedQuery(name="ContractTemplate.countByClient", query="select count(ct) from ContractTemplate ct where ct.clientCode = :clientCode"), @javax.persistence.NamedQuery(name="ContractTemplate.countByClientAndType", query="select count(ct) from ContractTemplate ct where ct.clientCode = :clientCode and ct.type in :typeList"), @javax.persistence.NamedQuery(name="ContractTemplate.getById", query="select ct from ContractTemplate ct where ct.id = :id and ct.clientCode = :clientCode"), @javax.persistence.NamedQuery(name="ContractTemplate.getDefault", query=" select ct from ContractTemplate ct where ct.isDefault = true and ct.clientCode = :clientCode"), @javax.persistence.NamedQuery(name="ContractTemplate.getDefaultByType", query=" select ct from ContractTemplate ct where ct.isDefault = true and ct.clientCode = :clientCode and ct.type=:type"), @javax.persistence.NamedQuery(name="ContractTemplate.deleteById", query=" DELETE from ContractTemplate ct where ct.id = :id and ct.clientCode = :clientCode"), @javax.persistence.NamedQuery(name="ContractTemplate.setDefault", query="update ContractTemplate ct set ct.isDefault = (case when(ct.id = :id) then TRUE else FALSE end) where ct.clientCode = :clientCode and ct.type = :type")})
public class ContractTemplate
  extends ClientScopeEntity
{
  @NotNull
  @Column(nullable=false)
  private String name;
  @Enumerated(EnumType.STRING)
  private ContractType type;
  @NotNull
  @Column(nullable=false)
  private boolean isDefault;
  @Column(nullable=false, updatable=false)
  @Temporal(TemporalType.TIMESTAMP)
  protected Date timeCreated;
  @Basic(fetch=FetchType.LAZY, optional=false)
  @Lob
  private byte[] content;
  
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ContractTemplate)) {
      return false;
    }
    ContractTemplate other = (ContractTemplate)o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$name = getName();Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
      return false;
    }
    Object this$type = getType();Object other$type = other.getType();
    if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
      return false;
    }
    if (isDefault() != other.isDefault()) {
      return false;
    }
    Object this$timeCreated = getTimeCreated();Object other$timeCreated = other.getTimeCreated();
    if (this$timeCreated == null ? other$timeCreated != null : !this$timeCreated.equals(other$timeCreated)) {
      return false;
    }
    return Arrays.equals(getContent(), other.getContent());
  }
  
  public int hashCode()
  {
    int PRIME = 31;int result = 1;Object $name = getName();result = result * 31 + ($name == null ? 0 : $name.hashCode());Object $type = getType();result = result * 31 + ($type == null ? 0 : $type.hashCode());result = result * 31 + (isDefault() ? 1231 : 1237);Object $timeCreated = getTimeCreated();result = result * 31 + ($timeCreated == null ? 0 : $timeCreated.hashCode());result = result * 31 + Arrays.hashCode(getContent());return result;
  }
  
  public String toString()
  {
    return "ContractTemplate(name=" + getName() + ", type=" + getType() + ", isDefault=" + isDefault() + ", timeCreated=" + getTimeCreated() + ", content=" + Arrays.toString(getContent()) + ")";
  }
  
  public boolean canEqual(Object other)
  {
    return other instanceof ContractTemplate;
  }
  
  @ConstructorProperties({"name", "type", "isDefault", "timeCreated", "content"})
  public ContractTemplate(String name, ContractType type, boolean isDefault, Date timeCreated, byte[] content)
  {
    this.name = name;this.type = type;this.isDefault = isDefault;this.timeCreated = timeCreated;this.content = content;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setType(ContractType type)
  {
    this.type = type;
  }
  
  public boolean isDefault()
  {
    return this.isDefault;
  }
  
  public void setDefault(boolean isDefault)
  {
    this.isDefault = isDefault;
  }
  
  public void setTimeCreated(Date timeCreated)
  {
    this.timeCreated = timeCreated;
  }
  
  public Date getTimeCreated()
  {
    return this.timeCreated;
  }
  
  public byte[] getContent()
  {
    return this.content;
  }
  
  public void setContent(byte[] content)
  {
    this.content = content;
  }
  
  public ContractType getType()
  {
    return this.type == null ? ContractType.LOAN : this.type;
  }
  
  public ContractTemplate() {}
}
