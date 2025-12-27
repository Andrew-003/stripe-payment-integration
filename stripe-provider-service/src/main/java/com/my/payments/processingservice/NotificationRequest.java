package com.my.payments.processingservice;

import java.util.Map;

import lombok.Data;

@Data
public class NotificationRequest {
	
    private String providerReference; // incoming request
    
    private String txnReference; // incoming request

    private String provider; //'STRIPE' incoming request
    
    private String notificationType; // PAYMENT_SUCCESS, PAYMENT_FAILED
    
    private Map<String, String> payload;
    
}
