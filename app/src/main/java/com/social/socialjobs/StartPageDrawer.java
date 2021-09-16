package com.social.socialjobs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.social.socialjobs.addon.Utilities;

/**
 * Created by Marco on 17/12/2019.
 *
 * Splash Screen Java class.
 * Pagina di caricamento iniziale.
 */
public class StartPageDrawer extends Utilities {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starter_activity);

        final int TIME = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), HomePageDrawer.class));
                finish();
            }
        }, TIME);
    }
}

