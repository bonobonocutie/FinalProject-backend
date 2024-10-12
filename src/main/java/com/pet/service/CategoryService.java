package com.pet.service;

import com.pet.dto.CategoryDTO;

public interface CategoryService {
	public CategoryDTO save(CategoryDTO dto);

	public CategoryDTO findByCtgIdx(Integer ctgIdx);
}
