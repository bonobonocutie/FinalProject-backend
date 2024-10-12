package com.pet.service;

import com.pet.dto.UserDTO;

public interface AuthenticationService {

	public UserDTO authenticate(String userEmail, String userPw);

	public UserDTO findByuserEmail(String userEmail);
	
	public UserDTO findByUserBN(String userBN);

}
