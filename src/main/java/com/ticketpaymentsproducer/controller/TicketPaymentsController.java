package com.ticketpaymentsproducer.controller;

import com.avro.ticketpayments.Address;
import com.avro.ticketpayments.Buyer;
import com.avro.ticketpayments.Ticket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ticketpaymentsproducer.domain.TicketRequest;
import com.ticketpaymentsproducer.producer.TicketPaymentsProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/v1/tickets")
public class TicketPaymentsController {

    @Autowired
    private TicketPaymentsProducer ticketPaymentProducer;


    @PostMapping
    public ResponseEntity<Ticket> reserveTicket(@Valid @RequestBody TicketRequest ticket) throws JsonProcessingException {
         Ticket ticketReq = Ticket.newBuilder()
                .setTitle(ticket.getTitle())
                .setAddressBuilder(Address.newBuilder()
                        .setNumber(ticket.getAddress().getNumber())
                        .setStreet(ticket.getAddress().getStreet()))
                .setBuyerBuilder(Buyer.newBuilder()
                        .setName(ticket.getBuyer().getName())
                        .setCpf(ticket.getBuyer().getCpf())
                        .setEmail(ticket.getBuyer().getEmail()))
                .setDateTime(ticket.getDateTime())
                .setAmount(ticket.getAmount())
                .setPrice(ticket.getPrice())
                 .setPayment(ticket.getPaymentRequest())
                .build();

        log.info("Send Ticket");

        ticketPaymentProducer.sendTicket2(ticketReq);

        return ResponseEntity.status(HttpStatus.CREATED).body(ticketReq);
    }
}
