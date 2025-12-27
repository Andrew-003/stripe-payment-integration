package com.my.payments.service;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.my.payments.constant.TransactionStatusEnum;
import com.my.payments.service.impl.processor.CreatedStatusProcessor;
import com.my.payments.service.impl.processor.FailedStatusProcessor;
import com.my.payments.service.impl.processor.InitiatedStatusProcessor;
import com.my.payments.service.impl.processor.PendingStatusProcessor;
import com.my.payments.service.impl.processor.SuccessStatusProcessor;
import com.my.payments.service.interfaces.TxnStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusFactory {
	
	private final ApplicationContext context;

	public TxnStatusProcessor getStatusProcessor(TransactionStatusEnum statusEnum) {
		log.info("Getting status processor for txnStatusId: {}", statusEnum);
		switch (statusEnum) {
		case CREATED:
			return context.getBean(CreatedStatusProcessor.class);
		case INITIATED:
			return context.getBean(InitiatedStatusProcessor.class);
		case PENDING:
			return context.getBean(PendingStatusProcessor.class);
		case SUCCESS:
			return context.getBean(SuccessStatusProcessor.class);
		case FAILED:
			return context.getBean(FailedStatusProcessor.class);
		default:
			return null;
		}
	}
	
	
	

}
