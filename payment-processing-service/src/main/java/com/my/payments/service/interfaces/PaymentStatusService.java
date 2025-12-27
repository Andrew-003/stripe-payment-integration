package com.my.payments.service.interfaces;

import com.my.payments.dto.TransactionDTO;

public interface PaymentStatusService {
	
	public TransactionDTO processStatus(TransactionDTO txnDto);

}
