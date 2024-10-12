package com.pet.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
public class Notice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer ntIdx;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "body", nullable = false)
	private String body;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "pdIdx")
	Product product;

//	@Enumerated(EnumType.STRING)
//	private NoticeEnum ntCtg;

//	private String ntMesg;

//	private String ntPhone;

	@Column(name = "read_status", nullable = false)
	private boolean readStatus = false;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userIdx")
	private User user;
}
