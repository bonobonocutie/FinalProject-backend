package com.pet.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class NoticeDTO {
	private Integer ntIdx;
	private String title;
	private String body;
	private LocalDateTime createdAt;
	private boolean readStatus;
	private Integer userIdx;

	public NoticeDTO(Integer ntIdx, String title, String body, LocalDateTime createdAt, boolean readStatus, Integer userIdx) {
		this.ntIdx = ntIdx;
		this.title = title;
		this.body = body;
		this.createdAt = createdAt;
		this.readStatus = readStatus;
		this.userIdx = userIdx;
	}

//	private ProductDTO product;
//	private Integer pdIdx;
//	private String ntCtg;
//	private String ntMesg;
//	private String ntPhone;
}
