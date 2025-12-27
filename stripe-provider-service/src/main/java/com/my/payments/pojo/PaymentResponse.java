package com.my.payments.pojo;

import lombok.Data;

@Data
public class PaymentResponse {
	
	private String id;
	private String url;
	
	private String sessionStatus;
	
	private String paymentStatus;

}
