package com.wesleyreisz.whos_next;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "whos_next";
    private GoogleApiClient googleClient;
    private TextView mTxtMessage;
    private String mMessageForDevice="{This is an important message updated from the server}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtMessage = (TextView)findViewById(R.id.txtMessages);

        Button sync = (Button) findViewById(R.id.btnSync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleClient = new GoogleApiClient.Builder(v.getContext())
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(MainActivity.this)
                    .addOnConnectionFailedListener(MainActivity.this)
                    .build();
                googleClient.connect();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }else if(id == R.id.notifications){
            Intent notificationIntent = new Intent(this, NotificationActivity.class);
            startActivity(notificationIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to wear");
        mTxtMessage.setText("Connected to wear");
        new SendToDataLayerThread("/whos_next_updates", mMessageForDevice).start();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Wear connection suspended");
        mTxtMessage.setText("Wear connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Unable to connect to device");
        mTxtMessage.setText("Unable to connect to device");
    }


    private class SendToDataLayerThread extends Thread {
        String path,message;
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }
        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result =
                    Wearable.MessageApi
                        .sendMessage(googleClient, node.getId(), path, message.getBytes())
                        .await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    Log.v(TAG, "ERROR: failed to send Message");
                }
            }
        }
    }
}
