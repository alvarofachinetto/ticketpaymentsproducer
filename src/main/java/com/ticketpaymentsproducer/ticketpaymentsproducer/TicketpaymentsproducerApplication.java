package com.ticketpaymentsproducer.ticketpaymentsproducer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.ticketpaymentsproducer.*")
public class TicketpaymentsproducerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TicketpaymentsproducerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
