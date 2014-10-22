package com.wesleyreisz.whosnext;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.wesleyreisz.whosnext.model.Schedule;
import com.wesleyreisz.whosnext.model.Team;
import com.wesleyreisz.whosnext.util.DateUtil;

import org.w3c.dom.Text;

import java.io.IOException;

public class WhatsNextClockFaceActivity extends Activity {

    private ImageView mImageView;
    private TextView mTextView;
    private TextView mTextViewLocation;
    private static final String TAG = "WhosNext";
    private Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_next_clock_face);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.message);
                mTextViewLocation = (TextView) stub.findViewById(R.id.location);
            }
        });

        // Register the local broadcast receiver, defined in step 3.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        BroadcastReceiver messageReceiver = new BroadcastReceiver() {
            @Override
                public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                Log.d(TAG, "Message received from phone: " + message);

                updateWatchFace(message);
            }
        };

        LocalBroadcastManager.getInstance(WhatsNextClockFaceActivity.this).registerReceiver(messageReceiver, messageFilter);
    }

    private void updateWatchFace(String message){
        team = mapObject(message);
        mTextView.setText(buildString(team));
        mTextViewLocation.setText(buildLocation(team));

        //cheated... =) Ran out of time. Go back and refactor
        mImageView = (ImageView)findViewById(R.id.watch_background);
        if(team.getSchedule().get(0).getLocation().toLowerCase().contains("auburn")){
            mImageView.setImageResource(R.drawable.auburn);
        }else{
            mImageView.setImageResource(R.drawable.louisville_black);
        }
    }

    private String buildString(Team team) {
        if(team!=null && team.getSchedule()!=null && team.getSchedule().size()>0) {
            Schedule schedule = team.getSchedule().get(0);
            return DateUtil.formatDateForDisplay(schedule.getDate()) + " " + schedule.getTime();
        }else{
            return "";
        }
    }

    private String buildLocation(Team team){
        if(team!=null && team.getSchedule()!=null && team.getSchedule().size()>0) {
            Schedule schedule = team.getSchedule().get(0);
            return schedule.getLocation();
        }else{
            return "";
        }
    }

    private Team mapObject(String message){
        ObjectMapper mapper = new ObjectMapper();
        Team team = null;
        try {
            team = mapper.readValue(message, Team.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return team;
    }
}
