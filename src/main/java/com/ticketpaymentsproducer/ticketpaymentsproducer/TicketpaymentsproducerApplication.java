package com.ticketpaymentsproducer.ticketpaymentsproducer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan("com.ticketpaymentsproducer.*")
@EnableWebMvc
public class TicketpaymentsproducerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TicketpaymentsproducerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
