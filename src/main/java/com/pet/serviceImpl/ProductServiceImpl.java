package com.pet.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.pet.dto.ProductDTO;
import com.pet.dto.ProductInfoDTO;
import com.pet.entity.Category;
import com.pet.entity.Product;
import com.pet.entity.ProductInfo;
import com.pet.entity.User;
import com.pet.repository.CategoryRepository;
import com.pet.repository.ProductInfoRepository;
import com.pet.repository.ProductRepository;
import com.pet.repository.UserRepository;
import com.pet.service.ProductService;
import com.pet.service.S3Service;
import com.pet.service.StockService;

@Service
public class ProductServiceImpl implements ProductService {

	S3Service s3Service;
	StockService stockService;
	ProductRepository productRepository;
	UserRepository userRepository;
	CategoryRepository categoryRepository;
	ProductInfoRepository productInfoRepository;

	public ProductServiceImpl(AmazonS3 amazonS3, ProductRepository productRepository,
							  CategoryRepository categoryRepository, ProductInfoRepository productInfoRepository,
							  @Value("${cloud.aws.s3.bucket}") String bucketName, UserRepository userRepository, S3Service s3Service, StockService stockService) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.productInfoRepository = productInfoRepository;
		this.userRepository = userRepository;
		this.s3Service = s3Service;
		this.stockService = stockService;
	}

	@Override
	public List<Integer> findAllIdx() {
		return productRepository.findAll()
				.stream()
				.map(Product::getPdIdx)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProductDTO> findAll(Integer userIdx) {
		ModelMapper mapper = new ModelMapper();
		List<Product> list = productRepository.findByUser_UserIdx(userIdx);

		List<ProductDTO> productList = list.stream()
				.map(e -> mapper.map(e, ProductDTO.class))
				.collect(Collectors.toList());

		return productList;
	}

	@Override
	public ProductDTO findById(Integer pdIdx) {
		ModelMapper mapper = new ModelMapper();
		Product product = productRepository.findById(pdIdx)
				.orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));
		return mapper.map(product, ProductDTO.class);
	}

	@Override
	public ProductInfoDTO findByPdInfoIdx(Integer pdIdx) {
		ModelMapper mapper = new ModelMapper();
		ProductInfo productInfo = productInfoRepository.findByPdInfoIdx(pdIdx);
		return mapper.map(productInfo, ProductInfoDTO.class);
	}

	@Override
	@Transactional
	public ProductInfoDTO productInfoSave(ProductInfoDTO dto) {
		ModelMapper mapper = new ModelMapper();
		ProductInfo productInfo = mapper.map(dto, ProductInfo.class);
		ProductInfo savedProductInfo = productInfoRepository.save(productInfo);
		dto.setPdInfoIdx(savedProductInfo.getPdInfoIdx()); // 저장된 ID를 DTO에 설정
		return dto;
	}

	// 상품 더미데이터 저장
	@Override
	public ProductDTO saveData(ProductDTO dto) {
		ModelMapper mapper = new ModelMapper();
		Product product = mapper.map(dto, Product.class);

		Category category = categoryRepository.findByCtgIdx(dto.getCtgIdx());
		product.setCategory(category);

		User user = userRepository.findByuserIdx(dto.getUserIdx());
		product.setUser(user);

		ProductInfo productInfo = productInfoRepository.findById(dto.getPdInfoIdx())
				.orElseThrow(() -> new RuntimeException("해당 상품 정보를 찾을 수 없습니다."));
		product.setProductInfo(productInfo);

		Product savedProduct = productRepository.save(product);
		dto.setPdIdx(savedProduct.getPdIdx());

		return dto;
	}


	@Override
	@Transactional
	public ProductDTO save(ProductDTO dto) {
		try {
			ModelMapper mapper = new ModelMapper();
			Product product = mapper.map(dto, Product.class);

			Category category = categoryRepository.findById(dto.getCtgIdx())
					.orElseThrow(() -> new RuntimeException("해당 카테고리를 찾을 수 없습니다."));
			product.setCategory(category);

			User user = userRepository.findById(dto.getUserIdx())
					.orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
			product.setUser(user);

			Product savedProduct = productRepository.save(product);
			dto.setPdIdx(savedProduct.getPdIdx());

			return dto;
		} catch (RuntimeException e) {
			throw new RuntimeException("상품 저장에 실패했습니다: " + e.getMessage());
		}
	}

	@Override
	public ProductDTO update(ProductDTO productDTO, String imgUrl) {
		ModelMapper mapper = new ModelMapper();
		Product product = mapper.map(productDTO, Product.class);

		Product findProduct = productRepository.findByPdIdx(product.getPdIdx());
		if (findProduct == null) {
			throw new RuntimeException("해당 상품을 찾을 수 없습니다.");
		}

		Category category = categoryRepository.findById(productDTO.getCtgIdx())
				.orElseThrow(() -> new RuntimeException("해당 카테고리를 찾을 수 없습니다."));
		findProduct.setCategory(category);

//		 사용자 조회 및 설정
		User user = userRepository.findById(productDTO.getUserIdx())
				.orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
		findProduct.setUser(user);

		findProduct.setPdName(productDTO.getPdName());
		findProduct.setPdPrice(productDTO.getPdPrice());
		findProduct.setPdLimit(productDTO.getPdLimit());

		Product saveProduct = productRepository.save(findProduct);

		ProductInfo productInfo = productInfoRepository.findByPdInfoIdx(productDTO.getPdIdx());
		if (productInfo == null) {
			throw new RuntimeException("해당 상품 정보를 찾을 수 없습니다.");
		}

		if (imgUrl != null) {
			productInfo.setPdImgUrl(imgUrl);
		}

		productInfoRepository.save(productInfo);

		return mapper.map(saveProduct, ProductDTO.class);
	}

	@Override
	public List<ProductDTO> search(String pdName) {
		ModelMapper mapper = new ModelMapper();
		List<Product> list = productRepository.search(pdName);

		List<ProductDTO> productList = list.stream()
				.map(e -> mapper.map(e, ProductDTO.class))
				.collect(Collectors.toList());

		return productList;
	}

	@Override
	public void deleteById(Integer pdIdx) {
		ProductInfo productInfo = productInfoRepository.findByPdInfoIdx(pdIdx);

		// 재고 삭제
		stockService.deleteStock(pdIdx);
		
		// Product 삭제
		Product product = productRepository.findById(pdIdx)
				.orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));
		productRepository.delete(product);


		if (productInfo != null) {
			String imageUrl = productInfo.getPdImgUrl();
			String qrCodeKey = "qr-codes/QrCodepdIdx" + pdIdx + ".png";

			if (imageUrl != null && !imageUrl.isEmpty()) {
				String imageKey = "pd-imgs/" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
				s3Service.deleteFile(imageKey);
			}
			s3Service.deleteFile(qrCodeKey);
		}

	}

}
