package com.pet.dto;

import java.time.LocalDate;

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
public class RevenueDTO {
	private Integer rvIdx;
	private Integer paymentListIdx;
	private LocalDate rvDate;
	private Integer rvTotalPrice;
	private Integer userIdx;
}
