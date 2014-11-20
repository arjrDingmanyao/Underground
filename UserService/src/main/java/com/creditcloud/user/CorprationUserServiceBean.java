/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user;

import com.creditcloud.common.bean.BaseBean;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.model.criteria.CriteriaInfo;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.user.api.CorporationUserService;
import com.creditcloud.model.user.corporation.CorporationType;
import com.creditcloud.user.api.UserService;
import com.creditcloud.user.entity.CorporationUser;
import com.creditcloud.user.entity.CorporationInfo;
import com.creditcloud.user.entity.dao.CorporationUserDAO;
import com.creditcloud.user.entity.dao.CorporationInfoDAO;
import com.creditcloud.user.entity.dao.UserDAO;
import com.creditcloud.user.local.ApplicationBean;
import com.creditcloud.user.utils.DTOUtils;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class CorprationUserServiceBean extends BaseBean implements CorporationUserService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    CorporationUserDAO corporationDAO;

    @EJB
    CorporationInfoDAO corporationInfoDAO;

    @EJB
    UserService userService;

    @EJB
    UserDAO userDAO;

    @Override
    public com.creditcloud.model.user.corporation.CorporationUser save(String clientCode, com.creditcloud.model.user.corporation.CorporationUser corporation) {
        appBean.checkClientCode(clientCode);

        try {
            getValidatorWrapper().tryValidate(corporation);
        } catch (InvalidException ex) {
            logger.error("corporation is not valid.\n{} \n{}", corporation.toString(), ex.getViolations());
            return null;
        } catch (Exception ex) {
            logger.error("Can't save corporation: {}", corporation == null ? "null" : corporation.toString(), ex);
            return null;
        }

        if (corporation.getUserId() == null || corporationDAO.find(corporation.getUserId()) == null) {
            com.creditcloud.model.user.User user = null;
            if (corporation.getUserId() != null) {
                //查看user是否已经存在
                user = userService.findByUserId(appBean.getClientCode(), corporation.getUserId());
            }
            if (user == null) {
                //不存在尝试添加user
                user = userService.addUser(appBean.getClientCode(), corporation.getUser());
            }
            if (user != null) {
                CorporationUser corporationUser = DTOUtils.convertCorporationDTO(corporation);
                corporationUser.setUser(userDAO.find(user.getId()));
                CorporationUser result = corporationDAO.create(corporationUser);
                if (result != null) {
                    //同时创建CorporationInfo
                    CorporationInfo info = new CorporationInfo();
                    info.setCorporation(result);
                    corporationInfoDAO.create(info);
                }
                return DTOUtils.getCorporationDTO(result);
            } else {
                logger.debug("fail to create user for corporation");
                return null;
            }
        }

        CorporationUser result = corporationDAO.find(corporation.getUserId());
        //更新user
        userService.updateUser(appBean.getClientCode(), corporation.getUser());
        //更新corporation
        result.setBusiCode(corporation.getBusiCode());
        result.setName(corporation.getName());
        result.setOrgCode(corporation.getOrgCode());
        result.setShortName(corporation.getShortName());
        result.setTaxCode(corporation.getTaxCode());
        result.setContractSealCode(corporation.getContractSealCode());
        result.setType(corporation.getType());
        result.setCategory(corporation.getCategory());
        result.setLegalPersonId(corporation.getLegalPersonId());
        result.setRtpo(corporation.isRtpo());
        corporationDAO.edit(result);

        // delete cache
        appBean.deleteCache(corporation.getUserId(), CacheConstant.KEY_PREFIX_USER_INFO);
        return DTOUtils.getCorporationDTO(result);
    }

    @Override
    public com.creditcloud.model.user.corporation.CorporationInfo updateInfo(String clientCode, com.creditcloud.model.user.corporation.CorporationInfo info) {
        appBean.checkClientCode(clientCode);

        try {
            getValidatorWrapper().tryValidate(info);
        } catch (InvalidException ex) {
            logger.error("CorporationInfo is not valid.\n{}", info.toString());
            return null;
        } catch (Exception ex) {
            logger.error("Can't save CorporationInfo: {}", info == null ? "null" : info.toString(), ex);
            return null;
        }

        if (info.getUserId() == null || corporationInfoDAO.find(info.getUserId()) == null) {
            return null;
        }

        CorporationInfo result = corporationInfoDAO.find(info.getUserId());
        if (result != null) {
            //只有以下項可以更改
            result.setAddress(info.getAddress());
            result.setBusinessScope(info.getBusinessScope());
            result.setContactEmail(info.getContactEmail());
            result.setContactPersion(info.getContactPersion());
            result.setContactPhone(info.getContactPhone());
            result.setDescription(info.getDescription());
            result.setRegisteredCapital(info.getRegisteredCapital());
            result.setRegisteredLocation(info.getRegisteredLocation());
            result.setTimeEstablished(info.getTimeEstablished());
            result.setUrl(info.getUrl());
            result.setBackground(info.getBackground());
            result.setCreditRank(info.getCreditRank());
            corporationInfoDAO.edit(result);
        } else {
            logger.warn("fail to find corporation info {}", info.getUserId());
        }

        // delete cache
        appBean.deleteCache(info.getUserId(), CacheConstant.KEY_PREFIX_USER_INFO);
        
        return DTOUtils.getCorporationInfoDTO(result);
    }

    @Override
    public com.creditcloud.model.user.corporation.CorporationUser getById(String clientCode, String corpId) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getCorporationDTO(corporationDAO.find(corpId));
    }

    @Override
    public com.creditcloud.model.user.corporation.CorporationInfo getInfoById(String clientCode, String id) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getCorporationInfoDTO(corporationInfoDAO.find(id));
    }

    @Override
    public com.creditcloud.model.user.corporation.CorporationUser getByName(String clientCode, String name) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getCorporationDTO(corporationDAO.getByName(name));
    }

    @Override
    public com.creditcloud.model.user.corporation.CorporationUser getByBusiCode(String clientCode, String busiCode) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getCorporationDTO(corporationDAO.getByBusiCode(busiCode));
    }

    @Override
    public com.creditcloud.model.user.corporation.CorporationUser getByTaxCode(String clientCode, String taxCode) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getCorporationDTO(corporationDAO.getByTaxCode(taxCode));
    }

    @Override
    public com.creditcloud.model.user.corporation.CorporationUser getByOrgCode(String clientCode, String orgCode) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getCorporationDTO(corporationDAO.getByOrgCode(orgCode));
    }

    @Override
    public PagedResult<com.creditcloud.model.user.corporation.CorporationUser> listByType(String clientCode, PageInfo info, CorporationType... type) {
        appBean.checkClientCode(clientCode);
        PagedResult<CorporationUser> corporations = corporationDAO.listByType(info, type);
        List<com.creditcloud.model.user.corporation.CorporationUser> result = new ArrayList<>(corporations.getResults().size());
        for (CorporationUser corporation : corporations.getResults()) {
            result.add(DTOUtils.getCorporationDTO(corporation));
        }
        return new PagedResult<>(result, corporations.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.model.user.corporation.CorporationUser> list(String clientCode, CriteriaInfo criteriaInfo) {
        appBean.checkClientCode(clientCode);
        PagedResult<CorporationUser> users = corporationDAO.list(criteriaInfo);
        List<com.creditcloud.model.user.corporation.CorporationUser> result = new ArrayList<>(users.getResults().size());
        for (CorporationUser user : users.getResults()) {
            result.add(DTOUtils.getCorporationDTO(user));
        }
        return new PagedResult<>(result, users.getTotalSize());
    }

    @Override
    public boolean isCorporationUser(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        return corporationDAO.find(userId) != null;
    }

    @Override
    public List<com.creditcloud.model.user.corporation.CorporationUser> listAll(String clientCode) {
        appBean.checkClientCode(clientCode);
        List<CorporationUser> users = corporationDAO.findAll();
        List<com.creditcloud.model.user.corporation.CorporationUser> result = new ArrayList<>(users.size());
        for (CorporationUser user : users) {
            result.add(DTOUtils.getCorporationDTO(user));
        }
        return result;
    }

    @Override
    public List<String> listLegalPerson(String clientCode) {
        appBean.checkClientCode(clientCode);
        return corporationDAO.listLegalPerson();
    }

    @Override
    public boolean checkLegalPerson(String clientCode, String legalPersonId) {
        appBean.checkClientCode(clientCode);
        return corporationDAO.countByLegalPerson(legalPersonId) > 0;
    }

    @Override
    public List<com.creditcloud.model.user.corporation.CorporationUser> listByLegalPerson(String clientCode, String legalPersonId) {
        appBean.checkClientCode(clientCode);
        List<CorporationUser> users = corporationDAO.listByLegalPerson(legalPersonId);
        List<com.creditcloud.model.user.corporation.CorporationUser> result = new ArrayList<>(users.size());
        for (CorporationUser user : users) {
            result.add(DTOUtils.getCorporationDTO(user));
        }
        return result;
    }

    @Override
    public List<com.creditcloud.model.user.corporation.CorporationUser> listByRtpo(String clientCode, Boolean rtpo) {
        appBean.checkClientCode(clientCode);
        List<CorporationUser> users = corporationDAO.listByRtpo(rtpo);
        List<com.creditcloud.model.user.corporation.CorporationUser> result = new ArrayList<>(users.size());
        for (CorporationUser user : users) {
            result.add(DTOUtils.getCorporationDTO(user));
        }
        return result;
    }
}
