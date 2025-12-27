package com.my.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.my.payments.constant.ErrorCodeEnum;
import com.my.payments.exception.ProcessingException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpServiceEngine {

	private RestClient restClient;

	public HttpServiceEngine(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}

	@CircuitBreaker(name = "payment-processing-service", 
			fallbackMethod = "fallbackProcessPayment")
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
			
			if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT
					|| e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
				throw new ProcessingException(
						ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE_PROVIDER.getErrorCode(),
						ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE_PROVIDER.getErrorMessage(), 
						HttpStatus.SERVICE_UNAVAILABLE);
			}
			
			// create ResponseEntity with status code and body from exception and return the object
			return ResponseEntity.status(e.getStatusCode())
					.body(e.getResponseBodyAsString());
			
		} catch (Exception e) {// Unable to connect
			log.error("Exception occurred while making HTTP call: {}", e.getMessage(), e);
			
			throw new ProcessingException(
					ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE_PROVIDER.getErrorCode(),
					ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE_PROVIDER.getErrorMessage(),
	                HttpStatus.SERVICE_UNAVAILABLE
	        );
			
		}
	}
	
	public ResponseEntity<String> fallbackProcessPayment(HttpRequest httpRequest, Throwable t) {
		// Handle fallback logic here
		log.error("Fallback method called due to: {}", t.getMessage(), t);
		throw new ProcessingException(
				ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE_PROVIDER.getErrorCode(),
				ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE_PROVIDER.getErrorMessage(),
               HttpStatus.SERVICE_UNAVAILABLE
       );  
	}


	@PostConstruct
	public void init() {
		log.info("HttpServiceEngine initialized with RestClient: {}", restClient);
	}

}
