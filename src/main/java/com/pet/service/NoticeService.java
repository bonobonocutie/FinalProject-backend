package com.pet.service;

import java.io.IOException;
import java.util.List;

import com.pet.dto.NoticeDTO;

public interface NoticeService {
	public void triggerLowStockAlert(Integer pdIdx, String pdName, int remainingStock) throws IOException;
	public List<NoticeDTO> showNotice(Integer userIdx);
}
