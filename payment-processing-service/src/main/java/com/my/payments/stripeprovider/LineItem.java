package com.my.payments.stripeprovider;

import lombok.Data;

@Data
public class LineItem {
	
	private String currency;
	private int quantity;
	private String productName;
	private int unitAmount;

}
