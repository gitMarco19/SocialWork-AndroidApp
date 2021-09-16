package com.social.socialjobs.addon;

import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.social.socialjobs.LoadingPageDrawer;
import com.social.socialjobs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by Marco on 24/12/2019.
 *
 * Utilities Java Class.
 * Qusta classe raccoglie le costanti e i metodi che servono più o meno in tutte le classi.
 */
public abstract class Utilities extends AppCompatActivity implements OnTouchListener {

    public final static FirebaseFirestore DATA_BASE = FirebaseFirestore.getInstance();
    public final static FirebaseAuth AUTH_STATUS = FirebaseAuth.getInstance();

    public final static FirebaseStorage STORAGE = FirebaseStorage.getInstance();
    public final static StorageReference STORAGE_REF = STORAGE.getReference();

    public static final int NUM_JOB = 8;


    @Override
    /*
     * Metodo che serve per gestire il tasto indietro
     */
    public void onBackPressed() {
        Intent aIntent = new Intent(Intent.ACTION_MAIN);
        aIntent.addCategory(Intent.CATEGORY_HOME);
        aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(aIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        this.closeKeyboard(view);
        return false;
    }

    /**
     * Chiude la keyboard quando si tocca un qualsiasi punto del view specificato.
     */
    public void closeKeyboard(@NonNull View view) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setBackArrow() {
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Funzione per il controllo del nome/cognome.
     */
    public boolean checkFieldName(@NonNull EditText field, TextInputLayout error) {
        String errorString = "";
        switch (field.getId()) {
            case R.id.input_nome:
                errorString = getResources().getString(R.string.nome_error);
                break;
            case R.id.input_cognome:
                errorString = getResources().getString(R.string.cognome_error);
                break;
            case R.id.input_city:
                errorString = getResources().getString(R.string.city_error);
                break;
        }

        if (field.getText().toString().isEmpty())
            error.setError(errorString);
        else {
            error.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    /**
     * Funzione per il controllo dell'email.
     */
    public boolean checkEmail(@NonNull EditText email, TextInputLayout emailError) {
        String errorString;

        if (email.getText().toString().isEmpty()) {
            errorString = getResources().getString(R.string.email_error);
            emailError.setError(errorString);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            errorString = getResources().getString(R.string.invalid_email);
            emailError.setError(errorString);
        } else  {
            emailError.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    /**
     * Funzione per il controllo della password.
     */
    public boolean checkPassword(@NonNull EditText password, TextInputLayout passError) {
        String errorString;
        int lenghtPwd = 6;

        if (password.getText().toString().isEmpty()) {
            errorString = getResources().getString(R.string.password_error);
            passError.setError(errorString);
        } else if (password.getText().length() < lenghtPwd) {
            errorString = getResources().getString(R.string.invalid_password);
            passError.setError(errorString);
        } else  {
            passError.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    /**
     * Controlla lo stato dell'utente.
     * Se lo stato è diverso da null allora il login e la registrazione sono andate a buon fine
     * e si viene indirizzati alla pagina di loading.
     */
    public void checkStatus(FirebaseUser stato, Context contesto, String messaggio) {
        if (stato != null) {
            Toast.makeText(contesto, messaggio, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoadingPageDrawer.class));
        } else
            Toast.makeText(contesto, messaggio, Toast.LENGTH_SHORT).show();
    }

    public Job mapping(Integer aInt) {
        Job aJob = null;
        switch(aInt) {
            case 0:
                aJob = new Job((TextView)findViewById(R.id.tvBabysitter),
                        (ImageView) findViewById(R.id.ivBabysitter));
                break;

            case 1:
                aJob = new Job((TextView)findViewById(R.id.tvDogsitter),
                        (ImageView) findViewById(R.id.ivDogsitter));
                break;

            case 2:
                aJob = new Job((TextView)findViewById(R.id.tvColf),
                        (ImageView) findViewById(R.id.ivColf));
                break;
            case 3:
                aJob = new Job((TextView)findViewById(R.id.tvInfermiere),
                        (ImageView) findViewById(R.id.ivInfermiere));
                break;

            case 4:
                aJob = new Job((TextView)findViewById(R.id.tvBadante),
                        (ImageView) findViewById(R.id.ivBadante));
                break;

            case 5:
                aJob = new Job((TextView)findViewById(R.id.tvCommissioni),
                        (ImageView) findViewById(R.id.ivCommissioni));
                break;

            case 6:
                aJob = new Job((TextView)findViewById(R.id.tvCompagnia),
                        (ImageView) findViewById(R.id.ivCompagnia));
                break;

            case 7:
                aJob = new Job((TextView)findViewById(R.id.tvRipetizioni),
                        (ImageView) findViewById(R.id.ivRipetizioni));
                break;
        }
        return aJob;
    }
}
