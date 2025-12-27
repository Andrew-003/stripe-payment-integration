package com.my.payments.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpRequest {
	
	private HttpMethod httpMethod;
	private String url;
	private HttpHeaders httpHeaders;
	private Object requestBody;

}
