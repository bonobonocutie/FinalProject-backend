package com.pet.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pet.dto.BusinessDTOList;
import com.pet.dto.UserDTO;
import com.pet.service.EmailService;
import com.pet.service.UserSerivce;

@RestController
@RequestMapping("/user")
public class UserController {

	UserSerivce userSerivce;
	EmailService emailService;
	
	public UserController(UserSerivce userSerivce, EmailService emailService){
		this.userSerivce = userSerivce;
		this.emailService = emailService;
	}

	@PostMapping("/userSave")
	public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto) {
		System.out.println(dto);

		String ecrptPW = new BCryptPasswordEncoder().encode(dto.getUserPw());
		dto.setUserPw(ecrptPW);
		UserDTO saveUser = userSerivce.save(dto);
		System.out.println(saveUser);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{userEmail}")
				.buildAndExpand(saveUser.getUserEmail()) //위의 {userEmail}를 치환시킴.
				.toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PostMapping("/business")
	public ResponseEntity<String> business(@RequestBody BusinessDTOList businessDTO) {
	    String result = userSerivce.business(businessDTO);
//	    System.out.println("+++++"+result);
	    return ResponseEntity.ok(result); // 외부 API의 응답을 클라이언트에 반환
	}
	
	@GetMapping("/sendEmailCode/{userEmail}")
    public String sendVerificationCode(@PathVariable String userEmail) {
		emailService.sendVerificationEmail(userEmail);
        return "전송 성공";
    }
	
	@GetMapping("/verifyEmailCode/{userEmail}/{emailCode}")
    public String verifyEmail(@PathVariable String userEmail, @PathVariable String emailCode) {
		return emailService.verifyEmail(userEmail, emailCode);
    }
}
