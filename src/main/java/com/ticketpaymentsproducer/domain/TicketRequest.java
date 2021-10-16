package com.ticketpaymentsproducer.domain;

import com.avro.ticketpayments.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    @NotEmpty
    @NotNull
    private String title;

    @NotNull
    private AddressRequest address;

    @NotNull
    private BuyerRequest buyer;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String dateTime;

    @NotNull
    private Integer amount;

    @NotNull
    private Double price;

    @NotNull
    private Payment paymentRequest;
}
