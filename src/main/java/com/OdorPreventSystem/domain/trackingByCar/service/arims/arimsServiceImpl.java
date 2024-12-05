package com.OdorPreventSystem.domain.trackingByCar.service.arims;

import com.OdorPreventSystem.domain.mapper.OdorProofSystemMapper;
import com.OdorPreventSystem.domain.trackingByCar.dto.carDTO;
import com.OdorPreventSystem.domain.trackingByCar.dto.chemicalDataDTO;
import com.OdorPreventSystem.domain.trackingByCar.dto.placeDataDTO;

import com.OdorPreventSystem.domain.trackingByCar.vo.location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class arimsServiceImpl implements arimsService {

  @Autowired
  private OdorProofSystemMapper mapper;

  @Override
  public chemicalDataDTO getMsv(String chemicalName) throws Exception {
    return mapper.getMsv(chemicalName);
  }
  @Override
  public List<placeDataDTO> getplace() throws Exception {
    return mapper.getplace();
  }
  @Override
  public List<chemicalDataDTO> getCarCsvContent(String fileName) throws Exception {
    return mapper.getCarCsvContent(fileName);
  }
  @Override
  public List<chemicalDataDTO> getPlaceCsvContent(String fileName) throws Exception {
    return mapper.getPlaceCsvContent(fileName);
  }

  @Override
  public List<carDTO> getCar(
    String startTime,
    String endTime,
    String selectCar
  ) throws Exception {
    return mapper.getCar(startTime, endTime, selectCar);
  }


  @Override
  public List<location> getCarLocation(
    String startTime,
    String endTime,
    String selectCar
  ) throws Exception {
    return mapper.getCarLocation(startTime, endTime, selectCar);
  }


  @Override
  public List<carDTO> getRealtimeCar(
    String startTime,
    String endTime,
    String selectCar,
    String lastQueryTime
  )
    throws Exception {
    return mapper.getRealtimeCar(startTime, endTime,selectCar,lastQueryTime);
  }

  @Override
  public List<location> getRealtimeCarLocation(
    String startTime,
    String endTime,
    String selectCar,
    String lastQueryTime
  )
    throws Exception {
    return mapper.getRealtimeCarLocation(startTime, endTime,selectCar,lastQueryTime);
  }

  @Override
  public String[] getGPSDate(String carCode) throws Exception {
    return mapper.getGPSDate(carCode);
  }

  @Override
  public String[] searchPlace(String name) throws Exception{
    return mapper.searchPlace(name);
  }



}
