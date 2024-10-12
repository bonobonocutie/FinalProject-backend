package com.pet.converter;

import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class IntegerJsonConverter implements AttributeConverter<List<Integer>, String> {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<Integer> attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting list to JSON", e);
		}
	}

	@Override
	public List<Integer> convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, List.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting JSON to list", e);
		}
	}


}
