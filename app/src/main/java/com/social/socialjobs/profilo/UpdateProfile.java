package com.social.socialjobs.profilo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.social.socialjobs.R;
import com.social.socialjobs.addon.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class UpdateProfile extends Utilities {

    private final int PICK_IMAGE_REQUEST = 71;
    private ImageButton btnChooseProfile, btnChooseBackground;
    private Button btnUpload;
    private ImageView ivProfile, ivBackground;
    private Uri filePathProfilo, filePathSfondo;
    private EditText editName, editSurname, editTown, editDescript, editPhoneNumber;
    private CheckBox cbBabySitter, cbDogSitter, cbBadante, cbColf, cbInfermiere, cbCommissioni, cbCompagnia, cbRipetizioni;
    private Bitmap bitmapProfilo=null, bitmapSfondo=null;
    private String image, extProfilo, extSfondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_profile);
        this.setBackArrow();

        ScrollView layout = this.findViewById(R.id.activity_update);
        layout.setOnTouchListener(this);

        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        filePathProfilo = null;
        filePathSfondo = null;

        btnChooseProfile = findViewById(R.id.updateProfImage);
        btnChooseBackground = findViewById(R.id.updateBackgroundImage);
        ivProfile = findViewById(R.id.profile_image);
        ivBackground = findViewById(R.id.copertina);
        btnUpload = findViewById(R.id.saveMod);

        editName = findViewById(R.id.nome);
        editSurname = findViewById(R.id.cognome);
        editTown = findViewById(R.id.citta);
        editDescript = findViewById(R.id.descrizione);
        editPhoneNumber = findViewById(R.id.cell);


        cbBabySitter = findViewById((R.id.checkBoxServizi0));
        cbBadante = findViewById((R.id.checkBoxServizi1));
        cbColf = findViewById((R.id.checkBoxServizi2));
        cbDogSitter = findViewById((R.id.checkBoxServizi3));
        cbInfermiere = findViewById((R.id.checkBoxServizi4));
        cbCommissioni = findViewById(R.id.checkBoxServizi5);
        cbCompagnia = findViewById(R.id.checkBoxServizi6);
        cbRipetizioni = findViewById(R.id.checkBoxServizi7);

        if (FromProfileToUpdate.Profile != null) {
            bitmapProfilo = FromProfileToUpdate.Profile;
            ivProfile.setImageBitmap(FromProfileToUpdate.Profile);
        }
        else {
            if(FromProfileToUpdate.sesso.equals("D"))
                ivProfile.setImageResource(R.drawable.avatar_woman);
            else
                ivProfile.setImageResource(R.drawable.avatar_man);
        }
        if (FromProfileToUpdate.Background != null) {
            bitmapSfondo = FromProfileToUpdate.Background;
            ivBackground.setImageBitmap(FromProfileToUpdate.Background);
        }

        editName.setText(FromProfileToUpdate.nome);
        editSurname.setText(FromProfileToUpdate.cognome);
        editTown.setText(FromProfileToUpdate.citta);
        editDescript.setText(FromProfileToUpdate.descrizione);
        editPhoneNumber.setText(FromProfileToUpdate.telefono);

        if (FromProfileToUpdate.servizi.charAt(0) == '1')
            this.cbBabySitter.setChecked(true);
        if (FromProfileToUpdate.servizi.charAt(1) == '1')
            this.cbDogSitter.setChecked(true);
        if (FromProfileToUpdate.servizi.charAt(2) == '1')
            this.cbColf.setChecked(true);
        if (FromProfileToUpdate.servizi.charAt(3) == '1')
            this.cbInfermiere.setChecked(true);
        if (FromProfileToUpdate.servizi.charAt(4) == '1')
            this.cbBadante.setChecked(true);
        if (FromProfileToUpdate.servizi.charAt(5) == '1')
            this.cbCommissioni.setChecked(true);
        if (FromProfileToUpdate.servizi.charAt(6) == '1')
            this.cbCompagnia.setChecked(true);
        if (FromProfileToUpdate.servizi.charAt(7) == '1')
            this.cbRipetizioni.setChecked(true);

        btnChooseProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //closeKeyboard(view);
                image = "profilo";
                chooseImage();
            }
        });

        btnChooseBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image = "sfondo";
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //viene chiamata in automatico al termine della startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            if (image.equals("profilo")) {
                filePathProfilo = data.getData();
                //recupero l'estensione del file immagine
                Cursor cursorFile = getContentResolver()
                        .query(filePathProfilo, null,
                                null, null, null);
                if (cursorFile.moveToFirst()) {
                    String fileName = cursorFile
                            .getString(cursorFile
                                    .getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    String[] fileNameArray = fileName.split("\\.");
                    extProfilo = fileNameArray[fileNameArray.length-1];
                }
                else
                    extProfilo = null;
                cursorFile.close();

                try {
                    bitmapProfilo = MediaStore
                            .Images.Media
                            .getBitmap(getContentResolver(), filePathProfilo);
                    ivProfile.setImageBitmap(bitmapProfilo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                filePathSfondo = data.getData();
                //recupero l'estensione del file immagine
                Cursor cursorFile = getContentResolver()
                        .query(filePathSfondo, null,
                                null, null, null);
                if (cursorFile.moveToFirst()) {
                    String fileName = cursorFile
                            .getString(cursorFile
                                    .getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    String[] fileNameArray = fileName.split("\\.");
                    extSfondo = fileNameArray[fileNameArray.length-1];
                }
                else
                    extSfondo = null;
                cursorFile.close();

                try {
                    bitmapSfondo = MediaStore
                            .Images.Media
                            .getBitmap(getContentResolver(), filePathSfondo);
                    ivBackground.setImageBitmap(bitmapSfondo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void upload() {
        String userMail = AUTH_STATUS.getCurrentUser().getEmail();
        String nome = editName.getText().toString().trim();
        String cognome = editSurname.getText().toString().trim();
        String citta = editTown.getText().toString().trim();
        String descrizione = editDescript.getText().toString().trim();
        String telefono = editPhoneNumber.getText().toString().trim();
        String servizi = "00000000000000000000";

        if (this.cbBabySitter.isChecked())
            servizi = '1' + servizi.substring(1);
        if (this.cbDogSitter.isChecked())
            servizi = servizi.substring(0, 1) + '1' + servizi.substring(2);
        if (this.cbColf.isChecked())
            servizi = servizi.substring(0, 2) + '1' + servizi.substring(3);
        if (this.cbInfermiere.isChecked())
            servizi = servizi.substring(0, 3) + '1' + servizi.substring(4);
        if (this.cbBadante.isChecked())
            servizi = servizi.substring(0, 4) + '1' + servizi.substring(5);
        if (this.cbCommissioni.isChecked())
            servizi = servizi.substring(0, 5) + '1' + servizi.substring(6);
        if (this.cbCompagnia.isChecked())
            servizi = servizi.substring(0, 6) + '1' + servizi.substring(7);
        if (this.cbRipetizioni.isChecked())
            servizi = servizi.substring(0, 7) + '1' + servizi.substring(8);


        DATA_BASE
                .collection("UTENTI")
                .document(userMail)
                .update("Nome", nome, "Cognome", cognome, "Città", citta,
                        "Descrizione", descrizione, "Servizi", servizi, "Telefono", telefono);

        FromProfileToUpdate.nome = nome;
        FromProfileToUpdate.cognome = cognome;
        FromProfileToUpdate.citta = citta;
        FromProfileToUpdate.descrizione = descrizione;
        FromProfileToUpdate.servizi = servizi;
        FromProfileToUpdate.telefono = telefono;

        if(filePathProfilo != null) {
            uploadProfileImage();
        }
        else if (filePathSfondo != null) {
            uploadBackgroundImage();
        }
        else {
            Toast.makeText(UpdateProfile.this,
                    "Profilo aggiornato con succcesso", Toast.LENGTH_SHORT).show();
            FromProfileToUpdate.fromUpdate = true;
            startActivity(new Intent(getApplicationContext(), ProfiloDrawer.class));
        }
    }


    private void uploadProfileImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setTitle("Caricamento immagine profilo...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        STORAGE_REF
                .child("images/"+ UUID.randomUUID().toString()+"."+extProfilo)
                .putFile(filePathProfilo)

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        String name = taskSnapshot.getStorage().getName();
                        String userMail = AUTH_STATUS.getCurrentUser().getEmail();

                        DATA_BASE
                                .collection("UTENTI")
                                .document(userMail)
                                .update("ProfileImage", name);

                        FromProfileToUpdate.Profile = bitmapProfilo;
                        if (filePathSfondo != null) {
                            uploadBackgroundImage();
                        }
                        else {
                            Toast.makeText(UpdateProfile.this, "Profilo aggiornato con successo", Toast.LENGTH_SHORT).show();
                            FromProfileToUpdate.fromUpdate = true;
                            startActivity(new Intent(getApplicationContext(), ProfiloDrawer.class));
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateProfile.this, "Caricamento immagine profilo fallita "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Caricamento "+(int)progress+"%");
                    }
                });
    }

    private void uploadBackgroundImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setTitle("Caricamento immagine di copertina...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        STORAGE_REF
                .child("images/" + UUID.randomUUID().toString()+"."+extSfondo)
                .putFile(filePathSfondo)

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        // Toast.makeText(UpdateProfile.this, "Immagine di copertina aggiornata", Toast.LENGTH_SHORT).show();

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        String name = taskSnapshot.getStorage().getName();
                        String userMail = AUTH_STATUS.getCurrentUser().getEmail();

                        DATA_BASE
                                .collection("UTENTI")
                                .document(userMail)
                                .update("Sfondo", name);

                        FromProfileToUpdate.Background = bitmapSfondo;
                        Toast.makeText(UpdateProfile.this,
                                "Profilo aggiornato con successo",
                                Toast.LENGTH_SHORT).show();
                        FromProfileToUpdate.fromUpdate = true;
                        startActivity(new Intent(getApplicationContext(), ProfiloDrawer.class));

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateProfile.this,
                                "Caricamento immagine di copertina fallito "
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Caricamento " + (int) progress + "%");
                    }
                });
    }

    @Override
    public void setBackArrow() {
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            FromProfileToUpdate.fromUpdate = true;
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}


