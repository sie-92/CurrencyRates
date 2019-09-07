package com.sie.currency.service.repository;
/**
 * Created by sie on 08/02/2018.
 */


import com.sie.currency.service.model.CurrencyRates;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {


    String BASE_URL = "https://revolut.duckdns.org/";

    @GET("latest")
    Observable<CurrencyRates> getCurrencyRates(@Query("base") String base);
}