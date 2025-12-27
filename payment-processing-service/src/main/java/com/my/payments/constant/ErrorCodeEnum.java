package com.my.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
	GENERIC_ERROR("30000", "Unable to process the request. Please try again later."),
	UNABLE_TO_CONNECT_TO_STRIPE_PROVIDER("30001", "Unable to connect to stripe provider"),
	UNABLE_TO_INITIATE_PAYMENT("30002", "Error while initiating payment");

	private final String errorCode;
	private final String errorMessage;

	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}