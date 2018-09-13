package com.example.teamnullpointer.ridesharenp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private DataBaseOperation myDB;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DataBaseOperation(this);
        ctx = getApplicationContext();
       // myDB.restart();   //restarting table testing purposes (LOCAL)
        tosChecker();

    }

    //Checks to start login or tos.
    private void tosChecker(){
        Cursor res = myDB.getAllData();
        Boolean contains = res.moveToNext();
        if(contains == true && res.getString(3).equals("ACPT")) {
            startActivity(new Intent(ctx, Login.class));
        }  else {
            startActivity(new Intent(ctx, TermsOfService.class));
        }
    }

}
