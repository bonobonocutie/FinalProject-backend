package com.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.entity.ProductInfo;

public interface ProductInfoRepository extends JpaRepository<ProductInfo, Integer> {
	public ProductInfo findByPdInfoIdx(Integer pdInfoIdx);
}
