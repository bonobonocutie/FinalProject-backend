package com.pet.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.pet.dto.OrdersDTO;
import com.pet.dto.StockDTO;
import com.pet.entity.Orders;
import com.pet.entity.Stock;
import com.pet.repository.OrdersRepository;
import com.pet.repository.StockRepository;
import com.pet.service.OrdersService;

@Service
public class OrdersServiceImpl implements OrdersService {

	OrdersRepository ordersRepository;
	StockRepository stockRepository;

	public OrdersServiceImpl(OrdersRepository ordersRepository, StockRepository stockRepository) {
		this.ordersRepository = ordersRepository;
		this.stockRepository = stockRepository;
	}

	private static final ModelMapper mapper = new ModelMapper();

	@Override
	public List<List<StockDTO>> selectStock(Integer userIdx) {
		List<List<StockDTO>> dtoList = new ArrayList<>();

		List<Stock> stock = stockRepository.findByProduct_User_UserIdxAndProduct_Category_ctgNum1(userIdx, "공통");
		List<StockDTO> dto = stock.stream()
				.map(e -> mapper.map(e, StockDTO.class))
				.collect(Collectors.toList());
		dtoList.add(dto);

		stock = stockRepository.findByProduct_User_UserIdxAndProduct_Category_ctgNum1(userIdx, "강아지");
		dto = stock.stream()
				.map(e -> mapper.map(e, StockDTO.class))
				.collect(Collectors.toList());
		dtoList.add(dto);

		stock = stockRepository.findByProduct_User_UserIdxAndProduct_Category_ctgNum1(userIdx, "고양이");
		dto = stock.stream()
				.map(e -> mapper.map(e, StockDTO.class))
				.collect(Collectors.toList());
		dtoList.add(dto);


		return dtoList;
	}

	@Override
	public OrdersDTO save(OrdersDTO dto) {
		Orders orders = mapper.map(dto, Orders.class);
		System.out.println(orders);
		ordersRepository.save(orders);
		return dto;
	}

	@Override
	public OrdersDTO select(LocalDate date, Integer userIdx) {
		Orders orders = ordersRepository.findByOrderDateAndUser_UserIdx(date, userIdx);
		OrdersDTO dto = mapper.map(orders, OrdersDTO.class);

		return dto;
	}

	@Override
	public List<Object[]> findByPdIdx(Integer pdIdx) {
		return ordersRepository.findByPdIdx(pdIdx);
	}

}
