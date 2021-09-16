package com.social.socialjobs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Marco on 26/12/2019.
 *
 * Loading page Java Class.
 * Serve come intermezzo per i caricamenti.
 */
public class LoadingPageDrawer extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

        final int TIME = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), HomePageDrawer.class));
                finish();
            }
        }, TIME);
    }
}
