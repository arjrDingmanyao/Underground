/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user;

import com.creditcloud.common.entities.embedded.LoginRecord;
import com.creditcloud.common.security.SecurityLevel;
import com.creditcloud.common.security.SecurityUtils;
import com.creditcloud.fund.api.UserAutoBidService;
import com.creditcloud.model.ElementCount;
import com.creditcloud.model.client.Client;
import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.model.constant.LoginConstant;
import com.creditcloud.model.criteria.CriteriaInfo;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.enums.LoginResult;
import com.creditcloud.model.enums.Source;
import static com.creditcloud.model.enums.Source.MOBILE;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.model.misc.RealmEntity;
import com.creditcloud.model.user.UserLoginResult;
import com.creditcloud.sms.SMSType;
import com.creditcloud.sms.api.SMSService;
import com.creditcloud.user.api.UserService;
import com.creditcloud.user.entity.SocialUser;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.dao.LoginRecordDAO;
import com.creditcloud.user.entity.dao.SocialUserDAO;
import com.creditcloud.user.entity.dao.UserAuthenticateDAO;
import com.creditcloud.user.entity.dao.UserDAO;
import com.creditcloud.user.entity.embedded.SocialId;
import com.creditcloud.user.entity.record.UserLoginRecord;
import com.creditcloud.user.local.ApplicationBean;
import com.creditcloud.user.local.UserLocalBean;
import com.creditcloud.user.social.ConnectSocialResult;
import com.creditcloud.user.utils.DTOUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 *
 * @author rooseek
 */
@Stateless
@Remote
public class UserServiceBean implements UserService {

    @Inject
    Logger logger;

    @EJB
    UserDAO userDAO;

    @EJB
    SocialUserDAO socialUserDAO;

    @EJB
    UserLocalBean userBean;

    @EJB
    ApplicationBean appBean;

    @EJB
    SMSService smsService;

    @EJB
    LoginRecordDAO loginRecordDAO;

    @EJB
    UserAuthenticateDAO authenticateDAO;

    @EJB
    UserAutoBidService autoBidService;

    @Override
    public List<com.creditcloud.model.user.User> listUsersByPage(String clientCode, PageInfo pageInfo) {
	long startTime = System.currentTimeMillis();
	appBean.checkClientCode(clientCode);
	//logger.debug("ListAllUsers called by client {}", clientCode);
	PagedResult<User> users = userDAO.listByClient(clientCode, pageInfo);
	List<com.creditcloud.model.user.User> result = new ArrayList<>(users.getResults().size());
	for (User user : users.getResults()) {
	    result.add(DTOUtils.getUserDTO(user));
	}
	logger.debug("ListAllUsers done in {}ms, return {} users", System.currentTimeMillis() - startTime, result.size());
	return result;
    }

    @Override
    public List<com.creditcloud.model.user.User> listAllUsers(String clientCode) {
	long startTime = System.currentTimeMillis();
	appBean.checkClientCode(clientCode);
	//logger.debug("ListAllUsers called by client {}", clientCode);
	PagedResult<User> users = userDAO.listByClient(clientCode, PageInfo.ALL);
	List<com.creditcloud.model.user.User> result = new ArrayList<>(users.getResults().size());
	for (User user : users.getResults()) {
	    result.add(DTOUtils.getUserDTO(user));
	}
	logger.debug("ListAllUsers done in {}ms, return {} users", System.currentTimeMillis() - startTime, result.size());
	return result;
    }

