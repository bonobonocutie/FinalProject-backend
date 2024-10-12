package com.pet.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.pet.dto.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
//@ToString
@Entity
public class Product {
	@ManyToOne
	@JoinColumn(name = "userIdx", nullable = false)
	private User user;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
	@Column(name = "pdIdx")
	private Integer pdIdx;

	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "pdInfoIdx", nullable = false)
	private ProductInfo productInfo;

	@ManyToOne
	@JoinColumn(name = "ctgIdx", nullable = false)
	private Category category;

	private String pdName;
	private Integer pdPrice;
	private Integer pdLimit;

	public static ProductDTO convertToDTO(Product product) {
		return ProductDTO.builder()
				.pdIdx(product.getPdIdx())
				.pdName(product.getPdName())
				.pdPrice(product.getPdPrice())
				.pdLimit(product.getPdLimit())
				.build();
	}
}