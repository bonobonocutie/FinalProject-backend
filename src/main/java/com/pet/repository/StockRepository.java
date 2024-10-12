package com.pet.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.pet.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {
	// 재고 - 전체 조회
	public List<Stock> findByProduct_User_UserIdx(Integer userIdx);

	// 발주 신청 - 재고 출력
	public List<Stock> findByProduct_User_UserIdxAndProduct_Category_ctgNum1(Integer userIdx, String ctgNum1);

	// 발주 내역 - 상품 출력
	public Stock findByProduct_PdIdx(Integer pdIdx);

	@Modifying
	@Query("delete from Stock s where s.product.pdIdx = :pdIdx")
	public void deleteStock(@Param("pdIdx") Integer pdIdx);

	@Modifying
	@Query("UPDATE Stock s SET s.stCount = s.stCount - :count WHERE s.product.pdIdx = :pdIdx")
	void reduceStock(@Param("pdIdx") Integer pdIdx, @Param("count") Integer count);

}
