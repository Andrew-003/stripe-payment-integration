package com.my.payments.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.payments.constant.Constant;
import com.my.payments.pojo.CreateTxnRequest;
import com.my.payments.pojo.InitiateTxnRequest;
import com.my.payments.pojo.NotificationRequest;
import com.my.payments.pojo.TxnResponse;
import com.my.payments.service.interfaces.NotificationService;
import com.my.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Constant.V1_PAYMENTS)
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;
	
	private final NotificationService notificationService;
	
	@PostMapping
	public TxnResponse createTxn(@RequestBody CreateTxnRequest createTxnRequest) {
		log.info("Creating payment transaction||createTxnRequest:{}", 
				createTxnRequest);
		
		System.out.println("Creating payment transaction||createTxnRequest:{}" + createTxnRequest);
		
		log.trace("trace() method invoked");
		log.debug("debug() method invoked");
		
		log.info("info() method invoked");
		log.warn("warn() method invoked");
		log.error("error() method invoked");   
		
		TxnResponse response = paymentService.createTxn(createTxnRequest);
		
		log.info("Response from service: {}", response);
		
		return response;
	}
	
	@PostMapping("/{txnReference}/initiate")
	public TxnResponse initiateTxn(@PathVariable String txnReference, 
			@RequestBody InitiateTxnRequest initiateTxnRequest) {
		log.info("Initiating payment transaction||id:{}|initiateTxnRequest:{}", 
				txnReference, initiateTxnRequest);
		
		TxnResponse response = paymentService.initiateTxn(
				txnReference, initiateTxnRequest);
		log.info("Response from service: {}", response);
		
		return response;
    }
	
	@PostMapping("/notifications")
	public String processNotification(
			@RequestBody NotificationRequest notificationRequest) {
		
		log.info("Processing payment notification"
				+ "||notificationRequest:{}", notificationRequest);
		
		notificationService.processNotification(notificationRequest);
		
		return "";
    }

}
