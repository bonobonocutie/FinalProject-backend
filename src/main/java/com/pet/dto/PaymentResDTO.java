package com.pet.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class PaymentResDTO {
	private String payType; // 결제 타입 - 카드/현금/포인트
	private Integer amount; // 가격 정보
	private String orderName; // 주문명
	private String orderId; // 주문 Id
	private String paymentKey;
	private String sessionId;
	private String successUrl; // 성공 시 리다이렉트 될 URL
	private String failUrl; // 실패 시 리다이렉트 될 URL
	private String failReason; // 실패 이유
	private boolean cancelYN; // 취소 YN
	private String cancelReason; // 취소 이유
	private LocalDateTime createdAt; // 결제가 이루어진 시간
}