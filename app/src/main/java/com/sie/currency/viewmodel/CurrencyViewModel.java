package com.sie.currency.viewmodel;

import androidx.lifecycle.ViewModel;

public class CurrencyViewModel extends ViewModel {

    public String curBase = "EUR";
    public double baseValue = 1;
    public int apiCallPeriod = 8000;
}
