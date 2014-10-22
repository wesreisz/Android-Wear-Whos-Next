package com.wesleyreisz.whosnext.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.wesleyreisz.whosnext.R;
import com.wesleyreisz.whosnext.model.Team;
import com.wesleyreisz.whosnext.util.DateUtil;
import com.wesleyreisz.whosnext.util.HttpUtil;
import com.wesleyreisz.whosnext.util.WhosNextConstants;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wesleyreisz on 10/19/14.
 */
public class WhatsNextFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "WhosNext";

    private List<Team> teams;
    private List<String> teamNames = new ArrayList<String>();
    private String selectedTeam;

    private TextView txtMessage;
    private Spinner spinner;
    private GoogleApiClient googleClient;
    private String messageForDevice="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_whats_next, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String msg;
        if(isConnected()) {
            msg="Getting Data";
            new ListTeamsHttpAsyncTask().execute(WhosNextConstants.GET_TEAMS);
        }else{
            msg="You are not Connected to the Internet";
        }
        txtMessage = (TextView)getActivity().findViewById(R.id.txtMessages);
        txtMessage.setText(msg);
        Button sync = (Button) getActivity().findViewById(R.id.btnSync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(),"Push to Wear", Toast.LENGTH_SHORT);
                toast.show();

                googleClient = new GoogleApiClient.Builder(getActivity())
                        .addApi(Wearable.API)
                        .addConnectionCallbacks(WhatsNextFragment.this)
                        .addOnConnectionFailedListener(WhatsNextFragment.this)
                        .build();

                googleClient.connect();
            }
        });

    }

    @Override
    public void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        txtMessage.setText("Connected to device");
        new SendToDataLayerThread("/message_path", messageForDevice).start();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        txtMessage.setText("Unable to connect to device");
    }

    private void buildSpinner() {
        if(teamNames!=null && teamNames.size()>0) {
            spinner = (Spinner) getActivity().findViewById(R.id.spinnerTeams);
            spinner.setAdapter(new ArrayAdapter<String>(getActivity(),
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
                    txtMessage.setText("Nothing selected");
                }
            });
        }
    }

    private void buildNextOpponent(Team team)  {
        if(team!=null && team.getSchedule()!=null){
            TextView textViewOpponentMessage = (TextView)getActivity().findViewById(R.id.txtNextOpponentMessage);
            textViewOpponentMessage.setText("Next Opponent:");

            TextView textViewOpponent = (TextView)getActivity().findViewById(R.id.txtNextOpponent);
            textViewOpponent.setText(team.getSchedule().get(0).getTeam());

            TextView textViewLocation = (TextView)getActivity().findViewById(R.id.txtLocation);
            textViewLocation.setText(team.getSchedule().get(0).getLocation());

            TextView textViewTime = (TextView)getActivity().findViewById(R.id.txtDateTime);
            Date date = null;
            textViewTime.setText(
                    DateUtil.formatDateForDisplay(team.getSchedule().get(0).getDate()) + " (" +
                            team.getSchedule().get(0).getTime() + ")"
            );

        }else{
            txtMessage.setText("Unable to determine next opponent");
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
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
                txtMessage.setText("Received Data");
            }else{
                txtMessage.setText("No Data Received... Check the service.");
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
            messageForDevice = results;

            ObjectMapper mapper = new ObjectMapper();
            Team team;
            try {
                team = mapper.readValue(results, Team.class);
                buildNextOpponent(team);
            } catch (IOException e) {
                e.printStackTrace();
            }

            txtMessage.setText("Received Data");


        }
    }

    private class SendToDataLayerThread extends Thread {
        String path,message;
        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }
        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    // Log an error
                    Log.v(TAG, "ERROR: failed to send Message");
                    txtMessage.setText("Failed to send Message to device");
                }
            }
        }
    }
}
