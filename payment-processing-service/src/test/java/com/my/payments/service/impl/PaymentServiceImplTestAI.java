package com.my.payments.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
import com.my.payments.service.interfaces.PaymentStatusService;
import com.my.payments.stripeprovider.StripeProviderPaymentResponse;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTestAI {

    @Mock
    private PaymentStatusService paymentStatusService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TransactionDao transactionDao;

    @Mock
    private HttpServiceEngine httpServiceEngine;

    @Mock
    private SPCreatePaymentHelper spCreatePaymentHelper;

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl;

    private TransactionDTO txnDto;
    private CreateTxnRequest createTxnRequest;

    @BeforeEach
    void setUp() {
        txnDto = new TransactionDTO();
        txnDto.setTxnReference(UUID.randomUUID().toString());
        txnDto.setTxnStatus(TransactionStatusEnum.CREATED.name());

        createTxnRequest = new CreateTxnRequest();
    }

    // -------------------- createTxn Tests --------------------

    @Test
    void testCreateTxn_HappyPath() {
        when(modelMapper.map(createTxnRequest, TransactionDTO.class)).thenReturn(txnDto);
        when(paymentStatusService.processStatus(any(TransactionDTO.class))).thenReturn(txnDto);

        TxnResponse txnResponse = paymentServiceImpl.createTxn(createTxnRequest);

        assertNotNull(txnResponse);
        assertNotNull(txnResponse.getTxnReference());
        assertNotNull(txnResponse.getTxnStatus());
        assertNull(txnResponse.getRedirectUrl());

        assertEquals("CREATED", txnResponse.getTxnStatus());
        assertEquals(36, txnResponse.getTxnReference().length());

        // UUID format validation
        assertDoesNotThrow(() -> UUID.fromString(txnResponse.getTxnReference()));

        // Status must be valid enum
        assertTrue(Arrays.stream(TransactionStatusEnum.values())
                .anyMatch(e -> e.name().equals(txnResponse.getTxnStatus())));

        // Verify interactions
        verify(modelMapper, times(1)).map(createTxnRequest, TransactionDTO.class);
        verify(paymentStatusService, times(1)).processStatus(any(TransactionDTO.class));
        verifyNoMoreInteractions(modelMapper, paymentStatusService);
    }

    // -------------------- initiateTxn Tests --------------------

    @Test
    void testInitiateTxn_HappyPath() {
        String txnReference = UUID.randomUUID().toString();
        InitiateTxnRequest initiateTxnRequest = new InitiateTxnRequest();

        TransactionEntity txnEntity = new TransactionEntity();
        when(transactionDao.getTransactionByTxnReference(txnReference)).thenReturn(txnEntity);
        when(modelMapper.map(eq(txnEntity), eq(TransactionDTO.class))).thenReturn(txnDto);
        when(paymentStatusService.processStatus(any(TransactionDTO.class))).thenReturn(txnDto);

        HttpRequest httpRequest = HttpRequest.builder().build();
        ResponseEntity<String> httpResponse = ResponseEntity.ok("success");
        StripeProviderPaymentResponse stripeResponse = new StripeProviderPaymentResponse();
        stripeResponse.setId("provider-123");
        stripeResponse.setUrl("https://redirect-url");

        when(spCreatePaymentHelper.prepareHttpRequest(initiateTxnRequest)).thenReturn(httpRequest);
        when(httpServiceEngine.makeHttpCall(httpRequest)).thenReturn(httpResponse);
        when(spCreatePaymentHelper.processResponse(httpResponse)).thenReturn(stripeResponse);

        TxnResponse txnResponse = paymentServiceImpl.initiateTxn(txnReference, initiateTxnRequest);

        assertNotNull(txnResponse);
        assertEquals(txnDto.getTxnReference(), txnResponse.getTxnReference());
        assertEquals("PENDING", txnResponse.getTxnStatus());
        assertEquals("https://redirect-url", txnResponse.getRedirectUrl());

        verify(transactionDao).getTransactionByTxnReference(txnReference);
        verify(modelMapper).map(eq(txnEntity), eq(TransactionDTO.class));
        verify(paymentStatusService, atLeastOnce()).processStatus(any(TransactionDTO.class));
        verify(spCreatePaymentHelper).prepareHttpRequest(initiateTxnRequest);
        verify(httpServiceEngine).makeHttpCall(httpRequest);
        verify(spCreatePaymentHelper).processResponse(httpResponse);
    }

    @Test
    void testInitiateTxn_WhenProcessingExceptionThrown() {
        String txnReference = UUID.randomUUID().toString();
        InitiateTxnRequest initiateTxnRequest = new InitiateTxnRequest();

        TransactionEntity txnEntity = new TransactionEntity();
        when(transactionDao.getTransactionByTxnReference(txnReference)).thenReturn(txnEntity);
        when(modelMapper.map(eq(txnEntity), eq(TransactionDTO.class))).thenReturn(txnDto);
        when(paymentStatusService.processStatus(any(TransactionDTO.class))).thenReturn(txnDto);

        when(spCreatePaymentHelper.prepareHttpRequest(initiateTxnRequest)).thenThrow(new ProcessingException("500", "Stripe Down", HttpStatus.INTERNAL_SERVER_ERROR));

        ProcessingException ex = assertThrows(ProcessingException.class,
                () -> paymentServiceImpl.initiateTxn(txnReference, initiateTxnRequest));

        assertEquals("500", ex.getErrorCode());
        assertEquals("Stripe Down", ex.getMessage());

        assertEquals("FAILED", txnDto.getTxnStatus());
        assertEquals("500", txnDto.getErrorCode());
        assertEquals("Stripe Down", txnDto.getErrorMessage());

        verify(transactionDao).getTransactionByTxnReference(txnReference);
        verify(modelMapper).map(eq(txnEntity), eq(TransactionDTO.class));
        verify(paymentStatusService, atLeastOnce()).processStatus(any(TransactionDTO.class));
        verify(spCreatePaymentHelper).prepareHttpRequest(initiateTxnRequest);
    }
}
