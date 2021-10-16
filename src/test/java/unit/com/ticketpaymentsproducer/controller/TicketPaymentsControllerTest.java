package com.ticketpaymentsproducer.controller;

import com.avro.ticketpayments.Address;
import com.avro.ticketpayments.Buyer;
import com.avro.ticketpayments.Payment;
import com.avro.ticketpayments.Ticket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpaymentsproducer.domain.AddressRequest;
import com.ticketpaymentsproducer.domain.BuyerRequest;
import com.ticketpaymentsproducer.domain.PaymentRequest;
import com.ticketpaymentsproducer.domain.TicketRequest;
import com.ticketpaymentsproducer.producer.TicketPaymentsProducer;
import com.ticketpaymentsproducer.ticketpaymentsproducer.TicketpaymentsproducerApplication;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = TicketpaymentsproducerApplication.class)
@AutoConfigureMockMvc
class TicketPaymentsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    TicketPaymentsProducer ticketPaymentsProducer;

    @Test
    void postTicketPayments() throws Exception {

        Ticket ticketSchema = new Ticket();
        AddressRequest address = AddressRequest.builder().
                street("Av Jucelino Kubistcheck")
                .number(25414)
                .build();

        BuyerRequest buyer = BuyerRequest.builder()
                .name("Álvaro Silva")
                .cpf("980.744.640-67")
                .email("alvaro.silva@gmail.com")
                .build();

        TicketRequest ticket = TicketRequest.builder()
                .title("Cars 4")
                .dateTime("2021-11-27 20:15:00")
                .address(address)
                .buyer(buyer)
                .amount(1)
                .price(15.35)
                .paymentRequest(Payment.PAYPAL)
                .build();

        JsonAvroConverter converter = new JsonAvroConverter();

        String ticketString = objectMapper.writeValueAsString(ticket);

        when(ticketPaymentsProducer.sendTicket2(isA(Ticket.class))).thenReturn(null);

        mockMvc.perform(post("/v1/tickets")
                .content(ticketString)
                .contentType(MediaType.APPLICATION_JSON.toString()))
                .andExpect(status().isCreated());

    }
}
