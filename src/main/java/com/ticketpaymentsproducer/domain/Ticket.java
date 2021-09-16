package com.ticketpaymentsproducer.domain;

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
public class Ticket {

    @NotEmpty
    @NotNull
    private String title;

    @NotNull
    private Address address;

    @NotNull
    private Buyer buyer;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    @NotNull
    private Integer amount;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Payment payment;
}
