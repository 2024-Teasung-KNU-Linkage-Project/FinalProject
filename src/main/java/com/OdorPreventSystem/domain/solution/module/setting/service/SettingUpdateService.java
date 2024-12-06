package com.OdorPreventSystem.domain.solution.module.setting.service;

import java.util.List;

import com.OdorPreventSystem.domain.solution.module.result.repository.OdorPreventionFacilityRepository;
import com.OdorPreventSystem.domain.solution.module.result.repository.OdorSubstanceRepository;
import com.OdorPreventSystem.domain.solution.module.setting.respository.RemovalEfficiencyRepository;
import com.OdorPreventSystem.domain.solution.module.setting.respository.WaterSolubilityRepository;
import org.springframework.stereotype.Service;

import com.OdorPreventSystem.domain.solution.module.setting.dto.OdorPreventionFacilityRequest;
import com.OdorPreventSystem.domain.solution.module.setting.dto.OdorSubstanceRequest;
import com.OdorPreventSystem.domain.solution.module.setting.dto.RemovalEfficiencyRequest;
import com.OdorPreventSystem.domain.solution.entity.OdorPreventionFacility;
import com.OdorPreventSystem.domain.solution.entity.OdorSubstance;
import com.OdorPreventSystem.domain.solution.entity.RemovalEfficiency;
import com.OdorPreventSystem.domain.solution.entity.WaterSolubility;

/*
 * SettingUpdateService: 
 *     설정을 데이터베이스에 업데이트
 */
@Service
public class SettingUpdateService {

    private final OdorSubstanceRepository odorSubstanceRepo;
    private final OdorPreventionFacilityRepository odorPreventionFacilityRepo;
    private final RemovalEfficiencyRepository removalEfficiencyRepo;
    private final WaterSolubilityRepository waterSolubilityRepo;

    public SettingUpdateService(OdorSubstanceRepository odorSubstanceRepo,
            OdorPreventionFacilityRepository odorPreventionFacilityRepo,
            RemovalEfficiencyRepository removalEfficiencyRepo,
            WaterSolubilityRepository waterSolubilityRepo) {
        this.odorSubstanceRepo = odorSubstanceRepo;
        this.odorPreventionFacilityRepo = odorPreventionFacilityRepo;
        this.removalEfficiencyRepo = removalEfficiencyRepo;
        this.waterSolubilityRepo = waterSolubilityRepo;
    }

    /*
     * updateOdor:
     *     악취기여물질 설정 데이터베이스에 업데이트
     */
    public void updateOdor(List<OdorSubstanceRequest> odorRequests) {
        for (OdorSubstanceRequest o : odorRequests) {
            OdorSubstance odor = odorSubstanceRepo.findById(o.getIdx()).orElse(null);
            WaterSolubility w = waterSolubilityRepo
                .findById(o.getWaterSolubility().getIdx()).orElse(null);
                  // name_en 값을 기존 객체에서 가져옵니다.
                String nameEn = odor != null ? odor.getName_en() : null;
                
            odorSubstanceRepo.save(new OdorSubstance(o.getIdx(),
                        odor.getName(), odor.getCategory(), o.getThreshold(),
                        new WaterSolubility(w.getIdx(), w.getName()), nameEn));
        }
    }

    /*
     * updateFacility:
     *     악취방지시설 설정 데이터베이스에 업데이트
     */
    public void updateFacility(List<OdorPreventionFacilityRequest> facilities,
        List<RemovalEfficiencyRequest> efficiencies) {
        for (OdorPreventionFacilityRequest f : facilities) {
            OdorPreventionFacility facility = odorPreventionFacilityRepo
                .findById(f.getIdx()).orElse(null);
            if (facility != null) 
                odorPreventionFacilityRepo.save(new OdorPreventionFacility(
                            f.getIdx(), facility.getName(), facility.getCategory(),
                            facility.getRequiredAdditional(),
                            facility.getPriority(), f.getBasePrice(),
                            f.getPriceByWind(), facility.getImage()));
        }
        for (RemovalEfficiencyRequest e : efficiencies) {
            RemovalEfficiency efficiency = removalEfficiencyRepo
                .findById(e.getIdx()).orElse(null);
            if (efficiency != null)
                removalEfficiencyRepo.save(new RemovalEfficiency(
                            e.getIdx(), efficiency.getFacility(), e.getEfficiency1(),
                            e.getEfficiency2(), e.getEfficiency3()));
        }
    }

}
