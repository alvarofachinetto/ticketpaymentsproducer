package com.ticketpaymentsproducer.controller;

import com.ticketpaymentsproducer.domain.Address;
import com.ticketpaymentsproducer.domain.Buyer;
import com.ticketpaymentsproducer.domain.Payment;
import com.ticketpaymentsproducer.domain.Ticket;
import com.ticketpaymentsproducer.ticketpaymentsproducer.TicketpaymentsproducerApplication;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TicketpaymentsproducerApplication.class)
@EmbeddedKafka(topics = {"ticket-payments"}, partitions = 1)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
"spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class TicketPaymentsControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;


    private Consumer<Integer, String> consumer;

    //criando um kafka embutido com configs basicas
    @BeforeEach
    void setup(){
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown(){consumer.close();}

    @Test
    @Timeout(5)
    void postTocketPayments(){
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

        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.set("content-tyupe", MediaType.APPLICATION_JSON.toString());

        HttpEntity<Ticket> request = new HttpEntity<>(ticket, httpHeaders);

        ResponseEntity<Ticket> ticketResponseEntity = testRestTemplate.exchange("/v1/tickets", HttpMethod.POST, request, Ticket.class);

        assertEquals(HttpStatus.CREATED, ticketResponseEntity.getStatusCode());

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "ticket-payments");

        String value = consumerRecord.value();

        assertNotNull(value);

    }

}
