package com.pet.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.pet.service.ProductService;
import com.pet.service.QrService;
import com.pet.service.S3Service;

@RestController
public class QrController {

	QrService qrService;
	S3Service s3Service;
	ProductService productService;

	public QrController(QrService qrService, S3Service s3Service, ProductService productService) {
		this.qrService = qrService;
		this.s3Service = s3Service;
		this.productService = productService;
	}

   // QR코드 생성
   @GetMapping("/qrCode/generateAllQRCodes")
   public String generateAllQRCodes() {
//      List<Integer> pdIdxList = productService.findAllIdx();
	   
	   List<Integer> pdIdxList = new ArrayList<>();
       for (int i = 1; i <= 55; i++) {
           pdIdxList.add(i);
       }
	   
      qrService.generateQRCodesForProducts(pdIdxList);
      return "모든 상품에 대한 QR 코드 생성 완료";
   }

   @GetMapping("/download/{key}")
   public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String key) throws IOException {
      String fileName = "qr-codes/" + key + ".png"; // 파일 이름에 확장자를 자동으로 추가
      byte[] pdfBytes = s3Service.downloadFile(fileName);

      return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + key + ".pdf") // inline으로 수정
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(pdfBytes.length)
            .body(new InputStreamResource(new ByteArrayInputStream(pdfBytes)));
   }

}
