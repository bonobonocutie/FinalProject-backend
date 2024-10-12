package com.pet.service;

import java.util.List;

import com.pet.dto.ProductDTO;
import com.pet.dto.ProductInfoDTO;

public interface ProductService {
	public List<Integer> findAllIdx();

	public List<ProductDTO> findAll(Integer userIdx);

	public ProductDTO findById(Integer pdIdx);

	public ProductInfoDTO findByPdInfoIdx(Integer pdIdx);

	public ProductDTO save(ProductDTO dto);

	public ProductInfoDTO productInfoSave(ProductInfoDTO dto);

	public ProductDTO saveData(ProductDTO dto);

	public ProductDTO update(ProductDTO dto, String imgUrl);

	public List<ProductDTO> search(String pdName);

	public void deleteById(Integer pdIdx);
}
