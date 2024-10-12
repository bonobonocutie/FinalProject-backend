package com.pet.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins(
					"https://www.petttory.com",
					"https://www.petttory.com:8090",
					"https://52.78.21.95",
					"https://127.0.0.1:3000",
					"http://127.0.0.1:3000",
					"https://127.0.0.1",
					"http://127.0.0.1",
					"https://ec2-52-78-21-95.ap-northeast-2.compute.amazonaws.com",
					"https://ec2-52-78-21-95.ap-northeast-2.compute.amazonaws.com:8090",
					"https://www.petttory.com:8090",
					"*"
				)
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.exposedHeaders("Authorization")
				.maxAge(3600)
				.allowedHeaders("*");
	}
}
