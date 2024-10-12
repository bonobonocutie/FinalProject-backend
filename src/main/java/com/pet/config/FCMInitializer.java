package com.pet.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FCMInitializer {
	private static final String FIREBASE_CONFIG_PATH = "finalproject-241f9-firebase-adminsdk-q2w00-d198dba604.json";

	@PostConstruct
	public void initialize() {
		try {
			if (FirebaseApp.getApps().isEmpty()) {
				GoogleCredentials googleCredentials = GoogleCredentials
						.fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream());
				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(googleCredentials)
						.build();
				FirebaseApp.initializeApp(options);
				log.info("FirebaseApp has been initialized successfully.");
			} else {
				log.info("FirebaseApp is already initialized.");
			}
		} catch (IOException e) {
			log.error("Failed to initialize FirebaseApp: " + e.getMessage(), e);
		}
	}
}
