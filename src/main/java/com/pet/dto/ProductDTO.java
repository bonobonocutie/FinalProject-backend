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
public class ProductDTO {
	private Integer pdIdx;
	private Integer ctgIdx;
	private Integer userIdx;
	private Integer pdInfoIdx;
	private String pdName;
	private Integer pdPrice;
	private Integer pdLimit;
	private ProductInfoDTO productInfo;
	private CategoryDTO category;
	private UserDTO user;
}
