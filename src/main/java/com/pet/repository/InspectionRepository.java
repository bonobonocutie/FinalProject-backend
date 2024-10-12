package com.pet.repository;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pet.entity.Inspection;
import com.pet.entity.Orders;

public interface InspectionRepository extends JpaRepository<Inspection, Integer> {
   
   public List<Inspection> findByInsDateAndOrders_User_UserIdx(LocalDate insDate, Integer userIdx);
   
   @Query("SELECT o "
         + "FROM Orders o LEFT JOIN Inspection i ON o.orderIdx = i.orders.orderIdx "
         + "WHERE i.orders.orderIdx IS NULL AND o.orderDate = :orderDate "
         + "AND o.user.userIdx = :userIdx")
   public Orders selectOrder(@Param("orderDate") LocalDate orderDate, @Param("userIdx") Integer userIdx);
}
