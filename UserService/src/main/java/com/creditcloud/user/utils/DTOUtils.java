/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.utils;

import static com.creditcloud.common.utils.DTOUtils.convertContactInfo;
import static com.creditcloud.common.utils.DTOUtils.convertPersonalInfo;
import static com.creditcloud.common.utils.DTOUtils.convertSocialInfo;
import static com.creditcloud.common.utils.DTOUtils.getContactInfo;
import static com.creditcloud.common.utils.DTOUtils.getPersonalInfo;
import static com.creditcloud.common.utils.DTOUtils.getSocialInfo;
import com.creditcloud.user.entity.Certificate;
import com.creditcloud.user.entity.CorporationInfo;
import com.creditcloud.user.entity.CorporationUser;
import com.creditcloud.user.entity.Proof;
import com.creditcloud.user.entity.RealEstate;
import com.creditcloud.user.entity.ShippingAddress;
import com.creditcloud.user.entity.SocialUser;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.UserAuthenticate;
import com.creditcloud.user.entity.UserCredit;
import com.creditcloud.user.entity.UserInfo;
import com.creditcloud.user.entity.Vehicle;
import com.creditcloud.user.entity.embedded.Assessment;
import com.creditcloud.user.entity.embedded.CareerInfo;
import com.creditcloud.user.entity.embedded.CompanyInfo;
import com.creditcloud.user.entity.embedded.FinanceInfo;
import com.creditcloud.user.entity.embedded.SocialId;
import com.creditcloud.user.entity.record.CareerInfoRecord;
import com.creditcloud.user.entity.record.CertificateRecord;
import com.creditcloud.user.entity.record.PersonalInfoRecord;
import com.creditcloud.user.entity.record.RealEstateRecord;
import com.creditcloud.user.entity.record.UserCreditRecord;
import com.creditcloud.user.entity.record.UserLoginRecord;
import com.creditcloud.user.entity.record.VehicleRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * data transfer between entity and model
 *
 * @author rooseek
 */
public class DTOUtils {

    /**
     * handle user
     *
     * @param user
     * @return
     */
    public static com.creditcloud.model.user.User getUserDTO(User user) {
        com.creditcloud.model.user.User result = null;
        if (user != null) {
            result = new com.creditcloud.model.user.User(user.getId(),
                                                         user.getClientCode(),
                                                         user.getName(),
                                                         user.getLoginName(),
                                                         user.getIdNumber(),
                                                         user.getMobile(),
                                                         user.getEmail(),
                                                         user.getSource(),
                                                         user.getEmployeeId(),
                                                         user.getLastModifiedBy(),
                                                         user.getLastLoginDate(),
                                                         user.getRegisterDate(),
                                                         com.creditcloud.common.utils.DTOUtils.getRealmEntity(user.getReferralEntity()));
            result.setEnabled(user.isEnabled());
            result.setEnterprise(user.isEnterprise());
            result.setReferralRewarded(user.isReferralRewarded());
            result.setRegistryRewarded(user.isRegistryRewarded());
        }
        return result;
    }

    public static User convertUserDTO(com.creditcloud.model.user.User user) {
        User result = null;
        if (user != null) {
            result = new User(user.getName(),
                              user.getLoginName(),
                              user.getIdNumber(),
                              user.getMobile(),
                              user.getEmail(),
                              user.getSource(),
                              user.getEmployeeId(),
                              user.getLastModifiedBy(),
                              user.isEnabled(),
                              com.creditcloud.common.utils.DTOUtils.convertRealmEntity(user.getReferralEntity()),
                              user.isEnterprise());
            result.setId(user.getId());
            result.setClientCode(user.getClientCode());
            result.setRegisterDate(user.getRegisterDate());
            result.setLastLoginDate(user.getLastLoginDate());
            result.setReferralRewarded(user.isReferralRewarded());
            result.setRegistryRewarded(user.isRegistryRewarded());
        }

        return result;
    }

