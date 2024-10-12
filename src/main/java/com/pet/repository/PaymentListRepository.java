package com.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.entity.PaymentList;

public interface PaymentListRepository extends JpaRepository<PaymentList, Integer> {

}
