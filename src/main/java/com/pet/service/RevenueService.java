package com.pet.service;

import java.util.List;
import java.util.Map;

import com.pet.dto.PaymentListDTO;
import com.pet.dto.RevenueDTO;
import com.pet.entity.Payment;

public interface RevenueService {
	public void updateRevenue(Payment payment, List<PaymentListDTO> paymentList, String sessionId);
	public RevenueDTO dailySales(Integer userIdx);
	public Map<String, Object> monthSales(Integer userIdx, Integer year, Integer month);
	public Map<String, Object> yearSales(Integer userIdx, Integer year);
}
