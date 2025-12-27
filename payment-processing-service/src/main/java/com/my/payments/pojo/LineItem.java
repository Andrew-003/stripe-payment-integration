package com.my.payments.pojo;

import lombok.Data;

@Data
public class LineItem {
	
	private String currency;
	private int quantity;
	private String productName;
	private int unitAmount;

}
