package com.ticketpaymentsproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpaymentsproducer.domain.Address;
import com.ticketpaymentsproducer.domain.Buyer;
import com.ticketpaymentsproducer.domain.Payment;
import com.ticketpaymentsproducer.domain.Ticket;
import com.ticketpaymentsproducer.producer.TicketPaymentsProducer;
import com.ticketpaymentsproducer.ticketpaymentsproducer.TicketpaymentsproducerApplication;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = TicketpaymentsproducerApplication.class)
@AutoConfigureMockMvc
class TicketPaymentsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TicketPaymentsProducer ticketPaymentsProducer;

    @Test
    void postTicketPayments() throws Exception {

        Address address = Address.builder().
                street("Av Jucelino Kubistcheck")
                .number(25414)
                .build();

        Buyer buyer = Buyer.builder()
                .name("Álvaro Silva")
                .cpf("980.744.640-67")
                .email("alvaro.silva@gmail.com")
                .build();

        Ticket ticket = Ticket.builder()
                .title("Cars 4")
                .dateTime(LocalDateTime.of(2021,11,27,20,15))
                .address(address)
                .buyer(buyer)
                .amount(1)
                .price(new BigDecimal(15.35))
                .payment(Payment.PAYPAL)
                .build();

        String ticketJson = new ObjectMapper().writeValueAsString(ticket);

        when(ticketPaymentsProducer.sendTicket2(isA(Ticket.class))).thenReturn(null);

        mockMvc.perform(post("/v1/tickets")
                .content(ticketJson)
                .contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(status().isCreated());

    }

}
