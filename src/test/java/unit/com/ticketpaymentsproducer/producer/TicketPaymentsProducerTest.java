package com.ticketpaymentsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpaymentsproducer.domain.Address;
import com.ticketpaymentsproducer.domain.Buyer;
import com.ticketpaymentsproducer.domain.Payment;
import com.ticketpaymentsproducer.domain.Ticket;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketPaymentsProducerTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    TicketPaymentsProducer ticketPaymentsProducer;

    @Test
    public void sendTicket2TestException(){
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

        SettableListenableFuture<Ticket> listenableFuture = new SettableListenableFuture<>();
        listenableFuture.setException(new RuntimeException("Exception calling kafka"));
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(listenableFuture);

        assertThrows(Exception.class, () -> ticketPaymentsProducer.sendTicket2(ticket).get());
    }

    @Test
    public void sendTicket2TestSuccess() throws JsonProcessingException, ExecutionException, InterruptedException {
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

        String ticketRecord = objectMapper.writeValueAsString(ticket);
        SettableListenableFuture future = new SettableListenableFuture();

        Integer key = ReflectionTestUtils.invokeMethod(ticketPaymentsProducer, "keyGenerator");

        ProducerRecord<Integer, String> record = new ProducerRecord<>("ticket-payments", key, ticketRecord);

        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("ticket-payments", 1),
                1, 1, 342, System.currentTimeMillis(), 1, 2);

        SendResult<Integer, String> result = new SendResult<>(record, recordMetadata);

        future.set(result);
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        ListenableFuture<SendResult<Integer, String>> listenableFuture =  ticketPaymentsProducer.sendTicket2(ticket);

        SendResult<Integer, String> result1 = listenableFuture.get();

        assertEquals(1, result1.getRecordMetadata().partition());
    }
}
