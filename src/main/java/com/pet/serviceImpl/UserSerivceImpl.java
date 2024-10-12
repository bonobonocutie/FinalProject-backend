package com.pet.serviceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pet.dto.BusinessDTOList;
import com.pet.dto.UserDTO;
import com.pet.entity.User;
import com.pet.repository.UserRepository;
import com.pet.service.UserSerivce;

@Service
public class UserSerivceImpl implements UserSerivce {

	UserRepository userRepository;
	String key;

	public UserSerivceImpl(UserRepository userRepository, @Value("${business.api.key}") String key) {
		this.userRepository = userRepository;
		this.key = key;
	}

	@Override
	public UserDTO save(UserDTO dto) {
		ModelMapper mapper = new ModelMapper();
		User user = mapper.map(dto, User.class);
		userRepository.save(user);
		return dto;
	}
	
	@Override
	public String business(BusinessDTOList businessDTO) {
			// API URL 생성
	    String apiUrl = "https://api.odcloud.kr/api/nts-businessman/v1/validate";

	    // 쿼리 스트링에 serviceKey 추가(인코딩)
//	    String fullUrl = UriComponentsBuilder.fromHttpUrl(apiUrl)
//	            .queryParam("serviceKey", serviceKey)
//	            .toUriString();
		
		// 쿼리 스트링에 serviceKey 추가 (인코딩 없이)
		String fullUrl = apiUrl + "?serviceKey=" + key;

		// POST 요청을 보내고 응답을 받음
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, businessDTO, String.class);
		
		return response.getBody(); // 응답 내용을 반환
	}
}
