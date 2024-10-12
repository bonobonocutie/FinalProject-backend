package com.pet.dto;

import com.pet.entity.Product;
import com.pet.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmSendDTO {
	private String token;
	private String title;
	private String body;
	private Product product; // 추가
	private User user;

	@Builder(toBuilder = true)
	public FcmSendDTO(String token, String title, String body, Product product, User user) {
		this.token = token;
		this.title = title;
		this.body = body;
		this.product = product;
		this.user = user;
	}
}