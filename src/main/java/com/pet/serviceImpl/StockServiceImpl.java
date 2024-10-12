package com.pet.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dto.StockDTO;
import com.pet.entity.Product;
import com.pet.entity.Stock;
import com.pet.repository.ProductRepository;
import com.pet.repository.StockRepository;
import com.pet.service.StockService;

@Service
public class StockServiceImpl implements StockService {
   
   StockRepository stockRepository;
   ProductRepository productRepository;

   public StockServiceImpl(StockRepository stockRepository, ProductRepository productRepositoryCategoryRepository,
         ProductRepository productRepository) {
      this.stockRepository = stockRepository;
      this.productRepository = productRepository;
   }
   
   private static final ModelMapper mapper = new ModelMapper();
   
   @Override
   public StockDTO saveData(StockDTO dto) {
      Stock stock = mapper.map(dto, Stock.class);
      Product product = productRepository.findByPdIdx(dto.getPdIdx());
      stock.setProduct(product);
      
      stockRepository.save(stock);
      return dto;
   }

   @Override
   public List<StockDTO> selectAll(Integer userIdx) {
      List<Stock> list = stockRepository.findByProduct_User_UserIdx(userIdx);
      
      List<StockDTO> stockList = list.stream()
                  .map(e->mapper.map(e, StockDTO.class))
                  .collect(Collectors.toList());
      
      return stockList;
   }


	@Override
	@Transactional
	public void deleteStock(Integer pdIdx) {
		stockRepository.deleteStock(pdIdx);
	}

}
