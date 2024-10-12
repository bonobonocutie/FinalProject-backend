package com.pet.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.pet.dto.StockDTO;
import com.pet.dto.UserDTO;
import com.pet.service.StockService;

@RestController
@RequestMapping("/stock")
@SessionAttributes(names= {"userDTO"})
public class StockController {
   
   StockService stockService;
   
   public StockController(StockService stockService) {
      this.stockService = stockService;
   }

   @PostMapping("/saveData")
   public List<StockDTO> saveData(@RequestBody List<StockDTO> dto){
      List<StockDTO> list = new ArrayList<>();
      for (StockDTO stockDTO : dto) {
         list.add(stockService.saveData(stockDTO));
      }
      return list;
   }
   
   @GetMapping("/selectAll")
   public List<StockDTO> selectAll(ModelMap model) {
      UserDTO dto = (UserDTO)model.getAttribute("userDTO");
      List<StockDTO> list = stockService.selectAll(dto.getUserIdx());
      
      return list;
   }

}
