package com.my.payments.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.my.payments.dto.TransactionDTO;
import com.my.payments.entity.TransactionEntity;
import com.my.payments.util.convert.idtoname.PaymentMethodEnumIdToNameConverter;
import com.my.payments.util.convert.idtoname.PaymentTypeEnumIdToNameConverter;
import com.my.payments.util.convert.idtoname.ProviderEnumIdToNameConverter;
import com.my.payments.util.convert.idtoname.TransactionStatusEnumIdToNameConverter;
import com.my.payments.util.convert.nametoid.PaymentMethodEnumConverter;
import com.my.payments.util.convert.nametoid.PaymentTypeEnumConverter;
import com.my.payments.util.convert.nametoid.ProviderEnumConverter;
import com.my.payments.util.convert.nametoid.TransactionStatusEnumConverter;

@Configuration
public class AppConfig {

	@Bean
	ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		// Only map when field names match exactly
		modelMapper.getConfiguration()
		.setMatchingStrategy(MatchingStrategies.STRICT)
		.setSkipNullEnabled(true); // Optional: ignore nulls

		Converter<String, Integer> paymentMethodEnumConverter = new PaymentMethodEnumConverter();
		Converter<String, Integer> providerEnumConverter = new ProviderEnumConverter();
		// Define converters for TxnStatusEnum and PaymentTypeEnum if needed
		Converter<String, Integer> paymentTypeEnumConverter = new PaymentTypeEnumConverter();
		Converter<String, Integer> transactionStatusEnumConverter = new TransactionStatusEnumConverter();


		modelMapper.addMappings(new PropertyMap<TransactionDTO, TransactionEntity>() {
			@Override
			protected void configure() {
				using(paymentMethodEnumConverter).map(source.getPaymentMethod(), destination.getPaymentMethodId());
				using(providerEnumConverter).map(source.getProvider(), destination.getProviderId());
				// Add mappings for txnStatusId and paymentTypeId with their respective converters
				using(paymentTypeEnumConverter).map(source.getPaymentType(), destination.getPaymentTypeId());
				using(transactionStatusEnumConverter).map(source.getTxnStatus(), destination.getTxnStatusId());
			}
		});

		Converter<Integer, String> paymentMethodEnumIdToNameConverter = new PaymentMethodEnumIdToNameConverter();
		Converter<Integer, String> providerEnumIdToNameConverter = new ProviderEnumIdToNameConverter();
		// Define converters for TxnStatusEnum and PaymentTypeEnum if needed
		Converter<Integer, String> paymentTypeEnumIdToNameConverter = new PaymentTypeEnumIdToNameConverter();
		Converter<Integer, String> transactionStatusEnumIdToNameConverter = new TransactionStatusEnumIdToNameConverter();

		modelMapper.addMappings(new PropertyMap<TransactionEntity, TransactionDTO>() {
			@Override
			protected void configure() {
				using(paymentMethodEnumIdToNameConverter).map(source.getPaymentMethodId(), destination.getPaymentMethod());
				using(providerEnumIdToNameConverter).map(source.getProviderId(), destination.getProvider());
				// Add mappings for txnStatusId and paymentTypeId with their respective converters
				using(paymentTypeEnumIdToNameConverter).map(source.getPaymentTypeId(), destination.getPaymentType());
				using(transactionStatusEnumIdToNameConverter).map(source.getTxnStatusId(), destination.getTxnStatus());
			}
		});

		return modelMapper;
	}

	//@Bean
	//@LoadBalanced
	RestClient.Builder loadBalancedRestClientBuilder() {
		return RestClient.builder();
	}

}
