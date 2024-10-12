package com.pet.dto;

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
public class UserDTO {
	private Integer userIdx;
	private String userPhone;
	private String userEmail;
	private String userPw;
	private String userBN;
	private String userName;
	private String userStoreName;
}
