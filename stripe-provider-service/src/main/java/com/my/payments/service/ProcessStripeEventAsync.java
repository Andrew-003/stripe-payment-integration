package com.my.payments.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.my.payments.http.HttpRequest;
import com.my.payments.http.HttpServiceEngine;
import com.my.payments.service.helper.StripeWebhookHelper;
import com.my.payments.stripe.StripeResponse;
import com.my.payments.util.JsonUtil;
import com.stripe.model.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessStripeEventAsync {

	private final JsonUtil jsonUtil;

	private final StripeWebhookHelper stripeWebhookHelper;

	private final HttpServiceEngine httpServiceEngine;

	private List<String> successEvents = List.of(
			"checkout.session.completed",
			"checkout.session.async_payment_succeeded"
			);

	private List<String> failedEvents = List.of(
			"checkout.session.async_payment_failed"
			);

	@Async
	public void processStripeEvent(Event event) {
		// Read incoming data,
		// data.object
		// understand which event
		log.info("Received Stripe Event: {}", event.getType());

		if (!successEvents.contains(event.getType())
				&& !failedEvents.contains(event.getType())) {
			log.info("Ignoring event type: {}", event.getType());
			return;
		}

		log.info("Processing incoming eventType {}", event.getType());

		String eventAsJson = event.getDataObjectDeserializer().getRawJson();
		log.info("eventAsJson: {}", eventAsJson);

		StripeResponse response = jsonUtil.convertJsonToObject(
				eventAsJson, StripeResponse.class);

		log.info("Mapped StripeResponse: {}", response);

		if (successEvents.contains(event.getType())) {
			// call processing service API to update txn as SUCCESS
			log.info("Payment Success for eventType: {}", event.getType());

			if(response.getPaymentStatus().equals("paid")) {
				triggerSuccessNotification(response);
			} else {
				log.warn("Payment not completed yet for eventType: {}", event.getType());
			}
			return;
		} 

		if (failedEvents.contains(event.getType())) {
			log.info("Payment Failed for eventType: {}", event.getType());

			triggerFailedNotification(response);

		}

	}

	private void triggerFailedNotification(StripeResponse response) {
		HttpRequest httpRequest = stripeWebhookHelper
				.prepareFailedNotificationRequest(response);
		log.info("Prepared FAILED notification request: {}", httpRequest);

		ResponseEntity<String> notificationResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("Response from processing-service for FAILED notification: {}", 
				notificationResponse);

		if(notificationResponse.getStatusCode().is2xxSuccessful()) {
			log.info("Successfully sent FAILED notification to processing-service");
		} else {
			log.error("Failed to send FAILED notification to processing-service. " + "Response: {}",
					notificationResponse);
		}
	}

	private void triggerSuccessNotification(StripeResponse response) {
		HttpRequest httpRequest = stripeWebhookHelper
				.prepareSuccessNotificationRequest(response);
		log.info("Prepared SUCCESS notification request: {}", httpRequest);

		ResponseEntity<String> notificationResponse = httpServiceEngine
				.makeHttpCall(httpRequest);
		log.info("Response from processing-service for FAILED notification: {}", 
				notificationResponse);

		if(notificationResponse.getStatusCode().is2xxSuccessful()) {
			log.info("Successfully sent SUCCESS notification to processing-service");
		} else {
			log.error("Failed to send SUCCESS notification to processing-service. " + "Response: {}",
					notificationResponse);
		}
	}

}
