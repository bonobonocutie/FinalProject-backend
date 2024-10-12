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
public class PaymentListDTO {
	private Integer paymentListIdx;
	private Integer amount;
	private LocalDateTime payTime;
	private Integer pdCount;
	private Integer pdIdx;
	private String pdName;
}
