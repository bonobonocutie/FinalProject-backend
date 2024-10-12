package com.pet.service;

import java.io.IOException;
import java.util.List;

import com.pet.dto.PaymentListDTO;
import com.pet.entity.Payment;

public interface PaymentListService {
	public List<PaymentListDTO> addPaymentList(Payment payment, String session) throws IOException;
	public List<PaymentListDTO> requestPaymentList(String paymentKey);
}
