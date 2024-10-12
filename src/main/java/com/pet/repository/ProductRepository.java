package com.pet.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pet.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	public Product findByPdIdx(Integer pdIdx);

	@Query("SELECT p From Product p where p.pdName LIKE %:pdName%")
	public List<Product> search(@Param("pdName") String pdName);
	
	public List<Product> findByUser_UserIdx(Integer userIdx);
	
	@Query("SELECT p From Product p join p.user u where p.pdName LIKE %:pdName% and u.userIdx = :userIdx")
	public List<Product> serachProduct(@Param("pdName") String pdName, @Param("userIdx") Integer userIdx);
}
