package com.pet.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pet.dto.FcmSendDTO;
import com.pet.entity.Notice;
import com.pet.repository.NoticeRepository;
import com.pet.service.FcmService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FcmServiceImpl implements FcmService {

	NoticeRepository noticeRepository;

	public FcmServiceImpl(NoticeRepository noticeRepository) {
		this.noticeRepository = noticeRepository;
	}

	@Override
	public Integer sendMessageTo(FcmSendDTO fcmSendDto) {
		try {
			// FCM 메시지 생성
			Message message = makeMessage(fcmSendDto);

			// FCM 서버에 메시지 전송
			String response = FirebaseMessaging.getInstance().send(message);
			log.info("FCM 메시지 전송 성공: " + response);

			// 알림 정보를 데이터베이스에 저장
			saveNotice(fcmSendDto);

			return 1;
		} catch (Exception e) {
			log.error("FCM 전송 오류: ", e);
			return 0;
		}
	}


	/**
	 * FCM 전송 정보를 기반으로 메시지를 구성합니다. (Object -> String)
	 *
	 * @param fcmSendDto FcmSendDto
	 * @return String
	 */
	private Message makeMessage(FcmSendDTO fcmSendDto) {
		// FCM Notification 생성
		Notification notification = Notification.builder()
				.setTitle(fcmSendDto.getTitle())
				.setBody(fcmSendDto.getBody())
				.build();

		// FCM Message 생성
		return Message.builder()
				.setToken(fcmSendDto.getToken())
				.setNotification(notification)
				.build();
	}

	/**
	 * FcmSendDto 정보를 기반으로 Notice 엔티티를 생성하고 저장합니다.
	 *
	 * @param fcmSendDto FcmSendDto
	 */

	private void saveNotice(FcmSendDTO fcmSendDto) {
		Notice notice = Notice.builder()
				.title(fcmSendDto.getTitle())
				.body(fcmSendDto.getBody())
//				.product(fcmSendDto.getProduct())
				.readStatus(false)
				.createdAt(LocalDateTime.now())
				.build();

		noticeRepository.save(notice);
	}
}
