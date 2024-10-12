package com.pet.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.service.EmailService;
import com.pet.service.MotionService;

@RestController
//@EnableScheduling
public class MotionController {
	
	MotionService motionService;
	EmailService emailService;
	
	private final ConcurrentMap<String, Object> values = new ConcurrentHashMap<>();
	
    public MotionController(MotionService motionService, EmailService emailService) {
		this.motionService = motionService;
		this.emailService = emailService;
		values.put("motion", "initial_value");
	}

	@GetMapping("/stream")
    public ResponseEntity<String> getStreamUrl() {
    	try {
    		// 카메라 ID에 해당하는 스트림 URL을 반환
            String streamUrl = "http://10.10.10.116:8080/video";
//            String streamUrl = "http://192.168.1.100:8080/video";
//            String streamUrl = "C:\\Users\\ssginc07\\Downloads\\first.mp4";
            System.out.println(values.get("motion"));
    		
            return ResponseEntity.ok(streamUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("스트림 URL을 가져오는 데 실패했습니다.");
        }
    }
    
	@GetMapping("/motion")
//	@Scheduled(fixedRate = 10000) // 10초마다 실행
	public ResponseEntity<String> motion(){
//		String streamUrl = "http://192.168.1.100:8080/video";
		String streamUrl = "http://10.10.10.116:8080/video";
		String motion = motionService.Capturing(streamUrl);
		motion = motion.replaceAll("[.\\s]", "");
		
		LocalDateTime now = LocalDateTime.now();
	    LocalDateTime lastEmailSentTime1 = (LocalDateTime) values.getOrDefault("lastEmailSentTime1", now.minusMinutes(11));
	    LocalDateTime lastEmailSentTime2 = (LocalDateTime) values.getOrDefault("lastEmailSentTime2", now.minusMinutes(11));
	    
		values.put("motion", motion);
		
			 if(motion.equals("기절") && ChronoUnit.MINUTES.between(lastEmailSentTime1, now) >= 10) {
				emailService.sendEmail("bonobonocutie@naver.com", "🚨🚨 매장 사고 발생 🚨🚨", "매장에 쓰러진 사람이 있습니다. 119로 신고바랍니다.");
				System.out.println("이메일 발송 성공");
				values.put("lastEmailSentTime1", now);
					
			} else if(motion.equals("화재") && ChronoUnit.MINUTES.between(lastEmailSentTime2, now) >= 10) {
				emailService.sendEmail("bonobonocutie@naver.com", "🚨🚨 매장 화재 발생 🚨🚨", "매장에 화재가 발생했습니다. 119로 신고바랍니다.");
				System.out.println("이메일 발송 성공");
				values.put("lastEmailSentTime2", now);
				
			} else {
		            System.out.println("이메일 발송이 제한되었습니다. (10분 간격)");
		      }
		
		System.out.println(">> " + motion + " <<");
		return ResponseEntity.ok(motion);
	}
	
}

