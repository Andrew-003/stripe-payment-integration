package com.my.payments.service.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.my.payments.constant.NotificationType;
import com.my.payments.http.HttpRequest;
import com.my.payments.processingservice.NotificationRequest;
import com.my.payments.stripe.StripeResponse;
import com.my.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeWebhookHelper {
	
	private static final String STRIPE = "STRIPE";

	@Value("${processing.notification.url}")
	private String processingNotificationUrl;

	private final JsonUtil jsonUtil;

	public HttpRequest prepareSuccessNotificationRequest(StripeResponse stripeResponse) {
		log.info("Preparing success notification request for stripeResponse: {}", 
				stripeResponse);
		
		HttpHeaders httpHeaders	 = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		NotificationRequest reqObj = new NotificationRequest();
		reqObj.setNotificationType(NotificationType.PAYMENT_SUCCESS.name());
		reqObj.setProvider(STRIPE);
		reqObj.setProviderReference(stripeResponse.getId());

		String reqAsJson = jsonUtil.convertObjectToJson(reqObj);
		log.info("Converted CreatePaymentRequest to JSON: {}", reqAsJson);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(processingNotificationUrl);
		httpRequest.setHttpHeaders(httpHeaders);
		httpRequest.setRequestBody(reqAsJson);
		
		log.info("Prepared HttpRequest: {}", httpRequest);
		return httpRequest;
	}
	
	
	public HttpRequest prepareFailedNotificationRequest(
			StripeResponse stripeNotification) {
		log.info("Preparing failed notification request for providerReference: {}", 
				stripeNotification);
		
		HttpHeaders httpHeaders	 = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		NotificationRequest reqObj = new NotificationRequest();
		reqObj.setNotificationType(NotificationType.PAYMENT_FAILED.name());
		reqObj.setProvider(STRIPE);
		reqObj.setProviderReference(stripeNotification.getId());
		
		
		// TODO set errorcode & errorMessage in payload	map
		Map<String, String> payload = new HashMap<>();
		payload.put("errorCode", "123"); // TODO get actual error code from stripeNotification
		payload.put("errorMessage", "Payment failed due to XYZ reason"); // TODO get actual error message from stripeNotification
		reqObj.setPayload(payload);
		

		String reqAsJson = jsonUtil.convertObjectToJson(reqObj);
		log.info("Converted CreatePaymentRequest to JSON: {}", reqAsJson);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(processingNotificationUrl);
		httpRequest.setHttpHeaders(httpHeaders);
		httpRequest.setRequestBody(reqAsJson);
		
		log.info("Prepared HttpRequest: {}", httpRequest);
		return httpRequest;
	}
	
}
