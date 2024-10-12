package com.pet.service;

import java.util.List;

import com.pet.dto.PaymentListDTO;
import com.pet.entity.Payment;


public interface PaymentService {
	public Payment requestTossPayment(Payment payment);

	public List<PaymentListDTO> tossPaymentConfirm(String paymentKey, String orderId, Integer amount, String session);

	public Payment verifyPayment(String orderId, Integer amount);

	public void requestPaymentAccept(String paymentKey, String orderId, Integer amount);
}
