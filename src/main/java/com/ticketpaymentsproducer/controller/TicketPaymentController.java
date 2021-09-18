package com.ticketpaymentsproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ticketpaymentsproducer.domain.Ticket;
import com.ticketpaymentsproducer.producer.TicketPaymentProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/tickets")
public class TicketPaymentController {

    @Autowired
    private TicketPaymentProducer ticketPaymentProducer;


    @PostMapping
    public ResponseEntity<Ticket> reserveTicket(@Valid @RequestBody Ticket ticket) throws JsonProcessingException {

        log.info("Send Ticket");

        ticketPaymentProducer.sendTicket(ticket);

        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
