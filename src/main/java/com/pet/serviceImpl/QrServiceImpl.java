package com.pet.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pet.service.QrService;
import com.pet.service.S3Service;

@Service
public class QrServiceImpl implements QrService {

	AmazonS3 amazonS3;
	String bucketName;
	S3Service s3Service;

	public QrServiceImpl(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucketName, S3Service s3Service) {
		this.amazonS3 = amazonS3;
		this.bucketName = bucketName;
		this.s3Service = s3Service;
	}

	// QR코드 이미지 생성
	@Override
	public String generateQRCodeImage(String text, int width, int height, String s3Key) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

		try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
			byte[] pngData = pngOutputStream.toByteArray();

			String fileUrl = s3Service.uploadQrCode(pngData, s3Key);

			return fileUrl;
		} catch (IOException e) {
			throw new RuntimeException("QR 코드 생성 및 업로드 실패: " + e.getMessage(), e);
		}
	}

	// 현재 보유중인 상품 QR코드 생성
	@Override
	public void generateQRCodesForProducts(List<Integer> pdIdxList) {
		for (Integer pdIdx : pdIdxList) {
			String text = "QrCodepdIdx" + pdIdx;
			String s3Key = "qr-codes/QrCodepdIdx" + pdIdx + ".png";
			try {
				generateQRCodeImage(text, 200, 200, s3Key);
			} catch (WriterException | IOException e) {
				System.err.println("QR 코드 생성 실패 (상품 ID: " + pdIdx + "): " + e.getMessage());
			}
		}
	}

}
