package com.pet.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
public class Stock {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
	Integer stIdx;

	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "pdIdx")
	Product product;

	Integer stCount;
}
