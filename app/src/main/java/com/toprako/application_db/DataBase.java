package com.toprako.application_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;
import android.widget.GridView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataBase extends SQLiteOpenHelper {
    //----------------------------------------------------------------------------------------------
    static GridView gridView;
    ArrayList<String> supplier_id = new ArrayList<>();
    ArrayList<String> product_id = new ArrayList<>();
    ArrayList<byte[]> image = new ArrayList<byte[]>();
    ArrayList<String> price = new ArrayList<>();
    static ArrayList<String> selected_product_id = new ArrayList<>();
    ArrayList<String> price1 = new ArrayList<>();
    ArrayList<String> price2 = new ArrayList<>();
    ArrayList<String> percent = new ArrayList<>();
    ArrayList<String> priceid = new ArrayList<>();
    //----------------------------------------------------------------------------------------------
    ArrayList<String> price1_db = new ArrayList<>();
    ArrayList<String> price2_db = new ArrayList<>();
    ArrayList<String>percent_db = new ArrayList<>();
    //----------------------------------------------------------------------------------------------
    static  ArrayList<String> qytpppercent = new ArrayList<>();//normal
    static ArrayList<String> adress = new ArrayList<>();
    static  ArrayList<byte[]> slip_img = new ArrayList<>();
    //----------------------------------------------------------------------------------------------
    ArrayList<String> get_shop_data_product_id = new ArrayList<>();
    ArrayList<String> get_shop_data_qytpercent = new ArrayList<>();//dbden çekiş
    ArrayList<String> get_shop_data_adress = new ArrayList<>();
    ArrayList<String> get_shop_data_qytp = new ArrayList<>();
    ArrayList<byte[]> get_shop_data_slipimg = new ArrayList<>();
    ArrayList<String> get_shop_data_supplier_id=new ArrayList<>();
    ArrayList<byte[]> get_shop_data_img = new ArrayList<byte[]>();
    ArrayList<String> get_shop_data_price = new ArrayList<>();
    //----------------------------------------------------------------------------------------------
    static String img_slip;


    SQLiteDatabase database;
    static  byte[] img;

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "app.db";

    //----------------------------------------------------------------------------------------------
    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL("Create Table Data(supplier_id TEXT ,product_id TEXT ,image BLOB ,price REAL ,add_time TEXT);");
            db.execSQL("Create Table setting(id INTEGER PRIMARY KEY AUTOINCREMENT,pricemin REAL,pricemax REAL,percent REAL);");
            db.execSQL("Create Table admin(product_id TEXT , qytpercent REAL , adress TEXT , qyt TEXT,slip_image BLOB)");
            stabil_data_add(db);
        }
        catch (Exception e)
        {
            Log.e("Error",e.toString());
        }
    }
    private void stabil_data_add(SQLiteDatabase db) throws InterruptedException {
        for (int i= 0;i<20;i++)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            ContentValues insertValues = new ContentValues();
            insertValues.put("supplier_id", String.valueOf(i));
            insertValues.put("product_id", String.valueOf(i*5));
            insertValues.put("image", img);
            insertValues.put("price", String.valueOf(i*100));
            insertValues.put("add_time",  dateFormat.format(date).toString());
            db.insert("Data", null, insertValues);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop Table IF EXISTS Data");
        db.execSQL("Drop Table IF EXISTS Price");
        onCreate(db);
    }
    //----------------------------------------------------------------------------------------------
    public void get_all_data(){
        get_all_data_d();
    }
    private void get_all_data_d(){
        price.clear();
        product_id.clear();
        image.clear();
        supplier_id.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Data ORDER BY date(add_time) ASC";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            do {
                supplier_id.add(cursor.getString(cursor.getColumnIndex("supplier_id")));
                product_id.add(cursor.getString(cursor.getColumnIndex("product_id")));
                image.add(cursor.getBlob(cursor.getColumnIndex("image")));
                price.add(cursor.getString(cursor.getColumnIndex("price")));
            }while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }
    //----------------------------------------------------------------------------------------------
    //Percent
    public void save_precent(int i){
        save_precent_d(i);
    }
    private void save_precent_d(int i){
        SQLiteDatabase db = getWritableDatabase();
        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("pricemin",String.valueOf(price1.get(i)));
            contentValues.put("percent", String.valueOf(percent.get(i)));
            contentValues.put("pricemax", String.valueOf(price2.get(i)));
            db.insertOrThrow("setting", null, contentValues);
        }catch (Exception e)
        {
            Log.e("Hata",e.toString());
        }
        db.close();
    }
    public void get_precent(){
        get_precent_d();
    }
    private void get_precent_d(){
        get_all_data_d();

        price1_db.clear();
        price2_db.clear();
        percent_db.clear();
        priceid.clear();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM setting";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            do {
               price1_db.add(cursor.getString(cursor.getColumnIndex("pricemin")));
               price2_db.add(cursor.getString(cursor.getColumnIndex("pricemax")));
               percent_db.add(cursor.getString(cursor.getColumnIndex("percent")));
               priceid.add(cursor.getString(cursor.getColumnIndex("id")));
            }while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }

        for (int i =0;i<price.size();i++) {
            for (int j = 0; j < price1_db.size(); j++) {
                if((Float.parseFloat(price.get(i))>= Float.parseFloat(price1_db.get(j))) &&  Float.parseFloat(price.get(i)) <= (Float.parseFloat(price2_db.get(j)))){
                    Float p  = Float.parseFloat(price.get(i));
                    Float pr = Float.parseFloat(percent_db.get(j));
                    p = ((p*pr)/100)+p;
                    price.set(i, p.toString());
                    break;
                }
            }
        }
    }
    public String value_control_setting(String min){
        return value_control_setting_d(min);
    }
    private String value_control_setting_d(String min){
        SQLiteDatabase db = this.getReadableDatabase();
        String geri="";
        String sql = "SELECT CASE WHEN EXISTS (SELECT * FROM [setting] WHERE pricemin = '"+min+"' ) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            do {
               geri=String.valueOf(cursor.getInt(0));
            }while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
    return geri;
    }
    public void update_percent(int i){
        update_percent_d(i);
    }
    private void update_percent_d(int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("pricemin",String.valueOf(price1.get(i)));
        contentValues.put("percent", String.valueOf(percent.get(i)));
        contentValues.put("pricemax", String.valueOf(price2.get(i)));
        db.update("setting",contentValues,"pricemin = '"+ price1_db.get(i)+"'" , null);
        db.close();
    }
    public void delete_percent(int i){
        delete_percent_d(i);
    }
    private void delete_percent_d(int i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("setting","pricemin = "+price1.get(i),null);
        db.close();
    }
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public void save_shop_data(String product_id,String qytpercent,String adress,String qytp,byte[] slipimg){
        save_shop_data_d(product_id,qytpercent,adress,qytp,slipimg);
    }
    private void save_shop_data_d(String product_id,String qytpercent,String adress,String qytp,byte[] slipimg){
        SQLiteDatabase db = getWritableDatabase();
        try
        {
            /*+
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            values.put(IMAGE_BITMAP, stream.toByteArray());
             */
            ContentValues contentValues = new ContentValues();
            contentValues.put("product_id",product_id);
            contentValues.put("qytpercent", qytpercent);
            contentValues.put("adress", adress);
            contentValues.put("qyt", qytp);
            contentValues.put("slip_image", slipimg);
            db.insertOrThrow("admin", null, contentValues);
        }catch (Exception e)
        {
            Log.e("Hata",e.toString());
        }
        db.close();
    }
    public void get_shop_data(){
        get_shop_data_d();
    }
    private void get_shop_data_d(){
        get_shop_data_product_id.clear();
        get_shop_data_qytpercent.clear();
        get_shop_data_adress.clear();
        get_shop_data_qytp.clear();
        get_shop_data_slipimg.clear();
        get_shop_data_supplier_id.clear();
        get_shop_data_img.clear();
        get_shop_data_price.clear();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM Data JOIN admin ON Data.product_id = admin.product_id";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            do {
                get_shop_data_product_id.add(cursor.getString(cursor.getColumnIndex("Data.product_id")));
                get_shop_data_qytpercent.add(cursor.getString(cursor.getColumnIndex("qytpercent")));
                get_shop_data_adress.add(cursor.getString(cursor.getColumnIndex("adress")));
                get_shop_data_qytp.add(String.valueOf(cursor.getString(cursor.getColumnIndex("qyt"))));
                get_shop_data_slipimg.add(cursor.getBlob(cursor.getColumnIndex("slip_image")));
                get_shop_data_supplier_id.add(cursor.getString(cursor.getColumnIndex("supplier_id")));
                get_shop_data_img.add(cursor.getBlob(cursor.getColumnIndex("Data.image")));
                get_shop_data_price.add(cursor.getString(cursor.getColumnIndex("Data.price")));
            }while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }
    public String value_control_shop(String value){
       return value_control_shop_d(value);
    }
    private String value_control_shop_d(String value){
        SQLiteDatabase db = this.getReadableDatabase();
        String geri="";
        String sql = "SELECT CASE WHEN EXISTS (SELECT * FROM [admin] WHERE product_id = '"+value+"' ) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            do {
                geri=String.valueOf(cursor.getInt(0));
            }while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return geri;
    }
    public void update_shop_data(String id,String qytpercent,String adress,String qytp,byte[] slipimg) {
        update_shop_data_d(id, qytpercent, adress, qytp, slipimg);
    }
    private void update_shop_data_d(String id,String qytpercent,String adress,String qytp,byte[] slipimg){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("qytpercent", qytpercent);
        contentValues.put("adress", adress);
        contentValues.put("qyt", qytp);
        contentValues.put("slip_image",slipimg);
        db.update("admin",contentValues,"product_id = '"+id+"'" , null);
        db.close();
    }
    public void delete_shop_data(String id){
        delete_shop_data_d(id);
    }
    private void delete_shop_data_d(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("admin","product_id = "+id,null);
        db.close();
    }
    public Boolean control_shop_data_all(String id){
        return control_shop_data_all_d(id);
    }
    private boolean  control_shop_data_all_d(String id){
        boolean geri=false;
        int i =0;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM admin where product_id = "+id ;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            do {
                 i = cursor.getColumnCount();
            } while (cursor.moveToNext());
            if(i > 0){
                geri=true;
            }
            else{
                geri=false;
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return geri;
    }

    /*SELECT CASE WHEN EXISTS (
    SELECT *
    FROM [Data]
    WHERE product_id = 0
    )
    THEN CAST(1 AS BIT)
    ELSE CAST(0 AS BIT) END*/

}
