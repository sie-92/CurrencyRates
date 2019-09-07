package com.sie.currency.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sie.currency.R;
import com.sie.currency.service.model.Currency;

import java.io.IOException;
import java.io.InputStream;
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
            cur.value = baseValue*cur.rate;
        }
        notifyDataSetChanged();
    }


    class ViewHolder {

        public TextView code;
        public EditText value;
        public ImageView image;
    }
}
