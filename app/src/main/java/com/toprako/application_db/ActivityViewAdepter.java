package com.toprako.application_db;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class ActivityViewAdepter extends ArrayAdapter<String > {
    //---------------------------------------------------------------------------------------
    Activity activity;
    Boolean buy_bool=false;
    ArrayList<String> supplier_id,product_id,price;
    Bitmap decodedByte;
    ArrayList<byte[]> image;
    //--------------------------------------------------------------------------------------
    int id;
    DataBase db;
    public ActivityViewAdepter(Activity activity, ArrayList<String> supplier_id , ArrayList<String> product_id , ArrayList<byte[]> image, ArrayList<String> price,DataBase db,int id) {
        super(activity.getApplicationContext(),R.layout.activity_viewadepter,supplier_id);
        this.activity=activity;
        this.supplier_id=supplier_id;
        this.product_id=product_id;
        this.image=image;
        this.price=price;
        this.db=db;
        this.id=id;
        decodedByte = BitmapFactory.decodeByteArray(image.get(0), 0, image.get(0).length);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_viewadepter, null, true);
        ImageView ımageView = (ImageView) view.findViewById(R.id.view_adepter_img);
        TextView view_adepter_supplier_id = (TextView) view.findViewById(R.id.view_adepter_supplier_id);
        TextView view_adepter_prise = (TextView) view.findViewById(R.id.view_adepter_prise);
        final TextView view_adepter_product_id =(TextView) view.findViewById(R.id.view_adepter_productid);
        final ImageButton ımageButton = (ImageButton) view.findViewById(R.id.view_adepter_buy_btn);
        if(id == 0){
            if(image!=null){
                // Picasso.get().load("content://media/external/images/media/44607").into(ımageView);
                ımageView.setImageBitmap(decodedByte);
                ımageView.requestLayout();
                ımageView.getLayoutParams().height = 400;
                ımageView.getLayoutParams().width = 400;

            }
        }
        else if(id == 1){

         //   Picasso.get().load("content://media/external/images/media/44607").into(ımageView);
            ımageView.requestLayout();
            ımageView.setImageBitmap(decodedByte);
            ımageView.getLayoutParams().height = 500;
            ımageView.getLayoutParams().width = 550;
        }
        else {
            if(image!=null){
                WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                final Display display = wm.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                float height = metrics.heightPixels;
                height = height/ metrics.density;
                height=height-150;//footer-header-tabhost
                height=height*metrics.density;
                ımageView.requestLayout();
                ımageView.getLayoutParams().height = (int) height;
                ımageView.getLayoutParams().width = 1054;
                ımageView.setImageBitmap(decodedByte);
              //  Picasso.get().load("content://media/external/images/media/44607").into(ımageView);
            }
        }
        view_adepter_product_id.setText(product_id.get(position));
        view_adepter_prise.setText("$"+price.get(position));
        view_adepter_supplier_id.setText(supplier_id.get(position));

        db.get_shop_data();
        for (int i=0;i<db.get_shop_data_product_id.size();i++){
            if(db.get_shop_data_product_id.get(i).equals(product_id.get(position))){
                buy_bool=true;
                ımageButton.setBackgroundResource(R.drawable.cheched);
                db.selected_product_id.add(product_id.get(position));
            }
        }
        for (int i= 0;i<db.selected_product_id.size();i++)
        {
            if(db.selected_product_id.get(i).equals(product_id.get(position))){
                buy_bool=true;
                ımageButton.setBackgroundResource(R.drawable.cheched);
            }
        }
        ımageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buy_bool){
                    ımageButton.setBackgroundResource(R.drawable.uncheched);
                    db.delete_shop_data(product_id.get(position));
                    db.selected_product_id.remove(product_id.get(position));
                    buy_bool=false;
                }
                else
                {
                    ımageButton.setBackgroundResource(R.drawable.cheched);
                    db.selected_product_id.add(product_id.get(position));
                    buy_bool=true;
                }
            }
        });
        ımageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buy_bool){
                    ımageButton.setBackgroundResource(R.drawable.uncheched);
                    db.selected_product_id.remove(product_id.get(position));
                    db.delete_shop_data(product_id.get(position));
                    buy_bool=false;
                }
                else
                {
                    db.selected_product_id.add(product_id.get(position));
                    ımageButton.setBackgroundResource(R.drawable.cheched);
                    buy_bool=true;
                }
            }
        });

        return view;
    }
}
