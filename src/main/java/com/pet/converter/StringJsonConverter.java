package com.pet.converter;

import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class StringJsonConverter implements AttributeConverter<List<String>, String> {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting list to JSON", e);
		}
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, List.class);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting JSON to list", e);
		}
	}


}
