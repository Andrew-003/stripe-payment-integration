package com.my.payments.service.helper;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.my.payments.constant.Constant;
import com.my.payments.constant.ErrorCodeEnum;
import com.my.payments.exception.StripeProviderException;
import com.my.payments.http.HttpRequest;
import com.my.payments.stripe.StripeResponse;
import com.my.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpirePaymentHelper {
	
	@Value("${stripe.api.key}")
	private String stripeApiKey;

	@Value("${stripe.expire-session.url}")
	private String expireSessionUrlTemplate;
	
	private final JsonUtil jsonUtil;
	
	private final ChatClient chatClient;
	
	public HttpRequest prepareHttpRequest(String id) {
		log.info("Preparing HttpRequest for ExpirePayment with id: {}", id);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(stripeApiKey, Constant.EMPTY_STRING);
		
		String expireSessionUrl = expireSessionUrlTemplate.replace("{id}", id);
		log.info("Constructed expireSessionUrl: {}", expireSessionUrl);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(expireSessionUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setRequestBody("");
		
		log.info("Prepared HttpRequest: {}", httpRequest);
		return httpRequest;
	}
	
	public StripeResponse processResponse(ResponseEntity<String> httpResponse) {
		log.info("Processing HTTP response| httpResponse:{}", httpResponse);

		if(httpResponse.getStatusCode().is2xxSuccessful()) {
			log.info("HTTP response is successful");

			StripeResponse response = jsonUtil.convertJsonToObject(
					httpResponse.getBody(), StripeResponse.class);
			log.info("Converted PaymentResponse: {}", response);

			if(response != null && response.getId() != null) {
				log.info("PaymentResponse is valid and contains necessary fields");
				return response;
			}
		}

		// if we reach this code, means it error & we should throw an exception.

		// if 4xx or 5xx, then also throw Payment creation failed exception
		if (httpResponse.getStatusCode().is4xxClientError() 
				|| httpResponse.getStatusCode().is5xxServerError()) {
			log.error("HTTP response indicates client or server error: {}", httpResponse.getStatusCode());

			String errorMessage = prepareErrorSummaryMessage(httpResponse);
			log.error("Prepared error message from AI model: {}", errorMessage);
				
			throw new StripeProviderException(
					ErrorCodeEnum.STRIPE_ERROR.getErrorCode(),
					errorMessage,
					HttpStatus.valueOf(httpResponse.getStatusCode().value()) 
					);

		} 
		
		log.error("Unexpected HTTP response status: {}", httpResponse.getStatusCode());
		throw new StripeProviderException(
				ErrorCodeEnum.GET_PAYMENT_FAILED.getErrorCode(),
				ErrorCodeEnum.GET_PAYMENT_FAILED.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR
				);
	}
	
	private String prepareErrorSummaryMessage(ResponseEntity<String> httpResponse) {
		// TODO currently just returning body, to make code work without Llama setup.
		if(true) {
			return httpResponse.getBody();
		}
		
		String promptTemplate = """
				Given the following json message from a third-party API, read the entire JSON, and summarize in 1 line:
				Instructions:
				1. Put a short, simple summary. Which exactly represents what error happened.
				2. Max length of summary less than 200 characters.
				3. Keep the output clear and concise.
				4. Summarize as message that we can send in API response to the client.
				5. Dont point any info to read external documentation or link.
				6. Dont write in double quotes.
				{error_json}
				""";
		
		String errorJson = httpResponse.getBody();
		
		String response = chatClient.prompt()
				.system("You are an technical analyst. which just retunrs 1 line summary of the json error")
				.user(promptUserSpec -> promptUserSpec
						.text(promptTemplate)
						.param("error_json", errorJson))
				.call()
				.content();
		
		log.info("AI Model response: {}", response);
		
		return response;
	}

}
