package com.pet.dto;


import java.util.List;
import java.util.UUID;

import com.pet.config.Auditable;
import com.pet.entity.Payment;
import com.pet.enums.PayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class PaymentReqDTO extends Auditable {

	@NonNull
	private PayType payType; // 결제 타입 - 카드/현금/포인트

	@NonNull
	private Integer amount; // 가격 정보

	private List<Integer> cartIdx;

	@NonNull
	private String orderName; // 주문

	private String yourSuccessUrl; // 성공 시 리다이렉트 될 URL

	private String yourFailUrl; // 실패 시 리다이렉트 될 URL

	public Payment toEntity() {
		return Payment.builder()
//				.cartIdx(cartIdx != null && !cartIdx.isEmpty() ? cartIdx.get(0) : null)
				.payType(payType)
				.amount(amount)
				.orderName(orderName)
				.orderId(UUID.randomUUID().toString())
				.paySuccessYN(false)
				.build();
	}

}
