package com.pet.service;

import java.time.LocalDate;
import java.util.List;

import com.pet.dto.OrdersDTO;
import com.pet.dto.StockDTO;

public interface OrdersService {
	public List<List<StockDTO>> selectStock(Integer userIdx);

	public OrdersDTO save(OrdersDTO dto);

	public OrdersDTO select(LocalDate date, Integer userIdx);

	public List<Object[]> findByPdIdx(Integer pdIdx);
}
