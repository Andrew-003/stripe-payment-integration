package com.my.payments.pojo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreateTxnRequest {
	
    private Integer userId; // incoming request
    
    private String paymentMethod; //1, 'APM' incoming request
    private String provider; //1, 'STRIPE' incoming request
    private String paymentType; //1, 'SALE' incoming request
    
    private BigDecimal amount; //incoming request
    private String currency; // incoming request
    private String merchantTransactionReference; // incoming request

}
