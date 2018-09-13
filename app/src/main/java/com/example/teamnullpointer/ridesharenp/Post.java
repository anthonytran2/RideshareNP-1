package com.example.teamnullpointer.ridesharenp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import PubNub_Chat.PubnubKeys;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

public class Post extends AppCompatActivity {
    private Context ctx;

    private TextView title;

    private EditText descript;

    private RadioGroup rd;
    private RadioButton rider, driver;

    private RadioButton chosenButton;
    private Button post;

    private DataBaseOperation mydb;

    private String emailToDB;

    String TAG = "ChatFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setTitle("Post");
        ctx = this.getApplicationContext();
        mydb = new DataBaseOperation(this);

        postRun();
    }

    private void postRun() {
        postLayout();
        postClick();
    }

    //Handle back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
           finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void postClick() {
        post.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startBackgroundTask();
            }

        });
    }

    private void postLayout() {
        rd = (RadioGroup) findViewById(R.id.postgroupid);
        driver = (RadioButton) findViewById(R.id.driverid);
        rider = (RadioButton) findViewById(R.id.riderid);
        title = (TextView) findViewById(R.id.titleid);
        descript = (EditText) findViewById(R.id.descriptid);
        post = (Button) findViewById(R.id.postid);

        title.setText("Posting Details");
        driver.setText("Driver");
        rider.setText("Rider");
        post.setText("Post");
        descript.setHint("Give a description...");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Get email from local database
    public void getEmail(){
        Cursor res = mydb.getAllData();
        Boolean contains = res.moveToNext();

        if(contains == true) {
            emailToDB = res.getString(1);
        }
    }

    private void startBackgroundTask(){
        String description = descript.getText().toString();

        int selectedId = rd.getCheckedRadioButtonId();
        chosenButton = (RadioButton) findViewById(selectedId);
        if (selectedId != -1 && !(descript.getText().toString().equals(""))) {
            String rdtype = chosenButton.getText().toString();
            String method = "post";

            getEmail();
            MYSQLBackgroundTask backgroundTask = new MYSQLBackgroundTask(this);
            backgroundTask.execute(method,description,rdtype,emailToDB);
            finish();
            startActivity(new Intent(ctx, CenteralHub.class));
        } else {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(Post.this);
            builder1.setMessage("-Please Choose to post as a \tDRIVER or RIDER\n\n" +
                    "-Enter a Description");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void setupChannel(){

        Pubnub pubnub = new Pubnub(PubnubKeys.PUBLISH_KEY, PubnubKeys.SUBSCRIBE_KEY);

        //SET channel name
        PubnubKeys pnk = new PubnubKeys(emailToDB);

        try {
            pubnub.subscribe(PubnubKeys.CHANNEL_NAME, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                    Log.d("successCallback", "message " + message);
                }

                @Override
                public void successCallback(String channel, Object message, String timetoken) {
                    super.successCallback(channel, message, timetoken);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    super.errorCallback(channel, error);
                    Log.d("errorCallback", "error " + error);
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    super.connectCallback(channel, message);
                    Log.d("connectCallback", "message " + message);
                }

                @Override
                public void reconnectCallback(String channel, Object message) {
                    super.reconnectCallback(channel, message);
                    Log.d("reconnectCallback", "message " + message);
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    //super.disconnectCallback(channel, message);
                    //Log.d("disconnectCallback", "message " + message);
                }
            });
        } catch (PubnubException pe) {
            Log.d(TAG, pe.toString());
        }
    }
}
