package com.social.socialjobs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;
import com.social.socialjobs.addon.Job;
import com.social.socialjobs.addon.NavigationMenu;

import java.util.ArrayList;


public class HomePageDrawer extends NavigationMenu {

    private EditText city;
    private TextInputLayout cityError;
    private ArrayList<Job> jobList;
    private SeekBar seekBar;
    private TextView km;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AUTH_STATUS.getCurrentUser() != null) {
            this.setMenu(R.layout.activity_drawer_homepage);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else
            setContentView(R.layout.content_home_page);

        RelativeLayout layout = this.findViewById(R.id.activity_home);
        layout.setOnTouchListener(this);
        ScrollView layout2 = this.findViewById(R.id.layout_seekbar);
        layout2.setOnTouchListener(this);

        this.jobList = new ArrayList<>(NUM_JOB);
        for (int i = 0; i < NUM_JOB; i++)
            this.jobList.add(new Job(mapping(i)));

        for (int i = 0; i < NUM_JOB; i++) {
            this.setonClickCategory(
                    this.jobList.get(i).getImageView(),
                    this.jobList.get(i).getTextView());
        }

        this.city = this.findViewById(R.id.input_city);
        this.cityError = this.findViewById(R.id.cityErr);

        km = findViewById(R.id.km);
        seekBar = findViewById(R.id.seekbar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                km.setText(progress + " km");
                closeKeyboard(findViewById(R.id.activity_home));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Settare on click del bottone cerca e richiamare l'actvity per la lista dei lavoratori.
        Button btnFind = this.findViewById(R.id.cerca);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                boolean work = false;
                int code = -1;
                for (int i = 0; i < NUM_JOB; i++) {
                    if (jobList.get(i)
                            .getTextView()
                            .getCurrentTextColor() == Color.rgb(228, 63, 63)) {
                        code = i;
                        work = true;
                    }
                }
                if (checkFieldName(city, cityError) && work) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, WorkerListDrawer.class);
                    intent.putExtra("Città", city.getText().toString().trim());
                    intent.putExtra("Raggio", seekBar.getProgress());
                    intent.putExtra("WorkCode", code);
                    context.startActivity(intent);
                } else {
                    if(!work)
                        Toast.makeText(HomePageDrawer.this,
                                "Seleziona un lavoro!", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView loginWord = findViewById(R.id.login_word);
        TextView loginLink = findViewById(R.id.link_login);
        if (AUTH_STATUS.getCurrentUser() != null) {
            loginWord.setVisibility(View.GONE);
            loginLink.setVisibility(View.GONE);
        } else {
            loginLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), LoginPageDrawer.class));
                }
            });
        }
    }

    private void setonClickCategory(ImageView anImage, TextView aTextView) {
        final TextView tempView = aTextView;
        anImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                for (int i = 0; i < NUM_JOB; i++)
                    jobList.get(i).getTextView().setTextColor(Color.BLACK);
                tempView.setTextColor(Color.rgb(228, 63, 63));
            }
        });

        aTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                for (int i = 0; i < NUM_JOB; i++)
                    jobList.get(i).getTextView().setTextColor(Color.BLACK);
                tempView.setTextColor(Color.rgb(228, 63, 63));
            }
        });
    }
}

