package com.pet.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.pet.dto.OrdersDTO;
import com.pet.dto.StockDTO;
import com.pet.dto.UserDTO;
import com.pet.service.OrdersService;

@RestController
@RequestMapping("/orders")
@SessionAttributes(names= {"userDTO"})
public class OrdersController {
   OrdersService ordersService;

   public OrdersController(OrdersService ordersService) {
      this.ordersService = ordersService;
   }
   
   @GetMapping("/selectStock")
   public List<List<StockDTO>> selectStock(ModelMap model){
      UserDTO user = (UserDTO)model.getAttribute("userDTO");
      List<List<StockDTO>> list = ordersService.selectStock(user.getUserIdx());
      return list;
   }
   
   @PostMapping("/save")
   public OrdersDTO save(@RequestBody OrdersDTO dto, ModelMap model){
      UserDTO user = (UserDTO)model.getAttribute("userDTO");
      dto.setUser(user);
      ordersService.save(dto);
      return dto;
   }
   
   @GetMapping("/select/{userIdx}/{orderDate}")
   public List<List<Object>> select(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate, @PathVariable Integer userIdx){
      OrdersDTO dto = ordersService.select(orderDate, userIdx);
      
      List<List<Object>> list = new ArrayList<>();
      int n=0;
      for (Integer pdIdx : dto.getPdIdx()) {
         List<Object[]> object = ordersService.findByPdIdx(pdIdx);
         List<Object> as = new ArrayList<>();
         as.add(object.get(0)[0]);
         as.add(object.get(0)[1]);
         as.add(object.get(0)[2]);
         as.add(dto.getOrderCount().get(n));
         list.add(as);
         n++;
      }
      return list;
  }
   
}
