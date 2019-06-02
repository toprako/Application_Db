package com.toprako.application_db;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivitySettingAdepter extends ArrayAdapter<String > {
    Activity activity;
    ArrayList<String> price1,price2,percent;
    DataBase db;
    public ActivitySettingAdepter(Activity activity, ArrayList<String> price1,ArrayList<String> price2,ArrayList<String> percent,DataBase db) {
        super(activity,R.layout.activity_settingadepter,price1);
        this.activity=activity;
        this.price1=price1;
        this.price2=price2;
        this.percent= percent;
        this.db=db;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_settingadepter, null, true);
        final EditText txt_price1 = (EditText) view.findViewById(R.id.settingadepter_edittext_value1);
        final EditText txt_price2 = (EditText) view.findViewById(R.id.settingadepter_edittext_value2);
        final EditText txt_percent = (EditText) view.findViewById(R.id.settingadepter_edittext_value3);

        txt_price1.setText(price1.get(position));
        txt_price2.setText(price2.get(position));
        txt_percent.setText(percent.get(position));
        txt_price1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String val1,val2;
                val1=txt_price1.getText().toString();
                val2=txt_price2.getText().toString();
                if(val1.equals(val2)){
                    if(!val2.equals("") && !val1.equals("")) {
                        Toast.makeText(activity.getApplicationContext(), "The minimum-maximum value cannot be the same.", Toast.LENGTH_LONG).show();
                    }
                }
                else if(position!=0){
                    if(val1.equals(price1.get(position-1)) || val1.equals(price2.get(position-1))){
                        Toast.makeText(activity.getApplicationContext(),"Range Values Same",Toast.LENGTH_LONG).show();
                    }
                }
                price1.set(position,txt_price1.getText().toString());
                db.price1=price1;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txt_price2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String val1,val2;

                val1=txt_price1.getText().toString();
                val2=txt_price2.getText().toString();
                if(val2.equals(val1)){
                    if(!val2.equals("") && !val1.equals("")){
                        Toast.makeText(activity.getApplicationContext(),"The minimum-maximum value cannot be the same.",Toast.LENGTH_LONG).show();
                    }
                }
                else if(position!=0){
                       if(val2.equals(price1.get(position-1)) || val2.equals(price2.get(position-1))) {
                           Toast.makeText(activity.getApplicationContext(),"Range Values Same",Toast.LENGTH_LONG).show();
                       }
                }
                price2.set(position,txt_price2.getText().toString());
                db.price2=price2;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txt_percent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                percent.set(position,txt_percent.getText().toString());
                db.percent=percent;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }
}
