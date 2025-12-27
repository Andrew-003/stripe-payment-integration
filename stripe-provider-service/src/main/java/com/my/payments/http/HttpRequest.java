package com.my.payments.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Data;

@Data
public class HttpRequest {
	
	private HttpMethod httpMethod;
	private String url;
	private HttpHeaders httpHeaders;
	private Object requestBody;

}
