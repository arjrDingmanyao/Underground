/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.dao;

import com.creditcloud.common.entities.dao.AbstractDAO;
import com.creditcloud.common.entities.embedded.RealmEntity;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.ElementCount;
import com.creditcloud.model.constant.EmailConstant;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.Source;
import static com.creditcloud.model.enums.Source.BACK;
import static com.creditcloud.model.enums.Source.MOBILE;
import static com.creditcloud.model.enums.Source.WEB;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.validation.group.BackSourceCheck;
import com.creditcloud.model.validation.group.IndividualUserCheck;
import com.creditcloud.model.validation.group.MobileSourceCheck;
import com.creditcloud.model.validation.group.WebSourceCheck;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.utils.DTOUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Validator;
import javax.validation.groups.Default;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;

/**
 *
 * @author sobranie
 */
@Stateless
@LocalBean
public class UserDAO extends AbstractDAO<User> {

    private static final String USER_DEFAULT_PASSWORD = "password";

    @Inject
    Logger logger;

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public UserDAO() {
        super(User.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean checkMobile(String mobile) {
        Long count = (Long) getEntityManager()
                .createNamedQuery("User.getUserCountByMobile")
                .setParameter("mobile", mobile)
                .getSingleResult();
        return count == 0;
    }

    public boolean checkLoginName(String loginName) {
        Long count = (Long) getEntityManager()
                .createNamedQuery("User.getUserCountByLoginName")
                .setParameter("loginName", loginName)
                .getSingleResult();
        return count == 0;
    }
    
    public boolean checkLoginNameOrMobile(String loginNameOrMobile) {
        Long count = (Long) getEntityManager()
                .createNamedQuery("User.getUserCountByLoginNameOrMobile")
                .setParameter("loginNameOrMobile", loginNameOrMobile)
                .getSingleResult();
        return count == 0;
    }

    public boolean checkIdNumber(String idNumber) {
        Long count = (Long) getEntityManager()
                .createNamedQuery("User.getUserCountByIdNumber")
                .setParameter("idNumber", idNumber)
                .getSingleResult();
        return count == 0;
    }

    public boolean checkEmail(String email) {
        Long count = (Long) getEntityManager()
                .createNamedQuery("User.getUserCountByEmail")
                .setParameter("email", email)
                .getSingleResult();
        return count == 0;
    }

    public User findByUserId(String userId) {
        return find(userId);
    }

    public User findByLoginName(String loginName) {
        User result = null;
        try {
            result = (User) getEntityManager()
                    .createNamedQuery("User.findByLoginName")
                    .setParameter("loginName", loginName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            logger.info("user with loginName not found.[loginName={}]", loginName);
        }
        return result;
    }

    public User findByIdNumber(String idNumber) {
        User result = null;
        try {
            result = (User) getEntityManager()
                    .createNamedQuery("User.findByIdNumber")
                    .setParameter("idNumber", idNumber)
                    .getSingleResult();
        } catch (NoResultException nre) {
            logger.info("user with idNumber not found.[idNumber={}]", idNumber);
        }
        return result;
    }

    public User findByMobile(String mobile) {
        User result = null;
        try {
            result = (User) getEntityManager()
                    .createNamedQuery("User.findByMobile")
                    .setParameter("mobile", mobile)
                    .getSingleResult();
        } catch (NoResultException nre) {
            logger.info("user with mobile not found.[mobile={}]", mobile);
        }
        return result;
    }

    public User findByEmail(String email) {
        User result = null;
        if (EmailConstant.DEFAULT_EMAIL.equalsIgnoreCase(email)) {
            //skip default email
            logger.warn("skip default email {} when findByEmail called.", email);
            return result;
        }
        try {
            result = (User) getEntityManager()
                    .createNamedQuery("User.findByEmail")
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            logger.info("user with email not found.[email={}]", email);
        }
        return result;
    }

    private void validate(User user) {
        Class sourceClazz = Default.class;
        switch (user.getSource()) {
            case WEB:
                sourceClazz = WebSourceCheck.class;
                break;
            case BACK:
                sourceClazz = BackSourceCheck.class;
                break;
            case MOBILE:
                sourceClazz = MobileSourceCheck.class;
                break;
        }
        Class userClazz = Default.class;
        if (user.isEnterprise()) {
            userClazz = IndividualUserCheck.class;
        }
        getValidatorWrapper().validate(user, sourceClazz, userClazz);
    }

    public User addNew(User user) {
        user.setId(null);
        if (StringUtils.isEmpty(user.getPassphrase())) {    //未设置密码则设置默认密码
            user.password(USER_DEFAULT_PASSWORD);
        }

        try {
            validate(user);
            return create(user);
        } catch (InvalidException ex) {
            logger.warn("user {} is not valid!", user.toString(), ex);
        } catch (Exception ex) {
            logger.warn("Add new user failed!!!\n{}", user.toString(), ex);
        }
        return null;
    }

    public User updateUser(User user) {
        User result = find(user.getId());
        if (result != null) {
            try {
                /**
                 * TODO只有下列信息可以通过updateUser更改，其他信息不可更改
                 */
                result.setLastLoginDate(user.getLastLoginDate());
                result.setEmail(user.getEmail());
                result.setMobile(user.getMobile());
                //TODO 如果本次update name或IdNumber 为null,那么不修改original value
                if (user.getName() != null) {
                    result.setName(user.getName());
                }
                if (user.getIdNumber() != null) {
                    result.setIdNumber(user.getIdNumber().toUpperCase());
                }
                result.setLastModifiedBy(user.getLastModifiedBy());
                result.setEmployeeId(user.getEmployeeId());
                result.setLoginName(user.getLoginName());
                validate(result);
            } catch (Exception ex) {
                logger.warn("Add new user failed!!!\n{}", user.toString(), ex);
            }
        } else {
            //do nothing
            //persist is preferable than merge in EJB, call addUser if you want to insert
            logger.warn("Fail to update user, user not exist.[user={}]", user);
        }
        return result;
    }

    /**
     * 获取某个员工开户的用户数目
     *
     * @param employeeId
     * @param source
     * @return
     */
    public int countByEmployee(String employeeId, Source... source) {
        if (source == null || source.length == 0) {
            return 0;
        }
        Long result = getEntityManager()
                .createNamedQuery("User.countByEmployee", Long.class)
                .setParameter("employeeId", employeeId)
                .setParameter("sourceList", Arrays.asList(source))
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    /**
     * 获取某个员工开户的用户列表
     *
     * @param employeeId
     * @param info
     * @param source
     * @return
     */
    public PagedResult<User> listByEmployee(String employeeId, PageInfo info, Source... source) {
        if (source == null || source.length == 0) {
            return new PagedResult<>(Collections.EMPTY_LIST, 0);
        }
        Query query = getEntityManager()
                .createNamedQuery("User.listByEmployee", User.class)
                .setParameter("employeeId", employeeId)
                .setParameter("sourceList", Arrays.asList(source));
        query.setFirstResult(info.getOffset());
        query.setMaxResults(info.getSize());

        List<User> users = query.getResultList();
        int totalSize = countByEmployee(employeeId, source);

        return new PagedResult(users, totalSize);
    }

    /**
     * 统计client下有多少客户
     *
     * @param clientCode
     * @return
     */
    public int countByClient(String clientCode) {
        Long result = getEntityManager()
                .createNamedQuery("User.countByClient", Long.class)
                .setParameter("clientCode", clientCode)
                .getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    /**
     * 统计client下有多少disabled客户
     *
     * @param clientCode
     * @return
     */
    public int countDisabledUsersByClient(String clientCode) {
        Long result = getEntityManager()
                .createNamedQuery("User.countDisabledUsersByClient", Long.class)
                .setParameter("clientCode", clientCode)
                .getSingleResult();

        return result == null ? 0 : result.intValue();
    }

    public PagedResult<User> listByClient(String clientCode, PageInfo info) {
        Query query = getEntityManager()
                .createNamedQuery("User.listByClient", User.class)
                .setParameter("clientCode", clientCode);
        query.setFirstResult(info.getOffset());
        query.setMaxResults(info.getSize());

        List<User> users = query.getResultList();
        int totalSize = countByClient(clientCode);

        return new PagedResult(users, totalSize);
    }

    //list all disabled users by clientcode
    public PagedResult<User> listDisabledUsersByClient(String clientCode, PageInfo info) {
        Query query = getEntityManager()
                .createNamedQuery("User.listDisabledUsersByClient", User.class)
                .setParameter("clientCode", clientCode);
        query.setFirstResult(info.getOffset());
        query.setMaxResults(info.getSize());

        List<User> users = query.getResultList();
        int totalSize = countDisabledUsersByClient(clientCode);

        return new PagedResult(users, totalSize);
    }

    /**
     * 按用戶来源统计用户数
     *
     * @return
     */
    public List<ElementCount<Source>> countEachBySource() {
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("User.countEachBySource")
                .getResultList();
        List<ElementCount<Source>> result = new ArrayList<>(objects.size());
        for (Object[] object : objects) {
            Source source = (Source) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(source, count == null ? 0 : count.intValue()));
        }

        return result;
    }

    /**
     * 按经办员工统计用户数
     *
     * @param source
     * @return
     */
    public List<ElementCount<String>> countEachByEmployee(Source... source) {
        if (source == null || source.length == 0) {
            return Collections.EMPTY_LIST;
        }

        List<Object[]> objects = getEntityManager()
                .createNamedQuery("User.countEachByEmployee")
                .setParameter("sourceList", Arrays.asList(source))
                .getResultList();
        List<ElementCount<String>> result = new ArrayList<>(objects.size());
        for (Object[] object : objects) {
            String employee = (String) object[0];
            Long count = (Long) object[1];
            result.add(new ElementCount<>(employee, count == null ? 0 : count.intValue()));
        }

        return result;
    }

    /**
     * 按天统计注册用户数
     *
     * @param from
     * @param to
     * @return
     */
    public List<ElementCount<LocalDate>> dailyRegister(Date from, Date to) {
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("User.dailyRegister")
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
        List<ElementCount<LocalDate>> result = new ArrayList<>(objects.size());
        for (Object[] object : objects) {
            Date day = (Date) object[0];
            int count = ((Long) object[1]).intValue();
            result.add(new ElementCount<>(new LocalDate(day), count));
        }
        return result;
    }

    public int countByReferral(RealmEntity referral, Date from, Date to) {
        Long result = getEntityManager()
                .createNamedQuery("User.countByReferral", Long.class)
                .setParameter("referralEntity", referral)
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<User> listByReferral(RealmEntity referral, Date from, Date to, PageInfo pageInfo) {
        List<User> result = getEntityManager()
                .createNamedQuery("User.listByReferral", User.class)
                .setParameter("referralEntity", referral)
                .setParameter("from", from)
                .setParameter("to", to)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countByReferral(referral, from, to));
    }

    public List<ElementCount<com.creditcloud.model.misc.RealmEntity>> countAllByReferral(Date from, Date to, boolean all) {
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("User.countAllByReferral")
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("allUser", all)
                .getResultList();
        List<ElementCount<com.creditcloud.model.misc.RealmEntity>> result = new ArrayList<>(objects.size());
        for (Object[] object : objects) {
            RealmEntity entity = (RealmEntity) object[0];
            int count = ((Long) object[1]).intValue();
            result.add(new ElementCount<>(com.creditcloud.common.utils.DTOUtils.getRealmEntity(entity), count));
        }

        return result;
    }

    public Map<com.creditcloud.model.misc.RealmEntity, List<com.creditcloud.model.user.User>> listAllByReferral(Date from, Date to, boolean all) {
        List<Object[]> objects = getEntityManager()
                .createNamedQuery("User.listAllByReferral")
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("allUser", all)
                .getResultList();
        Map<RealmEntity, List<com.creditcloud.model.user.User>> map = new HashMap<>(objects.size());
        for (Object[] object : objects) {
            RealmEntity entity = (RealmEntity) object[0];
            User user = (User) object[1];
            com.creditcloud.model.user.User result = DTOUtils.getUserDTO(user);
            if (map.get(entity) == null) {
                List<com.creditcloud.model.user.User> list = new ArrayList<>();
                list.add(result);
                map.put(entity, list);
            } else {
                map.get(entity).add(result);
            }
        }

        Map<com.creditcloud.model.misc.RealmEntity, List<com.creditcloud.model.user.User>> result = new HashMap();
        for (Entry<RealmEntity, List<com.creditcloud.model.user.User>> entry : map.entrySet()) {
            result.put(com.creditcloud.common.utils.DTOUtils.getRealmEntity(entry.getKey()), entry.getValue());
        }

        return result;
    }

    public int countReferral(Date from, Date to) {
        Long result = getEntityManager()
                .createNamedQuery("User.countReferral", Long.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<RealmEntity> listReferral(Date from, Date to, PageInfo pageInfo) {
        List<RealmEntity> result = getEntityManager()
                .createNamedQuery("User.listReferral", RealmEntity.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countReferral(from, to));
    }

    public boolean markReferralRewarded(String... ids) {
        if (ids == null || ids.length == 0) {
            return true;
        }
        int result = getEntityManager()
                .createNamedQuery("User.markReferralRewarded")
                .setParameter("ids", Arrays.asList(ids))
                .executeUpdate();
        return result > 0;
    }

    public boolean markRegistryRewarded(String... ids) {
        if (ids == null || ids.length == 0) {
            return true;
        }
        int result = getEntityManager()
                .createNamedQuery("User.markRegistryRewarded")
                .setParameter("ids", Arrays.asList(ids))
                .executeUpdate();
        return result > 0;
    }

    public int countByRegisterDate(Date from, Date to, boolean allUser) {
        Long result = getEntityManager()
                .createNamedQuery("User.countByRegisterDate", Long.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("allUser", allUser)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<User> listByRegisterDate(Date from, Date to, PageInfo pageInfo, boolean allUser) {
        List<User> result = getEntityManager()
                .createNamedQuery("User.listByRegisterDate", User.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("allUser", allUser)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countByRegisterDate(from, to, allUser));
    }

    public int countByLoginDate(Date from, Date to) {
        Long result = getEntityManager()
                .createNamedQuery("User.countByLoginDate", Long.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
        return result == null ? 0 : result.intValue();
    }

    public PagedResult<User> listByLoginDate(Date from, Date to, PageInfo pageInfo) {
        List<User> result = getEntityManager()
                .createNamedQuery("User.listByLoginDate", User.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .setFirstResult(pageInfo.getOffset())
                .setMaxResults(pageInfo.getSize())
                .getResultList();
        return new PagedResult<>(result, countByLoginDate(from, to));
    }
}
