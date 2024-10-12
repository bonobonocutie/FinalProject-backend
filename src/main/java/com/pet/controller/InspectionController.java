package com.pet.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.pet.dto.InspectionDTO;
import com.pet.dto.OrdersDTO;
import com.pet.dto.UserDTO;
import com.pet.service.InspectionService;
import com.pet.service.OrdersService;

@RestController
@RequestMapping("/inspection")
@SessionAttributes(names= {"userDTO"})
public class InspectionController {
   
   InspectionService inspectionService;
   OrdersService ordersService;

   public InspectionController(InspectionService inspectionService, OrdersService ordersService) {
      this.inspectionService = inspectionService;
      this.ordersService = ordersService;
   }
   
   @GetMapping("/selectOrder/{userIdx}/{orderDate}")
   public OrdersDTO selectOrder(@PathVariable LocalDate orderDate, @PathVariable Integer userIdx){
      OrdersDTO dto = inspectionService.selectOrder(orderDate, userIdx);
      return dto;
   }
   

   @PostMapping("/save")
   public InspectionDTO save(@RequestBody InspectionDTO dto, ModelMap model){
      UserDTO user = (UserDTO)model.getAttribute("userDTO");
      System.out.println(dto);
      return inspectionService.save(dto, user.getUserIdx());
   }
   
   @GetMapping("/select/{userIdx}/{insDate}")
   public List<List<List<Object>>> select(@PathVariable LocalDate insDate, @PathVariable Integer userIdx){
      List<InspectionDTO> iDto = inspectionService.select(insDate, userIdx);
      
      List<List<List<Object>>> list = new ArrayList<>();
      
      List<OrdersDTO> oDto = new ArrayList<>();
      int i = 0;
      for (InspectionDTO inspectionDTO : iDto) {
         oDto.add(inspectionService.findByOrderIdx(inspectionDTO.getOrders().getOrderIdx()));
         int n=0;
         List<List<Object>> asList = new ArrayList<>();
         for (Integer pdIdx : oDto.get(i).getPdIdx()) {
            List<Object[]> object = ordersService.findByPdIdx(pdIdx);
            List<Object> as = new ArrayList<>();
            as.add(object.get(0)[0]); // pdName
            as.add(oDto.get(i).getOrderCount().get(n));
            as.add(iDto.get(i).getInsCount().get(n));
            as.add(iDto.get(i).getInsExDate().get(n));
            as.add(iDto.get(i).getInsDetail().get(n));
            as.add(oDto.get(i).getOrderDate());
            asList.add(as);
            n++;
         }
         i++;
         list.add(asList);
      }
      
      return list;
   }
}
