package com.pet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	public Optional<Payment> findByOrderId(String orderId);

	public Payment findByPaymentKey(String paymentKey);
}