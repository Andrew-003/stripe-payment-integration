package com.my.payments.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonUtil {
	
	private final ObjectMapper objectMapper;
	
	public <T> T convertJsonToObject(String jsonString, Class<T> valueType) {
		try {
			return objectMapper.readValue(jsonString, valueType);
		} catch (Exception e) {
			log.error("Error converting JSON to Object: {}", e.getMessage());
			return null;
		}
	}

	public String convertObjectToJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			log.error("Error converting Object to JSON: {}", e.getMessage());
			return null;
		}
	}
}
