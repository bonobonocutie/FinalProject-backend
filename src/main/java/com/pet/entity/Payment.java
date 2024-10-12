package com.pet.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.pet.config.Auditable;
import com.pet.dto.PaymentResDTO;
import com.pet.enums.PayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer paymentIdx;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PayType payType;

	@Column(nullable = false)
	private Integer amount;

	@Column(nullable = false)
	private String orderName;

	@Column(nullable = false)
	private String orderId;

	private boolean paySuccessYN;

	@Column
	private String paymentKey;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "paymentIdx")
	private List<PaymentList> paymentLists;

	public PaymentResDTO toPaymentResDto() {
		return PaymentResDTO.builder()
				.payType(payType.getDescription())
				.amount(amount)
				.orderName(orderName)
				.orderId(orderId)
				.createdAt(getCreatedAt())
				.build();
	}


}
