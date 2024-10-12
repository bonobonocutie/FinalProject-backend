package com.pet.serviceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.pet.dto.UserDTO;
import com.pet.entity.User;
import com.pet.repository.UserRepository;
import com.pet.service.AuthenticationService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	UserRepository userRepository;

	public AuthenticationServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	private static final ModelMapper mapper = new ModelMapper();

	@Override
	public UserDTO authenticate(String userEmail, String userPw) {
		User user = userRepository.authenticate(userEmail, userPw);
		UserDTO dto = mapper.map(user, UserDTO.class);
		return dto;
	}


	@Override
	public UserDTO findByuserEmail(String userEmail) {
		User user = userRepository.findByuserEmail(userEmail);
		System.out.println(user);
		UserDTO dto = mapper.map(user, UserDTO.class);
		return dto;
	}

	@Override
	public UserDTO findByUserBN(String userBN) {
		User user = userRepository.findByUserBN(userBN);
		UserDTO dto = mapper.map(user, UserDTO.class);
		return dto;
	}

}
