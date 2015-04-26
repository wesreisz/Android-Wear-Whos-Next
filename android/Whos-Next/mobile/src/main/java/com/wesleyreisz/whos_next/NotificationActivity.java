package com.wesleyreisz.whos_next;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NotificationActivity extends Activity {
    private static final String CONF_LOCATION = "221 S 4th St, Louisville, KY 40202";
    private static final String YUM_LOCATION = "1 Arena Plaza, Louisville, KY 40202";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.WearableExtender wearFeatures =
                     new NotificationCompat.WearableExtender();
                wearFeatures.setBackground(BitmapFactory.decodeResource(getResources(),R.drawable.red_yum));
                wearFeatures.addPages(addPages());

                Notification notification =
                    new NotificationCompat.Builder(v.getContext())
                        .setSmallIcon(android.R.drawable.btn_star)
                        .setContentTitle("Hey! Over here...")
                        .setContentText("Hello CodepaLOUsa, this is my Notification!")
                        .extend(wearFeatures)
                        .addAction(android.R.drawable.ic_menu_set_as,"Open", getDetailIntent(v))
                        .addAction(android.R.drawable.ic_dialog_map, "Conf", getPendingMapIntent(v, CONF_LOCATION))
                        .addAction(android.R.drawable.ic_dialog_map, "Yum", getPendingMapIntent(v, YUM_LOCATION))
                        .build();
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                Toast toast = Toast.makeText(v.getContext(),"Notification Sent", Toast.LENGTH_LONG);
                toast.show();

                mNotifyMgr.notify(1, notification);
            }
        });

    }

    private List<Notification> addPages(){
        List<Notification> pages = new ArrayList<Notification>();
        for( int i = 1; i <= 2; i++ ){
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Page " + i)
                    .setContentText("Text for page " + i)
                    .build();
            pages.add(notification);
        }
        return pages;
    }

    private PendingIntent getDetailIntent(View v){
        Intent mapIntent = new Intent(v.getContext(),DetailActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(v.getContext(), 0, mapIntent, 0);
        return pendingIntent;
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
