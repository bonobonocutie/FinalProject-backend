package com.pet.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.amazonaws.services.rekognition.model.Face;
import com.pet.dto.UserDTO;
import com.pet.security.JwtTokenResponse;
import com.pet.security.JwtTokenService;
import com.pet.service.AuthenticationService;
import com.pet.service.FaceService;

@RestController
@SessionAttributes(names = {"userDTO"})
public class FaceController {
	
	FaceService faceService;
	JwtTokenService tokenService;
	
	public FaceController(FaceService faceService, JwtTokenService tokenService) {
		this.faceService = faceService;
		this.tokenService = tokenService;
	}
	
	@Autowired
	AuthenticationService authenticationService;

	@PostMapping("/compareFace")
	public ResponseEntity<?> compareFace(@RequestBody Map<String, String> payload, ModelMap model) {
		String base64Image = payload.get("image");
        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]); // Base64 분리 및 디코딩
        
        String collectionId = "user-face";
        
        String result = faceService.compareFace(imageBytes, collectionId);
       
        if(result.equals("실패")) {
        	System.out.println("얼굴정보 없음");
        	return ResponseEntity.ok("실패");
        	
        } else {
        	
            UserDTO user = authenticationService.findByUserBN(result);
            
            UsernamePasswordAuthenticationToken authenticationToken = null;
            
            UserDTO userDTO = null;
            
            if (user != null) {
            	List<GrantedAuthority> roles = new ArrayList<>();
    			roles.add(new SimpleGrantedAuthority("USER")); // 권한 부여, 현재는 모든 사용자권한을 USER로 지정한다.
    			authenticationToken = new UsernamePasswordAuthenticationToken(
    					new UserDTO(user.getUserIdx(), user.getUserPhone(), user.getUserEmail(),
    							user.getUserPw(), user.getUserBN(),
    							user.getUserName(), user.getUserStoreName()), null, roles);
    			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    			userDTO = (UserDTO) authenticationToken.getPrincipal();
    			model.addAttribute("userDTO", userDTO);
    			
    			String token = tokenService.generateToken(authenticationToken);
    			
    			return ResponseEntity.ok(new JwtTokenResponse(token));
            }
            else {
            	System.out.println("토큰발급 실패");
            	return ResponseEntity.ok("실패");
            }
        	
        }
        	
	}
	
	@PostMapping("collectionFaceAdd/{userBN}")
	public ResponseEntity<?> collectionFaceAdd(@RequestBody Map<String, String> payload, @PathVariable String userBN) {
		
		String base64Image = payload.get("image");
        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]); // Base64 분리 및 디코딩
        
        String collectionId = "user-face";
		String bucket = "facefacelist";
        
		faceService.collectionFaceAdd(imageBytes, userBN, collectionId, bucket);
		return ResponseEntity.ok("collectionFaceAdd success");
	}
	
	@GetMapping("collectionAdd")
	public ResponseEntity<?> collectionAdd(){
		faceService.collectionAdd();
		
		return ResponseEntity.ok("collectionAdd success");
	}
	
	@GetMapping("collectionDelete")
	public ResponseEntity<?> collectionDelete(){
		faceService.collectionDelete();
		
		return ResponseEntity.ok("collectionDelete success");
	} 
	
	@GetMapping("collectionList")
	public List<Face> collectionList() {
		
		faceService.collectionList();
		
		return faceService.collectionList();
	}
	
}
