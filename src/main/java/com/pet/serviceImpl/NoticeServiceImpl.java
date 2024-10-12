package com.pet.serviceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pet.dto.FcmSendDTO;
import com.pet.dto.NoticeDTO;
import com.pet.entity.Notice;
import com.pet.entity.Product;
import com.pet.entity.User;
import com.pet.repository.NoticeRepository;
import com.pet.repository.ProductRepository;
import com.pet.repository.UserRepository;
import com.pet.service.FcmService;
import com.pet.service.NoticeService;

@Service
public class NoticeServiceImpl implements NoticeService {

	NoticeRepository noticeRepository;
	ProductRepository productRepository;
	UserRepository userRepository;
	FcmService fcmService;

	public NoticeServiceImpl(NoticeRepository noticeRepository, ProductRepository productRepository, UserRepository userRepository, FcmService fcmService) {
		this.noticeRepository = noticeRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
		this.fcmService = fcmService;
	}

	public void triggerLowStockAlert(Integer pdIdx, String pdName, int remainingStock) throws IOException {
		String title = "재고 부족 알림";
		String body = pdName + "의 재고가 부족합니다. 남은 재고 : " + remainingStock;

		// 상품 및 사용자 정보 조회
		Product product = getProductById(pdIdx);
		User user = getUserByProductId(pdIdx);

		FcmSendDTO fcmSendDto = FcmSendDTO.builder()
				.title(title)
				.body(body)
				.product(product)
				.user(user)
				.build();

		// FCM 알림 전송
		fcmService.sendMessageTo(fcmSendDto);

		// 알림 정보를 데이터베이스에 저장
		saveNotification(user, product, title, body);
	}

	@Override
	public List<NoticeDTO> showNotice(Integer userIdx) {
//		ModelMapper mapper = new ModelMapper();
//		List<NoticeDTO> list = noticeRepository.findAllNotice(userIdx);
//		System.out.println(list);
//		List<NoticeDTO> noticeList = list.stream()
//				.map(e -> mapper.map(e, NoticeDTO.class))
//				.collect(Collectors.toList());
		List<NoticeDTO> noticeList = noticeRepository.findAllNotice(userIdx);
		return noticeList;
	}

	private void saveNotification(User user, Product product, String title, String body) {
		Notice notification = Notice.builder()
				.title(title)
				.body(body)
				.user(user)
				.product(product)
				.readStatus(false)
				.createdAt(LocalDateTime.now())
				.build();
		noticeRepository.save(notification);
	}

	private Product getProductById(Integer pdIdx) {
		return productRepository.findById(pdIdx).orElse(null);
	}

	private User getUserByProductId(Integer pdIdx) {
		Product product = getProductById(pdIdx);
		return product != null ? product.getUser() : null;
	}

}
