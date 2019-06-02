package com.toprako.application_db;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ActivityShopAdepter extends ArrayAdapter<String> {

    Activity activity;
    private ArrayList<String> numbers,qytpppercent,adress;
    ArrayList<String> supplier_id,product_id,price,qyt;
    DataBase db;
    ArrayList<byte[]> image,slip_img;

    public ActivityShopAdepter(Context context,Activity activity, ArrayList<String> supplier_id,ArrayList<String> product_id,
    ArrayList<byte[]> image,ArrayList<String> price,ArrayList<String> qytpppercent,
    ArrayList<String> adress,ArrayList<byte[]> slip_img,ArrayList<String> qyt,DataBase db) {
        super(activity,R.layout.activity_shopadepter,supplier_id);
        this.activity=activity;
        this.supplier_id=supplier_id;
        this.product_id=product_id;
        this.image=image;
        this.price=price;
        this.qytpppercent=qytpppercent;
        this.adress=adress;
        this.slip_img=slip_img;
        this.qyt=qyt;
        this.db=db;

        numbers=new ArrayList<>();
        for (int i = 1; i <= 1000; i++)
        {
            numbers.add(String.valueOf(i));
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_shopadepter, null, true);
        ImageView ımageView = (ImageView) view.findViewById(R.id.shopadepter_img);
        TextView shop_adepter_supplier_id = (TextView) view.findViewById(R.id.shopadepter_supplier_id);
        TextView shop_adepter_prise = (TextView) view.findViewById(R.id.shopadepter_price);
        TextView shop_adepter_product_id =(TextView) view.findViewById(R.id.shopadepter_productid);
        final EditText shop_adepter_shippingadres = (EditText) view.findViewById(R.id.shopadepter_adrestext);
        final Spinner shopadpter_spinner= (Spinner) view.findViewById(R.id.shopadpter_spinner);
        final TextView shopadepter_payable = (TextView) view.findViewById(R.id.shopadepter_payable);
        final TextView shopadepter_slip = (TextView) view.findViewById(R.id.shopadepter_slip);

        Bitmap decodedByte = BitmapFactory.decodeByteArray(image.get(position), 0, image.get(position).length);
        ımageView.setImageBitmap(decodedByte);

        final LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        ArrayAdapter<String> adapterState = new ArrayAdapter<String>(layoutInflater.getContext(), android.R.layout.simple_spinner_item, numbers);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shopadpter_spinner.setAdapter(adapterState);

        if(!qyt.get(position).equals("")){
            int a = Math.round(Float.parseFloat(qyt.get(position)));
            shopadpter_spinner.setSelection(a-1);
        }

        shop_adepter_shippingadres.setText(adress.get(position));
        shop_adepter_shippingadres.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adress.set(position,shop_adepter_shippingadres.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        shopadpter_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position2, long id) {
                qytpppercent.set(position, String.valueOf(((Float.parseFloat(price.get(position))) * (Float.parseFloat(String.valueOf(Integer.parseInt(parent.getSelectedItem().toString())))))));
                shopadepter_payable.setText("$"+qytpppercent.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        shop_adepter_prise.setText("$"+price.get(position));
        shop_adepter_product_id.setText(product_id.get(position));
        shop_adepter_supplier_id.setText(supplier_id.get(position));

        shopadepter_slip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                final Display display = wm.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                PopupWindow pwindo;
                final Point size = new Point();
                display.getSize(size);
                final LayoutInflater layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View inflatedView = layoutInflater.inflate(R.layout.upload_view_slip, null,false);
                final ImageView img = (ImageView) inflatedView.findViewById(R.id.upload_view_slip_img);//upload_view_slip_img
                Button bt_add = (Button) inflatedView.findViewById(R.id.bt_upload_view_slip_add);
                final Button bt_delete = (Button) inflatedView.findViewById(R.id.bt_upload_view_slip_delete);
                Button bt_cancel = (Button) inflatedView.findViewById(R.id.upload_view_slip_save_close);
                Button bt_view = (Button) inflatedView.findViewById(R.id.upload_view_slip_viewed);
                pwindo = new PopupWindow(inflatedView, size.x-5,size.y-300, true );
                pwindo.setBackgroundDrawable(activity.getResources().getDrawable(android.R.color.white));

                bt_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(intent, position);
                        Toast.makeText(activity.getApplicationContext(),"Add Succeed",Toast.LENGTH_LONG).show();
                    }
                });
                bt_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            slip_img.set(position,new byte[1]);
                            Toast.makeText(activity.getApplicationContext(),"Delete Succeed",Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                        }

                    }
                });
                final PopupWindow finalPwindo = pwindo;
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalPwindo.dismiss();
                    }
                });
                bt_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap decodedByte;
                        if (slip_img.get(position).length > 1) {
                            decodedByte = BitmapFactory.decodeByteArray(slip_img.get(position), 0, slip_img.get(position).length);
                            img.setImageBitmap(decodedByte);
                        }

                    }
                });


                pwindo.setFocusable(true);
                pwindo.setOutsideTouchable(true);
                inflatedView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.up));
                pwindo.showAtLocation(inflatedView, Gravity.CENTER, 0,0);
            }
        });
        return view;
    }

}
