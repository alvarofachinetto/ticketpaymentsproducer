package com.ticketpaymentsproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Buyer {

    @NotNull
    private String name;

    @CPF
    @NotNull
    private String cpf;

    @Email
    @NotNull
    private String email;
}
