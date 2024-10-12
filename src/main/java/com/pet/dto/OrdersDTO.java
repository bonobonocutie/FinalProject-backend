package com.pet.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class OrdersDTO {
	private Integer orderIdx;
	private Integer userIdx;
	@JsonProperty("pdIdx")
	private List<Integer> pdIdx;
	@JsonProperty("pdName")
	private List<String> pdName;
	@JsonProperty("orderCount")
	private List<Integer> orderCount;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate orderDate;
	private UserDTO user;
}
