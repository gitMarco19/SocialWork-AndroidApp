package com.social.socialjobs.addon;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Marco on 10/01/2020.
 *
 * Java class che identifica la struttura visiva di un lavoro nell'homepage.
 */
public class Job {
    private TextView aTextView;
    private ImageView anImageView;

    Job(TextView a, ImageView b) {
        aTextView = a;
        anImageView = b;
    }

    public Job(Job aJob) {
        this.aTextView = aJob.getTextView();
        this.anImageView = aJob.getImageView();
    }

    public TextView getTextView() {
        return this.aTextView;
    }

    public ImageView getImageView() {
        return this.anImageView;
    }
}

