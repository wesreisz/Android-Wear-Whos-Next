package com.wesleyreisz.whos_next;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.google.android.gms.wearable.Wearable;
import com.wesleyreisz.whos_next.model.Schedule;
import com.wesleyreisz.whos_next.model.Team;
import com.wesleyreisz.whos_next.util.DateUtil;

import java.util.List;

public class MainActivity extends Activity {

    private TextView mTextViewOpponent;
    private TextView mTextViewDate;
    private TextView mTextViewLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                WhosNextApplication app = ((WhosNextApplication) getApplicationContext());
                Team team = app.getTeam();
                if (team!=null && team.getSchedule()!=null) {
                    List<Schedule> schedules = team.getSchedule();
                    mTextViewOpponent = (TextView) stub.findViewById(R.id.textWhosNextInfo);
                    mTextViewDate = (TextView) stub.findViewById(R.id.textTime);
                    mTextViewLocation = (TextView) stub.findViewById(R.id.textLocation);
                    if (schedules.get(0) != null) {
                        Schedule schedule = schedules.get(0);
                        mTextViewOpponent.setText(schedule.getTeam());
                        String display = DateUtil.formatDateForDisplay(schedule.getDate()) + " " + schedule.getTime();
                        mTextViewDate.setText(display);
                        mTextViewLocation.setText(schedule.getLocation());
                    }else{
                        mTextViewOpponent.setText("No regular season game found next");
                    }
                }
            }
        });
    }
}
