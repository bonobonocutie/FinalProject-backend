package com.pet.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.util.Values;
import com.pet.dto.CartDTO;
import com.pet.service.CartService;
import com.pet.service.EmailService;
import com.pet.service.FaceService;

@RestController
@RequestMapping("/cart")
public class CartController {
	HttpSession httpSession;
	CartService cartService;
	FaceService faceService;
	EmailService emailService;
	
	private final ConcurrentMap<String, Object> values = new ConcurrentHashMap<>();

	public CartController(HttpSession httpSession, CartService cartService, FaceService faceService, EmailService emailService) {
		this.httpSession = httpSession;
		this.cartService = cartService;
		this.faceService = faceService;
		this.emailService = emailService;
	}

	@PostMapping("/addCart")
	public ResponseEntity<Map<String, String>> save(@RequestBody Map<String, String> payload) {
		String qrCodeText = payload.get("qrCodeText");

		String sessionId = payload.get("sessionId");

		if (sessionId == null || sessionId.isEmpty()) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "세션 ID가 제공되지 않았습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		int result = cartService.cartAdd(sessionId, qrCodeText);

		Map<String, String> response = new HashMap<>();
		if (result == 1) {
			response.put("message", "상품이 장바구니에 추가되었습니다!");
			return ResponseEntity.ok(response);
		} else {
			response.put("message", "상품을 장바구니에 추가하는 데 실패했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/cartList")
	public ResponseEntity<?> findAllCart(@RequestParam String sessionId) {

		if (sessionId == null || sessionId.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션 ID가 제공되지 않았습니다.");
		}

		try {
			List<CartDTO> allCart = cartService.findAllCart(sessionId);
			return ResponseEntity.ok(allCart);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("장바구니를 조회하는 데 실패했습니다.");
		}
	}

	@PutMapping("/updateCart")
	public ResponseEntity<Map<String, String>> batchUpdateCart(@RequestBody List<CartDTO> updates) {
		Map<String, String> response = new HashMap<>();

		if (updates.isEmpty()) {
			response.put("message", "업데이트할 항목이 없습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		String sessionId = updates.get(0).getSessionId();
		if (sessionId == null || sessionId.isEmpty()) {
			response.put("message", "세션 ID가 제공되지 않았습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		try {
			cartService.updateCart(sessionId, updates);

			response.put("message", "장바구니가 성공적으로 업데이트되었습니다.");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("message", "장바구니 업데이트 중 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@DeleteMapping("/deleteCartItem")
	public ResponseEntity<Map<String, String>> deleteCartItem(@RequestParam String sessionId, @RequestParam Integer pdIdx) {
		System.out.println(">>>"+ sessionId + pdIdx);
		if (sessionId == null || sessionId.isEmpty()) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "세션 ID가 제공되지 않았습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		if (pdIdx == null) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "제품 ID가 제공되지 않았습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		try {
			// 특정 세션과 pdIdx에 해당하는 항목 삭제
			cartService.deleteCartItem(sessionId, pdIdx);
			Map<String, String> response = new HashMap<>();
			response.put("message", "장바구니 항목이 성공적으로 삭제되었습니다.");
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "장바구니 항목 삭제 중 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}


	@DeleteMapping("/deleteCart")
	public ResponseEntity<Map<String, String>> deleteCart(@RequestParam String sessionId) {
		if (sessionId == null || sessionId.isEmpty()) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "세션 ID가 제공되지 않았습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		cartService.deleteCartBySessionId(sessionId);

		Map<String, String> response = new HashMap<>();
		response.put("message", "장바구니 항목이 성공적으로 삭제되었습니다.");
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("blacklistFaceAdd")
	public ResponseEntity<?> customerFace(@RequestBody Map<String, String> payload){
		String base64Image = payload.get("image");
        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
        
        String collectionId = "blacklist-face";
		String bucket = "petshopblacklist";
		String userBN = Long.toString(System.currentTimeMillis());
		
		faceService.collectionFaceAdd(imageBytes, userBN, collectionId, bucket);
        
        return ResponseEntity.ok("collectionFaceAdd success");
	}
	
	@PostMapping("customerFaceCompare")
	public ResponseEntity<?> customerFaceCompare(@RequestBody Map<String, String> payload){
		String base64Image = payload.get("image");
        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]); // Base64 분리 및 디코딩
        
        String collectionId = "blacklist-face";
        
        String result = faceService.compareFace(imageBytes, collectionId);
        
        LocalDateTime now = LocalDateTime.now();
	    LocalDateTime lastEmailSentTime1 = (LocalDateTime) values.getOrDefault("lastEmailSentTime1", now.minusMinutes(11));
        
        if(result.equals("실패")) {
        	System.out.println("얼굴정보 없음");
        	return ResponseEntity.ok("얼굴정보 없음");
        	
        } else{
        	System.out.println("얼굴정보 있음");
        	if(ChronoUnit.MINUTES.between(lastEmailSentTime1, now) >= 10) {
        		emailService.sendEmail("wonhong1994@naver.com", "🚨🚨 블랙리스트 대상자 발견 🚨🚨", "블랙리스트로 추정되는 고객이 매장에 출입하였습니다. 확인바랍니다.");
        		System.out.println("이메일 발송 성공");
        		///////
        	}
			values.put("lastEmailSentTime1", now);
			return ResponseEntity.ok("얼굴정보 있음");
        }
	}

}
