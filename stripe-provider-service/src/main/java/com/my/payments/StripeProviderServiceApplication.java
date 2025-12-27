package com.my.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
//@EnableDiscoveryClient
public class StripeProviderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StripeProviderServiceApplication.class, args);
	}

}
