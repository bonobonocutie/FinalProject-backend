package com.pet.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pet.dto.ProductDTO;
import com.pet.entity.Product;
import com.pet.repository.ProductRepository;

@Service
public class ChatBotService {
	ProductRepository productRepository;

	public ChatBotService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	private static final ModelMapper mapper = new ModelMapper();
	
	public ResponseEntity<?> serachProduct(String pdName, Integer userIdx) {
		List<Product> list = productRepository.serachProduct(pdName, userIdx);
		
		if(list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body("null");
		}
		
		List<ProductDTO> product = list.stream()
                .map(e->mapper.map(e, ProductDTO.class))
                .collect(Collectors.toList());
		
		return ResponseEntity.ok(product);
	}
}
