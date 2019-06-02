package com.toprako.application_db;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocalActivityManager localActivityManager;
    private DataBase db;
    private PopupWindow pwindo;
    int last_index_add=0;
    private ArrayList<Uri> imageUriArray = new ArrayList<Uri>();
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 1905;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new system_setting_cache().execute();
        setContentView(R.layout.activity_main);
        int permissionWRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            List<String> izinler = new ArrayList<>();
            izinler.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{izinler.get(0)}, REQUEST_ID_MULTIPLE_PERMISSIONS);
        }
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        new system_setting(savedInstanceState,tabHost).execute();
        ImageButton all_column = (ImageButton)  findViewById(R.id.main_column_all);
        ImageButton two_column = (ImageButton) findViewById(R.id.main_column_two);
        ImageButton one_column = (ImageButton) findViewById(R.id.main_column_one);
        ImageButton setting = (ImageButton) findViewById(R.id.setting_icon);
        ImageButton whatsapp = (ImageButton) findViewById(R.id.whatsapp_icon);
        ImageButton shop = (ImageButton) findViewById(R.id.shop_icon);


        all_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             new columns_setting(0,db).execute();
            }
        });
        two_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { new columns_setting(1,db).execute();
            }
        });
        one_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             new columns_setting(2,db).execute();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View inflatedView = layoutInflater.inflate(R.layout.activity_setting, null,false);
                //--------------------------------------------------------------------------------------------------------
                final GridView gridView=(GridView) inflatedView.findViewById(R.id.activity_setting_gridview);
                //--------------------------------------------------------------------------------------
                WindowManager wm = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
                final Display display = wm.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                float height = metrics.heightPixels;
                height = height/ metrics.density;
                height=height-180;//footer-header-tabhost
                height=height*metrics.density;
                gridView.getLayoutParams().height = (int)height;
                //-----------------------------------------------------------------------------------------
                Button done = (Button) inflatedView.findViewById(R.id.bt_activity_setting_done);
                ImageButton add=(ImageButton) inflatedView.findViewById(R.id.bt_activity_setting_add);
                ImageButton delete = (ImageButton) inflatedView.findViewById(R.id.bt_activity_setting_delete);
                db.get_all_data();
                db.get_precent();

                if (db.price2_db.size()>0) {
                    db.price1 = db.price1_db;
                    db.price2 = db.price2_db;
                    db.percent = db.percent_db;
                }
                else {
                    db.price1.clear();
                    db.price2.clear();
                    db.percent.clear();

                    db.price1.add("");
                    db.price2.add("");
                    db.percent.add("");
                }
                final ActivitySettingAdepter adepter = new ActivitySettingAdepter(MainActivity.this,db.price1,db.price2,db.percent,db);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        last_index_add++;
                        db.price1.add("");
                        db.price2.add("");
                        db.percent.add("");
                        adepter.notifyDataSetChanged();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        last_index_add--;
                        int max = db.price1.size();
                        if(max-1 >= 0){
                            if(!db.price1.get(max-1).equals("")){
                                db.delete_percent(max-1);
                            }
                            db.price1.remove(max-1);
                            db.price2.remove(max-1);
                            db.percent.remove(max-1);
                        }
                        adepter.notifyDataSetChanged();
                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i=0;i<db.price1.size();i++){
                            if(!db.price1.get(i).equals("")){
                                if(db.value_control_setting(db.price1.get(i)).equals("1")){
                                    db.update_percent(i);
                                }
                                else
                                {
                                    db.save_precent(i);
                                }
                                Toast.makeText(getApplicationContext(),"Successful Addition",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Do not leave free space",Toast.LENGTH_LONG).show();
                            }
                        }
                        new columns_setting(0,db).execute();
                        pwindo.dismiss();
                    }
                });
                gridView.setAdapter(adepter);
                //--------------------------------------------------------------------------------------------------------
                final Point size = new Point();
                display.getSize(size);
                pwindo = new PopupWindow(inflatedView, size.x-5,size.y-300, true );
                pwindo.setBackgroundDrawable(getResources().getDrawable(android.R.color.white));
                pwindo.setFocusable(true);
                pwindo.setOutsideTouchable(true);
                inflatedView.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.up));
                pwindo.showAtLocation(inflatedView, Gravity.CENTER, 0,0);
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.get_all_data();
                db.get_precent();
                imageUriArray.clear();
                ArrayList<byte[]>image;
                image= new ArrayList<>();
                for (int i = 0;i< db.product_id.size();i++){
                    for (int j = 0;j<db.selected_product_id.size();j++){
                        if(db.product_id.get(i).equals(db.selected_product_id.get(j))){
                            image.add(db.image.get(i));
                        }
                    }
                }
                for (int i =0;i<image.size();i++)
                {

                    Bitmap decodedByte = BitmapFactory.decodeByteArray(image.get(i), 0, image.get(i).length);
                    imageUriArray.add(getLocalBitmapUri(decodedByte));
                }
                try
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setPackage("com.whatsapp");
                    intent.setType("text/plain");
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
                    intent.setType("image/jpeg");
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"whatsapp application not found",Toast.LENGTH_LONG).show();
                }
            }
        });
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowManager wm = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
                final Display display = wm.getDefaultDisplay();

                final LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View inflatedView = layoutInflater.inflate(R.layout.activity_shop, null,false);
                final GridView gridView=(GridView) inflatedView.findViewById(R.id.activity_shop_gridview);
                Button bt_done_shop = (Button)inflatedView.findViewById(R.id.bt_activity_shop_done);

                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                float height = metrics.heightPixels;
                height = height/ metrics.density;
                height=height-180;//footer-header-tabhost
                height=height*metrics.density;
                gridView.getLayoutParams().height = (int)height;
                //--------------------------------------------------------------------------------------
                ArrayList<String>  supplier_id,price;
                ArrayList<String> product_id;
                ArrayList<byte[]> image;
                supplier_id=new ArrayList<>();
                product_id = new ArrayList<>();
                image= new ArrayList<>();
                price=new ArrayList<>();
                db.adress.clear();
                db.slip_img.clear();
                //-------------------------------------------------------------------------------------
                db.get_shop_data();
                db.get_all_data();
                db.get_precent();


                supplier_id = db.get_shop_data_supplier_id;
                product_id = db.get_shop_data_product_id;
                image = db.get_shop_data_img;
                price = db.get_shop_data_price;
                db.adress = db.get_shop_data_adress;
                db.slip_img = db.get_shop_data_slipimg;
                db.qytpppercent =db.get_shop_data_qytpercent;

                if(db.get_shop_data_supplier_id.size()>0){
                    for (int b =0;b<db.selected_product_id.size();b++){
                       if(!db.control_shop_data_all(db.selected_product_id.get(b))){
                           for (int i = 0;i< db.product_id.size();i++){
                                   if(db.product_id.get(i).equals(db.selected_product_id.get(b))){
                                       supplier_id.add(db.supplier_id.get(i));
                                       product_id.add(db.product_id.get(i));
                                       image.add(db.image.get(i));
                                       price.add(db.price.get(i));
                                       db.adress.add("");
                                       byte[] a = new byte[1];
                                       db.slip_img.add(a);
                                       db.qytpppercent.add("");
                                       db.get_shop_data_qytp.add("");
                                   }
                           }
                       }
                    }
                }else {
                   if(db.selected_product_id.size()>0){
                        for (int i = 0;i< db.product_id.size();i++){
                            for (int j = 0;j<db.selected_product_id.size();j++){
                                if(db.product_id.get(i).equals(db.selected_product_id.get(j))){
                                    supplier_id.add(db.supplier_id.get(i));
                                    product_id.add(db.product_id.get(i));
                                    image.add(db.image.get(i));
                                    price.add(db.price.get(i));
                                    db.adress.add("");
                                    byte[] a = new byte[1];
                                    db.slip_img.add(a);
                                    db.qytpppercent.add("");
                                    db.get_shop_data_qytp.add("");
                                }
                            }
                        }
                    }
                }

                final ArrayList<String> finalProduct_id = product_id;
                final ArrayList<String> finalPrice = price;
                bt_done_shop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(db.selected_product_id.size()>0){
                            for(int i = 0; i < finalProduct_id.size(); i++){
                                if(db.value_control_shop(finalProduct_id.get(i)).equals("1")){
                                    String dn = String.valueOf((Float.parseFloat(db.qytpppercent.get(i)) / Float.parseFloat(finalPrice.get(i))));
                                    db.update_shop_data(finalProduct_id.get(i),db.qytpppercent.get(i),db.adress.get(i),dn,db.slip_img.get(i));
                                }
                                else{
                                    String dn = String.valueOf((Float.parseFloat(db.qytpppercent.get(i)) / Float.parseFloat(finalPrice.get(i))));
                                    db.save_shop_data(finalProduct_id.get(i),db.qytpppercent.get(i),db.adress.get(i),dn,db.slip_img.get(i));
                                }

                            }
                            Toast.makeText(getApplicationContext(),"Transaction Successful",Toast.LENGTH_LONG).show();
                        }
                        pwindo.dismiss();
                    }
                });

              //  db.qytpppercent=price;
                ActivityShopAdepter adepter = new ActivityShopAdepter(getApplicationContext(),MainActivity.this,supplier_id,product_id,image,price,db.qytpppercent,db.adress,db.slip_img,db.get_shop_data_qytp,db);
                gridView.setAdapter(adepter);
                final Point size = new Point();
                display.getSize(size);
                pwindo = new PopupWindow(inflatedView, size.x-5,size.y-300, true );
                pwindo.setBackgroundDrawable(getResources().getDrawable(android.R.color.white));
                pwindo.setFocusable(true);
                pwindo.setOutsideTouchable(true);
                inflatedView.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.up));
                pwindo.showAtLocation(inflatedView, Gravity.CENTER, 0,0);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(localActivityManager!= null){
            localActivityManager.dispatchResume();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if(localActivityManager!=null)
            localActivityManager.dispatchPause(isFinishing());
    }
    private class columns_setting extends AsyncTask<Void,Void,Void> {
        ProgressDialog progress ;
        int id ;
        DataBase db ;
        public columns_setting(int id,DataBase db){
            this.db = db;
            this.id = id;
        }
        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            try {
                progress.dismiss();
            }catch (Exception e)
            {

            }
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Please wait, Page Loading");
            progress.setCancelable(false);
            progress.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
            progress.setIndeterminate(true);
            progress.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            db.get_all_data();
            db.get_precent();
            if(id == 0){
                final ActivityViewAdepter adepter = new ActivityViewAdepter(MainActivity.this,db.supplier_id,db.product_id,db.image,db.price,db,0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        db.gridView.setNumColumns(3);
                        adepter.notifyDataSetChanged();
                        db.gridView.setAdapter(adepter);
                    }
                });
            }
            else if(id == 1){
                final ActivityViewAdepter adepter = new ActivityViewAdepter(MainActivity.this,db.supplier_id,db.product_id,db.image,db.price,db,1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        db.gridView.setNumColumns(2);
                        adepter.notifyDataSetChanged();
                        db.gridView.setAdapter(adepter);
                    }
                });
            }
            else{
                final ActivityViewAdepter adepter = new ActivityViewAdepter(MainActivity.this,db.supplier_id,db.product_id,db.image,db.price,db,2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        db.gridView.setNumColumns(1);
                        adepter.notifyDataSetChanged();
                        db.gridView.setAdapter(adepter);
                    }
                });

            }
            return null;
        }
    }
    private class system_setting_cache extends AsyncTask<Void,Void,Void> {

        public system_setting_cache(){
        }
        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //-------------------------------------------------------------------------------------
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    db.img=imageBytes;
                    db=new DataBase(MainActivity.this);
                }
            });
            return null;
        }
    }
    private class system_setting extends AsyncTask<Void,Void,Void> {
        ProgressDialog progress;
        Bundle savedInstanceState;
        TabHost tabHost;
        public system_setting(Bundle savedInstanceState,TabHost tabHost){
            this.savedInstanceState=savedInstanceState;
            this.tabHost=tabHost;
        }
        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            try {
                progress.dismiss();
            }catch (Exception e)
            {

            }
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Please wait, Page Loading");
            progress.setCancelable(false);
            progress.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
            progress.setIndeterminate(true);
            progress.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WindowManager wm = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
                    final Display display = wm.getDefaultDisplay();
                    DisplayMetrics metrics = new DisplayMetrics();
                    display.getMetrics(metrics);
                    float height = metrics.heightPixels;
                    height = height/ metrics.density;
                    height=height-150;//footer-header-tabhost
                    height=height*metrics.density;

                    FrameLayout frameLayout= (FrameLayout) findViewById(android.R.id.tabcontent);
                    frameLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) height));
                    localActivityManager = new LocalActivityManager(MainActivity.this, false);
                    localActivityManager.dispatchCreate(savedInstanceState);
                    tabHost.setup(localActivityManager);
                    TabHost.TabSpec tab1 = tabHost.newTabSpec("All");
                    TabHost.TabSpec tab2 = tabHost.newTabSpec("Category1");
                    TabHost.TabSpec tab3 = tabHost.newTabSpec("Category2");
                    TabHost.TabSpec tab4 = tabHost.newTabSpec("Category3");
                    TabHost.TabSpec tab5 = tabHost.newTabSpec("Category4");
                    TabHost.TabSpec tab6 = tabHost.newTabSpec("Category5");
                    TabHost.TabSpec tab7 = tabHost.newTabSpec("Category6");
                    TabHost.TabSpec tab8 = tabHost.newTabSpec("Category7");
                    //------------------------------------------------------------------------------------------
                    tab1.setIndicator("All");
                    tab1.setContent(new Intent(MainActivity.this, ActivityView.class).putExtra("data","all").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    tabHost.addTab(tab1);

                    tab2.setIndicator("Category1");
                    tab2.setContent(new Intent(MainActivity.this, ActivityView.class).putExtra("data","category1").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    tabHost.addTab(tab2);

                    tab3.setIndicator("Category2");
                    tab3.setContent(new Intent(MainActivity.this, ActivityView.class).putExtra("data","category2").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    tabHost.addTab(tab3);

                    tab4.setIndicator("Category3");
                    tab4.setContent(new Intent(MainActivity.this, ActivityView.class).putExtra("data","category3").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    tabHost.addTab(tab4);

                    tab5.setIndicator("Category4");
                    tab5.setContent(new Intent(MainActivity.this, ActivityView.class).putExtra("data","category4").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    tabHost.addTab(tab5);

                    tab6.setIndicator("Category5");// ,
                    tab6.setContent(new Intent(MainActivity.this, ActivityView.class).putExtra("data","category5").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    tabHost.addTab(tab6);

                    tab7.setIndicator("Category6");// ,
                    tab7.setContent(new Intent(MainActivity.this, ActivityView.class).putExtra("data","category6").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    tabHost.addTab(tab7);

                    tab8.setIndicator("Category7");// ,
                    tab8.setContent(new Intent(MainActivity.this, ActivityView.class).putExtra("data","category7").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    tabHost.addTab(tab8);
                }
            });
            return null;
        }
    }
    private  Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            new background_image_get(data,requestCode).execute();
        }
    }
    private class background_image_get extends  AsyncTask<Void,Void,Void>{
        Intent data;
        int r;
        ProgressDialog progress;
        public background_image_get(Intent data,int r){
            this.data=data;
            this.r = r;
        }
        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            try {
                progress.dismiss();
            }catch (Exception e)
            {

            }
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Loading Please...\n");
            progress.setCancelable(false);
            progress.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
            progress.setIndeterminate(true);
            progress.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            final Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            String a =picturePath;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
            final int sizeInBytes = thumbnail.getByteCount();
                byte[] imageBytes = stream.toByteArray();
                db.slip_img.set(r,imageBytes);

            System.gc();
            return null;
        }
    }

}
