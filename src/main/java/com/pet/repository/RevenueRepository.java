package com.pet.repository;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pet.entity.Revenue;

public interface RevenueRepository extends JpaRepository<Revenue, Integer> {
	public Revenue findByRvDate(LocalDate rvDate);

	@Query("SELECT r FROM Revenue r WHERE r.rvDate = current_date AND r.userIdx = :userIdx")
	Revenue findByToday(@Param("userIdx") Integer userIdx);

	@Query("SELECT r FROM Revenue r WHERE MONTH(r.rvDate) = :month AND YEAR(r.rvDate) = :year AND r.userIdx = :userIdx ORDER BY r.rvDate")
	public List<Revenue> findByMonth(@Param("year") Integer year, @Param("month") Integer month, @Param("userIdx") Integer userIdx);

	@Query("SELECT SUM(r.rvTotalPrice) FROM Revenue r WHERE MONTH(r.rvDate) = :month AND YEAR(r.rvDate) = :year AND r.userIdx = :userIdx")
	public Integer findMonthlySales(@Param("year") Integer year, @Param("month") Integer month, @Param("userIdx") Integer userIdx);

	@Query("SELECT r FROM Revenue r WHERE YEAR(r.rvDate) = :year AND r.userIdx = :userIdx ORDER BY MONTH(r.rvDate)")
	public List<Revenue> findByYear(@Param("year") Integer year, @Param("userIdx") Integer userIdx);

	@Query("SELECT SUM(r.rvTotalPrice) FROM Revenue r WHERE YEAR(r.rvDate) = :year AND r.userIdx = :userIdx")
	public Integer findYearlySales(@Param("year") Integer year, @Param("userIdx") Integer userIdx);

}
