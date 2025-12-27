package com.my.payments.service.helper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.my.payments.constant.TransactionStatusEnum;
import com.my.payments.dao.interfaces.TransactionDao;
import com.my.payments.dto.TransactionDTO;
import com.my.payments.entity.TransactionEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessorHelper {

	private final TransactionDao transactionDao;

	private final ModelMapper modelMapper;
	
	private static final List<TransactionStatusEnum> FINAL_STATUSES = List.of(
			TransactionStatusEnum.SUCCESS, 
			TransactionStatusEnum.FAILED);

	public boolean isTxnInFinalState(TransactionDTO txnDto) {
		TransactionEntity existingTxnObj = transactionDao.getTransactionByTxnReference(txnDto.getTxnReference());
		TransactionDTO existingTxnDto = modelMapper.map(
				existingTxnObj, TransactionDTO.class);

		if (FINAL_STATUSES.contains(TransactionStatusEnum.fromName(
				existingTxnDto.getTxnStatus()))) {
			log.info("Transaction already in final status: {}. No update performed.", existingTxnDto.getTxnStatus());
			return true;
		}

		log.info("Transaction not in final status: {}. Proceeding with update.", existingTxnDto.getTxnStatus());
		return false;
	}

}
