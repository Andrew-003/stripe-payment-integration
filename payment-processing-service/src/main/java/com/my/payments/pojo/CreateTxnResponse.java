package com.my.payments.pojo;

import lombok.Data;

@Data
public class CreateTxnResponse {
	
	private String txnReference;
	private String txnStatus;

}
