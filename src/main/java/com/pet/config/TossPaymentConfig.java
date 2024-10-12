package com.pet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class TossPaymentConfig {
	private String URL = "https://api.tosspayments.com/v1/payments/";

	// 결제 승인 요청 보낼 URL
	@Value("${payment.toss.test_client_api_key}")
	private String testClientApiKey;

	@Value("${payment.toss.test_secret_api_key}")
	private String testSecretKey;

}
