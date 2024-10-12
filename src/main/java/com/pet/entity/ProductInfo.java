package com.pet.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
@Entity
public class ProductInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer pdInfoIdx;

	@Column(nullable = false)
	private String pdImgUrl;

	//	@Column(nullable = false)
	private String pdQrCode;
}
