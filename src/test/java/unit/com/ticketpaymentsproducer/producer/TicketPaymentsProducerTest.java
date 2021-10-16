package com.ticketpaymentsproducer.producer;

import com.avro.ticketpayments.Address;
import com.avro.ticketpayments.Buyer;
import com.avro.ticketpayments.Payment;
import com.avro.ticketpayments.Ticket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketPaymentsProducerTest {

    @Mock
    KafkaTemplate<Integer, Ticket> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    TicketPaymentsProducer ticketPaymentsProducer;

    @Test
    public void sendTicket2TestException(){
        Ticket ticket = Ticket.newBuilder()
                .setTitle("Cars 4")
                .setAddressBuilder(Address.newBuilder()
                        .setNumber(25414)
                        .setStreet("Av Jucelino Kubistcheck"))
                .setBuyerBuilder(Buyer.newBuilder()
                        .setName("Álvaro Silva")
                        .setCpf("980.744.640-67")
                        .setEmail("alvaro.silva@gmail.com"))
                .setDateTime("2021-09-12")
                .setAmount(1)
                .setPrice(15.35)
                .setPayment(Payment.PAYPAL)
                .build();

        SettableListenableFuture future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Exception calling kafka"));

        when(kafkaTemplate.send(new ProducerRecord<Integer, Ticket>("ticket-payments", ticket))).thenReturn(future);

        assertThrows(Exception.class, () -> ticketPaymentsProducer.sendTicket2(ticket).get());
    }

    @Test
    public void sendTicket2TestSuccess() throws JsonProcessingException, ExecutionException, InterruptedException {

        Ticket ticket = Ticket.newBuilder()
                .setTitle("Cars 4")
                .setAddressBuilder(Address.newBuilder()
                        .setNumber(25414)
                        .setStreet("Av Jucelino Kubistcheck"))
                .setBuyerBuilder(Buyer.newBuilder()
                        .setName("Álvaro Silva")
                        .setCpf("980.744.640-67")
                        .setEmail("alvaro.silva@gmail.com"))
                .setDateTime("2021-09-12")
                .setAmount(1)
                .setPrice(15.35)
                .setPayment(Payment.PAYPAL)
                .build();

        SettableListenableFuture future = new SettableListenableFuture();

        Integer key = ReflectionTestUtils.invokeMethod(ticketPaymentsProducer, "keyGenerator");

        ProducerRecord<Integer, Ticket> record = new ProducerRecord<Integer, Ticket>("ticket-payments", key, ticket);

        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("ticket-payments", 1),
                1, 1, 342, System.currentTimeMillis(), 1, 2);

        SendResult<Integer, Ticket> result = new SendResult(record, recordMetadata);

        future.set(result);
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        ListenableFuture<SendResult<Integer, Ticket>> listenableFuture =  ticketPaymentsProducer.sendTicket2(ticket);

        SendResult<Integer, Ticket> result1 = listenableFuture.get();

        assertEquals(1, result1.getRecordMetadata().partition());
    }
}
