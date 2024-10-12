package com.pet.exception;

public class SessionIdNotFoundException extends RuntimeException {

	// 메시지를 받아 처리하는 생성자
	public SessionIdNotFoundException(String message) {
		super(message);
	}

}
