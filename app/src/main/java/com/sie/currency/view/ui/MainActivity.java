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
import com.sie.currency.viewmodel.CurrencyViewModel;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
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
    private ListCurrencyAdapter myAdapter;
    private CurrencyViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(CurrencyViewModel.class);

        apiService = RetrofitClient.getClient();

        startDisposable();

        valueBase.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(valueBase.getTag()==null && s.toString().length() != 0 ) {
                    try {
                        mViewModel.baseValue = Double.parseDouble(s.toString());
                        myAdapter.update(mViewModel.baseValue);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
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
    public void setBaseValue(Double val) {
        mViewModel.baseValue = val;
        valueBase.setText(mViewModel.baseValue+"");

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (disposable.isDisposed()) {
            startDisposable();
        }
    }

    private void startDisposable() {
        disposable = Observable.interval(0, mViewModel.apiCallPeriod,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callApi, this::onError);
    }

    private void callApi(Long aLong) {


        Observable<CurrencyRates> observable = apiService.getCurrencyRates(mViewModel.curBase);
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
            BigDecimal bd = new BigDecimal(Double.parseDouble(valueBase.getText().toString())*rate)
                    .setScale(5, RoundingMode.HALF_UP);
            currencies.add(new Currency(key,rate,bd.doubleValue()));
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
            mViewModel.curBase = currencies.get(i).code;
            disposable.dispose();
            startDisposable();
            Log.d("bbbb",mViewModel.curBase);
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
