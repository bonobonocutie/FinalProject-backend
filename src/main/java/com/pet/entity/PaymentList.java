package com.pet.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.pet.dto.PaymentListDTO;

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
public class PaymentList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer paymentListIdx;

	@Column(nullable = false)
	private Integer pdIdx;

	@Column(nullable = false)
	private String pdName;

	@Column(nullable = false)
	private Integer pdCount;

	@Column(nullable = false)
	private LocalDateTime payTime;

	@Column(nullable = false)
	private Integer amount;

	@ManyToOne
	@JoinColumn(name="rvIdx", insertable = false, updatable = false)
	private Revenue revenue;

	public PaymentListDTO convertToDTO() {
		return PaymentListDTO.builder()
				.pdIdx(this.pdIdx)
				.pdName(this.pdName)
				.amount(this.amount)
				.pdCount(this.pdCount)
				.payTime(this.payTime)
				.build();
	}

}