    @Override
    public PagedResult<com.creditcloud.model.user.User> listUsers(String clientCode, CriteriaInfo criteriaInfo) {
	appBean.checkClientCode(clientCode);
	//logger.debug("List user for client.[client={}][criteria={}]", clientCode, criteriaInfo);
	PagedResult<User> users = userDAO.list(criteriaInfo);
	List<com.creditcloud.model.user.User> result = new ArrayList<>(users.getResults().size());
	for (User user : users.getResults()) {
	    result.add(DTOUtils.getUserDTO(user));
	}
	return new PagedResult<>(result, users.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.model.user.User> listByEmployee(String clientCode, String employeeId, PageInfo info, Source... source) {
	appBean.checkClientCode(clientCode);
	logger.debug("List user for employee.[clientCode={}][employeeId={}][source={}]", clientCode, employeeId, Arrays.asList(source));
	PagedResult<User> users = userDAO.listByEmployee(employeeId, info, source);
	List<com.creditcloud.model.user.User> result = new ArrayList<>(users.getResults().size());
	for (User user : users.getResults()) {
	    result.add(DTOUtils.getUserDTO(user));
	}

	return new PagedResult<>(result, users.getTotalSize());
    }

    @Override
    public com.creditcloud.model.user.User addUser(String clientCode, com.creditcloud.model.user.User user) {
	appBean.checkClientCode(clientCode);
	String password;
	if (appBean.defaultPassword() == null) {
	    //生成随机密码发送到用户手机
	    password = SecurityUtils.randomPassword(SecurityLevel.GOOD);
	} else {
	    password = appBean.defaultPassword();
	}
	com.creditcloud.model.user.User result = DTOUtils.getUserDTO(addUser(clientCode, user, password, true));
	if (appBean.defaultPassword() == null && result != null) {
	    //生成随机密码发送到用户手机
	    Client client = appBean.getClient();
	    smsService.sendMessage(client,
		    SMSType.NOTIFICATION_CREDITMARKET_USER_CREATED,
		    result.getMobile(),
		    client.getShortName(),
		    result.getLoginName(),
		    password);
	}

	return result;
    }

    @Override
    public com.creditcloud.model.user.User addUser(String clientCode,
	    com.creditcloud.model.user.User user,
	    String password,
	    Map<String, String> loginInfo,
	    Source source) {
	appBean.checkClientCode(clientCode);
	User result = addUser(clientCode, user, password, false);
	/**
	 * 对于前端开户的用户，注册成功后将直接进入页面，需要记录一次登录行为
	 */
	if (result != null && Source.WEB == source) {
	    loginRecordDAO.create(new UserLoginRecord(result,
		    new LoginRecord(user.getLoginName(),
			    new Date(),
			    getLoginInfo(loginInfo, source),
			    source,
			    true)));
	}
	return DTOUtils.getUserDTO(result);
    }

    private User addUser(String clientCode, com.creditcloud.model.user.User user, String password, boolean needChangePassword) {
	appBean.checkClientCode(clientCode);
	logger.debug("Adding user for client.[client={}][user={}]", clientCode, user);
	User entityUser = DTOUtils.convertUserDTO(user);
	entityUser.setLastLoginDate(new Date());
	entityUser.password(password);
	entityUser.setNeedChangePassword(needChangePassword);
	return userBean.createUser(entityUser);
    }

    /**
     * 增加Social类User
     *
     * @param clientCode
     * @param user
     * @param socialId
     * @param socialInfo
     * @return
     */
    @Override
    public com.creditcloud.model.user.User addUser(String clientCode,
	    com.creditcloud.model.user.User user,
	    com.creditcloud.user.social.SocialId socialId,
	    Map<String, String> socialInfo) {
	appBean.checkClientCode(clientCode);
	if (checkIdNumber(clientCode, user.getIdNumber())
		&& checkMobile(clientCode, user.getMobile())
		&& checkLoginName(clientCode, user.getLoginName())) {
	    logger.debug("Create SocialUser for client.[clientCode={}][socialId={}][socialType={}]",
		    clientCode,
		    socialId.getId(),
		    socialId.getType());
	    //生成用户
	    User entityUser = DTOUtils.convertUserDTO(user);
	    entityUser.setPassphrase("nopassphrase");
	    entityUser.setSalt("nosalt");
	    entityUser = userBean.createUser(entityUser);
	    //生成SocialUser
	    SocialUser socialUser = new SocialUser(DTOUtils.convertSocialIdDTO(socialId), entityUser, new Date());
	    socialUser = userBean.createSocialUser(socialUser);
	    return DTOUtils.getUserDTO(socialUser.getUser());
	} else {
	    logger.warn("User already exists, try connect socialId with user instead.[clientCode={}][loginName={}][mobile={}][idNumber={}]",
		    clientCode,
		    user.getLoginName(),
		    user.getMobile(),
		    user.getIdNumber());
	    return null;
	}
    }

    @Override
    public com.creditcloud.model.user.User updateUser(String clientCode, com.creditcloud.model.user.User user) {
	appBean.checkClientCode(clientCode);
	logger.debug("Update user for client.[client={}][user={}]", clientCode, user);

	com.creditcloud.model.user.User result = DTOUtils.getUserDTO(userDAO.updateUser(DTOUtils.convertUserDTO(user)));

	// clear cache
	appBean.deleteCache(user.getId(), CacheConstant.KEY_PREFIX_USER_ACCOUNT);
	return result;
    }

    @Override
    public com.creditcloud.model.user.User findByLoginName(String clientCode, String loginName) {
	appBean.checkClientCode(clientCode);
	return DTOUtils.getUserDTO(userDAO.findByLoginName(loginName));
    }

    @Override
    public com.creditcloud.model.user.User findByUserId(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	return DTOUtils.getUserDTO(userDAO.findByUserId(userId));
    }

    @Override
    public com.creditcloud.model.user.User findByMobile(String clientCode, String mobile) {
	appBean.checkClientCode(clientCode);
	return DTOUtils.getUserDTO(userDAO.findByMobile(mobile));
    }

    @Override
    public com.creditcloud.model.user.User findByIdNumber(String clientCode, String idNumber) {
	appBean.checkClientCode(clientCode);
	return DTOUtils.getUserDTO(userDAO.findByIdNumber(idNumber.toUpperCase()));
    }

    @Override
    public com.creditcloud.model.user.User findByEmail(String clientCode, String email) {
	appBean.checkClientCode(clientCode);
	return DTOUtils.getUserDTO(userDAO.findByEmail(email));
    }

    @Override
    public void deleteByUserId(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	logger.debug("Delete user by user id.[userId={}]", userId);
	userDAO.removeById(userId);
    }

    @Override
    public boolean checkMobile(String clientCode, String mobile) {
	appBean.checkClientCode(clientCode);
	return userDAO.checkMobile(mobile);
    }

    @Override
    public boolean checkIdNumber(String clientCode, String idNumber) {
	appBean.checkClientCode(clientCode);
	return userDAO.checkIdNumber(idNumber.toUpperCase());
    }

    @Override
    public boolean checkLoginName(String clientCode, String loginName) {
	appBean.checkClientCode(clientCode);
	return userDAO.checkLoginName(loginName);
    }

    @Override
    public boolean checkEmail(String clientCode, String email) {
	appBean.checkClientCode(clientCode);
	return userDAO.checkEmail(email);
    }

    @Override
    public UserLoginResult login(String clientCode,
	    String loginName,
	    String password,
	    Map<String, String> loginInfo,
	    Source source) {
	appBean.checkClientCode(clientCode);
	User user = userDAO.findByLoginName(loginName);
	if (user == null) {
	    logger.warn("user with loginName not exist.[loginName={}]", loginName);
	    loginRecordDAO.create(new UserLoginRecord(null, new LoginRecord(loginName,
		    new Date(),
		    getLoginInfo(loginInfo, source),
		    source,
		    false)));
	    return new UserLoginResult(LoginResult.FAILED, null, 0);
	}
	//check user enabled/disabled
	if (!user.isEnabled()) {
	    return new UserLoginResult(LoginResult.USER_DISABLED,
		    null,
		    loginRecordDAO.countFailedLoginByUser(user.getId(),
			    user.getLastLoginDate()));
	}

	//match loginName/password
	if (SecurityUtils.matchPassphrase(user.getPassphrase(), user.getSalt(), password)) {
	    //update last login time
	    user.setLastLoginDate(new Date());

	    //add login record
	    loginRecordDAO.create(new UserLoginRecord(user,
		    new LoginRecord(loginName,
			    new Date(),
			    getLoginInfo(loginInfo, source),
			    source,
			    true)));
	    if (user.isNeedChangePassword()) {
		return new UserLoginResult(LoginResult.NEED_CHANGE_PASSWORD, DTOUtils.getUserDTO(user), 0);
	    } else {
		return new UserLoginResult(LoginResult.SUCCESSFUL, DTOUtils.getUserDTO(user), 0);
	    }
	} else {
	    //TODO: 过多次登陆失败应该采取措施，比如封禁用户登陆，给相关邮箱、手机发送异常通知等
	    loginRecordDAO.create(new UserLoginRecord(user,
		    new LoginRecord(loginName,
			    new Date(),
			    getLoginInfo(loginInfo, source),
			    source,
			    false)));
	    int failedAttempts = loginRecordDAO.countFailedLoginByUser(user.getId(),
		    user.getLastLoginDate());
	    logger.debug("Login failed {} times since {}.[userId={}]",
		    failedAttempts,
		    user.getLastLoginDate(),
		    user.getId());
	    if (failedAttempts > LoginConstant.MAX_ATTEMPTS) {
		//TODO:disable user method here
		disableUser(clientCode, user.getId());
		logger.warn("User disabled due to too many failed login attempts.[userId={}]", user.getId());
		return new UserLoginResult(LoginResult.TOO_MANY_ATTEMPT, null, failedAttempts);
	    } else {
		return new UserLoginResult(LoginResult.FAILED, null, failedAttempts);
	    }
	}
    }

    @Override
    public UserLoginResult loginMobile(String clientCode,
	    String mobile,
	    String password,
	    Map<String, String> loginInfo,
	    Source source) {
	appBean.checkClientCode(clientCode);
	User user = userDAO.findByMobile(mobile);
	if (user == null) {
	    logger.warn("User with mobile not exist.[mobile={}]", mobile);
	    loginRecordDAO.create(new UserLoginRecord(null, new LoginRecord(mobile,
		    new Date(),
		    getLoginInfo(loginInfo, source),
		    source,
		    false)));
	    return new UserLoginResult(LoginResult.FAILED, null, 0);
	}
	//check user enabled/disabled
	if (!user.isEnabled()) {
	    return new UserLoginResult(LoginResult.USER_DISABLED,
		    null,
		    loginRecordDAO.countFailedLoginByUser(user.getId(),
			    user.getLastLoginDate()));
	}

	//match loginName/password
	if (SecurityUtils.matchPassphrase(user.getPassphrase(), user.getSalt(), password)) {
	    //update last login time
	    user.setLastLoginDate(new Date());

	    //add login record
	    loginRecordDAO.create(new UserLoginRecord(user,
		    new LoginRecord(mobile,
			    new Date(),
			    getLoginInfo(loginInfo, source),
			    source,
			    true)));
	    if (user.isNeedChangePassword()) {
		return new UserLoginResult(LoginResult.NEED_CHANGE_PASSWORD, DTOUtils.getUserDTO(user), 0);
	    } else {
		return new UserLoginResult(LoginResult.SUCCESSFUL, DTOUtils.getUserDTO(user), 0);
	    }
	} else {
	    loginRecordDAO.create(new UserLoginRecord(user,
		    new LoginRecord(mobile,
			    new Date(),
			    getLoginInfo(loginInfo, source),
			    source,
			    false)));
	    int failedAttempts = loginRecordDAO.countFailedLoginByUser(user.getId(),
		    user.getLastLoginDate());
	    logger.debug("Login failed {} times since {}.[userId={}]",
		    failedAttempts,
		    user.getLastLoginDate(),
		    user.getId());
	    if (failedAttempts > LoginConstant.MAX_ATTEMPTS) {
		//TODO:disable user method here
		user.setEnabled(false);
		logger.warn("User disabled due to too many failed login attempts.[userId={}]", user.getId());
		return new UserLoginResult(LoginResult.TOO_MANY_ATTEMPT, null, failedAttempts);
	    } else {
		return new UserLoginResult(LoginResult.FAILED, null, failedAttempts);
	    }
	}
    }

    @Override
    public boolean changePassword(String clientCode, String loginName, String oldPassword, String newPassword) {
	appBean.checkClientCode(clientCode);
	logger.debug("change password for user.[clientCode={}][loginName={}]", clientCode, loginName);
	User user = userDAO.findByLoginName(loginName);
	if (user == null) {
	    logger.warn("user with loginName not exist.[loginName={}]", loginName);
	    return false;
	}
	if (!SecurityUtils.matchPassphrase(user.getPassphrase(), user.getSalt(), oldPassword)) {
	    logger.warn("orignial password not correct.[clientCode={}][loginName={}]", clientCode, loginName);
	    return false;
	}
	user.password(newPassword);
	user.setNeedChangePassword(false);
	userDAO.edit(user);

	return true;
    }

    @Override
    public boolean resetPassword(String clientCode, String mobile) {
	appBean.checkClientCode(clientCode);
	logger.debug("reset password for user.[clientCode={}][mobile={}]", clientCode, mobile);
	User user = userDAO.findByMobile(mobile);
	if (user == null) {
	    logger.warn("user with mobile {} not found", mobile);
	    return false;
	}
	//generate random password for user
	String randomPassword = SecurityUtils.randomPassword(SecurityLevel.GOOD);
	user.password(randomPassword);
	user.setNeedChangePassword(true);
	//send sms
	smsService.sendMessage(appBean.getClient(),
		SMSType.CREDITMARKET_RESET_PASSWORD,
		user.getMobile(),
		randomPassword);
	return true;
    }

    private String getLoginInfo(Map<String, String> loginInfo, Source source) {
	if (loginInfo == null) {
	    return "";
	}
	switch (source) {
	    /**
	     * 对于User登录方式只可能是Web端的CreditMarket和mobile(TODO移动端目前还不支持)，
	     * web端返回ip,mobile端返回imei号
	     */
	    case WEB:
		return loginInfo.get(LoginConstant.IP);
	    case MOBILE:
		//TODO 应该返回更多信息，如型号，操作系统版本等
		return loginInfo.get(LoginConstant.IMEI);
	    default:
		throw new IllegalArgumentException(String.format("Invalid login source %s for user.", source));
	}
    }

    @Override
    public int countByClient(String clientCode) {
	return clientCode == null ? 0 : userDAO.countByClient(clientCode);
    }

    @Override
    public boolean markIDAuthenticated(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	com.creditcloud.user.entity.UserAuthenticate result = authenticateDAO.find(userId);
	result.setIDAuthenticated(true);
	return true;
    }

    @Override
    public boolean markEmailAuthenticated(String clientCode, String userId, boolean authenticated) {
	appBean.checkClientCode(clientCode);
	com.creditcloud.user.entity.UserAuthenticate result = authenticateDAO.find(userId);
	result.setEmailAuthenticated(authenticated);
	return true;
    }

    @Override
    public boolean markMobileAuthenticated(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	com.creditcloud.user.entity.UserAuthenticate result = authenticateDAO.find(userId);
	result.setMobileAuthenticated(true);
	return true;
    }

    @Override
    public boolean markWeiboAuthenticated(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	com.creditcloud.user.entity.UserAuthenticate result = authenticateDAO.find(userId);
	result.setWeiboAuthenticated(true);
	return true;
    }

    @Override
    public boolean markWechatAuthenticated(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	com.creditcloud.user.entity.UserAuthenticate result = authenticateDAO.find(userId);
	result.setWechatAuthenticated(true);
	return true;
    }

    @Override
    public com.creditcloud.user.UserAuthenticate getUserAuthenticate(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	com.creditcloud.user.entity.UserAuthenticate result = authenticateDAO.find(userId);
	if (result == null) {
	    User user = userDAO.find(userId);
	    if (user != null) {
		result = authenticateDAO.create(new com.creditcloud.user.entity.UserAuthenticate(user, false, false, false));
	    }
	}
	return DTOUtils.getUserAuthenticate(result);
    }

    @Override
    public void disableUser(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	User user = userDAO.findByUserId(userId);
	if (user != null) {
	    user.setEnabled(false);
	    autoBidService.enableAutoBid(appBean.getClientCode(), userId, false);
	}
    }

    @Override
    public void enableUser(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	User user = userDAO.findByUserId(userId);
	if (user != null) {
	    user.setEnabled(true);
	    //这里设置最后登录时间为当前是为了刷新登录失败记录的统计时段，相当于变相给用户若干次重新登录的（错误）机会
	    user.setLastLoginDate(new Date());
            //TODO自动投标单独控制
	    //autoBidService.enableAutoBid(appBean.getClientCode(), userId, true);
	}
    }

    @Override
    public boolean setReferral(String clientCode, String userId, RealmEntity referral) {
	appBean.checkClientCode(clientCode);
	User user = userDAO.findByUserId(userId);
	if (user != null) {
	    user.setReferralEntity(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(referral));
	    return true;
	}
	return false;
    }

    @Override
    public PagedResult<com.creditcloud.model.user.User> listByReferral(String clientCode, Date from, Date to, RealmEntity referral, PageInfo pageInfo) {
	appBean.checkClientCode(clientCode);
	PagedResult<User> users = userDAO.listByReferral(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(referral), from, to, pageInfo);
	List<com.creditcloud.model.user.User> result = new ArrayList<>(users.getResults().size());
	for (User user : users.getResults()) {
	    result.add(DTOUtils.getUserDTO(user));
	}
	return new PagedResult<>(result, users.getTotalSize());
    }

    @Override
    public int countByReferral(String clientCode, Date from, Date to, RealmEntity referral) {
	appBean.checkClientCode(clientCode);
	return userDAO.countByReferral(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(referral), from, to);
    }

    @Override
    public List<ElementCount<RealmEntity>> countAllByReferral(String clientCode, Date from, Date to, boolean all) {
	appBean.checkClientCode(clientCode);
	return userDAO.countAllByReferral(from, to, all);
    }

    @Override
    public Map<RealmEntity, List<com.creditcloud.model.user.User>> listAllByReferral(String clientCode, Date from, Date to, boolean all) {
	appBean.checkClientCode(clientCode);
	return userDAO.listAllByReferral(from, to, all);
    }

    @Override
    public PagedResult<RealmEntity> listReferral(String clientCode, Date from, Date to, PageInfo pageInfo) {
	PagedResult<com.creditcloud.common.entities.embedded.RealmEntity> referral = userDAO.listReferral(from, to, pageInfo);
	List<RealmEntity> result = new ArrayList<>(referral.getResults().size());
	for (com.creditcloud.common.entities.embedded.RealmEntity entity : referral.getResults()) {
	    result.add(com.creditcloud.common.utils.DTOUtils.getRealmEntity(entity));
	}
	return new PagedResult<>(result, referral.getTotalSize());
    }

    @Override
    public int countReferral(String clientCode, Date from, Date to) {
	appBean.checkClientCode(clientCode);
	return userDAO.countReferral(from, to);
    }

    @Override
    public void markReferralRewarded(String clientCode, String... userId) {
	appBean.checkClientCode(clientCode);
	userDAO.markReferralRewarded(userId);
    }

    @Override
    public void markRegistryRewarded(String clientCode, String... userId) {
	appBean.checkClientCode(clientCode);
	userDAO.markRegistryRewarded(userId);
    }

    @Override
    public PagedResult<com.creditcloud.model.user.User> listByRegisterDate(String clientCode, Date from, Date to, PageInfo info, boolean all) {
	appBean.checkClientCode(clientCode);
	PagedResult<User> users = userDAO.listByRegisterDate(from, to, info, all);
	List<com.creditcloud.model.user.User> result = new ArrayList<>(users.getResults().size());
	for (User user : users.getResults()) {
	    result.add(DTOUtils.getUserDTO(user));
	}
	return new PagedResult<>(result, users.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.model.user.User> listByLoginDate(String clientCode, Date from, Date to, PageInfo pageInfo) {
	appBean.checkClientCode(clientCode);
	PagedResult<User> users = userDAO.listByLoginDate(from, to, pageInfo);
	List<com.creditcloud.model.user.User> result = new ArrayList<>(users.getResults().size());
	for (User user : users.getResults()) {
	    result.add(DTOUtils.getUserDTO(user));
	}
	return new PagedResult<>(result, users.getTotalSize());
    }

    @Override
    public List<com.creditcloud.model.user.User> listDisabledUsers(String clientCode) {
	long startTime = System.currentTimeMillis();
	appBean.checkClientCode(clientCode);
	//logger.debug("ListAllUsers called by client {}", clientCode);
	PagedResult<User> users = userDAO.listDisabledUsersByClient(clientCode, PageInfo.ALL);
	List<com.creditcloud.model.user.User> result = new ArrayList<>(users.getResults().size());
	for (User user : users.getResults()) {
	    result.add(DTOUtils.getUserDTO(user));
	}
	logger.debug("ListDisabledAllUsers done in {}ms, return {} users", System.currentTimeMillis() - startTime, result.size());
	return result;
    }

    @Override
    public UserLoginResult loginSocial(String clientCode, com.creditcloud.user.social.SocialId socialId, Map<String, String> loginInfo) {
	appBean.checkClientCode(clientCode);
	SocialUser socialUser = socialUserDAO.find(DTOUtils.convertSocialIdDTO(socialId));
	if (socialUser != null) {
	    User user = socialUser.getUser();
	    if (user != null && !user.isEnabled()) {
		return new UserLoginResult(LoginResult.USER_DISABLED,
			null,
			loginRecordDAO.countFailedLoginByUser(user.getId(),
				user.getLastLoginDate()));
	    }
	    //update last login time
	    user.setLastLoginDate(new Date());
	    //add login record
	    loginRecordDAO.create(new UserLoginRecord(user,
		    new LoginRecord(socialId.getId(),
			    new Date(),
			    getLoginInfo(loginInfo, Source.WEB),
			    Source.WEB,
			    true)));
	    if (user.isNeedChangePassword()) {
		return new UserLoginResult(LoginResult.NEED_CHANGE_PASSWORD, DTOUtils.getUserDTO(user), 0);
	    } else {
		return new UserLoginResult(LoginResult.SUCCESSFUL, DTOUtils.getUserDTO(user), 0);
	    }
	} else {
	    loginRecordDAO.create(new UserLoginRecord(null, new LoginRecord(socialId.getId(),
		    new Date(),
		    getLoginInfo(loginInfo, Source.WEB),
		    Source.WEB,
		    false)));
	    return new UserLoginResult(LoginResult.FAILED, null, 0);
	}
    }

    @Override
    public UserLoginResult connectSocial(String clientCode,
	    String loginName,
	    String password,
	    com.creditcloud.user.social.SocialId socialId,
	    Map<String, String> socialInfo) {
	appBean.checkClientCode(clientCode);
	UserLoginResult result = login(clientCode, loginName, password, Collections.EMPTY_MAP, Source.WEB);
	if (result.getUser() != null) {
	    User entityUser = userDAO.find(result.getUser().getId());
	    SocialUser socialUser = new SocialUser(DTOUtils.convertSocialIdDTO(socialId), entityUser, new Date());
	    userBean.createSocialUser(socialUser);
	    logger.info("SocialUser connected.[clientCode={}][loginName={}][socialId={}][socialType={}]",
		    clientCode,
		    loginName,
		    socialId.getId(),
		    socialId.getType());
	} else {
	    logger.warn("Failed connect social because User authenticate failed.[loginName={}][socialId={}][socialType={}]",
		    loginName,
		    socialId.getId(),
		    socialId.getType());
	}
	return result;
    }

    @Override
    public ConnectSocialResult connectSocial(String clientCode, String userId, com.creditcloud.user.social.SocialId socialId) {
	appBean.checkClientCode(clientCode);
	try {
	    SocialId entitySocialId = DTOUtils.convertSocialIdDTO(socialId);
	    SocialUser socialUser = socialUserDAO.find(entitySocialId);
	    if (socialUser != null) {
		logger.info("SocialUser already connected.[userId={}][socialId={}][socialType={}]",
			userId,
			socialId.getId(),
			socialId.getType());
		return ConnectSocialResult.CONNECTED;
	    }
	    User user = userDAO.find(userId);
	    if (user != null) {
		socialUser = new SocialUser(entitySocialId, user, new Date());
		userBean.createSocialUser(socialUser);
		logger.info("SocialUser connected.[userId={}][socialId={}][socialType={}]",
			userId,
			socialId.getId(),
			socialId.getType());
		return ConnectSocialResult.SUCCEED;
	    }
	    logger.warn("fail to connect social because user not exist.[userId={}][socialId={}][socialType={}]",
		    userId,
		    socialId.getId(),
		    socialId.getType());
	    return ConnectSocialResult.USER_NOT_FOUND;
	} catch (Exception e) {
	    logger.warn("got exception while connect social.[userId={}][socialId={}][socialType={}]",
		    userId,
		    socialId.getId(),
		    socialId.getType());
	    return ConnectSocialResult.FAILED;
	}
    }

    @Override
    public boolean checkSocialId(String clientCode, com.creditcloud.user.social.SocialId socialId) {
	appBean.checkClientCode(clientCode);
	return socialUserDAO.find(DTOUtils.convertSocialIdDTO(socialId)) == null;
    }

    @Override
    public com.creditcloud.user.social.SocialUser findSocialUserBySocialId(String clientCode,
	    com.creditcloud.user.social.SocialId socialId) {
	appBean.checkClientCode(clientCode);
	return DTOUtils.getSocialUser(socialUserDAO.find(DTOUtils.convertSocialIdDTO(socialId)));
    }

    @Override
    public List<com.creditcloud.user.social.SocialUser> listSocialUserByUserId(String clientCode, String userId) {
	appBean.checkClientCode(clientCode);
	return DTOUtils.getSocialUser(socialUserDAO.listByUserId(userId));
    }

    @Override
    public boolean foundPassword(String clientCode, String loginName, String newPassword) {
        appBean.checkClientCode(clientCode);
	logger.debug("found password for user.[clientCode={}][loginName={}]", clientCode, loginName);
	User user = userDAO.findByLoginName(loginName);
	if (user == null) {
	    logger.warn("user with loginName {} not found", loginName);
	    return false;
	}
	user.password(newPassword);
	userDAO.edit(user);
	return true;
    }

}
