package com.my.payments.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.my.payments.constant.ErrorCodeEnum;
import com.my.payments.exception.StripeProviderException;
import com.my.payments.http.HttpRequest;
import com.my.payments.http.HttpServiceEngine;
import com.my.payments.pojo.CreatePaymentRequest;
import com.my.payments.pojo.PaymentResponse;
import com.my.payments.service.helper.CreatePaymentHelper;
import com.my.payments.service.helper.ExpirePaymentHelper;
import com.my.payments.service.helper.GetPaymentHelper;
import com.my.payments.service.interfaces.PaymentService;
import com.my.payments.stripe.StripeResponse;
import com.my.payments.util.StripeResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final HttpServiceEngine httpServiceEngine;

	private final CreatePaymentHelper createPaymentHelper;

	private final GetPaymentHelper getPaymentHelper;
	
	private final ExpirePaymentHelper expirePaymentHelper;

	private final ChatClient chatClient;

	@Override
	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {
		log.info("Processing payment creation|| "
				+ "createPaymentRequest: {}", createPaymentRequest);

		// if createPaymementRequest 1st line item quantity is 0 or less then throw exception
		if (createPaymentRequest.getLineItems().get(0).getQuantity() <= 0) {
			throw new StripeProviderException(
					ErrorCodeEnum.INVALID_QUANTITY.getErrorCode(),
					ErrorCodeEnum.INVALID_QUANTITY.getErrorMessage(),
					HttpStatus.BAD_REQUEST
					);
		}

		HttpRequest httpRequest = createPaymentHelper.prepareHttpRequest(
				createPaymentRequest);
		log.info("Prepared HttpRequest: {}", httpRequest);

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HTTP Service response: {}", httpResponse);

		StripeResponse stripeResponse = createPaymentHelper.processResponse(httpResponse);
		log.info("Final PaymentResponse to be returned: {}", stripeResponse);

		PaymentResponse paymentRes = StripeResponseUtil.preparePaymentResponse(
				stripeResponse);
		log.info("PaymentResponse constructed: {}", paymentRes);

		return paymentRes;
	}

	@Override
	public PaymentResponse getPayment(String id) {
		log.info("Get Payment called| id: {}", id);

		HttpRequest httpRequest = getPaymentHelper.prepareHttpRequest(id);
		log.info("Prepared HttpRequest: {}", httpRequest);

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HTTP Service response: {}", httpResponse);

		StripeResponse stripeResponse = getPaymentHelper.processResponse(httpResponse);
		log.info("Final PaymentResponse to be returned: {}", stripeResponse);

		PaymentResponse paymentRes = StripeResponseUtil.preparePaymentResponse(
				stripeResponse);
		log.info("PaymentResponse constructed: {}", paymentRes);

		return paymentRes;
	}

	@Override
	public PaymentResponse expirePayment(String id) {
		log.info("Expire Payment called| id: {}", id);

		HttpRequest httpRequest = expirePaymentHelper.prepareHttpRequest(id);
		log.info("Prepared HttpRequest: {}", httpRequest);

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HTTP Service response: {}", httpResponse);

		StripeResponse stripeResponse = expirePaymentHelper.processResponse(httpResponse);
		log.info("Final PaymentResponse to be returned: {}", stripeResponse);

		PaymentResponse paymentRes = StripeResponseUtil.preparePaymentResponse(
				stripeResponse);
		log.info("PaymentResponse constructed: {}", paymentRes);

		return paymentRes;
	}

}
