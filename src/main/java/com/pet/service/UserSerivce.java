package com.pet.service;

import com.pet.dto.BusinessDTOList;
import com.pet.dto.UserDTO;

public interface UserSerivce {
	public UserDTO save(UserDTO dto);
	public String business(BusinessDTOList businessDTO);
}

