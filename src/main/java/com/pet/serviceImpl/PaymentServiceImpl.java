package com.pet.serviceImpl;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.transaction.Transactional;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

import com.pet.config.TossPaymentConfig;
import com.pet.dto.PaymentListDTO;
import com.pet.entity.Payment;
import com.pet.repository.PaymentRepository;
import com.pet.service.PaymentListService;
import com.pet.service.PaymentService;
import com.pet.service.RevenueService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	TossPaymentConfig tossPaymentConfig;
	PaymentRepository paymentRepository;
	PaymentListService paymentListService;
	RevenueService revenueService;

	public PaymentServiceImpl(TossPaymentConfig tossPaymentConfig, PaymentRepository paymentRepository, PaymentListService paymentListService, RevenueService revenueService) {
		this.tossPaymentConfig = tossPaymentConfig;
		this.paymentRepository = paymentRepository;
		this.paymentListService = paymentListService;
		this.revenueService = revenueService;
	}

	// 결제 요청
	@Override
	public Payment requestTossPayment(Payment payment) {
		if (payment.getAmount() < 1000) {
			throw new IllegalArgumentException("결제금액이 1,000원이상 되어야 합니다.");
		}
		return paymentRepository.save(payment);
	}

	// 결제 요청 성공 시
	@Override
	public List<PaymentListDTO> tossPaymentConfirm(String paymentKey, String orderId, Integer amount, String session) {
		try {
			Payment payment = verifyPayment(orderId, amount);  // 요청 가격 == 결제된 금액

			requestPaymentAccept(paymentKey, orderId, amount);

			payment.setPaymentKey(paymentKey);
			payment.setPaySuccessYN(true);

			// 결제 내역 추가
			List<PaymentListDTO> paymentListDTOS = paymentListService.addPaymentList(payment, session);

			// 매출 업데이트
			revenueService.updateRevenue(payment, paymentListDTOS, session);

			// 결제내역 반환
			return paymentListDTOS;
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	// 요청가격과 결제된 금액 비교
	@Override
	public Payment verifyPayment(String orderId, Integer amount) {
		Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
			throw new IllegalArgumentException("Payment not found for orderId: " + orderId);
		});
		if (!payment.getAmount().equals(amount)) {
			throw new IllegalArgumentException("Payment amount does not match for orderId: " + orderId);
		}
		return payment;
	}

	// 최종 결제 승인 요청
	@Override
	public void requestPaymentAccept(String paymentKey, String orderId, Integer amount) {
		JSONObject params = new JSONObject();

		params.put("orderId", orderId);
		params.put("amount", amount);
		params.put("paymentKey", paymentKey);

		try {
			Base64.Encoder encoder = Base64.getEncoder();
			byte[] encodedBytes = encoder.encode((tossPaymentConfig.getTestSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
			String authorizations = "Basic " + new String(encodedBytes);

			URL url = new URL(tossPaymentConfig.getURL());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", authorizations);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			try (OutputStream outputStream = connection.getOutputStream()) {
				outputStream.write(params.toString().getBytes(StandardCharsets.UTF_8));
			}

//			int code = connection.getResponseCode();
//			boolean isSuccess = code == 200;

//			try (InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
//				 Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {

//				JSONParser parser = new JSONParser();
//				JSONObject jsonObject = (JSONObject) parser.parse(reader);
//				System.out.println(jsonObject);
//			}

		} catch (HttpClientErrorException e) {
			throw new ResponseStatusException(e.getStatusCode(), "Client error: " + e.getStatusText());
		} catch (HttpServerErrorException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error: " + e.getStatusText());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error occurred.");
		}
	}


}
