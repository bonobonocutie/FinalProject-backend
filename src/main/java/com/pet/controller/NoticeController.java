package com.pet.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.NoticeDTO;
import com.pet.service.NoticeService;

@RestController
@RequestMapping("/notice")
public class NoticeController {

	NoticeService noticeService;

	public NoticeController(NoticeService noticeService) {
		this.noticeService = noticeService;
	}

	@GetMapping("findAll/{userIdx}")
	public List<NoticeDTO> showNotice(@PathVariable Integer userIdx) {
		List<NoticeDTO> noticeList = noticeService.showNotice(userIdx);
		return noticeList;
	}

}
