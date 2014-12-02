package com.creditcloud.contract.utils;

import org.apache.commons.lang3.ArrayUtils;

public class DTOUtils
{
  public static com.creditcloud.contract.ContractTemplate toModel(com.creditcloud.contract.entities.ContractTemplate template, boolean fetchContent)
  {
    com.creditcloud.contract.ContractTemplate result = null;
    if (template != null) {
      result = new com.creditcloud.contract.ContractTemplate(template.getId(), template.getName(), template.getType(), template.getTimeCreated(), false, fetchContent ? template.getContent() : ArrayUtils.EMPTY_BYTE_ARRAY);
    }
    return result;
  }
  
  public static com.creditcloud.contract.ContractSeal toModel(com.creditcloud.contract.entities.ContractSeal contractSeal, com.creditcloud.contract.entities.Seal seal)
  {
    com.creditcloud.contract.ContractSeal result = null;
    if (contractSeal != null) {
      result = new com.creditcloud.contract.ContractSeal(contractSeal.getId(), contractSeal.getPage(), contractSeal.getX(), contractSeal.getY(), toModel(seal), contractSeal.getContractTemplateEntity().getEntityId());
    }
    return result;
  }
  
  public static com.creditcloud.contract.Seal toModel(com.creditcloud.contract.entities.Seal seal)
  {
    com.creditcloud.contract.Seal result = null;
    if (seal != null) {
      result = new com.creditcloud.contract.Seal(seal.getId(), seal.getCode(), seal.getType(), seal.getContent().getBytes(), seal.getEntity().getEntityId(), null, null);
    }
    return result;
  }
  
  public static com.creditcloud.contract.ContractTemplate getContractTemplateDTO(com.creditcloud.contract.entities.ContractTemplate template, boolean fetchContent)
  {
    com.creditcloud.contract.ContractTemplate result = null;
    if (template != null) {
      result = new com.creditcloud.contract.ContractTemplate(template.getId(), template.getName(), template.getType(), template.getTimeCreated(), false, fetchContent ? template.getContent() : ArrayUtils.EMPTY_BYTE_ARRAY);
    }
    return result;
  }
}
