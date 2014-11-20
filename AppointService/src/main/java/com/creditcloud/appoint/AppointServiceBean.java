/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.appoint;

import com.creditcloud.appoint.api.AppointService;
import com.creditcloud.appoint.entities.AppointAgent;
import com.creditcloud.appoint.entities.AppointRequest;
import com.creditcloud.appoint.entities.AppointUser;
import com.creditcloud.appoint.entities.Appointment;
import com.creditcloud.appoint.entities.dao.AppointAgentDAO;
import com.creditcloud.appoint.entities.dao.AppointRequestDAO;
import com.creditcloud.appoint.entities.dao.AppointUserDAO;
import com.creditcloud.appoint.entities.dao.AppointmentDAO;
import com.creditcloud.appoint.enums.AppointRequestStatus;
import com.creditcloud.appoint.enums.AppointmentStatus;
import static com.creditcloud.appoint.enums.AppointmentStatus.INITIATED;
import static com.creditcloud.appoint.enums.AppointmentStatus.OPENED;
import static com.creditcloud.appoint.enums.AppointmentStatus.SCHEDULED;
import com.creditcloud.appoint.local.ApplicationBean;
import com.creditcloud.appoint.model.AppointResult;
import com.creditcloud.appoint.model.BranchAppointStat;
import com.creditcloud.appoint.model.DailyAppointStat;
import com.creditcloud.appoint.model.UserAppointStat;
import com.creditcloud.appoint.utils.DTOUtils;
import com.creditcloud.common.bean.BaseBean;
import com.creditcloud.common.entities.embedded.InvestRule;
import com.creditcloud.common.validation.InvalidException;
import com.creditcloud.model.ElementCount;
import com.creditcloud.model.criteria.CriteriaInfo;
import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import java.util.ArrayList;
import java.util.Date;
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
public class AppointServiceBean extends BaseBean implements AppointService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    AppointmentDAO appointmentDAO;

    @EJB
    AppointRequestDAO requestDAO;

    @EJB
    AppointAgentDAO agentDAO;

    @EJB
    AppointUserDAO userDAO;

    @Override
    public com.creditcloud.appoint.model.Appointment saveAppointment(String clientCode, com.creditcloud.appoint.model.Appointment appointment) {
        appBean.checkClientCode(clientCode);
        try {
            getValidatorWrapper().tryValidate(appointment);
            if (appointment.getId() == null || appointmentDAO.find(appointment.getId()) == null) {
                //id为空直接添加
                Appointment result = appointmentDAO.create(DTOUtils.convertAppointment(appointment));
                logger.debug("new Appointment added.{}", result);
                return DTOUtils.getAppointment(result);
            }

            Appointment result = appointmentDAO.find(appointment.getId());
            switch (appointment.getStatus()) {
                case INITIATED:
                case SCHEDULED:
                    //这两种状态下 只有下面几项可以更改
                    result.setDescription(appointment.getDescription());
                    result.setInvestRule(com.creditcloud.common.utils.DTOUtils.convertInvestRule(appointment.getInvestRule()));
                    result.setQuota(appointment.getQuota());
                    result.setStatus(appointment.getStatus());
                    result.setTimeOpened(appointment.getTimeOpened());
                    result.setTimeOut(appointment.getTimeOut());
                    result.setTitle(appointment.getTitle());
                    break;
                case OPENED:
                    if (result.getStatus().equals(AppointmentStatus.OPENED)) {
                        // 允许后台开放募集时动态修改quota
                        if (appointment.getQuota() < result.getAmount()) {
                            logger.error("quota {} must be large than amount {} for appointment {}",
                                         appointment.getQuota(), result.getAmount(), result.getId());
                            return null;
                        } else {
                            result.setQuota(appointment.getQuota());
                        }
                    } else if (result.getStatus().equals(AppointmentStatus.SCHEDULED)) {
                        //到点开放认购
                        result.setStatus(AppointmentStatus.OPENED);
                    } else if (result.getStatus().equals(AppointmentStatus.FINISHED)) {
                        //关闭后又重新打开，例如某些用户取消了认购申请，就有新的配额空出,这时管理员可以重新打开认购
                        logger.debug("reopen appointment {}.", result.getId());
                        result.setStatus(AppointmentStatus.OPENED);
                        result.setTimeFinished(null);
                    }
                    break;
                case FINISHED:
                    //允许后台更改status提前结束
                    result.setStatus(AppointmentStatus.FINISHED);
                    result.setTimeFinished(new Date());
                    break;
                default:
                    logger.debug("Appointment {} already {}, can not update", appointment.getId(), appointment.getStatus());
                    return null;
            }
            appointmentDAO.edit(result);
            logger.debug("update Appointment.{}", appointment);
            return DTOUtils.getAppointment(appointmentDAO.find(appointment.getId()));
        } catch (InvalidException ex) {
            logger.error("Appointment is not valid.{}\n{}", ex.getMessage(), appointment);
            return null;
        } catch (Exception ex) {
            logger.error("Can't save Appointment: {}", appointment == null ? "null" : appointment, ex);
            return null;
        }
    }

    @Override
    public AppointResult appoint(String clientCode, String userId, int expectedAmount, String appointmentId, String branchId) {
        appBean.checkClientCode(clientCode);
        try {
            Appointment appointment = appointmentDAO.find(appointmentId);
            if (appointment == null) {
                logger.warn("appointment {} not found", appointmentId);
                return AppointResult.APPOINT_NOT_FOUND;
            }
            if (!InvestRule.valid(appointment.getInvestRule(), expectedAmount)) {
                logger.warn("Invalid request amount {} for appointment {}.", expectedAmount, appointmentId);
                return AppointResult.INVALID_AMOUNT;
            }
            //TODO开放状态才能认购，后台可能会提前取消或结束认购，前台则有一定延迟才知晓
            switch (appointment.getStatus()) {
                case INITIATED:
                case SCHEDULED:
                    logger.warn("fail to appoint, appointment {} already {}", appointmentId, appointment.getStatus());
                    return AppointResult.APPOIN_NOT_OPEN;
                case OPENED:
                    break;
                default:
                    logger.warn("fail to appoint, appointment {} already {}", appointmentId, appointment.getStatus());
                    return AppointResult.APPOINT_ALREADY_FINISHED;
            }
            //检查是否有足够额度
            if (appointment.getAmount() == appointment.getQuota()) {
                logger.warn("no available amount left for appointment {}", appointment.getId());
                return AppointResult.APPOIN_NO_BALANCE;
            }
            //检查是否超过认购次数上线
            int appointCount = requestDAO.countByAppointmentAndUser(appointmentId, userId, AppointRequestStatus.APPOINTED);
            if (appointCount >= AppointConstant.MAX_APPOINT_COUNT) {
                logger.warn("failt to appoint, exceed total appoint limit {} for user.", AppointConstant.MAX_APPOINT_COUNT);
                return AppointResult.EXCEED_LIMIT;
            }
            //检查是否超过认购总金额限制，TODO目前是每人只能认购一次，所以不需要检查
            /*
             * long appointSum =
             * requestDAO.sumByAppointmentAndUser(appointmentId, userId,
             * AppointRequestStatus.APPOINTED); if (appointSum + expectedAmount
             * > AppointConstant.MAX_APPOINT_SUM) { logger.warn("failt to
             * appoint, exceed total appoint amount {} for user.",
             * AppointConstant.MAX_APPOINT_SUM); return
             * AppointResult.EXCEED_AMOUNT; }
             */
            int actualAmount;
            if (expectedAmount + appointment.getAmount() > appointment.getQuota()) {
                //部分成功
                actualAmount = appointment.getQuota() - appointment.getAmount();
            } else {
                actualAmount = expectedAmount;
            }
            AppointRequest request = new AppointRequest(appointment, userId, AppointRequestStatus.APPOINTED, actualAmount, branchId);
            //添加认购记录
            requestDAO.create(request);
            //更新认购
            appointment.setCount(appointment.getCount() + 1);
            appointment.setAmount(appointment.getAmount() + actualAmount);
            //TODO 最后剩余的不够投，暂时结束
            if (appointment.getAmount() + appointment.getInvestRule().getMinAmount() > appointment.getQuota()) {
                //认购已满
                appointment.setTimeFinished(new Date());
                appointment.setStatus(AppointmentStatus.FINISHED);
            }
            appointmentDAO.edit(appointment);
            logger.debug("new AppointRequest added.{}", request);
            if (expectedAmount == actualAmount) {
                return AppointResult.SUCCESSFUL;
            } else {
                return AppointResult.PARTLY_SUCCESSFUL;
            }
        } catch (Exception ex) {
            logger.error("fail to appoint {} for user {} with amount {}", appointmentId, userId, expectedAmount, ex);
            return AppointResult.FAILED;
        }
    }

    @Override
    public boolean cancelRequest(String clientCode, String requestId) {
        appBean.checkClientCode(clientCode);
        AppointRequest request = requestDAO.find(requestId);
        if (request == null) {
            logger.warn("AppointRequest {} to cancel not exist", requestId);
            return false;
        }
        switch (request.getStatus()) {
            case SETTLED:
                logger.debug("AppointRequest {} alread settled, can not cancel", requestId);
                return false;
            case CANCELED:
                return true;
            case APPOINTED:
                Appointment appointment = request.getAppointment();
                switch (appointment.getStatus()) {
                    case FINISHED:
                    case SETTLED:
                        logger.debug("Appointment for AppoitRequest {} already {},can not cancel request",
                                     requestId, request.getStatus());
                        return false;
                    case OPENED:
                        request.setStatus(AppointRequestStatus.CANCELED);
                        requestDAO.edit(request);
                        //释放配额
                        appointment = appointmentDAO.find(appointment.getId());
                        appointment.setAmount(appointment.getAmount() - request.getAmount());
                        appointment.setCount(appointment.getCount() - 1);
                        appointmentDAO.edit(appointment);
                        return true;
                    default:
                    //do nothing
                }
                return true;
            default:
                //现有状态不可能到达此处
                return false;
        }
    }

    @Override
    public void markRequestStatus(String clientCode, AppointRequestStatus status, String... requestIds) {
        appBean.checkClientCode(clientCode);
        requestDAO.markStatus(status, requestIds);
    }

    @Override
    public com.creditcloud.appoint.model.AppointAgent saveAgent(String clientCode, com.creditcloud.appoint.model.AppointAgent agent) {
        appBean.checkClientCode(clientCode);
        try {
            getValidatorWrapper().tryValidate(agent);
            if (agent.getUserId() == null || agentDAO.find(agent.getUserId()) == null) {
                AppointAgent result = agentDAO.create(DTOUtils.convertAppointAgent(agent));
                logger.debug("new AppointAgent added.{}", result);
                return DTOUtils.getAppointAgent(result);
            }
            AppointAgent result = agentDAO.find(agent.getUserId());
            result.setDescription(agent.getDescription());
            result.setQuota(agent.getQuota());
            agentDAO.edit(result);
            logger.debug("update AppointAgent.{}", agent);
            return DTOUtils.getAppointAgent(agentDAO.find(agent.getUserId()));
        } catch (InvalidException ex) {
            logger.error("AppointAgent is not valid.\n{}", agent);
            return null;
        } catch (Exception ex) {
            logger.error("Can't save AppointAgent: {}", agent == null ? "null" : agent, ex);
            return null;
        }
    }

    @Override
    public PagedResult<com.creditcloud.appoint.model.AppointRequest> listRequestByAppointment(String clientCode,
                                                                                              String appointmentId,
                                                                                              PageInfo pageInfo,
                                                                                              AppointRequestStatus... status) {
        appBean.checkClientCode(clientCode);
        List<com.creditcloud.appoint.model.AppointRequest> result = new ArrayList<>();
        PagedResult<AppointRequest> requests = requestDAO.listByAppointment(appointmentId, pageInfo, status);
        for (AppointRequest request : requests.getResults()) {
            result.add(DTOUtils.getAppointRequest(request));
        }

        return new PagedResult<>(result, requests.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.appoint.model.AppointRequest> listRequestByUser(String clientCode,
                                                                                       String userId,
                                                                                       PageInfo pageInfo,
                                                                                       AppointRequestStatus... status) {
        appBean.checkClientCode(clientCode);
        List<com.creditcloud.appoint.model.AppointRequest> result = new ArrayList<>();
        PagedResult<AppointRequest> requests = requestDAO.listByUser(userId, pageInfo, status);
        for (AppointRequest request : requests.getResults()) {
            result.add(DTOUtils.getAppointRequest(request));
        }

        return new PagedResult<>(result, requests.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.appoint.model.AppointRequest> listRequestByAppointmentAndUser(String clientCode,
                                                                                                     String appointmentId,
                                                                                                     String userId,
                                                                                                     PageInfo pageInfo,
                                                                                                     AppointRequestStatus... status) {
        appBean.checkClientCode(clientCode);
        List<com.creditcloud.appoint.model.AppointRequest> result = new ArrayList<>();
        PagedResult<AppointRequest> requests = requestDAO.listByAppointmentAndUser(appointmentId, userId, pageInfo, status);
        for (AppointRequest request : requests.getResults()) {
            result.add(DTOUtils.getAppointRequest(request));
        }

        return new PagedResult<>(result, requests.getTotalSize());
    }

    @Override
    public List<com.creditcloud.appoint.model.Appointment> listAppointment(String clientCode, AppointmentStatus... status) {
        appBean.checkClientCode(clientCode);
        List<com.creditcloud.appoint.model.Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointmentDAO.listByStatus(status)) {
            result.add(DTOUtils.getAppointment(appointment));
        }
        return result;
    }

    @Override
    public com.creditcloud.appoint.model.Appointment getAppointmentById(String clientCode, String appointmentId) {
        appBean.checkClientCode(clientCode);
        Appointment result = appointmentDAO.find(appointmentId);
        return DTOUtils.getAppointment(result);
    }

    @Override
    public com.creditcloud.appoint.model.AppointRequest getAppointRequestById(String clientCode, String requestId) {
        appBean.checkClientCode(clientCode);
        AppointRequest request = requestDAO.find(requestId);
        return DTOUtils.getAppointRequest(request);
    }

    @Override
    public com.creditcloud.appoint.model.AppointAgent getAppointAgentById(String clientCode, String agentId) {
        appBean.checkClientCode(clientCode);
        AppointAgent agent = agentDAO.find(agentId);
        return DTOUtils.getAppointAgent(agent);
    }

    @Override
    public com.creditcloud.appoint.model.AppointUser saveAppointUser(String clientCode, com.creditcloud.appoint.model.AppointUser user) {
        appBean.checkClientCode(clientCode);
        try {
            getValidatorWrapper().tryValidate(user);
            if (user.getId() == null || userDAO.find(user.getId()) == null) {
                return DTOUtils.getAppointUser(userDAO.create(DTOUtils.convertAppointUser(user)));
            }
            AppointUser result = userDAO.find(user.getId());
            result.setIdNumber(user.getIdNumber());
            result.setBranch(user.getBranch());
            result.setBranchId(user.getBranchId());
            result.setName(user.getName());
            userDAO.edit(result);
            return DTOUtils.getAppointUser(result);
        } catch (InvalidException ex) {
            logger.error("AppointUser is not valid.{}\n{}", ex.getMessage(), user);
            throw ex;
        } catch (Exception ex) {
            logger.error("Can't add AppointUser: {}", user == null ? "null" : user, ex);
            throw ex;
        }
    }

    @Override
    public boolean importAppointUser(String clientCode, String whiteListFilePath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PagedResult<com.creditcloud.appoint.model.AppointUser> listAllAppointUser(String clientCode, PageInfo pageInfo) {
        appBean.checkClientCode(clientCode);
        List<com.creditcloud.appoint.model.AppointUser> result = new ArrayList<>();
        PagedResult<AppointUser> users = userDAO.listAll(pageInfo);
        for (AppointUser user : users.getResults()) {
            result.add(DTOUtils.getAppointUser(user));
        }
        return new PagedResult<>(result, users.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.appoint.model.AppointUser> listAppointUserByCriteria(String clientCode, CriteriaInfo criteriaInfo) {
        appBean.checkClientCode(clientCode);
        long startTime = System.currentTimeMillis();
        List<com.creditcloud.appoint.model.AppointUser> result = new ArrayList<>();
        PagedResult<AppointUser> users = userDAO.list(criteriaInfo);
        for (AppointUser user : users.getResults()) {
            result.add(DTOUtils.getAppointUser(user));
        }
        logger.debug("listAppointUserByCriteria called. time {} ms", System.currentTimeMillis() - startTime);
        return new PagedResult<>(result, users.getTotalSize());
    }

    @Override
    public PagedResult<com.creditcloud.appoint.model.AppointUser> listAppointUserByBranch(String clientCode, PageInfo pageInfo, String... branch) {
        appBean.checkClientCode(clientCode);
        List<com.creditcloud.appoint.model.AppointUser> result = new ArrayList<>();
        PagedResult<AppointUser> users = userDAO.listByBranch(pageInfo, branch);
        for (AppointUser user : users.getResults()) {
            result.add(DTOUtils.getAppointUser(user));
        }
        return new PagedResult<>(result, users.getTotalSize());
    }

    @Override
    public List<ElementCount<String>> countUserByBranch(String clientCode) {
        appBean.checkClientCode(clientCode);
        return userDAO.countEachByBranch();
    }

    @Override
    public List<BranchAppointStat> getBranchAppointStat(String clientCode, String appointmentId, AppointRequestStatus... status) {
        appBean.checkClientCode(clientCode);
        return requestDAO.getBranchAppointStat(appointmentId, status);
    }

    @Override
    public List<BranchAppointStat> getBranchAppointStat(String clientCode, AppointRequestStatus... status) {
        appBean.checkClientCode(clientCode);
        return requestDAO.getBranchAppointStat(status);
    }

    @Override
    public com.creditcloud.appoint.model.AppointUser getAppointUserById(String clientCode, String appointUserId) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getAppointUser(userDAO.find(appointUserId));
    }

    @Override
    public boolean deleteAppointUser(String clientCode, String appointUserId) {
        appBean.checkClientCode(clientCode);
        logger.debug("try delete appoint user {}.", appointUserId);
        userDAO.removeById(appointUserId);
        return true;
    }

    @Override
    public PagedResult<UserAppointStat> getUserAppointStat(String clientCode, String appoinmentId, PageInfo pageInfo, AppointRequestStatus... status) {
        appBean.checkClientCode(clientCode);
        return requestDAO.getUserStat(appoinmentId, pageInfo, status);
    }

    @Override
    public PagedResult<UserAppointStat> getUserAppointStat(String clientCode, PageInfo pageInfo, AppointRequestStatus... status) {
        appBean.checkClientCode(clientCode);
        return requestDAO.getUserStat(pageInfo, status);
    }

    @Override
    public List<ElementCount<String>> countAppointedUserByBranch(String clientCode) {
        appBean.checkClientCode(clientCode);
        return requestDAO.countUserByBranch(AppointRequestStatus.APPOINTED, AppointRequestStatus.SETTLED);
    }

    @Override
    public List<ElementCount<String>> countAppointedUserByBranch(String clientCode, String appointmnetId) {
        appBean.checkClientCode(clientCode);
        return requestDAO.countUserByBranchAndAppointment(appointmnetId, AppointRequestStatus.APPOINTED, AppointRequestStatus.SETTLED);
    }

    @Override
    public List<DailyAppointStat> getDailyAppointStat(String clientCode, Date from, Date to, AppointRequestStatus... status) {
        appBean.checkClientCode(clientCode);
        return requestDAO.getDailyStat(from, to, status);
    }

    @Override
    public List<DailyAppointStat> getDailyAppointStat(String clientCode, String appointmentId, Date from, Date to, AppointRequestStatus... status) {
        appBean.checkClientCode(clientCode);
        return requestDAO.getDailyStat(appointmentId, from, to, status);
    }
}
