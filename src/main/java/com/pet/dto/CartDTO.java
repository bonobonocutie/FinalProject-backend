package com.pet.dto;

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
public class CartDTO {
	private Integer cartIdx;
	private Integer cartCount;
	private String pdName;
	private Integer pdIdx;
	private Integer pdPrice;
	private String sessionId;
}
