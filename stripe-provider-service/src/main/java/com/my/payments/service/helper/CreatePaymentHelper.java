package com.my.payments.service.helper;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.my.payments.constant.Constant;
import com.my.payments.constant.ErrorCodeEnum;
import com.my.payments.exception.StripeProviderException;
import com.my.payments.http.HttpRequest;
import com.my.payments.pojo.CreatePaymentRequest;
import com.my.payments.pojo.LineItem;
import com.my.payments.stripe.StripeResponse;
import com.my.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatePaymentHelper {
	
	@Value("${stripe.api.key}")
	private String stripeApiKey;

	@Value("${stripe.create-session.url}")
	private String createSessionUrl;
	
	private final JsonUtil jsonUtil;
	
	private final ChatClient chatClient;
	
	public HttpRequest prepareHttpRequest(CreatePaymentRequest createPaymentRequest) {
		log.info("Preparing HttpRequest for CreatePaymentRequest: {}", 
				createPaymentRequest);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(stripeApiKey, Constant.EMPTY_STRING);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Prepare form data
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add(Constant.MODE, Constant.MODE_PAYMENT);// by stripe-provider-service
		
		requestBody.add(Constant.SUCCESS_URL, createPaymentRequest.getSuccessUrl());
		requestBody.add(Constant.CANCEL_URL, createPaymentRequest.getCancelUrl());
		
		for (int i = 0; i < createPaymentRequest.getLineItems().size(); i++) {
			LineItem lineItem = createPaymentRequest.getLineItems().get(i);
			
			requestBody.add("line_items[" + i + "][price_data][currency]",
					lineItem.getCurrency());
			requestBody.add("line_items[" + i + "][quantity]",
					String.valueOf(lineItem.getQuantity()));
			requestBody.add("line_items[" + i + "][price_data][product_data][name]",
					lineItem.getProductName());
			requestBody.add("line_items[" + i + "][price_data][unit_amount]",
					String.valueOf(lineItem.getUnitAmount()));
		}

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(createSessionUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setRequestBody(requestBody);
		
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

			if(response != null 
					&& response.getId() != null 
					&& response.getUrl() != null) {
				log.info("PaymentResponse is valid and contains necessary fields");
				return response;
			}
		}

		// if we reach this code, means it error & we should throw an exception.

		// if 4xx or 5xx, then also throw Payment creation failed exception
		if (httpResponse.getStatusCode().is4xxClientError() 
				|| httpResponse.getStatusCode().is5xxServerError()) {
			log.error("HTTP response indicates client or server error: {}", httpResponse.getStatusCode());

			// what to pass in errorCode & errorMessage

			// TODO, integrate with some AI model, give the errorJson to AI model, and ask for 1 liner errorDescription.
			// for now passing errorMessage field
			
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
				ErrorCodeEnum.PAYMENT_CREATION_FAILED.getErrorCode(),
				ErrorCodeEnum.PAYMENT_CREATION_FAILED.getErrorMessage(),
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
