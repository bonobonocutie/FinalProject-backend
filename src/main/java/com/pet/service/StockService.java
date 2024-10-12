package com.pet.service;

import java.util.List;

import com.pet.dto.StockDTO;

public interface StockService {
	public StockDTO saveData(StockDTO dto);

	public List<StockDTO> selectAll(Integer userIdx);

	public void deleteStock(Integer pdIdx);
}
