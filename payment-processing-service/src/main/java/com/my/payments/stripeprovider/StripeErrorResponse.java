package com.my.payments.stripeprovider;

import lombok.Data;

@Data
public class StripeErrorResponse {
    private String errorCode;
    private String errorMessage;

    public StripeErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}