package com.my.payments.pojo;

import lombok.Data;

@Data
public class TxnResponse {
	
	private String txnReference;
	private String txnStatus;
	private String redirectUrl;

}
