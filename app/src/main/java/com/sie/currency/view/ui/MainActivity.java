package com.sie.currency.view.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sie.currency.R;
import com.sie.currency.service.model.Currency;
import com.sie.currency.service.model.CurrencyRates;
import com.sie.currency.service.repository.APIService;
import com.sie.currency.service.repository.RetrofitClient;
import com.sie.currency.view.adapter.ListCurrencyAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.codeBase)
    TextView codeBase;
    @BindView(R.id.imgBase)
    ImageView imgBase;
    @BindView(R.id.valueBase)
    EditText valueBase;
    @BindView(R.id.list)
    ListView list;

    Disposable disposable;
    private APIService apiService;
    private String curBase = "EUR";
    private ListCurrencyAdapter myAdapter;
    int period = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        apiService = RetrofitClient.getClient();

        startDisposable();

        valueBase.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(valueBase.getTag()==null && s.toString().length() != 0 ) {
                    myAdapter.update(Double.parseDouble(s.toString()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (disposable.isDisposed()) {
            startDisposable();
        }
    }

    private void startDisposable() {
        disposable = Observable.interval(0, period,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callApi, this::onError);
    }

    private void callApi(Long aLong) {


        Observable<CurrencyRates> observable = apiService.getCurrencyRates(curBase);
        observable.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .map(result -> result)
                .subscribe(this::handleResults, this::handleError);
    }

    private void onError(Throwable throwable) {
        Toast.makeText(this, "OnError in Observable Timer",
                Toast.LENGTH_LONG).show();
        Log.d("aaaa",throwable.getMessage());
    }


    private void handleResults(CurrencyRates currency) throws JSONException {

        codeBase.setText(currency.getBase());
        //valueBase.setTag("tag");
        //valueBase.setText("1");
        //valueBase.setTag(null);

        try
        {
            InputStream ims = getAssets().open(codeBase.getText().toString().toLowerCase()+".png");
            Drawable d = Drawable.createFromStream(ims, null);
            imgBase.setImageDrawable(d);
            ims .close();
        }
        catch(IOException ex)
        {    }

        JsonObject ratesObj = currency.getRates();
        Set<String> ratesKeys = ratesObj.keySet();
        //Log.d("aaaa",ratesObj.get("USD").getAsString());

        ArrayList<Currency> currencies = new ArrayList<Currency>();
        for (String key:ratesKeys) {
            Double rate = ratesObj.get(key).getAsDouble();
            currencies.add(new Currency(key,rate,Double.parseDouble(valueBase.getText().toString())*rate));
        }

        if(list.getAdapter()==null) {
            myAdapter = new ListCurrencyAdapter(this, 0, currencies);
            list.setAdapter(myAdapter);
        }
        else{
            myAdapter.setItems(currencies);
            myAdapter.notifyDataSetChanged();
        }


        list.setOnItemClickListener((adapterView, view, i, l) -> {
            curBase = currencies.get(i).code;
            disposable.dispose();
            startDisposable();
            Log.d("bbbb",curBase);
        });

    }

    private void handleError(Throwable t) {
    }

    @Override
    protected void onPause() {
        super.onPause();

        disposable.dispose();
    }
}
