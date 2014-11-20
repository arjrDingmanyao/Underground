/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user;

import com.creditcloud.model.constant.CacheConstant;
import com.creditcloud.model.enums.Realm;
import com.creditcloud.model.enums.misc.CacheType;
import com.creditcloud.model.enums.user.credit.CertificateType;
import com.creditcloud.model.enums.user.credit.ProofContentType;
import com.creditcloud.model.misc.RealmEntity;
import com.creditcloud.redis.api.SentinelService;
import com.creditcloud.user.api.UserAssetService;
import com.creditcloud.user.entity.Certificate;
import com.creditcloud.user.entity.Proof;
import com.creditcloud.user.entity.RealEstate;
import com.creditcloud.user.entity.User;
import com.creditcloud.user.entity.Vehicle;
import com.creditcloud.user.entity.dao.CertificateDAO;
import com.creditcloud.user.entity.dao.ProofDAO;
import com.creditcloud.user.entity.dao.RealEstateDAO;
import com.creditcloud.user.entity.dao.RealEstateRecordDAO;
import com.creditcloud.user.entity.dao.UserDAO;
import com.creditcloud.user.entity.dao.VehicleDAO;
import com.creditcloud.user.entity.dao.VehicleRecordDAO;
import com.creditcloud.user.entity.record.RealEstateRecord;
import com.creditcloud.user.entity.record.VehicleRecord;
import com.creditcloud.user.local.ApplicationBean;
import com.creditcloud.user.local.CreditLocalBean;
import com.creditcloud.user.utils.DTOUtils;
import java.util.ArrayList;
import java.util.Collections;
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
public class UserAssetServiceBean implements UserAssetService {

    @Inject
    Logger logger;

    @EJB
    UserDAO userDAO;

    @EJB
    RealEstateDAO estateDAO;

    @EJB
    VehicleDAO vehicleDAO;

    @EJB
    ProofDAO proofDAO;

    @EJB
    ApplicationBean appBean;

    @EJB
    CertificateDAO certificateDAO;

    @EJB
    CreditLocalBean creditBean;

    @EJB
    RealEstateRecordDAO estateRecordDAO;

    @EJB
    VehicleRecordDAO vehicleRecordDAO;

