package com.pet.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.CategoryDTO;
import com.pet.service.CategoryService;

@RestController
public class CategoryController {

	CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@PostMapping("/category/save")
	public List<CategoryDTO> save(@RequestBody List<CategoryDTO> dto) {
		List<CategoryDTO> list = new ArrayList<>();

		for (CategoryDTO categoryDTO : dto) {
			list.add(categoryService.save(categoryDTO));
		}
		return list;
	}

}
