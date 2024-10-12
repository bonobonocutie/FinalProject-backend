package com.pet.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
	public String uploadQrCode(byte[] data, String key);

	public String uploadImage(MultipartFile multipartFile, String oldImageKey) throws IOException;

	public void deleteFile(String key);

	public String getImage(String fileName);

	public byte[] downloadFile(String key) throws IOException;
}
