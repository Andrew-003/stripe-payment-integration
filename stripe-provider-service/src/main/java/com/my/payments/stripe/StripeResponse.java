package com.my.payments.stripe;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StripeResponse {
	
	private String id;
	private String url;
	
	// session status
	private String status;
	
	// paid / unpaid
	@JsonProperty("payment_status")
	private String paymentStatus;

}
