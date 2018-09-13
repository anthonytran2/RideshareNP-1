
package PubNub_Chat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teamnullpointer.ridesharenp.CenteralHub;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.teamnullpointer.ridesharenp.R;

/**
 * Created by Anjith Sasindran
 * on 11-Oct-15.
 */
public class ChatFragment extends Fragment implements View.OnClickListener {

    private String TAG = "ChatFragment";
    private SharedPreferences sharedPreferences;
    private Context context;
    private Pubnub pubnub;
    private EditText chatMessage;
    private Button send;
    private RecyclerView chatList;
    private ArrayList<String> chatMessageList;
    private ChatAdapter chatAdapter;
    private Gson gson;
    private JSONObject messageObject;
    private String username, host, hostusername;
    private Button homebut, mapbut, usersbut, reportbut;

    private GoogleApiClient client;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        setHasOptionsMenu(true);


        context = getActivity();
        sharedPreferences = context.getSharedPreferences("details", Context.MODE_PRIVATE);
        gson = new Gson();


        host = sharedPreferences.getString("hostEmail", null);
        username = sharedPreferences.getString("userName", null);
        hostusername = sharedPreferences.getString("hostuserName", null);
        getActivity().setTitle(hostusername + "'s room");


        chatMessageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessageList, username);

        chatList = (RecyclerView) view.findViewById(R.id.chatlist);
        chatList.setLayoutManager(new LinearLayoutManager(context));
        chatList.setAdapter(chatAdapter);

        chatMessage = (EditText) view.findViewById(R.id.message);
        send = (Button) view.findViewById(R.id.send);
        send.setOnClickListener(this);

        homebut = (Button) view.findViewById(R.id.homebutid);
        mapbut = (Button) view.findViewById(R.id.mapbutid);
        usersbut = (Button) view.findViewById(R.id.viewuserbutid);
        reportbut = (Button) view.findViewById(R.id.reportbutid);

        homebut.setText("home");
        mapbut.setText("Map");
        usersbut.setText("Users");
        reportbut.setText("Report");

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(context).addApi(AppIndex.API).build();

        clicks();

        pubnub = new Pubnub(PubnubKeys.PUBLISH_KEY, PubnubKeys.SUBSCRIBE_KEY);

        //SET channel name
        PubnubKeys pnk = new PubnubKeys(host);

        try {
            pubnub.subscribe(PubnubKeys.CHANNEL_NAME, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                    chatMessageList.add(message.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                         //   chatAdapter.notifyItemInserted(chatMessageList.size() - 1); //BUGGED WITH RECYCLE VIEW
                            chatAdapter.notifyDataSetChanged();
                            chatList.scrollToPosition(chatMessageList.size() - 1);
                        }
                    });
                    Log.d("successCallback", "message YOOOOP" + message);
                }

                @Override
                public void successCallback(String channel, Object message, String timetoken) {
                    super.successCallback(channel, message, timetoken);
                    Log.d("successCallBack", "message" + message);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    super.errorCallback(channel, error);
                    Log.d("errorCallback", "error " + error);
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    super.connectCallback(channel, message);
                    Log.d("connectCallback", "message SUBSCRIBE" + message);
                }

                @Override
                public void reconnectCallback(String channel, Object message) {
                    super.reconnectCallback(channel, message);
                    Log.d("reconnectCallback", "message " + message);
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    super.disconnectCallback(channel, message);
                    Log.d("disconnectCallback", "message " + message);
                }
            });
        } catch (PubnubException pe) {
            Log.d(TAG, pe.toString());
        }


        pubnub.history(host, 100, true, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                Log.d("connectCallback", "message REJOIN" + message);

                String u = "";
                String m = "";

                final List<Message> chatMsgs = new ArrayList<Message>();

                JSONArray mJsonArray = null;
                try {
                    mJsonArray = (JSONArray) message;

                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONArray ia = mJsonArray.optJSONArray(i);
                        for(int j = 0; j < ia.length(); j++) {
                            JSONObject jo = ia.getJSONObject(j);
                            m = jo.get("message").toString();
                            u = jo.get("username").toString();

                            String msg = gson.toJson(new Message(u, m));
                            chatMessageList.add(msg);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //   chatAdapter.notifyItemInserted(chatMessageList.size() - 1); //BUGGED WITH RECYCLE VIEW
                                    chatAdapter.notifyDataSetChanged();
                                    chatList.scrollToPosition(chatMessageList.size() - 1);
                                }
                            });
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        return view;

    }

    protected void sendEmail() {
        //Log.i("Send email", "");
        String[] TO = {"NullPointerException@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //Log.i("Finished sending email...", "");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private void clicks() {

        mapbut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String zip1 = "95624";
                String zip2 = "95824";

                String uri = "https://www.google.com/maps/dir/" + zip1 + "/" + zip2;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }

        });


        homebut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CenteralHub.class));
            }

        });

        reportbut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendEmail();
            }

        });
    }


    @Override
    public void onClick(View view) {
        if (send.getId() == view.getId()) {
            String message = chatMessage.getText().toString().trim();
            if (message.length() != 0) {
                message = gson.toJson(new Message(username, message));
                try {
                    messageObject = new JSONObject(message);
                } catch (JSONException je) {
                    Log.d(TAG, je.toString());
                }
                chatMessage.setText("");
                pubnub.publish(PubnubKeys.CHANNEL_NAME, messageObject, new Callback() {
                    @Override
                    public void successCallback(String channel, Object message) {
                        super.successCallback(channel, message);
                        Log.d("successCallback", "message " + message);
                    }

                    @Override
                    public void errorCallback(String channel, PubnubError error) {
                        super.errorCallback(channel, error);
                        Log.d("errorCallback", "error " + error);
                    }
                });
            } else {
                Toast.makeText(context, "Please enter message", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CenteralHub Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.teamnullpointer.ridesharenp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CenteralHub Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.teamnullpointer.ridesharenp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();

        pubnub.unsubscribe(PubnubKeys.CHANNEL_NAME);
        Log.d(TAG, "Un subscribed");
    }

}