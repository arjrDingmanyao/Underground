/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.local;

import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.model.enums.user.credit.CertificateStatus;
import com.creditcloud.model.enums.user.credit.CertificateType;
import com.creditcloud.user.entity.Certificate;
import com.creditcloud.user.entity.record.CertificateRecord;
import com.creditcloud.user.entity.dao.CertificateDAO;
import com.creditcloud.user.entity.dao.CertificateRecordDAO;
import com.creditcloud.user.entity.dao.UserCreditDAO;
import com.creditcloud.user.entity.embedded.Assessment;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author rooseek
 */
@LocalBean
@Stateless
public class CreditLocalBean {

    private static final String DEFAULT_AUDITOR = "SYSTEM";
    
    private static final String AUDIT_INFO_ON_CREATE = "create";

    private static final Assessment DEFAULT_ASSESSMENT = new Assessment(0);

    @EJB
    UserCreditDAO creditDAO;

    @EJB
    CertificateDAO certificateDAO;

    @EJB
    CertificateRecordDAO recordDAO;

    @EJB
    ApplicationBean appBean;
    
    /**
     * 如果CertificateType对应的认证不存在，那么创建出来
     *
     * @param userId
     * @param type
     * @return
     */
    public Certificate getCertificateByUserAndType(String userId, CertificateType type) {
        Certificate result = certificateDAO.findByUserAndType(userId, type);
        if (result == null) {
            result = new Certificate(creditDAO.find(userId),
                                     type,
                                     CertificateStatus.UNCHECKED,
                                     DEFAULT_AUDITOR,
                                     AUDIT_INFO_ON_CREATE,
                                     DEFAULT_ASSESSMENT);
            Certificate certificate = certificateDAO.create(result);

            CertificateRecord record = new CertificateRecord(certificate,
                                                             certificate.getStatus(),
                                                             certificate.getAuditor(),
                                                             certificate.getAuditInfo(),
                                                             certificate.getAssessment());

            recordDAO.create(record);
        }

        return result;
    }

    /**
     * 更新认证，并生成记录信息
     *
     * @param certificate
     * @param auditor
     * @param auditorInfo
     * @return
     */
    public Certificate updateCertificate(Certificate certificate) {
        Certificate oldCertificate = certificateDAO.find(certificate.getId());
        if (!oldCertificate.equals(certificate)) {
            certificateDAO.edit(certificate);
            Certificate result = certificateDAO.find(certificate.getId());
            CertificateRecord record = new CertificateRecord(result,
                                                             certificate.getStatus(),
                                                             certificate.getAuditor(),
                                                             certificate.getAuditInfo(),
                                                             certificate.getAssessment());
            recordDAO.create(record);
            
            // delete cache
            appBean.deleteCache(certificate.getCredit().getUserId(), CacheConstant.KEY_PREFIX_USER_INFO);
            
            return result;
        }

        return oldCertificate;
    }
}
