package com.example.teamnullpointer.ridesharenp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyPosts extends AppCompatActivity {
    private Button delbut;
    private String email;
    private DataBaseOperation mydb;

    String json_string, descGet;
    JSONObject jsonObject;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_posts);
        setTitle("My Posts");
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
                backgroundTask.execute("Delete Post", email);
                finish();
            }
        });

    }



}

