package com.ticketpaymentsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpaymentsproducer.domain.Ticket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
public class TicketPaymentProducer {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String TOPIC = "ticket-payments";

    public void sendTicket(Ticket ticket) throws JsonProcessingException {
        Integer key = keyGenerator();
        String value = objectMapper.writeValueAsString(ticket);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(key, value);

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handlerFailure(ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handlerSuccess(key, value, result);
            }
        });

    }

    private void handlerSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Send message SuccessFully for key: {}, value: {} and partition: {}", key, value, result.getProducerRecord().partition());
    }

    private void handlerFailure(Throwable ex) {
        log.error("Error sending the Message and Exception is: {}",  ex.getMessage());
    }

    private Integer keyGenerator(){
        return new Random().hashCode();
    }
}
