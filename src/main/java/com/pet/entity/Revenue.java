package com.pet.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
public class Revenue {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer rvIdx;

	private LocalDate rvDate;

	private Integer rvTotalPrice;
	
	private Integer userIdx;

	@OneToMany(mappedBy = "revenue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<PaymentList> paymentLists;
}
