package com.my.payments.service.interfaces;

import com.my.payments.pojo.CreatePaymentRequest;
import com.my.payments.pojo.PaymentResponse;

public interface PaymentService {
	
	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest);

	public PaymentResponse getPayment(String id);

	public PaymentResponse expirePayment(String id);

}
