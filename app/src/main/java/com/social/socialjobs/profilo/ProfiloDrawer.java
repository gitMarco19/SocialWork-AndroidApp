package com.social.socialjobs.profilo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.social.socialjobs.R;
import com.social.socialjobs.prenotazioni.AddReservation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;

import com.social.socialjobs.addon.NavigationMenu;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ProfiloDrawer extends NavigationMenu {

    private static final int REQUEST_CALL = 1;
    private TextView textNameSurname, textTown, textDescript;
    private ImageView ivProfile, ivBackground;
    private String emailPassata = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emailPassata = getIntent().getStringExtra("Email");
        if (emailPassata != null) {
            this.setContentView(R.layout.content_profilo);
            this.setBackArrow();
        }
        else
            this.setMenu(R.layout.activity_drawer_profilo);

        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        textNameSurname = (TextView) findViewById(R.id.tvNomeCognome);
        textTown = (TextView) findViewById(R.id.tvCitta);
        textDescript = (TextView) findViewById(R.id.tvDescrizione);

        ivProfile = (ImageView) findViewById(R.id.profile_image);
        ivBackground = (ImageView) findViewById(R.id.copertina);
        DocumentReference docRef;

        if (FromProfileToUpdate.fromUpdate){
            FromProfileToUpdate.fromUpdate = false;

            textNameSurname.setText(FromProfileToUpdate.nome + " " + FromProfileToUpdate.cognome);
            textTown.setText(FromProfileToUpdate.citta);
            textDescript.setText(FromProfileToUpdate.descrizione);
            if (FromProfileToUpdate.Profile != null){
                ivProfile.setImageBitmap(FromProfileToUpdate.Profile);
            }
            else{
                if (FromProfileToUpdate.sesso.equals("D"))
                    ivProfile.setImageResource(R.drawable.avatar_woman);
                else
                    ivProfile.setImageResource(R.drawable.avatar_man);
            }
            if (FromProfileToUpdate.Background != null)
                ivBackground.setImageBitmap(FromProfileToUpdate.Background);

        }
        else {

            if (emailPassata == null) {
                docRef = DATA_BASE.collection("UTENTI").document(AUTH_STATUS.getCurrentUser().getEmail());
            } else {
                docRef = DATA_BASE.collection("UTENTI").document(emailPassata);
            }

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FromProfileToUpdate.nome = document.get("Nome").toString();
                            FromProfileToUpdate.cognome = document.get("Cognome").toString();
                            FromProfileToUpdate.citta = document.get("Città").toString();
                            FromProfileToUpdate.descrizione = document.get("Descrizione").toString();
                            FromProfileToUpdate.servizi = document.get("Servizi").toString();
                            FromProfileToUpdate.telefono = document.get("Telefono").toString();
                            FromProfileToUpdate.sesso = document.get("Sesso").toString();

                            textNameSurname.setText(FromProfileToUpdate.nome + " " + FromProfileToUpdate.cognome);
                            textTown.setText(FromProfileToUpdate.citta);
                            textDescript.setText(FromProfileToUpdate.descrizione);

                            String ImageRef = document.get("ProfileImage").toString();
                            String SfondoRef = document.get("Sfondo").toString();

                            if (!ImageRef.equals("")) {
                                String extProfilo = ImageRef.substring(ImageRef.lastIndexOf(".") + 1);
                                final File localFileProfile;
                                try {
                                    localFileProfile = File.createTempFile("images", extProfilo);

                                    final ProgressDialog progressDialog = new ProgressDialog(ProfiloDrawer.this, R.style.AppTheme_Dark_Dialog);
                                    progressDialog.setTitle("Download...");
                                    progressDialog.show();
                                    progressDialog.setCanceledOnTouchOutside(false);

                                    STORAGE_REF
                                            .child("images/" + ImageRef)
                                            .getFile(localFileProfile)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    progressDialog.dismiss();
                                                    FromProfileToUpdate.Profile = BitmapFactory.decodeFile(localFileProfile.getAbsolutePath());
                                                    ivProfile.setImageBitmap(FromProfileToUpdate.Profile);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle failed download
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ProfiloDrawer.this, "Immagine non trovata ", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                            .getTotalByteCount());
                                                    progressDialog.setMessage("Download " + (int) progress + "%");
                                                }
                                            });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                FromProfileToUpdate.Profile = null;
                                if (FromProfileToUpdate.sesso.equals("D"))
                                    ivProfile.setImageResource(R.drawable.avatar_woman);
                                else
                                    ivProfile.setImageResource(R.drawable.avatar_man);
                            }

                            if (!SfondoRef.equals("")) {
                                String extSfondo = SfondoRef.substring(SfondoRef.lastIndexOf(".") + 1);
                                final File localFile;
                                try {
                                    localFile = File.createTempFile("images", extSfondo);

                                    final ProgressDialog progressDialog = new ProgressDialog(ProfiloDrawer.this, R.style.AppTheme_Dark_Dialog);
                                    progressDialog.setTitle("Download...");
                                    progressDialog.show();
                                    progressDialog.setCanceledOnTouchOutside(false);

                                    STORAGE_REF
                                            .child("images/" + SfondoRef)
                                            .getFile(localFile)

                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    progressDialog.dismiss();
                                                    FromProfileToUpdate.Background = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                    ivBackground.setImageBitmap(FromProfileToUpdate.Background);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle failed download
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ProfiloDrawer.this, "Immagine non trovata ", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                            .getTotalByteCount());
                                                    progressDialog.setMessage("Download " + (int) progress + "%");
                                                }
                                            });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                FromProfileToUpdate.Background = null;
                            }
                        } else {
                            Toast.makeText(ProfiloDrawer.this, "Immagine non trovata ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfiloDrawer.this, "Fallito " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        ImageButton btnModifyProf = this.findViewById(R.id.updateProf);
        btnModifyProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
            }
        });

        ImageButton call = (ImageButton) findViewById(R.id.buttonCall);
        TextView labelCall = (TextView) findViewById(R.id.labelChiama);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        ImageButton sendMail = (ImageButton) findViewById(R.id.buttonMail);
        TextView labelMail = (TextView) findViewById(R.id.labelMail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

        ImageButton reservation = (ImageButton) findViewById(R.id.buttonReservation);
        TextView labelReservation = (TextView) findViewById(R.id.labelPrenota);
        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddReservation.class);
                intent.putExtra("Email", emailPassata);

                startActivity(intent);
            }
        });

        //Se l'utente è loggato
        if (AUTH_STATUS.getCurrentUser() != null) {
            //se l'utente loggato guarda il profilo di un altro
            if ((emailPassata != null) && (!AUTH_STATUS.getCurrentUser().getEmail().equals(emailPassata))) {
                //nascondo il tasto di modifica profilo
                btnModifyProf.setVisibility(View.GONE);
            }
            else{
                call.setVisibility(View.GONE);
                labelCall.setVisibility(View.GONE);
                sendMail.setVisibility(View.GONE);
                labelMail.setVisibility(View.GONE);
                reservation.setVisibility(View.GONE);
                labelReservation.setVisibility(View.GONE);
            }
        }
        //se l'utente non è loggato
        else {
            //nascondo il tasto di modifica perchè non puo modificare il profilo finchè non si logga
            btnModifyProf.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
            sendMail.setVisibility(View.GONE);
            reservation.setVisibility(View.GONE);
            labelCall.setVisibility(View.GONE);
            labelMail.setVisibility(View.GONE);
            labelReservation.setVisibility(View.GONE);
        }
    }

    private void makePhoneCall(){
        if (FromProfileToUpdate.telefono.length() > 0) {
            if (ContextCompat.checkSelfPermission(ProfiloDrawer.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProfiloDrawer.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + FromProfileToUpdate.telefono;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(ProfiloDrawer.this, "Numero di telefono non presente", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }
            else{
                Toast.makeText(this, "Permesso negato", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailPassata, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EMAIL DA SOCIALJOBS");
        startActivity(Intent.createChooser(emailIntent, "Invia con"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}


