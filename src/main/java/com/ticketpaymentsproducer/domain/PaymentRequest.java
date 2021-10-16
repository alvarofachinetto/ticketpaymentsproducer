package com.ticketpaymentsproducer.domain;

public enum PaymentRequest {

    DEBT("debt"), INSTALLMENTS("installments"), PAYPAL("paypal");

    private final String payment;

    PaymentRequest(String payment) {
        this.payment = payment;
    }

    public String getPayment(){
        return this.payment;
    }
}
