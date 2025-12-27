package com.my.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableDiscoveryClient
public class PaymentProcessingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentProcessingServiceApplication.class, args);
	}

}
