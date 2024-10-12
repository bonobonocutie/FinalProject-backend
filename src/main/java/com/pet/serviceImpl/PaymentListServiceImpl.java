package com.pet.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dto.PaymentListDTO;
import com.pet.entity.Cart;
import com.pet.entity.Payment;
import com.pet.entity.PaymentList;
import com.pet.entity.Stock;
import com.pet.exception.ReduceStockException;
import com.pet.repository.CartRepository;
import com.pet.repository.PaymentRepository;
import com.pet.repository.ProductRepository;
import com.pet.repository.StockRepository;
import com.pet.service.NoticeService;
import com.pet.service.PaymentListService;

@Service
@Transactional
public class PaymentListServiceImpl implements PaymentListService {

	ProductRepository productRepository;
	PaymentRepository paymentRepository;
	CartRepository cartRepository;
	StockRepository stockRepository;
	NoticeService noticeService;

	public PaymentListServiceImpl(ProductRepository productRepository, PaymentRepository paymentRepository, CartRepository cartRepository, StockRepository stockRepository, NoticeService noticeService) {
		this.productRepository = productRepository;
		this.paymentRepository = paymentRepository;
		this.cartRepository = cartRepository;
		this.stockRepository = stockRepository;
		this.noticeService = noticeService;
	}

	// 결제 내역 추가
	@Transactional
	public List<PaymentListDTO> addPaymentList(Payment payment, String session) throws IOException {
		List<Cart> cartList = cartRepository.findBySessionId(session);

		List<PaymentList> paymentLists = new ArrayList<>();
		List<PaymentListDTO> paymentListDTOs = new ArrayList<>();

		for (Cart cart : cartList) {
			Integer pdIdx = cart.getProduct().getPdIdx();

			// 결제된 수량만큼 재고 감소
			Stock stock = stockRepository.findByProduct_PdIdx(pdIdx);

			if (stock.getStCount() < cart.getCartCount()) {
				throw new ReduceStockException("상품 재고가 부족합니다.");
			}

			stockRepository.reduceStock(pdIdx, cart.getCartCount());

			// 재고 부족 알림
			int remainStock = stock.getStCount() - cart.getCartCount();
			int minimumStockThreshold = cart.getProduct().getPdLimit();

			if (remainStock <= minimumStockThreshold) {
				// 최소 수량 이하일 때 알림 트리거
				noticeService.triggerLowStockAlert(pdIdx, cart.getProduct().getPdName(), remainStock);
			}

			PaymentList paymentList = PaymentList.builder()
					.payTime(payment.getUpdatedAt())
					.amount(cart.getProduct().getPdPrice())
					.pdCount(cart.getCartCount())
					.pdName(cart.getProduct().getPdName())
					.pdIdx(pdIdx)
					.build();
			PaymentListDTO paymentListDTO = paymentList.convertToDTO();
			paymentListDTOs.add(paymentListDTO);
			paymentLists.add(paymentList);
		}

		payment.setPaymentLists(paymentLists);
		paymentRepository.save(payment);

		return paymentListDTOs;
	}

	// 결제내역 조회
	@Override
	public List<PaymentListDTO> requestPaymentList(String paymentKey) {
		Payment payment = paymentRepository.findByPaymentKey(paymentKey);

		if (payment == null) {
			throw new IllegalArgumentException("존재하지 않는 결제 코드입니다");
		}

		List<PaymentListDTO> paymentListDTOs = payment.getPaymentLists().stream()
				.map(PaymentList::convertToDTO)
				.collect(Collectors.toList());

		System.out.println(paymentListDTOs);

		return paymentListDTOs;
	}
}
