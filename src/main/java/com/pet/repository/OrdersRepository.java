package com.pet.repository;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pet.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

   public Orders findByOrderDateAndUser_UserIdx(LocalDate orderDate, Integer userIdx);
   
   @Query("SELECT p.pdName, c.ctgNum2, s.stCount  " +
            "FROM Stock s JOIN s.product p JOIN p.category c " +
         "WHERE p.pdIdx = :pdIdx")
   public List<Object[]> findByPdIdx(@Param("pdIdx") Integer pdIdx);
   
   // 발주 내역
   public List<Orders> findByUser_UserIdx(Integer userIdx);
   
   // 검수
   public Orders findByOrderDate(LocalDate orderDate);
   
   // 검수 내역 - 발주코드 -> 상품 출력
   public Orders findByOrderIdx(Integer orderIdx);
}