    /**
     * handle socialId
     *
     * @param socialId
     * @return
     */
    public static com.creditcloud.user.social.SocialId getSocialIdDTO(SocialId socialId) {
        com.creditcloud.user.social.SocialId result = null;
        if (socialId != null) {
            result = new com.creditcloud.user.social.SocialId(socialId.getId(),
                                                              socialId.getType());
        }
        return result;
    }

    public static SocialId convertSocialIdDTO(com.creditcloud.user.social.SocialId socialId) {
        SocialId result = null;
        if (socialId != null) {
            result = new SocialId(socialId.getId(),
                                  socialId.getType());
        }
        return result;
    }

    /**
     * handle SocialUser
     *
     * @param user
     * @return
     */
    public static com.creditcloud.user.social.SocialUser getSocialUser(SocialUser user) {
        com.creditcloud.user.social.SocialUser result = null;
        if (user != null) {
            result = new com.creditcloud.user.social.SocialUser(getUserDTO(user.getUser()),
                                                                getSocialIdDTO(user.getSocialInfo()),
                                                                user.getTimeConnected());
        }
        return result;
    }

    public static List<com.creditcloud.user.social.SocialUser> getSocialUser(List<SocialUser> users) {
        if (users == null || users.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<com.creditcloud.user.social.SocialUser> result = new ArrayList<>(users.size());
        for (SocialUser user : users) {
            result.add(getSocialUser(user));
        }
        return result;
    }

    /**
     * handle CompanyInfo
     *
     * @param company
     * @return
     */
    public static com.creditcloud.model.user.info.CompanyInfo getCompanyInfo(CompanyInfo company) {
        com.creditcloud.model.user.info.CompanyInfo result = null;
        if (company != null) {
            result = new com.creditcloud.model.user.info.CompanyInfo(company.getName(),
                                                                     company.getType(),
                                                                     company.getIndustry(),
                                                                     company.getCompanySize(),
                                                                     company.getPhone(),
                                                                     company.getAddress());
        }
        return result;
    }

    public static CompanyInfo convertCompanyInfo(com.creditcloud.model.user.info.CompanyInfo company) {
        CompanyInfo result = null;
        if (company != null) {
            result = new CompanyInfo(company.getName(),
                                     company.getType(),
                                     company.getIndustry(),
                                     company.getCompanySize(),
                                     company.getPhone(),
                                     company.getAddress());
        }
        return result;
    }

    /**
     * handle CareerInfo
     *
     * @param career
     * @return
     */
    public static com.creditcloud.model.user.info.CareerInfo getCareerInfo(CareerInfo career) {
        com.creditcloud.model.user.info.CareerInfo result = null;
        if (career != null) {
            result = new com.creditcloud.model.user.info.CareerInfo(career.getCareerStatus(),
                                                                    getCompanyInfo(career.getCompany()),
                                                                    career.getProvince(),
                                                                    career.getCity(),
                                                                    career.getPosition(),
                                                                    career.getSalary(),
                                                                    career.getYearOfService(),
                                                                    career.getWorkMail());
            result.setTotalYearOfService(career.getTotalYearOfService());
        }
        return result;
    }

    public static CareerInfo convertCareerInfo(com.creditcloud.model.user.info.CareerInfo career) {
        CareerInfo result = null;
        if (career != null) {
            result = new CareerInfo(career.getCareerStatus(),
                                    convertCompanyInfo(career.getCompany()),
                                    career.getProvince(),
                                    career.getCity(),
                                    career.getPosition(),
                                    career.getSalary(),
                                    career.getYearOfService(),
                                    career.getWorkMail());
            result.setTotalYearOfService(career.getTotalYearOfService());
        }
        return result;
    }

    /**
     * handle finance info
     *
     * @param finance
     * @return
     */
    public static com.creditcloud.model.user.info.FinanceInfo getFinanceInfo(FinanceInfo finance) {
        com.creditcloud.model.user.info.FinanceInfo result = null;
        if (finance != null) {
            result = new com.creditcloud.model.user.info.FinanceInfo(finance.hasHouse(),
                                                                     finance.getHouseNumber(),
                                                                     finance.hasHouseLoan(),
                                                                     finance.hasCar(),
                                                                     finance.getCarNumber(),
                                                                     finance.hasCarLoan());
        }

        return result;
    }

    public static FinanceInfo convertFinanceInfo(com.creditcloud.model.user.info.FinanceInfo finance) {
        FinanceInfo result = null;
        if (finance != null) {
            result = new FinanceInfo(finance.hasHouse(),
                                     finance.getHouseNumber(),
                                     finance.hasHouseLoan(),
                                     finance.hasCar(),
                                     finance.getCarNumber(),
                                     finance.hasCarLoan());
        }

        return result;
    }

    /**
     * handle user info
     *
     * @param userInfo
     * @param user
     * @return
     */
    public static com.creditcloud.model.user.info.UserInfo getUserInfo(UserInfo userInfo, User user) {
        com.creditcloud.model.user.info.UserInfo result = null;
        if (userInfo != null) {
            result = new com.creditcloud.model.user.info.UserInfo(getUserDTO(user),
                                                                  getPersonalInfo(userInfo.getPersonal()),
                                                                  getFinanceInfo(userInfo.getFinance()),
                                                                  getCareerInfo(userInfo.getCareer()),
                                                                  getContactInfo(userInfo.getContact()),
                                                                  getSocialInfo(userInfo.getSocial()));
            result.setPriv(userInfo.getPriv());
        }

        return result;
    }

    public static UserInfo convertUserInfo(com.creditcloud.model.user.info.UserInfo userInfo, User user) {
        UserInfo result = null;
        if (userInfo != null) {
            result = new UserInfo(user,
                                  convertPersonalInfo(userInfo.getPersonal()),
                                  convertFinanceInfo(userInfo.getFinance()),
                                  convertCareerInfo(userInfo.getCareer()),
                                  convertContactInfo(userInfo.getContact()),
                                  convertSocialInfo(userInfo.getSocial()));
            result.setPriv(userInfo.getPriv());
        }

        return result;
    }

    /**
     * handle user credit
     *
     * @param credit
     * @return
     */
    public static com.creditcloud.model.user.credit.UserCredit getUserCredit(UserCredit credit) {
        com.creditcloud.model.user.credit.UserCredit result = null;
        if (credit != null) {
            result = new com.creditcloud.model.user.credit.UserCredit(credit.getUserId(),
                                                                      credit.getCreditRank(),
                                                                      getAssessment(credit.getAssessment()),
                                                                      credit.getCreditLimit(),
                                                                      credit.getCreditAvailable(),
                                                                      credit.getLastModifiedBy(),
                                                                      credit.getTimeCreated(),
                                                                      credit.getTimeLastUpdated());
        }

        return result;
    }

    public static UserCredit convertUserCredit(com.creditcloud.model.user.credit.UserCredit credit, User user) {
        UserCredit result = null;
        if (credit != null) {
            result = new UserCredit(user,
                                    credit.getCreditRank(),
                                    convertAssessment(credit.getAssessment()),
                                    credit.getCreditLimit(),
                                    credit.getCreditAvailable(),
                                    credit.getLastModifiedBy());
        }

        return result;
    }

    /**
     * handle Proof
     *
     * @param proof
     * @return
     */
    public static com.creditcloud.model.user.credit.Proof getProof(Proof proof) {
        com.creditcloud.model.user.credit.Proof result = null;
        if (proof != null) {
            result = new com.creditcloud.model.user.credit.Proof(proof.getId(),
                                                                 proof.getCertificate().getCredit().getUserId(),
                                                                 com.creditcloud.common.utils.DTOUtils.getRealmEntity(proof.getOwner()),
                                                                 proof.getProofType(),
                                                                 proof.getContentType(),
                                                                 proof.getContent(),
                                                                 proof.getDescription(),
                                                                 proof.getSource(),
                                                                 proof.getSubmitTime(),
                                                                 proof.getEmployee(),
                                                                 proof.isMosaic(),
                                                                 proof.getLongitude(),
                                                                 proof.getLatitude());
            result.setCover(proof.isCover());
        }
        return result;
    }

    public static Proof convertProof(com.creditcloud.model.user.credit.Proof proof, Certificate certificate) {
        Proof result = null;
        if (proof != null) {
            result = new Proof(certificate,
                               com.creditcloud.common.utils.DTOUtils.convertRealmEntity(proof.getOwner()),
                               proof.getProofType(),
                               proof.getContentType(),
                               proof.getContent(),
                               proof.getDescription(),
                               proof.getSource(),
                               proof.getSubmitTime(),
                               proof.getEmployee(),
                               proof.isMosaic(),
                               proof.getLongitude(),
                               proof.getLatitude(),
                               proof.isCover());
            result.setId(proof.getId());
        }

        return result;
    }

    /**
     * handle collection of Proof
     *
     * @param proofs
     * @return
     */
    public static Collection<com.creditcloud.model.user.credit.Proof> getProofCollection(Collection<Proof> proofs) {
        Collection<com.creditcloud.model.user.credit.Proof> result = new ArrayList<>();
        if (proofs != null) {
            for (Proof proof : proofs) {
                result.add(DTOUtils.getProof(proof));
            }
        }
        return result;
    }

    /**
     * handle Certificate
     *
     * @param certificate
     * @return
     */
    public static com.creditcloud.model.user.credit.Certificate getCertificate(Certificate certificate) {
        com.creditcloud.model.user.credit.Certificate result = null;
        if (certificate != null) {
            result = new com.creditcloud.model.user.credit.Certificate(certificate.getId(),
                                                                       certificate.getCredit().getUserId(),
                                                                       certificate.getType(),
                                                                       certificate.getStatus(),
                                                                       certificate.getAuditor(),
                                                                       certificate.getAuditInfo(),
                                                                       getAssessment(certificate.getAssessment()),
                                                                       certificate.getTimeCreated(),
                                                                       certificate.getTimeLastUpdated());
        }

        return result;
    }

    public static Certificate convertCertificate(com.creditcloud.model.user.credit.Certificate certificate, UserCredit credit) {
        Certificate result = null;
        if (certificate != null) {
            result = new Certificate(credit,
                                     certificate.getType(),
                                     certificate.getStatus(),
                                     certificate.getAuditor(),
                                     certificate.getAuditInfo(),
                                     convertAssessment(certificate.getAssessment()));
            result.setTimeLastUpdated(certificate.getTimeLastModified());
            result.setId(certificate.getId());
        }

        return result;
    }

    /**
     * handle Assessment
     *
     * @param assess
     * @return
     */
    public static com.creditcloud.model.user.credit.Assessment getAssessment(Assessment assess) {
        com.creditcloud.model.user.credit.Assessment result = null;
        if (assess != null) {
            result = new com.creditcloud.model.user.credit.Assessment(assess.getScore());
        }
        return result;
    }

    public static Assessment convertAssessment(com.creditcloud.model.user.credit.Assessment assess) {
        Assessment result = null;
        if (assess != null) {
            result = new Assessment(assess.getScore());
        }
        return result;
    }

    /**
     * handle CreditRecord
     *
     * @param record
     * @return
     */
    public static com.creditcloud.model.user.credit.CertificateRecord getCertificateRecord(CertificateRecord record) {
        com.creditcloud.model.user.credit.CertificateRecord result = null;
        if (record != null) {
            result = new com.creditcloud.model.user.credit.CertificateRecord(record.getStatus(),
                                                                             record.getAuditor(),
                                                                             record.getAuditInfo(),
                                                                             getAssessment(record.getAssessment()),
                                                                             record.getTimeRecorded());
        }
        return result;
    }

    /**
     * handle RealEstate
     *
     * @param estate
     * @return
     */
    public static com.creditcloud.model.user.asset.RealEstate getRealEstate(RealEstate estate) {
        com.creditcloud.model.user.asset.RealEstate result = null;
        if (estate != null) {
            result = new com.creditcloud.model.user.asset.RealEstate(estate.getId(),
                                                                     estate.getUser().getId(),
                                                                     estate.getType(),
                                                                     estate.getLocation(),
                                                                     estate.getArea(),
                                                                     estate.isLoan(),
                                                                     estate.getEstimatedValue(),
                                                                     estate.getDescription(),
                                                                     estate.getTimeCreated(),
                                                                     estate.getTimeLastUpdated(),
                                                                     estate.getLongitude(),
                                                                     estate.getLatitude(),
                                                                     estate.getLastModifiedBy(),
                                                                     estate.getSource());
        }
        return result;
    }

    public static RealEstate convertRealEstate(com.creditcloud.model.user.asset.RealEstate estate, User user, Collection<Proof> proofs) {
        RealEstate result = null;
        if (estate != null) {
            result = new RealEstate(user,
                                    estate.getType(),
                                    estate.getLocation(),
                                    estate.getArea(),
                                    estate.isLoan(),
                                    estate.getEstimatedValue(),
                                    estate.getDescription(),
                                    estate.getLongitude(),
                                    estate.getLatitude(),
                                    estate.getLastModifiedBy(),
                                    estate.getSource());
            result.setId(estate.getId());
        }
        return result;
    }

    /**
     * handle Vehicle
     *
     * @param vehicle
     * @return
     */
    public static com.creditcloud.model.user.asset.Vehicle getVehicle(Vehicle vehicle) {
        com.creditcloud.model.user.asset.Vehicle result = null;
        if (vehicle != null) {
            result = new com.creditcloud.model.user.asset.Vehicle(vehicle.getId(),
                                                                  vehicle.getUser().getId(),
                                                                  com.creditcloud.common.utils.DTOUtils.getRealmEntity(vehicle.getOwner()),
                                                                  vehicle.getModel(),
                                                                  vehicle.getType(),
                                                                  vehicle.getVehicleLicense(),
                                                                  vehicle.getPlateNumber(),
                                                                  vehicle.getYearOfPurchase(),
                                                                  vehicle.getPriceOfPurchase(),
                                                                  vehicle.getEstimatedValue(),
                                                                  vehicle.getDescription(),
                                                                  vehicle.getBrand(),
                                                                  vehicle.isOperating(),
                                                                  vehicle.getMileage(),
                                                                  vehicle.getSource(),
                                                                  vehicle.getLastModifiedBy());
            result.setTimeCreated(vehicle.getTimeCreated());
            result.setTimeLastUpdated(vehicle.getTimeLastUpdated());
        }
        return result;
    }

    public static Vehicle convertVehicle(com.creditcloud.model.user.asset.Vehicle vehicle, User user, Collection<Proof> proofs) {
        Vehicle result = null;
        if (vehicle != null) {
            result = new Vehicle(user,
                                 com.creditcloud.common.utils.DTOUtils.convertRealmEntity(vehicle.getOwner()),
                                 vehicle.getBrand(),
                                 vehicle.isOperating(),
                                 vehicle.getMileage(),
                                 vehicle.getModel(),
                                 vehicle.getType(),
                                 vehicle.getVehicleLicense(),
                                 vehicle.getPlateNumber(),
                                 vehicle.getYearOfPurchase(),
                                 vehicle.getPriceOfPurchase(),
                                 vehicle.getEstimatedValue(),
                                 vehicle.getDescription(),
                                 vehicle.getSource(),
                                 vehicle.getLastModifiedBy());
            result.setId(vehicle.getId());
        }
        return result;
    }

    /**
     * handle VehicleRecord
     *
     * @param record
     * @return
     */
    public static com.creditcloud.model.user.asset.VehicleRecord getVehicleRecord(VehicleRecord record) {
        com.creditcloud.model.user.asset.VehicleRecord result = null;
        if (record != null) {
            result = new com.creditcloud.model.user.asset.VehicleRecord(record.getModel(),
                                                                        record.getType(),
                                                                        record.getVehicleLicense(),
                                                                        record.getPlateNumber(),
                                                                        record.getYearOfPurchase(),
                                                                        record.getPriceOfPurchase(),
                                                                        record.getEstimatedValue(),
                                                                        record.getDescription(),
                                                                        record.getModifiedBy(),
                                                                        record.getSource(),
                                                                        record.getTimeRecorded());
        }
        return result;
    }

    /**
     * handle RealEstateRecord
     *
     * @param record
     * @return
     */
    public static com.creditcloud.model.user.asset.RealEstateRecord getRealEstateRecord(RealEstateRecord record) {
        com.creditcloud.model.user.asset.RealEstateRecord result = null;
        if (record != null) {
            result = new com.creditcloud.model.user.asset.RealEstateRecord(record.getType(),
                                                                           record.getLocation(),
                                                                           record.getArea(),
                                                                           record.isLoan(),
                                                                           record.getEstimatedValue(),
                                                                           record.getDescription(),
                                                                           record.getModifiedBy(),
                                                                           record.getSource(),
                                                                           record.getTimeRecorded(),
                                                                           record.getLongitude(),
                                                                           record.getLatitude());
        }
        return result;
    }

    /**
     * handle PersonalInfoRecord
     *
     * @param record
     * @return
     */
    public static com.creditcloud.model.user.info.PersonalInfoRecord getPlaceInfoRecord(PersonalInfoRecord record) {
        com.creditcloud.model.user.info.PersonalInfoRecord result = null;
        if (record != null) {
            result = new com.creditcloud.model.user.info.PersonalInfoRecord(record.getId(),
                                                                            record.getUser().getId(),
                                                                            record.getCurrentAddress(),
                                                                            record.getModifiedBy(),
                                                                            record.getSource(),
                                                                            record.getLongitude(),
                                                                            record.getLatitude());
        }
        return result;
    }

    /**
     * handle ComanyInfoRecord
     *
     * @param record
     * @return
     */
    public static com.creditcloud.model.user.info.CareerInfoRecord getCompanyInfoRecord(CareerInfoRecord record) {
        com.creditcloud.model.user.info.CareerInfoRecord result = null;
        if (record != null) {
            result = new com.creditcloud.model.user.info.CareerInfoRecord(record.getId(),
                                                                          record.getUser().getId(),
                                                                          record.getAddress(),
                                                                          record.getModifiedBy(),
                                                                          record.getSource(),
                                                                          record.getLongitude(),
                                                                          record.getLatitude());
        }
        return result;
    }

    /**
     * handle UserAuthenticate
     *
     * @param authenticate
     * @return
     */
    public static com.creditcloud.user.UserAuthenticate getUserAuthenticate(UserAuthenticate authenticate) {
        com.creditcloud.user.UserAuthenticate result = null;
        if (authenticate != null) {
            result = new com.creditcloud.user.UserAuthenticate(authenticate.getUserId(),
                                                               authenticate.isIDAuthenticated(),
                                                               authenticate.isMobileAuthenticated(),
                                                               authenticate.isEmailAuthenticated());
            result.setWechatAuthenticated(authenticate.isWechatAuthenticated());
            result.setWeiboAuthenticated(authenticate.isWeiboAuthenticated());
        }
        return result;
    }

    /**
     * handle UserCreditRecord
     *
     * @param record
     * @return
     */
    public static com.creditcloud.user.credit.UserCreditRecord getUserCreditRecord(UserCreditRecord record) {
        com.creditcloud.user.credit.UserCreditRecord result = null;
        if (record != null) {
            result = new com.creditcloud.user.credit.UserCreditRecord(record.getCreditRank(),
                                                                      getAssessment(record.getAssessment()),
                                                                      record.getCreditLimit(),
                                                                      record.getCreditAvailable(),
                                                                      record.getModifiedBy());
        }
        return result;
    }

    /**
     * handle CorporationUserUser
     *
     * @param corporation
     * @return
     */
    public static com.creditcloud.model.user.corporation.CorporationUser getCorporationDTO(CorporationUser corporation) {
        com.creditcloud.model.user.corporation.CorporationUser result = null;
        if (corporation != null) {
            result = new com.creditcloud.model.user.corporation.CorporationUser(DTOUtils.getUserDTO(corporation.getUser()),
                                                                                corporation.getName(),
                                                                                corporation.getShortName(),
                                                                                corporation.getOrgCode(),
                                                                                corporation.getBusiCode(),
                                                                                corporation.getTaxCode(),
                                                                                corporation.getType(),
                                                                                corporation.getCategory(),
                                                                                corporation.getLegalPersonId(),
                                                                                corporation.getRtpo());
            result.setContractSealCode(corporation.getContractSealCode());
        }
        return result;
    }

    public static CorporationUser convertCorporationDTO(com.creditcloud.model.user.corporation.CorporationUser corporation) {
        CorporationUser result = null;
        if (corporation != null) {
            result = new CorporationUser(convertUserDTO(corporation.getUser()),
                                         corporation.getName(),
                                         corporation.getShortName(),
                                         corporation.getOrgCode(),
                                         corporation.getBusiCode(),
                                         corporation.getTaxCode(),
                                         corporation.getType(),
                                         corporation.getCategory(),
                                         corporation.getLegalPersonId(),
                                         corporation.isRtpo());
            result.setContractSealCode(corporation.getContractSealCode());
        }
        return result;
    }

    /**
     * handle CorporationUserUserInfo
     *
     * @param info
     * @return
     */
    public static com.creditcloud.model.user.corporation.CorporationInfo getCorporationInfoDTO(CorporationInfo info) {
        com.creditcloud.model.user.corporation.CorporationInfo result = null;
        if (info != null) {
            result = new com.creditcloud.model.user.corporation.CorporationInfo(info.getUserId(),
                                                                                info.getUrl(),
                                                                                info.getAddress(),
                                                                                info.getContactPersion(),
                                                                                info.getContactPhone(),
                                                                                info.getContactEmail(),
                                                                                info.getRegisteredCapital(),
                                                                                info.getRegisteredLocation(),
                                                                                info.getTimeEstablished(),
                                                                                info.getBusinessScope(),
                                                                                info.getDescription());
            result.setBackground(info.getBackground());
            result.setCreditRank(info.getCreditRank());
        }
        return result;
    }

    public static com.creditcloud.model.user.UserLoginRecord getUserLoginRecord(UserLoginRecord info) {
        com.creditcloud.model.user.UserLoginRecord result = null;
        if (info != null) {
            result = new com.creditcloud.model.user.UserLoginRecord(info.getId(), info.getUser().getId(), getLoginRecord(info.getRecord()));
        }
        return result;
    }

    private static com.creditcloud.model.misc.LoginRecord getLoginRecord(com.creditcloud.common.entities.embedded.LoginRecord info) {
        com.creditcloud.model.misc.LoginRecord result = null;
        if (info != null) {
            result = new com.creditcloud.model.misc.LoginRecord(info.getLoginTime(), info.getLoginInfo(), info.getSource());
        }
        return result;
    }

    /**
     * handle ShippingAddress
     *
     * @param address
     * @return
     */
    public static com.creditcloud.user.model.ShippingAddress getShippingAddress(ShippingAddress address) {
        com.creditcloud.user.model.ShippingAddress result = null;
        if (address != null) {
            result = new com.creditcloud.user.model.ShippingAddress(address.getId(),
                                                                    address.getUserId(),
                                                                    address.getRealName(),
                                                                    address.getContact(),
                                                                    address.getEmail(),
                                                                    address.getDetail(),
                                                                    address.isDefaultAddress(),
                                                                    address.getAlias());
        }
        return result;
    }

    public static ShippingAddress convertShippingAddress(com.creditcloud.user.model.ShippingAddress address) {
        ShippingAddress result = null;
        if (address != null) {
            result = new ShippingAddress(address.getUserId(),
                                         address.getRealName(),
                                         address.getContact(),
                                         address.getEmail(),
                                         address.getDetail(),
                                         address.isDefaultAddress(),
                                         address.getAlias());
            result.setId(address.getId());
        }
        return result;
    }
}
