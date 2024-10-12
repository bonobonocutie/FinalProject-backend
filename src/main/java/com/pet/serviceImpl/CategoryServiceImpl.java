package com.pet.serviceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.pet.dto.CategoryDTO;
import com.pet.entity.Category;
import com.pet.repository.CategoryRepository;
import com.pet.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	CategoryRepository categoryRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public CategoryDTO save(CategoryDTO dto) {
		ModelMapper mapper = new ModelMapper();
		Category category = mapper.map(dto, Category.class);

		categoryRepository.save(category);

		return dto;
	}

	@Override
	public CategoryDTO findByCtgIdx(Integer ctgIdx) {
		ModelMapper mapper = new ModelMapper();
		Category category = categoryRepository.findByCtgIdx(ctgIdx);
		CategoryDTO dto = mapper.map(category, CategoryDTO.class);
		return dto;
	}

}
