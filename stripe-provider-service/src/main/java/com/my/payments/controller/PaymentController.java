package com.my.payments.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.payments.constant.Constant;
import com.my.payments.pojo.CreatePaymentRequest;
import com.my.payments.pojo.PaymentResponse;
import com.my.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Constant.PAYMENTS)
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;
	
	@PostMapping
	public PaymentResponse createPayment(
			@RequestBody CreatePaymentRequest createPaymentRequest) {
		log.info("Creating a new payment| "
				+ "createPaymentRequest:{}", createPaymentRequest);
		
		PaymentResponse response = paymentService.createPayment(createPaymentRequest);
		log.info("Payment creation response: {}", response);
		
		return response;
	}
	
	@GetMapping("/{id}")
	public PaymentResponse getPayment(@PathVariable String id) {
		log.info("Get Payment called| id: {}", id);
		
		PaymentResponse response = paymentService.getPayment(id);
		log.info("Get Payment response: {}", response);
		
		return response;
	}
	
	@PostMapping("/{id}/expire")
	public PaymentResponse expirePayment(@PathVariable String id) {
		log.info("Expire Payment called| id: {}", id);
		
		PaymentResponse response = paymentService.expirePayment(id);
		log.info("Expire Payment response: {}", response);
		
		return response;
	}
}
