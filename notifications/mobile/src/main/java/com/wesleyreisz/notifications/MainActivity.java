package com.wesleyreisz.notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.WearableExtender wearFeatures =
                     new NotificationCompat.WearableExtender();
                wearFeatures.setBackground(BitmapFactory.decodeResource(getResources(),R.drawable.red_yum));

                Notification notification =
                    new NotificationCompat.Builder(v.getContext())
                        .setSmallIcon(android.R.drawable.btn_star)
                        .setContentTitle("Notification")
                        .setContentText("Hello Prairie Dev Con, this is my Notification!")
                        .extend(wearFeatures)
                        .build();
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                mNotifyMgr.notify(1, notification);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
