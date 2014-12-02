package com.creditcloud.contract.local;

import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.misc.RealmEntity;
import com.creditcloud.tag.api.TagService;
import com.creditcloud.tag.constants.CreditCloudTags;
import com.creditcloud.tag.model.Tag;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

@LocalBean
@Stateless
public class TagBridge
{
  @Inject
  Logger logger;
  @EJB
  TagService tagService;
  @EJB
  ApplicationBean appBean;
  
  public String getShadowBorrower(String requestId)
  {
    if (!this.appBean.isEnableShadowLoan()) {
      return null;
    }
    List<Tag> tags = this.tagService.listTagByRealm(this.appBean.getClientCode(), new RealmEntity(Realm.LOANREQUEST, requestId), Realm.SHADOW_BORROWER);
    if (tags.size() > 1) {
      this.logger.error("more than one shadow borrower {} found for loan request {}", Integer.valueOf(tags.size()), requestId);
    }
    if (tags.size() > 0) {
      return ((Tag)tags.get(0)).getName();
    }
    return null;
  }
  
  public boolean isLoanAgent(String userId)
  {
    return this.tagService.checkTagExist(this.appBean.getClientCode(), CreditCloudTags.LOAN_AGENT, new RealmEntity(Realm.USER, userId));
  }
}
