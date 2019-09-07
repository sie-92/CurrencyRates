package com.sie.currency.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sie.currency.R;
import com.sie.currency.service.model.Currency;
import com.sie.currency.view.ui.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by ip-d SIE on 01/12/15.
 */
public class ListCurrencyAdapter extends ArrayAdapter<Currency> {
    Context context;
    List<Currency> items;
    LayoutInflater inflater;

    public ListCurrencyAdapter(Context context, int resource, List<Currency> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(List<Currency> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        convertView = inflater.inflate(R.layout.row, null);
        holder = new ViewHolder();
        holder.code = (TextView) convertView.findViewById(R.id.code);
        holder.value = (EditText) convertView.findViewById(R.id.value);

        holder.image = (ImageView) convertView.findViewById(R.id.img);


        holder.code.setText(items.get(position).code);
        holder.value.setText(items.get(position).value+"");

        holder.value.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(holder.value.getTag()==null && s.toString().length() != 0 ) {
                    try {
                        BigDecimal bd = new BigDecimal(Double.parseDouble(s.toString()) / items.get(position).rate)
                                .setScale(5, RoundingMode.HALF_UP);
                        ((MainActivity)context).setBaseValue(bd.doubleValue());
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

        try
        {
            InputStream ims = context.getAssets().open(items.get(position).code.toLowerCase()+".png");
            Drawable d = Drawable.createFromStream(ims, null);
            holder.image.setImageDrawable(d);
            ims .close();
        }
        catch(IOException ex)
        {    }

        return convertView;
    }

    public void update(Double baseValue) {
        for (Currency cur: items) {

            BigDecimal bd = new BigDecimal(baseValue*cur.rate)
                    .setScale(5, RoundingMode.HALF_UP);
            cur.value = bd.doubleValue();
        }
        notifyDataSetChanged();
    }


    class ViewHolder {

        public TextView code;
        public EditText value;
        public ImageView image;
    }
}
