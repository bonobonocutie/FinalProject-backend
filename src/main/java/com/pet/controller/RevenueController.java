package com.pet.controller;

import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.pet.dto.RevenueDTO;
import com.pet.dto.UserDTO;
import com.pet.service.RevenueService;

@RestController
@RequestMapping("/revenue")
@SessionAttributes(names= {"userDTO"})
public class RevenueController {
	RevenueService revenueService;

	public RevenueController(RevenueService revenueService) {
		this.revenueService = revenueService;
	}

	// 일 매출 조회
	@GetMapping("/dailySales")
	public RevenueDTO daliySales(ModelMap model) {
		UserDTO user = (UserDTO)model.getAttribute("userDTO");
		Integer userIdx = user.getUserIdx();
		RevenueDTO daliySales = revenueService.dailySales(userIdx);
		return daliySales;
	}

	// 월 매출 조회
	@GetMapping("/monthSales/{year}/{month}")
	public Map<String, Object> monthSales(ModelMap model, @PathVariable Integer year, @PathVariable Integer month) {
		UserDTO user = (UserDTO)model.getAttribute("userDTO");
		Integer userIdx = user.getUserIdx();
		Map<String, Object> monthSales = revenueService.monthSales(userIdx, year, month);
		return monthSales;
	}

	// 연 매출 조회
	@GetMapping("/yearSales/{year}")
	public Map<String, Object> yearSales(ModelMap model, @PathVariable Integer year) {
		UserDTO user = (UserDTO)model.getAttribute("userDTO");
		Integer userIdx = user.getUserIdx();
		Map<String, Object> yearSales = revenueService.yearSales(userIdx, year);
		return yearSales ;
	}

}
