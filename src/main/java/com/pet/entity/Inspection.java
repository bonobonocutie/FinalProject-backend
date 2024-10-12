package com.pet.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.pet.converter.IntegerJsonConverter;
import com.pet.converter.StringJsonConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Inspection {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer insIdx;

	@OneToOne
	@JoinColumn(name = "orderIdx")
	Orders orders;

	@Convert(converter = IntegerJsonConverter.class)
	private List<Integer> insCount;

	@Convert(converter = StringJsonConverter.class)
	private List<String> insDetail;

	@Convert(converter = StringJsonConverter.class)
	private List<String> insExDate;

	private LocalDate insDate;
}
