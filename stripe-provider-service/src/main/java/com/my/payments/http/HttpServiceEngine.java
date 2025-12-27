package com.my.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.my.payments.constant.ErrorCodeEnum;
import com.my.payments.exception.StripeProviderException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpServiceEngine {

	private RestClient restClient;

	public HttpServiceEngine(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}

	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("Making an HTTP call");
		
		try {
			ResponseEntity<String> httpResponse = restClient
					.method(httpRequest.getHttpMethod())
					.uri(httpRequest.getUrl())
					.headers(t -> t.addAll(httpRequest.getHttpHeaders()))
					.body(httpRequest.getRequestBody())
					.retrieve()
					.toEntity(String.class);

			log.info("HTTP call completed httpResponse:{}", httpResponse);

			return httpResponse;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			// valid error response from Stripe.
			log.error("HTTP error response from Stripe: {}", e.getMessage(), e);
			
			// 504 Gateway timeout & 503 Service unavailable, we should throw Unable to connect to Stripe exception
			if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT
					|| e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
				throw new StripeProviderException(
						ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorCode(),
						ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorMessage(), 
						HttpStatus.SERVICE_UNAVAILABLE);
			}
			
			// create ResponseEntity with status code and body from exception and return the object
			return ResponseEntity.status(e.getStatusCode())
					.body(e.getResponseBodyAsString());
			
		} catch (Exception e) {// Unable to connect
			log.error("Exception occurred while making HTTP call: {}", e.getMessage(), e);
			
			throw new StripeProviderException(
					ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorCode(),
					ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorMessage(),
	                HttpStatus.SERVICE_UNAVAILABLE
	        );
			
		}
	}

	@PostConstruct
	public void init() {
		log.info("HttpServiceEngine initialized with RestClient: {}", restClient);
	}

}
