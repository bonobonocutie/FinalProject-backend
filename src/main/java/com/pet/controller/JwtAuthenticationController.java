package com.pet.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.pet.dto.UserDTO;
import com.pet.security.JwtTokenResponse;
import com.pet.security.JwtTokenService;
import com.pet.service.AuthenticationService;
import com.pet.service.NoticeService;
import com.pet.service.StockService;

@RestController
@SessionAttributes(names = {"userDTO"})
public class JwtAuthenticationController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private JwtTokenService tokenService;
	StockService stockService;
	NoticeService noticeService;

	public JwtAuthenticationController(JwtTokenService tokenService, StockService stockService, NoticeService noticeService) {
		this.tokenService = tokenService;
		this.stockService = stockService;
		this.noticeService = noticeService;
	}

	@Autowired
	AuthenticationService authenticationService;

	// 로그인 처리 + token 얻기
	@PostMapping("/authenticate")
	public ResponseEntity<?> generateToken(
			@RequestBody Map<String, String> jwtTokenRequest, ModelMap model) {

		logger.info("logger: jwtTokenRequest: {}", jwtTokenRequest);
		
		UserDTO user = authenticationService.findByuserEmail(jwtTokenRequest.get("userEmail"));

		PasswordEncoder passwordEncoder = passwordEncoder();
		UsernamePasswordAuthenticationToken authenticationToken = null;
		UserDTO userDTO = null;

		if (user != null && passwordEncoder.matches(jwtTokenRequest.get("userPw"), user.getUserPw())) { // 일치하는 사용자와 비번이 일치하면
			List<GrantedAuthority> roles = new ArrayList<>();
			roles.add(new SimpleGrantedAuthority("USER")); // 권한 부여, 현재는 모든 사용자권한을 USER로 지정한다.
			authenticationToken = new UsernamePasswordAuthenticationToken(
					new UserDTO(user.getUserIdx(), user.getUserPhone(), jwtTokenRequest.get("userEmail"),
							jwtTokenRequest.get("userPw"), user.getUserBN(),
							user.getUserName(), user.getUserStoreName()), null, roles);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			userDTO = (UserDTO) authenticationToken.getPrincipal();
			model.addAttribute("userDTO", userDTO);
			String token = tokenService.generateToken(authenticationToken);
			return ResponseEntity.ok(new JwtTokenResponse(token));
		}

		else {
			System.out.println("로그인 실패");
			return ResponseEntity.ok("로그인 실패");
		}

	}

	// 암호화 객체 생성
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		return new ResponseEntity<>("Logout successful", HttpStatus.OK);
	}

}