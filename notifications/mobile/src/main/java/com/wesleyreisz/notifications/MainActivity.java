package com.wesleyreisz.notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    private static final String CONF_LOCATION = "1405 St. Matthews Ave Winnipeg, MB R3G 0K5";
    private static final String YUM_LOCATION = "1 Arena Plaza, Louisville, KY 40202";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PendingIntent mapPendingConfIntent = getPendingMapIntent(v, CONF_LOCATION);
                PendingIntent mapPendingYumIntent = getPendingMapIntent(v, YUM_LOCATION);

                NotificationCompat.WearableExtender wearFeatures =
                     new NotificationCompat.WearableExtender();
                wearFeatures.setBackground(BitmapFactory.decodeResource(getResources(),R.drawable.red_yum));

                Notification notification =
                    new NotificationCompat.Builder(v.getContext())
                        .setSmallIcon(android.R.drawable.btn_star)
                        .setContentTitle("Notification")
                        .setContentText("Hello Prairie Dev Con, this is my Notification!")
                        .extend(wearFeatures)
                        .addAction(android.R.drawable.ic_dialog_map, "Conf", mapPendingConfIntent)
                        .addAction(android.R.drawable.ic_dialog_map, "Yum", mapPendingYumIntent)
                        .build();
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                mNotifyMgr.notify(1, notification);
            }
        });

    }




    private PendingIntent getPendingMapIntent(View v, String location){
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
        mapIntent.setData(geoUri);
        PendingIntent mapPendingIntent =
                PendingIntent.getActivity(v.getContext(), 0, mapIntent, 0);
        return mapPendingIntent;
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
