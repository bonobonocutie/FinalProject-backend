package com.pet.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dto.CartDTO;
import com.pet.entity.Cart;
import com.pet.entity.Product;
import com.pet.repository.CartRepository;
import com.pet.repository.ProductRepository;
import com.pet.service.CartService;

@Service
@Transactional
public class CartServiceImpl implements CartService {

	ProductRepository productRepository;
	CartRepository cartRepository;

	@Autowired
	public CartServiceImpl(ProductRepository productRepository, CartRepository cartRepository) {
		this.productRepository = productRepository;
		this.cartRepository = cartRepository;
	}

	@Override
	public int cartAdd(String sessionId, String qrCodeText) {
		// QR 코드 텍스트에서 제품 ID 추출
		String productIdStr = qrCodeText.replaceAll("[^0-9]", "").trim();
		int productId = Integer.parseInt(productIdStr);

		// 제품 조회
		Optional<Product> productOpt = productRepository.findById(productId);
		if (!productOpt.isPresent()) {
			// 제품을 찾지 못한 경우 예외 처리 또는 실패 반환
			return 0;
		}

		Product product = productOpt.get();

		// 특정 사용자의 장바구니 항목 조회
		List<Cart> userCarts = cartRepository.findBySessionId(sessionId);

		// 장바구니에서 해당 제품이 포함된 항목 찾기
		boolean productFoundInCart = false;
		for (Cart cart : userCarts) {
			if (cart.getProduct().getPdIdx().equals(product.getPdIdx())) {
				// 제품이 이미 장바구니에 있는 경우 수량 증가
				cart.setCartCount(cart.getCartCount() + 1);
				cartRepository.save(cart);
				productFoundInCart = true;
				break;
			}
		}

		if (!productFoundInCart) {
			// 제품이 장바구니에 없는 경우 새로운 항목 추가
			Cart newCart = Cart.builder()
					.sessionId(sessionId)
					.product(product)  // Product 객체를 직접 사용
					.cartCount(1)
					.build();
			cartRepository.save(newCart);
		}

		return 1; // 성공 시 1 반환
	}

	@Override
	public List<CartDTO> findAllCart(String sessionId) {
		List<Cart> list = cartRepository.findBySessionId(sessionId);

		return list.stream()
				.map(cart -> new CartDTO(
						cart.getCartIdx(),
						cart.getCartCount(),
						cart.getProduct().getPdName(),
						cart.getProduct().getPdIdx(),
						cart.getProduct().getPdPrice(),
						cart.getSessionId()
				))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public int updateCart(String sessionId, List<CartDTO> updates) {
		int updateCount = 0;

		for (CartDTO update : updates) {
			Product product = productRepository.findById(update.getPdIdx())
					.orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + update.getPdIdx()));

			Cart cart = cartRepository.findBySessionIdAndProduct(sessionId, product);

			if (cart != null) {
				cart.setCartCount(update.getCartCount());
				cartRepository.save(cart);
				updateCount++;
			} else {

			}
		}

		return updateCount; // 성공적으로 업데이트된 항목의 수를 반환
	}

	public void deleteCartItem(String sessionId, Integer pdIdx) {
		Product product = productRepository.findById(pdIdx)
				.orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + pdIdx));

		Cart cart = cartRepository.findBySessionIdAndProduct(sessionId, product);
		cart.setProduct(null);
		
		try {
				cartRepository.delete(cart);
				cartRepository.flush();
				
		} catch (Exception e) {
		    System.err.println("삭제 실패: " + e.getMessage());
		}
	}

	@Override
	public void deleteCartBySessionId(String sessionId) {
		List<Cart> cartItems = cartRepository.findBySessionId(sessionId);
		
		for (Cart cart : cartItems) {
			cart.setProduct(null);
		}
		
		if (!cartItems.isEmpty()) {
			cartRepository.deleteAll(cartItems);
		}
	}
}

