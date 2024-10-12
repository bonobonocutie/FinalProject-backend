package com.pet.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController {

	@GetMapping("/main")
	public String main() {
		return "main";
	}

	@GetMapping("/testHealth")
	public String testEndpoint() {
		return "Test endpoint is working";
	}

	@PostMapping("/check")
	public ResponseEntity<String> checkHealth(@RequestBody(required = false) Map<String, Object> requestBody) {
		// 헬스체크 로직 (예: ping, 외부 API 호출 등)
		int httpStatus = 200; // 실제 로직에서 상태를 확인
		if (httpStatus == HttpStatus.OK.value()) {
			return ResponseEntity.ok("Health is UP");
		}
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Health is DOWN");
	}

	@Component
	public class OtherServerHealthIndicator implements HealthIndicator {
		@Override
		public Health health() {
			int httpStatus = 200; // 여기서 서버로 ping을 날리든, health api를 호출하든 응답을 받는다.
			if (httpStatus == HttpStatus.OK.value()) {
				return Health.up().build();
			}

			return Health.down().build();
		}
	}
}
