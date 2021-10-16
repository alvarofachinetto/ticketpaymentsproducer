package com.ticketpaymentsproducer.controller;

import com.avro.ticketpayments.Address;
import com.avro.ticketpayments.Buyer;
import com.avro.ticketpayments.Payment;
import com.avro.ticketpayments.Ticket;
import com.ticketpaymentsproducer.ticketpaymentsproducer.TicketpaymentsproducerApplication;
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

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


    private Consumer<Integer, Object> consumer;

    //criando um kafka embutido com configs basicas
    @BeforeEach
    void setup(){
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new KafkaAvroDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown(){consumer.close();}

    @Test
    @Timeout(5)
    void postTocketPayments(){

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

        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        httpHeaders.set("content-tyupe", MediaType.APPLICATION_JSON.toString());

        HttpEntity<Ticket> request = new HttpEntity<>(ticket, httpHeaders);

        ResponseEntity<Ticket> ticketResponseEntity = testRestTemplate.exchange("/v1/tickets", HttpMethod.POST, request, Ticket.class);

        assertEquals(HttpStatus.CREATED, ticketResponseEntity.getStatusCode());

        ConsumerRecord<Integer, Object> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "ticket-payments");

        Object value = consumerRecord.value();

        assertNotNull(value);

    }

}
