package com.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	public Category findByCtgIdx(Integer ctgIdx);
}
