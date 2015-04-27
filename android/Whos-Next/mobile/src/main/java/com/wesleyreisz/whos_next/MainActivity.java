package com.wesleyreisz.whos_next;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.wesleyreisz.whos_next.model.Team;
import com.wesleyreisz.whos_next.util.DateUtil;
import com.wesleyreisz.whos_next.util.HttpUtil;
import com.wesleyreisz.whos_next.util.WhosNextConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "whos_next";
    private GoogleApiClient googleClient;
    private TextView mTxtMessage;
    private String mMessageForDevice="{This is an important message updated from the server}";
    private List<Team> teams;
    private String selectedTeam;
    private List<String> teamNames = new ArrayList<String>();
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String msg;
        if(isConnected()) {
            msg="Getting Data";
            new ListTeamsHttpAsyncTask().execute(WhosNextConstants.GET_TEAMS);
        }else{
            msg="You are not Connected to the Internet";
        }

        mTxtMessage = (TextView)this.findViewById(R.id.txtMessages);
        mTxtMessage.setText(msg);

        Button sync = (Button) findViewById(R.id.btnSync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWearable(v.getContext());
                sendNotification(v);
            }
        });
    }

    private void sendNotification(View v) {
        NotificationCompat.WearableExtender wearFeatures =
                new NotificationCompat.WearableExtender();
        wearFeatures.setBackground(BitmapFactory.decodeResource(getResources(),R.drawable.red_yum));

        Notification notification =
                new NotificationCompat.Builder(v.getContext())
                        .setSmallIcon(android.R.drawable.btn_star)
                        .setContentTitle("Great News!")
                        .setContentText("We found your team's next opponent.")
                        .extend(wearFeatures)
                        .build();
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(1, notification);
    }

    @Override
    public void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
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

    private void buildSpinner() {
        if(teamNames!=null && teamNames.size()>0) {
            spinner = (Spinner) findViewById(R.id.spinnerTeams);
            spinner.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    teamNames));

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Team team = teams.get(position);
                    selectedTeam = team.getNickname();
                    new GetNextHttpAsyncTask().execute(String.format(WhosNextConstants.GET_NEXT_OPPONENT, team.getNickname()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mTxtMessage.setText("Nothing selected");
                }
            });
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    private void buildNextOpponent(Team team)  {
        if(team!=null && team.getSchedule()!=null){
            TextView textViewOpponentMessage = (TextView)findViewById(R.id.txtNextOpponentMessage);
            textViewOpponentMessage.setText("Next Opponent:");

            TextView textViewOpponent = (TextView)findViewById(R.id.txtNextOpponent);
            textViewOpponent.setText(team.getSchedule().get(0).getTeam());

            TextView textViewLocation = (TextView)findViewById(R.id.txtLocation);
            textViewLocation.setText(team.getSchedule().get(0).getLocation());

            TextView textViewTime = (TextView)findViewById(R.id.txtDateTime);
            Date date = null;
            textViewTime.setText(
                    DateUtil.formatDateForDisplay(team.getSchedule().get(0).getDate()) + " (" +
                            team.getSchedule().get(0).getTime() + ")"
            );

        }else{
            mTxtMessage.setText("Unable to determine next opponent");
        }
    }

    private void updateWearable(Context context){
        //when multiple clients were connected, both did not
        //receive the connection client. This needs to be looked
        //at closer. -wtr
        googleClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .build();

        googleClient.connect();
    }

    private class ListTeamsHttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return HttpUtil.getJson(urls[0]);
        }

        @Override
        protected void onPostExecute(String results) {
            Log.d(TAG,"received: " + results);
            ObjectMapper mapper = new ObjectMapper();
            try {
                teams = mapper.readValue(results, new TypeReference<List<Team>>(){});
                for(Team team : teams){
                    teamNames.add(team.getTeam());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(results.length()>0){
                mTxtMessage.setText("Received Data");
            }else{
                Log.d(TAG,"No Data Received... Check the service.");
                mTxtMessage.setText("No Data Received... Check the service.");
            }

            if(spinner==null){
                buildSpinner();
            }

        }
    }

    private class GetNextHttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return HttpUtil.getJson(urls[0]);
        }

        @Override
        protected void onPostExecute(String results) {
            Log.d(TAG,"received: " + results);
            mMessageForDevice = results;

            ObjectMapper mapper = new ObjectMapper();
            Team team;
            try {
                team = mapper.readValue(results, Team.class);
                buildNextOpponent(team);
                updateWearable(MainActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mTxtMessage.setText("Received Data");
        }
    }
}
