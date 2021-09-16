package com.social.socialjobs;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.social.socialjobs.addon.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by Marco on 11/12/2019.
 *
 * Registrazione Java class.
 * Serve per gestire la pagina di registrazione.
 */

public class RegistrationPageDrawer extends Utilities {

    private EditText nome;
    private EditText cognome;
    private EditText city;
    private EditText email;
    private EditText password;
    private EditText rePassword;
    private TextInputLayout nomeError;
    private TextInputLayout cognomeError;
    private TextInputLayout cityError;
    private TextInputLayout emailError;
    private TextInputLayout passError;
    private TextInputLayout rePassErr;
    private RadioButton rbUomo, rbDonna;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        this.setInputID();
        this.setErrorsID();

        Button btnLogin = this.findViewById(R.id.btnSignup);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                registration(email.getText().toString(), password.getText().toString());
            }
        });

        TextView loginLink = findViewById(R.id.link_login);
        loginLink.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                startActivity(new Intent(getApplicationContext(), LoginPageDrawer.class));
            }
        });

        this.rbDonna.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                rbUomo.setChecked(false);
            }
        });
        this.rbUomo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                rbDonna.setChecked(false);
            }
        });

        LinearLayout layout = this.findViewById(R.id.activity_join);
        layout.setOnTouchListener(this);
    }

    //Serve per gestire il tasto indietro
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Metodo che esegue la registrazione dell'account utente tramite email e password.
     */
    private void registration(String anEmail, String password) {
        if (this.inputValidation()) {
            AUTH_STATUS.createUserWithEmailAndPassword(anEmail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                addUserData();

                                           /* String token_id = FirebaseInstanceId.getInstance().getToken();
                                            //oppure .getUid()
                                            String current_id = AUTH_STATUS.getCurrentUser().getEmail();

                                            Map<String, Object> tokenMap = new HashMap<>();
                                            tokenMap.put("token_id", token_id);

                                            DATA_BASE.collection("UTENTI").document(current_id).update(tokenMap);*/



                                checkStatus(AUTH_STATUS.getCurrentUser(),
                                        RegistrationPageDrawer.this,
                                        getString(R.string.regSuccess));

                                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                                mUser.getIdToken(true)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    String idToken = task.getResult().getToken();
                                                    Map<String, Object> tokenMap = new HashMap<>();
                                                    tokenMap.put("token_id", idToken);

                                                    DATA_BASE.collection("UTENTI").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).update(tokenMap);
                                                } else {
                                                    // Handle error -> task.getException();
                                                }
                                            }
                                        });

                            } else {
                                emailError.setError(getString(R.string.disp_email));
                                checkStatus(null,RegistrationPageDrawer.this,
                                        getString(R.string.regFailed));
                            }

                        }
                    });
        }
    }

    /**
     * Metodo che aggiunge i dati dell'utente nel data base.
     */
    private void addUserData() {
        Map<String, Object> utente = new HashMap<>();
        utente.put("Nome", this.nome.getText().toString().trim());
        utente.put("Cognome", this.cognome.getText().toString().trim());
        utente.put("Email", this.email.getText().toString().trim());
        utente.put("Password", this.password.getText().toString().trim());
        utente.put("Città", this.city.getText().toString().trim());
        utente.put("ProfileImage", "");
        utente.put("Descrizione", "");
        utente.put("Servizi", "00000000000000000000");
        utente.put("Telefono", "");
        utente.put("Sfondo", "");
        utente.put("token_id", "");
        if (this.rbDonna.isChecked())
            utente.put("Sesso", "D");
        else
            utente.put("Sesso", "U");

        DATA_BASE
                .collection("UTENTI")
                .document(this.email.getText().toString())
                .set(utente);
    }

    /**
     * Metodo che controlla se i dati di registrazione in input sono validi.
     */
    private boolean inputValidation() {
        boolean validName = this.checkFieldName(this.nome, this.nomeError);

        boolean validSurname = this.checkFieldName(this.cognome, this.cognomeError);

        /*
         * Non so come controllare se una città esiste davvero
         */
        boolean validCity = this.checkFieldName(this.city, this.cityError);

        boolean validEmail = this.checkEmail(this.email, this.emailError);

        boolean validPwd = this.checkPassword(this.password, this.passError);

        //Controllo se la password reinserita è uguale alla precedente
        boolean validRePwd = false;
        if (this.rePassword.getText().toString()
                .equals(this.password.getText().toString())) {
            validRePwd = true;
            this.rePassErr.setErrorEnabled(false);
        } else
            this.rePassErr.setError(getResources()
                    .getString(R.string.invalid_rePass));

        return (validName && validSurname && validCity &&
                validEmail && validPwd && validRePwd);
    }

    private void setInputID() {
        this.nome = this.findViewById(R.id.input_nome);
        this.cognome = this.findViewById(R.id.input_cognome);
        this.city = this.findViewById(R.id.input_city);
        this.email = this.findViewById(R.id.input_email);
        this.password = this.findViewById(R.id.input_password);
        this.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        this.rePassword = this.findViewById(R.id.input_reEnterPassword);
        this.rePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        this.rbDonna = findViewById(R.id.rbDonna);
        this.rbUomo = findViewById(R.id.rbUomo);
    }

    private void setErrorsID() {
        this.nomeError = this.findViewById(R.id.name_err);
        this.cognomeError = this.findViewById(R.id.cognome_err);
        this.cityError = this.findViewById(R.id.address_err);
        this.emailError = this.findViewById(R.id.email_err);
        this.passError = this.findViewById(R.id.pass_err);
        this.rePassErr = this.findViewById(R.id.rePass_err);
    }
}