    @Override
    public com.creditcloud.model.user.asset.RealEstate getRealEstateById(String clientCode, String estateId) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getRealEstate(estateDAO.find(estateId));
    }

    @Override
    public com.creditcloud.model.user.asset.RealEstate addRealEstate(String clientCode,
                                                                     com.creditcloud.model.user.asset.RealEstate estate,
                                                                     com.creditcloud.model.user.credit.Proof... proofs) {
        appBean.checkClientCode(clientCode);
        User user = userDAO.find(estate.getUserId());
        if (user == null) {
            logger.warn("real estate with user {} not found.", estate.getUserId());
            return null;
        }
        List<Proof> entityProofs = new ArrayList<>();
        if (proofs != null && proofs.length != 0) {
            //get the corresponding certificate for real estate
            Certificate certifcate = creditBean.getCertificateByUserAndType(user.getId(), CertificateType.REALESTATE);
            for (com.creditcloud.model.user.credit.Proof proof : proofs) {
                if (!CertificateType.REALESTATE.equals(proof.getProofType().getCertificateType())) {
                    //invalid proofType
                    logger.warn("Illegal proof type {} to call addRealEstate", proof.getProofType());
                    continue;
                }
                //generate attached proof
                Proof entityProof = proofDAO.addOrUpdate(DTOUtils.convertProof(proof, certifcate));
                //check whether the proof already exist
                entityProofs.add(entityProof);
            }
        }
        //create real estate
        RealEstate entityEstate = DTOUtils.convertRealEstate(estate, user, entityProofs);
        RealEstate result = estateDAO.create(entityEstate);
        //create change record for this real estate
        RealEstateRecord record = new RealEstateRecord(result,
                                                       result.getType(),
                                                       result.getLocation(),
                                                       result.getArea(),
                                                       result.isLoan(),
                                                       result.getEstimatedValue(),
                                                       result.getDescription(),
                                                       estate.getLastModifiedBy(),
                                                       estate.getSource(),
                                                       result.getLongitude(),
                                                       result.getLatitude());
        estateRecordDAO.create(record);

        // delete cache
        appBean.deleteCache(user.getId(), CacheConstant.KEY_PREFIX_USER_INFO);
        
        return DTOUtils.getRealEstate(result);
    }

    @Override
    public boolean addProofForRealEstate(String clientCode,
                                         String estateId,
                                         com.creditcloud.model.user.credit.Proof... proofs) {
        appBean.checkClientCode(clientCode);
        if (proofs != null && proofs.length != 0) {
            RealEstate estate = estateDAO.find(estateId);
            if (estate == null) {
                logger.warn("real estate {} not found.", estateId);
                return false;
            }
            //get and create if not exist the certificate for estate
            Certificate certifcate = creditBean.getCertificateByUserAndType(estate.getUser().getId(), CertificateType.REALESTATE);
            for (com.creditcloud.model.user.credit.Proof proof : proofs) {
                if (!CertificateType.REALESTATE.equals(proof.getProofType().getCertificateType())) {
                    //invalid proofType
                    logger.warn("Illegal proof type {} to call addProofForRealEstate", proof.getProofType());
                    continue;
                }
                proofDAO.addOrUpdate(DTOUtils.convertProof(proof, certifcate));
            }
            //update estate
            
            // delete cache
            appBean.deleteCache(estate.getUser().getId(), CacheConstant.KEY_PREFIX_USER_INFO);
        
            return true;
        }
        return false;
    }

    @Override
    public List<com.creditcloud.model.user.credit.Proof> listProofByRealEstate(String clientCode, String estateId) {
        appBean.checkClientCode(clientCode);
        RealEstate estate = estateDAO.find(estateId);
        if (estate == null) {
            logger.warn("real estate {} not found.", estateId);
            return Collections.EMPTY_LIST;
        }
        List<Proof> proofs = proofDAO.listByUserAndOwner(estate.getUser().getId(),
                                                         new com.creditcloud.common.entities.embedded.RealmEntity(Realm.REALESTATE, estateId),
                                                         ProofContentType.values());
        List<com.creditcloud.model.user.credit.Proof> result = new ArrayList<>(proofs.size());
        for (Proof proof : proofs) {
            result.add(DTOUtils.getProof(proof));
        }

        return result;
    }

    @Override
    public com.creditcloud.model.user.asset.Vehicle getVehicleById(String clientCode, String vehicleId) {
        appBean.checkClientCode(clientCode);
        return DTOUtils.getVehicle(vehicleDAO.find(vehicleId));
    }

    @Override
    public com.creditcloud.model.user.asset.Vehicle addVehicle(String clientCode,
                                                               com.creditcloud.model.user.asset.Vehicle vehicle,
                                                               com.creditcloud.model.user.credit.Proof... proofs) {
        appBean.checkClientCode(clientCode);
        User user = userDAO.find(vehicle.getUserId());
        if (user == null) {
            logger.warn("vehicle with user {} not found", vehicle.getUserId());
            return null;
        }

//        Vehicle entity = vehicleDAO.findByPlateNumber(vehicle.getUserId(), vehicle.getPlateNumber());
//        if (entity != null) {
//            logger.warn("vehicle has the same plateNumber {} ", vehicle.getPlateNumber());
//            return null;
//        }
        List<Proof> entityProofs = new ArrayList<>();
        if (proofs != null && proofs.length != 0) {
            //get and create if not exist the certificate for vehicle
            Certificate certifcate = creditBean.getCertificateByUserAndType(user.getId(), CertificateType.VEHICLE);
            for (com.creditcloud.model.user.credit.Proof proof : proofs) {
                if (!CertificateType.VEHICLE.equals(proof.getProofType().getCertificateType())) {
                    //invalid proofType
                    logger.warn("Illegal proof type {} to call addVehicle", proof.getProofType());
                    continue;
                }
                //generate attached proof
                Proof entityProof = proofDAO.addOrUpdate(DTOUtils.convertProof(proof, certifcate));
                //check whether the proof already exist
                entityProofs.add(entityProof);
            }
        }
        //create vehicle
        Vehicle result = vehicleDAO.create(DTOUtils.convertVehicle(vehicle, user, entityProofs));
        //create record for this addtion
        VehicleRecord record = new VehicleRecord(result,
                                                 vehicle.getModel(),
                                                 vehicle.getType(),
                                                 vehicle.getVehicleLicense(),
                                                 vehicle.getPlateNumber(),
                                                 vehicle.getYearOfPurchase(),
                                                 vehicle.getPriceOfPurchase(),
                                                 vehicle.getEstimatedValue(),
                                                 vehicle.getDescription(),
                                                 vehicle.getLastModifiedBy(),
                                                 vehicle.getSource());
        vehicleRecordDAO.create(record);
        
        // delete cache
        appBean.deleteCache(user.getId(), CacheConstant.KEY_PREFIX_USER_INFO);
        
        return DTOUtils.getVehicle(result);
    }

    @Override
    public boolean addProofForVehicle(String clientCode, String vehicleId, com.creditcloud.model.user.credit.Proof... proofs) {
        appBean.checkClientCode(clientCode);
        if (proofs != null && proofs.length != 0) {
            Vehicle vehicle = vehicleDAO.find(vehicleId);
            if (vehicle == null) {
                logger.warn("vehicle {} not found.", vehicleId);
                return false;
            }
            //get the corresponding certificate
            Certificate certifcate = creditBean.getCertificateByUserAndType(vehicle.getUser().getId(), CertificateType.VEHICLE);
            for (com.creditcloud.model.user.credit.Proof proof : proofs) {
                if (!CertificateType.VEHICLE.equals(proof.getProofType().getCertificateType())) {
                    //invalid proofType
                    logger.warn("Illegal proof type {} to call addProofForVehicle", proof.getProofType());
                    continue;
                }
                proofDAO.addOrUpdate(DTOUtils.convertProof(proof, certifcate));
            }
            
            // delete cache
            appBean.deleteCache(vehicle.getId(), CacheConstant.KEY_PREFIX_USER_INFO);
            return true;
        }
        return false;
    }

    @Override
    public List<com.creditcloud.model.user.credit.Proof> listProofByVehicle(String clientCode, String vehicleId) {
        appBean.checkClientCode(clientCode);

        Vehicle vehicle = vehicleDAO.find(vehicleId);
        if (vehicle == null) {
            logger.warn("vehicle {} not found.", vehicleId);
            return Collections.EMPTY_LIST;
        }
        List<Proof> proofs = proofDAO.listByUserAndOwner(vehicle.getUser().getId(),
                                                         new com.creditcloud.common.entities.embedded.RealmEntity(Realm.VEHICLE, vehicleId),
                                                         ProofContentType.values());
        List<com.creditcloud.model.user.credit.Proof> result = new ArrayList<>(proofs.size());
        for (Proof proof : proofs) {
            result.add(DTOUtils.getProof(proof));
        }
        return result;
    }

    @Override
    public List<com.creditcloud.model.user.asset.RealEstate> listRealEstateByUser(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        List<RealEstate> estates = estateDAO.listByUser(userId);
        List<com.creditcloud.model.user.asset.RealEstate> result = new ArrayList<>(estates.size());
        for (RealEstate estate : estates) {
            result.add(DTOUtils.getRealEstate(estate));
        }
        return result;
    }

    @Override
    public List<com.creditcloud.model.user.asset.Vehicle> listVehicleByUser(String clientCode, String userId) {
        appBean.checkClientCode(clientCode);
        List<Vehicle> vehicles = vehicleDAO.listByUser(userId);
        List<com.creditcloud.model.user.asset.Vehicle> result = new ArrayList<>(vehicles.size());
        for (Vehicle vehicle : vehicles) {
            result.add(DTOUtils.getVehicle(vehicle));
        }
        return result;
    }

    @Override
    public boolean updateRealEstate(String clientCode, com.creditcloud.model.user.asset.RealEstate estate) {
        appBean.checkClientCode(clientCode);
        //update estate
        estateDAO.update(estate);
        
        // delete cache
        appBean.deleteCache(estate.getUserId(), CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public boolean updateVehicle(String clientCode, com.creditcloud.model.user.asset.Vehicle vehicle) {
        appBean.checkClientCode(clientCode);
        //update vehicle
        vehicleDAO.update(vehicle);
        
        // delete cache
        appBean.deleteCache(vehicle.getUserId(), CacheConstant.KEY_PREFIX_USER_INFO);
        return true;
    }

    @Override
    public List<com.creditcloud.model.user.asset.RealEstateRecord> listRecordByRealEstate(String clientCode, String estateId) {
        appBean.checkClientCode(clientCode);
        List<RealEstateRecord> records = estateRecordDAO.listByEstate(estateId);
        List<com.creditcloud.model.user.asset.RealEstateRecord> result = new ArrayList<>(records.size());
        for (RealEstateRecord record : records) {
            result.add(DTOUtils.getRealEstateRecord(record));
        }
        return result;
    }

    @Override
    public List<com.creditcloud.model.user.asset.VehicleRecord> listRecordByVehicle(String clientCode, String vehicleId) {
        appBean.checkClientCode(clientCode);
        List<VehicleRecord> records = vehicleRecordDAO.listByVehicle(vehicleId);
        List<com.creditcloud.model.user.asset.VehicleRecord> result = new ArrayList<>(records.size());
        for (VehicleRecord record : records) {
            result.add(DTOUtils.getVehicleRecord(record));
        }
        return result;
    }

    @Override
    public List<com.creditcloud.model.user.asset.Vehicle> listVehicleByOwner(String clientCode, RealmEntity owner) {
        appBean.checkClientCode(clientCode);
        List<Vehicle> vehicles = vehicleDAO.listByOwner(com.creditcloud.common.utils.DTOUtils.convertRealmEntity(owner));
        List<com.creditcloud.model.user.asset.Vehicle> result = new ArrayList<>(vehicles.size());
        for (Vehicle vehicle : vehicles) {
            result.add(DTOUtils.getVehicle(vehicle));
        }
        return result;
    }

    @Override
    public List<com.creditcloud.model.user.asset.Vehicle> listVehicleByUserAndPlateNumber(String clientCode, String userId, String plateNumber) {
        appBean.checkClientCode(clientCode);
        List<Vehicle> vehicles = vehicleDAO.listByPlateNumber(userId, plateNumber);
        List<com.creditcloud.model.user.asset.Vehicle> result = new ArrayList<>(vehicles.size());
        for (Vehicle vehicle : vehicles) {
            result.add(DTOUtils.getVehicle(vehicle));
        }
        return result;
    }
}
