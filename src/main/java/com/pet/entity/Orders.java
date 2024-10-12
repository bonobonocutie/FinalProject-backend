package com.pet.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.pet.converter.IntegerJsonConverter;
import com.pet.converter.StringJsonConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userIdx", "orderDate"}))
public class Orders {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
	@Column(name = "orderIdx")
	private Integer orderIdx;

	@ManyToOne
	@JoinColumn(name = "userIdx")
	private User user;

	@Convert(converter = IntegerJsonConverter.class)
	private List<Integer> pdIdx;

	@Convert(converter = StringJsonConverter.class)
	private List<String> pdName;

	private LocalDate orderDate;

	@Convert(converter = IntegerJsonConverter.class)
	private List<Integer> orderCount;

}
