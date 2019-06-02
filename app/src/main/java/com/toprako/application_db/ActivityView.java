package com.toprako.application_db;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;



public class ActivityView extends AppCompatActivity {
    private GridView gridView;
    private DataBase db;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        gridView  =(GridView)findViewById(R.id.view_gridview);
        db = new DataBase(getApplicationContext());
        db.gridView=gridView;
        Bundle extras = getIntent().getExtras();
        final String value = extras.getString("data");
        if(value == "all")
        {
            db.get_all_data();
            db.get_precent();
            ActivityViewAdepter adepter=new ActivityViewAdepter(this,db.supplier_id,db.product_id,db.image,db.price,db,0);
            gridView.setAdapter(adepter);
        }
        else
        {
            String[] numbers = new String[] {
                    "A", "B", "C", "D", "E",
                    "F", "G", "H", "I", "J",
                    "K", "L", "M", "N", "O",
                    "P", "Q", "R", "S", "T",
                    "U", "V", "W", "X", "Y", "Z", "\n","\n","\n","\n",
                    "a", "b","c","d","e",
                    "f","g","h","i","j",
                    "k","l","m","n","o",
                    "p","q","r","s","t",
                    "u","v","w","x","y",
                    "z"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, numbers);
            gridView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Do you want to close the application?");
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
                else
                {
                    finish();
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog ac = dialog.create();
        ac.show();
    }
}
