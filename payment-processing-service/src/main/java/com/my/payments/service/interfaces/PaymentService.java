package com.my.payments.service.interfaces;

import com.my.payments.pojo.CreateTxnRequest;
import com.my.payments.pojo.TxnResponse;
import com.my.payments.pojo.InitiateTxnRequest;

public interface PaymentService {
	
	public TxnResponse createTxn(CreateTxnRequest createTxnRequest);
	
	public TxnResponse initiateTxn(String id, InitiateTxnRequest initiateTxnRequest);

}
