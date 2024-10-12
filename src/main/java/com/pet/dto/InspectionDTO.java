package com.pet.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class InspectionDTO {
	private Integer insIdx;
	private OrdersDTO orders;
	@JsonProperty("insCount")
	private List<Integer> insCount;
	@JsonProperty("insDetail")
	private List<String> insDetail;
	@JsonProperty("insExDate")
	private List<String> insExDate;
	private LocalDate insDate;
}
