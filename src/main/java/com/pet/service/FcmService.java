package com.pet.service;

import com.pet.dto.FcmSendDTO;

public interface FcmService {
	Integer sendMessageTo(FcmSendDTO fcmSendDto);
}
