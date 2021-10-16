package com.ticketpaymentsproducer.producer;

import com.avro.ticketpayments.Ticket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class TicketPaymentsProducer {

    @Autowired
    private KafkaTemplate<Integer, Ticket> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String TOPIC = "ticket-payments";

    public void sendTicket(Ticket ticketRequest) throws JsonProcessingException {
        Integer key = keyGenerator();

        ListenableFuture<SendResult<Integer, Ticket>> listenableFuture = kafkaTemplate.sendDefault(key, ticketRequest);

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, Ticket>>() {
            @Override
            public void onFailure(Throwable ex) {
                handlerFailure(ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, Ticket> result) {
                handlerSuccess(key, ticketRequest, result);
            }
        });
    }

    public ListenableFuture<SendResult<Integer, Ticket>> sendTicket2(Ticket ticketRequest) throws JsonProcessingException {
        Integer key = keyGenerator();

        ProducerRecord<Integer, Ticket> record = buildProducerRecord(key, ticketRequest, TOPIC);

        ListenableFuture<SendResult<Integer, Ticket>> listenableFuture = kafkaTemplate.send(record);

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, Ticket>>() {
            @Override
            public void onFailure(Throwable ex) {
                handlerFailure(ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, Ticket> result) {
                handlerSuccess(key, ticketRequest, result);
            }
        });

        return listenableFuture;
    }

    private ProducerRecord<Integer, Ticket> buildProducerRecord(Integer key, Ticket value, String topic) {

        List<Header> headerList = List.of(new RecordHeader("event-source", "scanner".getBytes()));

        return new ProducerRecord<Integer, Ticket>(topic, null, null, key, value, headerList);
    }

    private void handlerSuccess(Integer key, Ticket value, SendResult<Integer, Ticket> result) {
        log.info("Send message SuccessFully for key: {}, value: {} and partition: {}", key, value, result.getProducerRecord().partition());
    }

    private void handlerFailure(Throwable ex) {
        log.error("Error sending the Message and Exception is: {}",  ex.getMessage());
    }

    private Integer keyGenerator(){
        return new Random().hashCode();
    }
}
