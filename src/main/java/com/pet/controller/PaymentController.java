package com.pet.controller;


import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.PaymentListDTO;
import com.pet.dto.PaymentReqDTO;
import com.pet.dto.PaymentResDTO;
import com.pet.dto.PaymentSMSRequestDTO;
import com.pet.exception.SessionIdNotFoundException;
import com.pet.service.PaymentListService;
import com.pet.service.PaymentService;

import net.nurigo.java_sdk.api.Message;


@RestController
@RequestMapping("/payment")
public class PaymentController {
	
	PaymentService paymentService;
	PaymentListService paymentListService;

	public PaymentController(PaymentService paymentService, PaymentListService paymentListService) {
		this.paymentService = paymentService;
		this.paymentListService = paymentListService;
	}

	@PostMapping("/toss")
	public ResponseEntity<PaymentResDTO> requestTossPayment(@RequestBody PaymentReqDTO paymentReqDto) {
		PaymentResDTO paymentResDto = paymentService.requestTossPayment(paymentReqDto.toEntity()).toPaymentResDto();
		return ResponseEntity.ok(paymentResDto);
	}

	@PostMapping("/confirm")
	public List<PaymentListDTO> handlePaymentConfirm(@RequestBody PaymentResDTO paymentResDTO) {
		try {
			String paymentKey = paymentResDTO.getPaymentKey();
			String orderId = paymentResDTO.getOrderId();
			Integer amount = paymentResDTO.getAmount();
			String sessionId = paymentResDTO.getSessionId();

			// sessionId가 여전히 없을 경우 예외 발생
			if (sessionId == null) {
				throw new SessionIdNotFoundException("세션 ID가 제공되지 않았습니다.");
			}
			List<PaymentListDTO> paymentList = paymentService.tossPaymentConfirm(paymentKey, orderId, amount, sessionId);

			return paymentList;
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	// 주문 내역 발송
	@PostMapping("/sendPaymentList")
	public List<PaymentListDTO> handleSendPaymentList(@RequestBody PaymentSMSRequestDTO requestDTO) {
		String api_key = "";
		String api_secret = "";
		Message message = new Message(api_key, api_secret);

		String phoneNum = requestDTO.getPhoneNum();
		String paymentKey = requestDTO.getPaymentKey();

		try {
			List<PaymentListDTO> paymentList = paymentListService.requestPaymentList(paymentKey);
			String payTime = paymentList.get(0).getPayTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

			int totalAmount = paymentList.stream()
					.mapToInt(item -> item.getAmount() * item.getPdCount()) // 각 항목의 가격에 수량을 곱합니다.
					.sum();
			StringBuilder paymentDetails = new StringBuilder();

			paymentDetails.append("[My Lovely Pet 주문내역]\n");
			paymentDetails.append("결제 일자 : ").append(payTime).append("\n");
			paymentDetails.append("----------------------------------\n");

			for (PaymentListDTO item : paymentList) {
				paymentDetails.append("상품명 : ").append(item.getPdName()).append("\n")
						.append("수량 : ").append(item.getPdCount()).append("개\n")
						.append("가격 : ").append(String.format("%,d원", item.getAmount())).append("\n")
						.append("----------------------------------\n");
			}

			paymentDetails.append("총 결제 금액 : ").append(String.format("%,d원", totalAmount));

			HashMap<String, String> params = new HashMap<>();
			params.put("to", phoneNum);
			params.put("from", "01049264171");
			params.put("type", "lms");
			params.put("text", paymentDetails.toString());
			params.put("subject", "My Lovely Pet 주문내역");
			message.send(params);

			return paymentList;
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

}
