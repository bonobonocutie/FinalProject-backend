package com.pet.service;

import java.time.LocalDate;
import java.util.List;

import com.pet.dto.InspectionDTO;
import com.pet.dto.OrdersDTO;

public interface InspectionService {
   public OrdersDTO selectOrder(LocalDate orderDate, Integer userIdx);
   public InspectionDTO save(InspectionDTO dto, Integer userIdx);
   public List<InspectionDTO> select(LocalDate date, Integer userIdx);
   public OrdersDTO findByOrderIdx(Integer orderIdx);
}
