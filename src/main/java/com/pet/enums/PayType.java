package com.pet.enums;

public enum PayType {
	NORMAL("일반결제"),
	BILLING("자동결제"),
	BRANDPAY("브랜드페이");

	private final String description;

	PayType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}