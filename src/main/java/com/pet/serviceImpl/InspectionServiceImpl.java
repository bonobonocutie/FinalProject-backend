package com.pet.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.pet.dto.InspectionDTO;
import com.pet.dto.OrdersDTO;
import com.pet.entity.Inspection;
import com.pet.entity.Orders;
import com.pet.entity.Stock;
import com.pet.repository.InspectionRepository;
import com.pet.repository.OrdersRepository;
import com.pet.repository.StockRepository;
import com.pet.service.InspectionService;

@Service
public class InspectionServiceImpl implements InspectionService {

   InspectionRepository inspectionRepository;
   OrdersRepository ordersRepository;
   StockRepository stockRepository;
   
   public InspectionServiceImpl(InspectionRepository inspectionRepository, OrdersRepository ordersRepositorym, StockRepository stockRepository, OrdersRepository ordersRepository) {
      this.inspectionRepository = inspectionRepository;
      this.ordersRepository = ordersRepository;
      this.stockRepository = stockRepository;
   }

   private static final ModelMapper mapper = new ModelMapper();

   @Override
   public InspectionDTO save(InspectionDTO dto, Integer userIdx) {
      
      Inspection ins = mapper.map(dto, Inspection.class);
      
      Orders orders = ordersRepository.findByOrderDateAndUser_UserIdx(dto.getInsDate(), userIdx);
      
      ins.setOrders(orders);
      LocalDate today = LocalDate.now();
      ins.setInsDate(today);
      
      inspectionRepository.save(ins);
      
      // 재고 증가
      int n=0;
      for (Integer pdIdx : ins.getOrders().getPdIdx()) {
         Stock stock = stockRepository.findByProduct_PdIdx(pdIdx);
         stock.setStCount(stock.getStCount()+ins.getInsCount().get(n));
         stockRepository.save(stock);
         n++;
      }
      
      return dto;
   }

   @Override
   public List<InspectionDTO> select(LocalDate date, Integer userIdx) {
      List<Inspection> ins = inspectionRepository.findByInsDateAndOrders_User_UserIdx(date, userIdx);
      
      List<InspectionDTO> list = ins.stream()
            .map(e->mapper.map(e, InspectionDTO.class))
            .collect(Collectors.toList());
            
      return list;
   }

   @Override
   public OrdersDTO findByOrderIdx(Integer orderIdx) {
      Orders orders = ordersRepository.findByOrderIdx(orderIdx);
      OrdersDTO dto = mapper.map(orders, OrdersDTO.class);
      return dto;
   }

   @Override
   public OrdersDTO selectOrder(LocalDate orderDate, Integer userIdx) {
      Orders orders= inspectionRepository.selectOrder(orderDate, userIdx);
      OrdersDTO dto = mapper.map(orders, OrdersDTO.class);
      return dto;
   }

}
