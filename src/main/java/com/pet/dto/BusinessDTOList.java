package com.pet.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
class BusinessDTO {
	@JsonProperty("b_no")
	private String b_no;
	
	@JsonProperty("start_dt")
	private String start_dt;
	
	@JsonProperty("p_nm")
	private String p_nm;
	
	@JsonProperty("p_nm2")
	private String p_nm2;
	
	@JsonProperty("b_nm")
	private String b_nm;
	
	@JsonProperty("corp_no")
	private String corp_no;
	
	@JsonProperty("b_sector")
	private String b_sector;
	
	@JsonProperty("b_type")
	private String b_type;
	
	@JsonProperty("b_adr")
	private String b_adr;
}

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class BusinessDTOList {
	@JsonProperty("businesses")
	private List<BusinessDTO> businesses;
}
