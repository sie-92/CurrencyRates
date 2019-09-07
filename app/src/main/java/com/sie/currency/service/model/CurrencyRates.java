package com.sie.currency.service.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class CurrencyRates {

    @SerializedName("base")
    private String base;

    @SerializedName("date")
    private String date;

    @SerializedName("rates")
    private JsonObject rates;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public JsonObject  getRates() {
        return rates;
    }

    public void setRates(JsonObject  rates) {
        this.rates = rates;
    }

}
