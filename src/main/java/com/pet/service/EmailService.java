package com.pet.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender emailSender;
	
	public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
	
	 private Map<String, String> emailMap = new ConcurrentHashMap<>();
	
	 public String sendVerificationEmail(String userEmail) {
	        String verificationCode = generateVerificationCode();
	        emailMap.put(userEmail, verificationCode);
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(userEmail);
	        message.setSubject("이메일 인증 코드");
	        message.setText("다음 인증 코드를 입력하세요: " + verificationCode);
	        emailSender.send(message);
	        
	        return verificationCode; // 인증코드 반환
	    }
	 
	 public String verifyEmail(String userEmail, String emailCode) {
		 if (emailCode != null && emailCode.equals(emailMap.get(userEmail))) {
	            return "성공";
	        } else {
	            return "실패";
	        }
	 }
	 
	 private String generateVerificationCode() {
	        Random random = new Random();
	        return String.format("%06d", random.nextInt(1000000)); // 6자리 인증번호 생성
	 }
}
