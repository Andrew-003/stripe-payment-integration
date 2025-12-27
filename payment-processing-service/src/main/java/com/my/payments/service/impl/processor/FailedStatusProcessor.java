package com.my.payments.service.impl.processor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.my.payments.dao.interfaces.TransactionDao;
import com.my.payments.dto.TransactionDTO;
import com.my.payments.entity.TransactionEntity;
import com.my.payments.service.helper.PaymentProcessorHelper;
import com.my.payments.service.interfaces.TxnStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FailedStatusProcessor implements TxnStatusProcessor {
	
	private final TransactionDao transactionDao;
	
	private final ModelMapper modelMapper;
	
	private final PaymentProcessorHelper paymentProcessorHelper;

	@Override
	public TransactionDTO processStatus(TransactionDTO txnDto) {
		log.info("Processing FAILED status for txnDto: {}", txnDto);
		
		if(paymentProcessorHelper.isTxnInFinalState(txnDto)) {
			log.warn("Transaction is already in a final state. No update performed for txnReference: {}",
					txnDto.getTxnReference());
			return txnDto;
		}
		
		TransactionEntity txnEntity = modelMapper
				.map(txnDto, TransactionEntity.class);
		log.info("Mapped txnEntity: {}", txnEntity);
		
		transactionDao.updateTransactionStatusDetailsByTxnReference(
				txnEntity);
		
		// TODO Async Message FAILED message to ActiveMq Queue for further processing
		
		log.info("Updated transaction status successfully for txnReference: {}", 
				txnDto.getTxnReference());
		
		return txnDto;
	}
	
}
