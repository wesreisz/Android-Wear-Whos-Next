package com.wesleyreisz.whosnext;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.wesleyreisz.whosnext.fragment.WhatsNextFragment;

public class WhatsNextActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_next);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                .add(R.id.container, new WhatsNextFragment())
                .commit();
        }
    }
}
