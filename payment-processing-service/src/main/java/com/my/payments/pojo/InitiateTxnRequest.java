package com.my.payments.pojo;

import java.util.List;

import lombok.Data;

@Data
public class InitiateTxnRequest {
	
	private String successUrl;
	private String cancelUrl;
	
	private List<LineItem> lineItems;

}
