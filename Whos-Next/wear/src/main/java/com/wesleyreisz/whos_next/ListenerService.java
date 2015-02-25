package com.wesleyreisz.whos_next;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.wesleyreisz.whos_next.model.Team;

import java.io.IOException;

public class ListenerService extends WearableListenerService {
    private static final String TAG = "whos_next";

    public ListenerService() {
    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/whos_next_updates")) {
            final String message = new String(messageEvent.getData());
            Log.d(TAG, "Message received: " + message);


            ObjectMapper mapper = new ObjectMapper();
            Team team = new Team();
            try {
                team = mapper.readValue(message, Team.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //set into global state
            WhosNextApplication app = ((WhosNextApplication) getApplicationContext());
            app.setTeam(team);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}
