/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user;

import com.creditcloud.model.ElementCount;
import com.creditcloud.model.enums.Source;
import com.creditcloud.model.enums.user.credit.CertificateType;
import com.creditcloud.model.enums.user.credit.CreditRank;
import com.creditcloud.model.enums.user.credit.ProofType;
import com.creditcloud.user.api.UserStatService;
import com.creditcloud.user.entity.dao.LoginRecordDAO;
import com.creditcloud.user.entity.dao.ProofDAO;
import com.creditcloud.user.entity.dao.UserCreditDAO;
import com.creditcloud.user.entity.dao.UserDAO;
import com.creditcloud.user.local.ApplicationBean;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import org.joda.time.LocalDate;

/**
 *
 * @author rooseek
 */
@Remote
@Stateless
public class UserStatServiceBean implements UserStatService {

    @EJB
    ApplicationBean appBean;

    @EJB
    UserDAO userDAO;

    @EJB
    LoginRecordDAO loginRecordDAO;

    @EJB
    ProofDAO proofDAO;

    @EJB
    UserCreditDAO creditDAO;

    @Override
    public List<ElementCount<Source>> countUserBySource(String clientCode) {
        appBean.checkClientCode(clientCode);
        return userDAO.countEachBySource();
    }

    @Override
    public List<ElementCount<String>> countUserByEmployee(String clientCode, Source... source) {
        appBean.checkClientCode(clientCode);
        return userDAO.countEachByEmployee(source);
    }

    public List<ElementCount<String>> countUserCreditByRank(String clientCode) {
        return null;
    }

    @Override
    public List<ElementCount<Source>> countProofBySource(String clientCode) {
        appBean.checkClientCode(clientCode);
        return proofDAO.countEachBySource();
    }

    @Override
    public List<ElementCount<String>> countProofByEmployee(String clientCode, Source... source) {
        appBean.checkClientCode(clientCode);
        return proofDAO.countEachByEmployee(source);
    }

    @Override
    public List<ElementCount<ProofType>> countProofByProofType(String clientCode, Source... source) {
        appBean.checkClientCode(clientCode);
        return proofDAO.countEachByProofType(source);
    }

    @Override
    public List<ElementCount<CertificateType>> countProofByCertificateType(String clientCode, Source... source) {
        appBean.checkClientCode(clientCode);
        return proofDAO.countEachByCertificateType(source);
    }

    @Override
    public List<ElementCount<CreditRank>> countUserByCreditRank(String clientCode) {
        appBean.checkClientCode(clientCode);
        return creditDAO.countEachByRank();
    }

    @Override
    public List<ElementCount<LocalDate>> dailyRegister(String clientCode, Date from, Date to) {
        appBean.checkClientCode(clientCode);
        return userDAO.dailyRegister(from, to);
    }

    @Override
    public List<ElementCount<LocalDate>> dailyLogin(String clientCode, Date from, Date to) {
        appBean.checkClientCode(clientCode);
        return loginRecordDAO.dailyLogin(from, to);
    }

    @Override
    public List<ElementCount<LocalDate>> dailyLoginUser(String clientCode, Date from, Date to) {
        appBean.checkClientCode(clientCode);
        return loginRecordDAO.dailyLoginUser(from, to);
    }

    @Override
    public List<String> listByLoginDate(String clientCode, Date from, Date to) {
        appBean.checkClientCode(clientCode);
        return loginRecordDAO.listByLoginDate(from, to);
    }
}
