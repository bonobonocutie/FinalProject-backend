package com.pet.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.ProductDTO;
import com.pet.service.ChatBotService;
import com.pet.service.EmailService;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {
	
	ChatBotService chatBotService;
	EmailService emailService;
	
	public ChatBotController(ChatBotService chatBotService, EmailService emailService) {
		this.chatBotService = chatBotService;
		this.emailService = emailService;
	}

	@GetMapping("/{userNum}/{pdName:.+}")
	public ResponseEntity<?> serachProduct(@PathVariable Integer userNum, @PathVariable String pdName){
		return chatBotService.serachProduct(pdName, userNum);
	}
	
	@GetMapping("/{mailType}")
	public void chatbotMail(@PathVariable String mailType) {
		if(mailType.equals("종이")) {
			emailService.sendEmail("wonhong1994@naver.com", "💡💡 종이가방 재고 부족 💡💡", "현재 매장에 비치된 종이가방 재고가 부족한 상태입니다. 보충 요청드립니다.");			
		} else {
			emailService.sendEmail("wonhong1994@naver.com", "💡💡 매장 청결 상태 점검 💡💡", "매장 청결 상태에 문제가 발견되었습니다. 점검 및 조치 부탁드립니다.");			
		}
	}

}
