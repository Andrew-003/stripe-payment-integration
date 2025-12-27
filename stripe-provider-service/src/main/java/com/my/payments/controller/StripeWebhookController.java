package com.my.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.payments.service.interfaces.StripeWebhookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/stripe/webhook")
@Slf4j
@RequiredArgsConstructor
public class StripeWebhookController {
	
	private final StripeWebhookService stripeWebhookService;
	
	@PostMapping
	public String handleStripeWebhook(
			@RequestHeader("Stripe-Signature") String sigHeader,
			@RequestBody String jsonPayload) {
		log.info("Received Stripe Webhook|| sigHeader:{} | jsonPayload: {}", 
				sigHeader, jsonPayload);
		
		String response = stripeWebhookService.handleStripeWebhook(
				sigHeader, jsonPayload);
		log.info("Stripe Webhook processed|| response: {}", response);
		
		return "response";
	}

}
