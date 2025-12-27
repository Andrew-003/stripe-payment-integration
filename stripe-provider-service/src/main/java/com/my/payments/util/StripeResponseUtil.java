package com.my.payments.util;

import com.my.payments.pojo.PaymentResponse;
import com.my.payments.stripe.StripeResponse;

public class StripeResponseUtil {
	
	private StripeResponseUtil() {
	}
	
	public static PaymentResponse preparePaymentResponse(StripeResponse stripeResponse) {
		PaymentResponse paymentRes = new PaymentResponse();
		paymentRes.setId(stripeResponse.getId());
		paymentRes.setUrl(stripeResponse.getUrl());
		paymentRes.setSessionStatus(stripeResponse.getStatus());
		paymentRes.setPaymentStatus(stripeResponse.getPaymentStatus());
		return paymentRes;
	}

}
