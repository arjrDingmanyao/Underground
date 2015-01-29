/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user;

import com.creditcloud.model.criteria.PageInfo;
import com.creditcloud.model.misc.PagedResult;
import com.creditcloud.user.api.UserLoginRecordService;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.dao.LoginRecordDAO;
import com.creditcloud.user.entity.dao.UserDAO;
import com.creditcloud.user.entity.record.UserLoginRecord;
import com.creditcloud.user.local.ApplicationBean;
import com.creditcloud.user.utils.DTOUtils;
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
 * @author Administrator
 */
@Stateless
@Remote
public class UserLoginRecordServiceBean implements UserLoginRecordService {

    @Inject
    Logger logger;

    @EJB
    ApplicationBean appBean;

    @EJB
    LoginRecordDAO loginRecordDAO;

    @EJB
    UserDAO userDAO;

    @Override
    public List<com.creditcloud.model.user.UserLoginRecord> listByLoginDate(String clientCode, Date from, Date to, PageInfo pageInfo) {

	PagedResult<UserLoginRecord> records = loginRecordDAO.listByLoginDateRange(from, to, PageInfo.ALL);
	List<com.creditcloud.model.user.User> users = new ArrayList<com.creditcloud.model.user.User>();
	List<com.creditcloud.model.user.UserLoginRecord> loginRecords = new ArrayList<>();
	for (UserLoginRecord record : records.getResults()) {

	    loginRecords.add(DTOUtils.getUserLoginRecord(record));
//	    users.add(DTOUtils.getUserDTO(record.getUser()));
	}
	return loginRecords;
    }
}
