package com.sie.currency.service.model;

public class Currency {
    public String code;
    public Double rate;
    public Double value;

    public Currency(String code, Double rate, Double value) {
        this.code = code;
        this.rate = rate;
        this.value = value;
    }
}
