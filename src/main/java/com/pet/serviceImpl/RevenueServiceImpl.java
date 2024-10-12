package com.pet.serviceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.pet.dto.PaymentListDTO;
import com.pet.dto.RevenueDTO;
import com.pet.entity.Cart;
import com.pet.entity.Payment;
import com.pet.entity.Revenue;
import com.pet.repository.CartRepository;
import com.pet.repository.RevenueRepository;
import com.pet.service.RevenueService;

@Service
public class RevenueServiceImpl implements RevenueService {

	RevenueRepository revenueRepository;
	CartRepository cartRepository;

	public RevenueServiceImpl(RevenueRepository revenueRepository, CartRepository cartRepository) {
		this.revenueRepository = revenueRepository;
		this.cartRepository = cartRepository;
	}

	@Override
	public void  updateRevenue(Payment payment, List<PaymentListDTO> paymentList, String sessionId) {
		LocalDate today = LocalDate.now();
		Revenue revenue = revenueRepository.findByRvDate(today);

		Cart cart = cartRepository.findBySessionId(sessionId).get(0);
		Integer userIdx = cart.getProduct().getUser().getUserIdx();

		// 총 매출액을 계산합니다.
		int totalAmount = paymentList.stream()
				.mapToInt(item -> item.getAmount() * item.getPdCount()) // 각 항목의 가격에 수량을 곱합니다.
				.sum();

		System.out.println(totalAmount);

		if (revenue  != null) {
			revenue.setRvTotalPrice(revenue.getRvTotalPrice() + totalAmount);
		} else {
			revenue = Revenue.builder()
					.rvTotalPrice(totalAmount)
					.rvDate(today)
					.userIdx(userIdx)
					.build();
			revenueRepository.save(revenue);
		}
	}

	@Override
	public RevenueDTO dailySales(Integer userIdx) {
		ModelMapper mapper = new ModelMapper();
		
		Revenue byToday = revenueRepository.findByToday(userIdx);

		RevenueDTO revenueDTO = mapper.map(byToday, RevenueDTO.class);

		return revenueDTO;
	}

	@Override
	public Map<String, Object> monthSales(Integer userIdx, Integer year, Integer month) {
		ModelMapper mapper = new ModelMapper();

		// 달 일일 매출
		List<Revenue> byMonth = revenueRepository.findByMonth(year, month, userIdx);
		System.out.println(byMonth);
		
		// 한달 매출 금액
		Integer totalPrice = revenueRepository.findMonthlySales(year, month, userIdx);

		List<RevenueDTO> revenueDTO = byMonth.stream()
				.map(revenue -> mapper.map(revenue, RevenueDTO.class))
				.collect(Collectors.toList());

		Map<String, Object> result = new HashMap<>();
		result.put("dailySales", revenueDTO);
		result.put("totalPrice", totalPrice);

		return result;
	}

	@Override
	public Map<String, Object> yearSales(Integer userIdx, Integer year) {
		ModelMapper mapper = new ModelMapper();

		// 달 일일 매출
		List<Revenue> byYear = revenueRepository.findByYear(year, userIdx);

		// 연 매출 금액       
		Integer totalPrice = revenueRepository.findYearlySales(year, userIdx);

		Map<Integer, Integer> monthlySalesMap = byYear.stream()
				.collect(Collectors.groupingBy(
						revenue -> revenue.getRvDate().getMonthValue(),
						Collectors.summingInt(Revenue::getRvTotalPrice)
				));

		Map<Integer, Integer> formattedMonthlySales = new LinkedHashMap<>();
		formattedMonthlySales.putAll(monthlySalesMap);

		Map<String, Object> result = new HashMap<>();
		result.put("monthlySales", formattedMonthlySales);
		result.put("totalPrice", totalPrice);

		return result;
	}

}
