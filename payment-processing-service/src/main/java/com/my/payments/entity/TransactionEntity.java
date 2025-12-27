package com.my.payments.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class TransactionEntity {
    private Integer id;
    private Integer userId;

    private Integer paymentMethodId;
    private Integer providerId;
    private Integer paymentTypeId;
    private Integer txnStatusId;

    private BigDecimal amount;
    private String currency;

    private String merchantTransactionReference;
    private String txnReference;
    private String providerReference;

    private String errorCode;
    private String errorMessage;

    private Timestamp creationDate;
    private Integer retryCount;
}
