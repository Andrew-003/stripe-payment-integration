package com.my.payments.service.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.my.payments.constant.TransactionStatusEnum;
import com.my.payments.dao.interfaces.TransactionDao;
import com.my.payments.dto.TransactionDTO;
import com.my.payments.entity.TransactionEntity;
import com.my.payments.exception.ProcessingException;
import com.my.payments.http.HttpRequest;
import com.my.payments.http.HttpServiceEngine;
import com.my.payments.pojo.CreateTxnRequest;
import com.my.payments.pojo.InitiateTxnRequest;
import com.my.payments.pojo.TxnResponse;
import com.my.payments.service.helper.SPCreatePaymentHelper;
import com.my.payments.service.interfaces.PaymentService;
import com.my.payments.service.interfaces.PaymentStatusService;
import com.my.payments.stripeprovider.StripeProviderPaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentStatusService paymentStatusService;

	private final ModelMapper modelMapper;

	private final TransactionDao transactionDao;
	
	private final HttpServiceEngine httpServiceEngine;
	
	private final SPCreatePaymentHelper spCreatePaymentHelper;

	@Override
	public TxnResponse createTxn(CreateTxnRequest createTxnRequest) {
		log.info("Creating payment transaction createTxnRequest:{}",
				createTxnRequest);

		TransactionDTO txnDto = modelMapper.map(
				createTxnRequest, TransactionDTO.class);
		log.info("Mapped txnDto: {}", txnDto);

		String txnStatus = TransactionStatusEnum.CREATED.name();
		String txnReference = generateUniqueTxnRef();

		txnDto.setTxnStatus(txnStatus);
		txnDto.setTxnReference(txnReference);

		log.info("Final txnDto to be saved: {}", txnDto);

		TransactionDTO response = paymentStatusService.processStatus(txnDto);
		log.info("Response from PaymentStatusService: {}", response);

		TxnResponse createTxnResponse = new TxnResponse();
		createTxnResponse.setTxnReference(response.getTxnReference());
		createTxnResponse.setTxnStatus(response.getTxnStatus());

		log.info("CreateTxnResponse to be returned: {}", createTxnResponse);

		return createTxnResponse;
	}

	private String generateUniqueTxnRef() {
		return UUID.randomUUID().toString();
	}

	@Override
	public TxnResponse initiateTxn(String txnReference, InitiateTxnRequest initiateTxnRequest) {
		log.info("Initiating payment transaction||id:{}|initiateTxnRequest:{}", 
				txnReference, initiateTxnRequest);

		// update DB as INITIATED
		TransactionEntity txnEntity = transactionDao
				.getTransactionByTxnReference(txnReference);
		TransactionDTO txnDto = modelMapper.map(txnEntity, TransactionDTO.class);
		log.info("Mapped txnDto: {}", txnDto);

		txnDto.setTxnStatus(TransactionStatusEnum.INITIATED.name());
		txnDto = paymentStatusService.processStatus(txnDto);
		log.info("Updated txnDto to INITIATED: {}", txnDto);

		StripeProviderPaymentResponse successResponse = null;
		try {
			// Make Rest Http API call to stripe-provider-service for create-payment api
			HttpRequest httpRequest = spCreatePaymentHelper
					.prepareHttpRequest(initiateTxnRequest);
			log.info("Prepared HttpRequest for stripe-provider-service: {}", httpRequest);
			
			ResponseEntity<String> response = httpServiceEngine.makeHttpCall(httpRequest);
			log.info("Response from stripe-provider-service: {}", response);
			
			successResponse = spCreatePaymentHelper.processResponse(response);
			log.info("Processed StripeProviderPaymentResponse: {}", successResponse);
			
		} catch (ProcessingException e) {
			log.error("Error during initiating transaction: {}", e.getMessage());
			// Handle exception, possibly update txnDto to FAILED status
			txnDto.setTxnStatus(TransactionStatusEnum.FAILED.name());
			txnDto.setErrorCode(e.getErrorCode());
			txnDto.setErrorMessage(e.getMessage());
			txnDto = paymentStatusService.processStatus(txnDto);
			log.info("Updated txnDto to FAILED due to error: {}", txnDto);

			throw e; // re-throw to Global Exception handling 
		}
		
		// Update DB as PENDING, providerReference
		txnDto.setTxnStatus(TransactionStatusEnum.PENDING.name());
		txnDto.setProviderReference(successResponse.getId());
		
		txnDto = paymentStatusService.processStatus(txnDto);
		log.info("Updated txnDto to PENDING: {}", txnDto);

		// return the url back to the invoker.
		TxnResponse txnResponse = new TxnResponse();
		txnResponse.setTxnReference(txnDto.getTxnReference());
		txnResponse.setTxnStatus(txnDto.getTxnStatus());
		txnResponse.setRedirectUrl(successResponse.getUrl());
		log.info("Final TxnResponse to be returned: {}", txnResponse);

		return txnResponse;
	}

}
