package com.example.teamnullpointer.ridesharenp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by anthony on 6/4/2016.
 */
public class Settings extends AppCompatActivity {
    private Button delbut;
    private String email;
    private DataBaseOperation mydb;

    String json_string, descGet;
    JSONObject jsonObject;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setTitle("Settings");
        mydb = new DataBaseOperation(getApplicationContext());

        run();
    }

    private void run(){
        layout();
        clicks();
    }



    private void layout(){
        delbut = (Button) findViewById(R.id.delbutid);
    }


    public void getEmail() {
        Cursor res = mydb.getAllData();
        Boolean contains = res.moveToNext();

        if (contains == true) {
            email = res.getString(1);
        }
    }


    private void clicks(){
        delbut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getEmail();
                MYSQLBackgroundTask backgroundTask = new MYSQLBackgroundTask(getApplicationContext());
                backgroundTask.execute("Delete Account", email);
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }


}
