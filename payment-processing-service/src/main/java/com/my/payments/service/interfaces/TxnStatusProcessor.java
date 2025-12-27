package com.my.payments.service.interfaces;

import com.my.payments.dto.TransactionDTO;

public interface TxnStatusProcessor {
	
	public TransactionDTO processStatus(TransactionDTO txnDto);

}
