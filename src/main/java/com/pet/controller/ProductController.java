package com.pet.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.pet.dto.ProductDTO;
import com.pet.dto.ProductInfoDTO;
import com.pet.dto.StockDTO;
import com.pet.dto.UserDTO;
import com.pet.service.ProductService;
import com.pet.service.QrService;
import com.pet.service.S3Service;
import com.pet.service.StockService;

@RestController
@RequestMapping("/product")
@SessionAttributes(names= {"userDTO"})
public class ProductController {

	ProductService productService;
	StockService stockService;
	QrService qrService;
	S3Service s3Service;

	public ProductController(ProductService productService, StockService stockService, QrService qrService, S3Service s3Service) {
		this.productService = productService;
		this.stockService = stockService;
		this.qrService = qrService;
		this.s3Service = s3Service;
	}

	@GetMapping("/findAll")
	public List<ProductDTO> findAll(ModelMap model) {
		UserDTO user = (UserDTO)model.getAttribute("userDTO");
		System.out.println("user : " + user);
		List<ProductDTO> list = productService.findAll(user.getUserIdx());
		return list;
	}

	@GetMapping("/image/{fileName}")
	public ResponseEntity<String> getImage(@PathVariable String fileName) {
		String imageUrl = s3Service.getImage(fileName);
		return new ResponseEntity<>(imageUrl, HttpStatus.OK);
	}

	// 상품 더미데이터 저장
	@PostMapping("/saveData")
	public List<ProductDTO> saveData(@RequestBody List<ProductDTO> dto) {
		List<ProductDTO> list = new ArrayList<>();

		for (ProductDTO productDTO : dto) {
			list.add(productService.saveData(productDTO));
		}
		return list;
	}

	@PostMapping("/save")
	public ResponseEntity<String> save(@RequestPart String product, @RequestPart("file") MultipartFile multipartFile) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ProductDTO productDto = objectMapper.readValue(product, ProductDTO.class);

			System.out.println(product);

			// 이미지 업로드
			String imageUrl = s3Service.uploadImage(multipartFile, null);

			// 상품 정보 생성
			ProductInfoDTO productInfoDto = new ProductInfoDTO();
			productInfoDto.setPdImgUrl(imageUrl);

			// 상품 정보 임시 저장
			ProductInfoDTO savedProductInfo = productService.productInfoSave(productInfoDto);

			// 상품 저장
			productDto.setProductInfo(savedProductInfo);
			ProductDTO savedProduct = productService.save(productDto);

			// QR 코드 생성
			String text = "QrCodepdIdx" + savedProductInfo.getPdInfoIdx();
			String s3Key = "qr-codes/QrCodepdIdx" + savedProductInfo.getPdInfoIdx() + ".png";
			String qrUrl = qrService.generateQRCodeImage(text, 200, 200, s3Key);

			savedProductInfo.setPdQrCode(qrUrl);
			productService.productInfoSave(savedProductInfo);

			// 추가된 상품 재고에 추가
			StockDTO stockDTO = new StockDTO();
			stockDTO.setPdIdx(savedProduct.getPdIdx());
			stockDTO.setStCount(0);
			stockService.saveData(stockDTO);

			return new ResponseEntity<>("상품 저장에 성공했습니다", HttpStatus.CREATED);
		} catch (IOException | WriterException e) {
			return new ResponseEntity<>("이미지 업로드에 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 단일 상품 조회
	@GetMapping("/findOne/{pdIdx}")
	public ProductDTO findOne(@PathVariable("pdIdx") Integer pdIdx) {
		ProductDTO dto = productService.findById(pdIdx);
		return dto;
	}

	// 상품 검색
	@PostMapping("/search")
	public List<ProductDTO> search(String pdName) {
		List<ProductDTO> list = productService.search(pdName);
		return list;
	}

	@PutMapping("/update/{pdIdx}")
	public ResponseEntity<String> update(@PathVariable("pdIdx") Integer pdIdx,
										 @RequestPart String product,
										 @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ProductDTO productDto = objectMapper.readValue(product, ProductDTO.class);
			productDto.setPdIdx(pdIdx);

			String newImageUrl = null;
			if (multipartFile != null && !multipartFile.isEmpty()) {
				ProductInfoDTO productInfoDto = productService.findByPdInfoIdx(productDto.getPdIdx());
				String oldImageKey = productInfoDto.getPdImgUrl().substring(productInfoDto.getPdImgUrl().lastIndexOf("/") + 1);
				newImageUrl = s3Service.uploadImage(multipartFile, "pd-imgs/" + oldImageKey);
			}

			// 상품 수정
			ProductDTO updateProduct = productService.update(productDto, newImageUrl);

			// 상품 정보 저장
			ProductInfoDTO productInfoDto = productService.findByPdInfoIdx(updateProduct.getPdIdx());
			if (newImageUrl != null) {
				productInfoDto.setPdImgUrl(newImageUrl);
			}

			productService.productInfoSave(productInfoDto);

			return new ResponseEntity<>("상품 수정에 성공했습니다", HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>("상품 수정에 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (RuntimeException e) {
			return new ResponseEntity<>("상품 수정에 실패했습니다: " + e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("delete/{pdIdx}")
	public ResponseEntity<String> delete(@PathVariable("pdIdx") Integer pdIdx) {
		try {
			productService.deleteById(pdIdx);
			return new ResponseEntity<>("상품 삭제에 성공했습니다", HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>("상품 삭제에 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/productInfo/save")
	public List<ProductInfoDTO> productInfoSave(@RequestBody List<ProductInfoDTO> dto) {
		List<ProductInfoDTO> list = new ArrayList<>();
		for (ProductInfoDTO productInfoDTO : dto) {
			list.add(productService.productInfoSave(productInfoDTO));
		}
		return list;
	}

}
