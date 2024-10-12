package com.pet.serviceImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.pet.service.S3Service;

@Service
public class S3ServiceImpl implements S3Service {

	AmazonS3 amazonS3;
	String bucketName;

	public S3ServiceImpl(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucketName) {
		this.amazonS3 = amazonS3;
		this.bucketName = bucketName;
	}

	// QR코드 업로드
	public String uploadQrCode(byte[] data, String key) {
		try {
			// 이미 존재하는 파일은 삭제
			if (amazonS3.doesObjectExist(bucketName, key)) {
				amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
				System.out.println("기존 파일 삭제 성공: " + key);
			}

			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentLength(data.length);
				metadata.setContentType("image/png");
//				PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
				amazonS3.putObject(bucketName, key, inputStream, metadata);
			}
		} catch (Exception e) {
			System.err.println("파일 업로드 실패: " + e.getMessage());
			throw new RuntimeException(e);
		}
		String fileUrl = amazonS3.getUrl(bucketName, key).toString();
		return fileUrl;
	}

	// 이미지 업로드
	public String uploadImage(@RequestParam("file") MultipartFile multipartFile, String oldImageKey) throws IOException {
		String originalFilename = multipartFile.getOriginalFilename();
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String randomFileName = UUID.randomUUID().toString() + extension; // 난수 파일명 생성
		String directory = "pd-imgs/";
		String s3key = directory + randomFileName;

		// 상품 수정시 전에 있던 이미지 파일 삭제
		if (oldImageKey != null && amazonS3.doesObjectExist(bucketName, oldImageKey)) {
			amazonS3.deleteObject(new DeleteObjectRequest(bucketName, oldImageKey));
		}

		// 이미 존재하는 파일은 삭제
		if (amazonS3.doesObjectExist(bucketName, s3key)) {
			amazonS3.deleteObject(new DeleteObjectRequest(bucketName, s3key));
		}

		try (InputStream inputStream = multipartFile.getInputStream()) {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(multipartFile.getSize());
			metadata.setContentType(multipartFile.getContentType());
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3key, inputStream, metadata);
			amazonS3.putObject(putObjectRequest);
		}
		String fileUrl = amazonS3.getUrl(bucketName, s3key).toString();
		return fileUrl;
	}

	// 파일 삭제
	public void deleteFile(String key) {
		try {
			amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
		} catch (Exception e) {
			throw new RuntimeException("파일 삭제에 실패했습니다: " + e.getMessage());
		}
	}

	// 이미지 출력
	@Override
	public String getImage(String fileName) {
		String directory = "pd-imgs/";
		String s3key = directory + (fileName.endsWith(".jpg") ? fileName : fileName + ".jpg");

		URL url = amazonS3.getUrl(bucketName, s3key);
		return url.toString();
	}

	// QR코드 pdf 다운로드
	@Override
	public byte[] downloadFile(String key) throws IOException {
		S3Object s3Object = amazonS3.getObject(bucketName, key);
		InputStream inputStream = s3Object.getObjectContent();

		// PDF 변환
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);

		PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, inputStream.readAllBytes(), key);
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		contentStream.drawImage(pdImage, 20, 20, page.getMediaBox().getWidth() - 40, page.getMediaBox().getHeight() - 40);
		contentStream.close();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		document.save(byteArrayOutputStream);
		document.close();

		return byteArrayOutputStream.toByteArray();
	}

}
