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
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wesleyreisz.whosnext.R;
import com.wesleyreisz.whosnext.model.Team;
import com.wesleyreisz.whosnext.util.HttpUtil;
import com.wesleyreisz.whosnext.util.WhosNextConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wesleyreisz on 10/19/14.
 */
public class WhatsNextFragment extends Fragment {
    private static final String TAG = "WhosNext";
    private View view;

    private List<Team> teams;
    private List<String> teamNames = new ArrayList<String>();

    private TextView txtMessage;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view =  inflater.inflate(R.layout.fragment_whats_next, container, false);

        String msg;
        if(isConnected()) {
            msg="Getting Data";
            new ListTeamsHttpAsyncTask().execute(WhosNextConstants.GET_TEAMS);
        }else{
            msg="You are not Connected to the Internet";
        }
        txtMessage = (TextView)view.findViewById(R.id.txtMessages);
        txtMessage.setText(msg);

        return view;
    }

    private void buildSpinner() {
        if(teamNames!=null && teamNames.size()>0) {
            Log.d(TAG, "Teams size: " + teamNames.size());

            spinner = (Spinner) view.findViewById(R.id.spinnerTeams);
            spinner.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item,
                    teamNames));

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Team team = teams.get(position);
                    new GetNextHttpAsyncTask().execute(String.format(WhosNextConstants.GET_NEXT_OPPONENT, team.getNickname()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    txtMessage.setText("Nothing selected");
                }
            });
        }
    }

    private void buildNextOpponent(Team team) {
        if(team!=null && team.getSchedule()!=null){
            TextView textViewOpponentMessage = (TextView)view.findViewById(R.id.txtNextOpponentMessage);
            textViewOpponentMessage.setText("Next Opponent:");

            TextView textViewOpponent = (TextView)view.findViewById(R.id.txtNextOpponent);
            textViewOpponent.setText(team.getSchedule().get(0).getTeam());

            TextView textViewLocation = (TextView)view.findViewById(R.id.txtLocation);
            textViewLocation.setText(team.getSchedule().get(0).getLocation());

            TextView textViewTime = (TextView)view.findViewById(R.id.txtDateTime);
            textViewTime.setText(
                team.getSchedule().get(0).getDate() + " (" +
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
}
