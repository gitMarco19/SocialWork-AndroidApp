package com.social.socialjobs;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * Created by Marco on 04/12/2019.
 *
 * Login Java class.
 * Serve per gestire la pagina di login.
 */

public class LoginPageDrawer extends Utilities {

    private EditText email;
    private EditText password;
    private TextInputLayout emailError;
    private TextInputLayout passError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.email = this.findViewById(R.id.inputEmail);
        this.password = this.findViewById(R.id.inputPassword);
        this.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        this.emailError = this.findViewById(R.id.email);
        this.passError = this.findViewById(R.id.pass);

        Button btnLogin = this.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                login(email.getText().toString(), password.getText().toString());
            }
        });

        TextView signupLink = this.findViewById(R.id.linkSignup);
        signupLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                startActivity(new Intent(getApplicationContext(), RegistrationPageDrawer.class));
            }
        });

        LinearLayout layout = this.findViewById(R.id.activity_login);
        layout.setOnTouchListener(this);
    }

    /**
     * Metodo che esegue il login dell'utente con email e password.
     */
    private void login(String anEmail, String password) {
        if (inputValidation()) {
            AUTH_STATUS.signInWithEmailAndPassword(anEmail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkStatus(AUTH_STATUS.getCurrentUser(),
                                        LoginPageDrawer.this, getString(R.string.logSuccess));

                                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                                mUser.getIdToken(true)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    String idToken = task.getResult().getToken();
                                                    Map<String, Object> tokenMap = new HashMap<>();
                                                    tokenMap.put("token_id", idToken);

                                                    DATA_BASE.collection("UTENTI").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).update(tokenMap);
                                                }
                                            }
                                        });

                            }
                            else{
                                emailError.setError(getString(R.string.err_email));
                                checkStatus(null, LoginPageDrawer.this,
                                        getString(R.string.logFailed));
                            }
                        }
                    });
        }
    }

    /**
     * Metodo che controlla se email e password sono valide.
     */
    private boolean inputValidation() {
        boolean validEmail = this.checkEmail(this.email, this.emailError);

        boolean validPwd = this.checkPassword(this.password, this.passError);

        return (validEmail && validPwd);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}

