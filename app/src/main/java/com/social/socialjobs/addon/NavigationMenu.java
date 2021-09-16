package com.social.socialjobs.addon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.social.socialjobs.HomePageDrawer;
import com.social.socialjobs.lavori.LavoriDrawer;
import com.social.socialjobs.prenotazioni.PrenotazioniDrawer;
import com.social.socialjobs.profilo.ProfiloDrawer;
import com.social.socialjobs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Created by Marco on 04/12/2019.
 *
 * Java abstract class per il menu
 *
 * L'idea è quella di far ereditare a ogni activity, che andrà a contenere il menu, questa classe.
 * In questo modo l'activity potrà utilizzare tutte le funzioni inerenti al menu senza doverle replicare
 * in ogni classe in cui viene utilizzato.
 *
 * Ovviamente è una bozza, va completata.
 */
public abstract class NavigationMenu extends Utilities
        implements NavigationView.OnNavigationItemSelectedListener {

    //Attributi
    private Integer layout;


    //Metodi

    /**
     * Questo metodo crea un menu per qualisasi layout specificato come parametro.
     * Grazie a quest'ultimo, si riesce anche a specificare in maniera esatta il tipo di operazioni
     * da fare quando si clicca un elemento del menu. (Vedi metodo successivo).
     */
    public void setMenu(int layout) {
        this.layout = layout;
        this.setContentView(this.layout);
        this.creaMenu();
    }

    @Override
    /*
     * Metodo che serve per gestire i vari link del menu.
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.home:
                if (this.layout == R.layout.activity_drawer_homepage)
                    this.closeMenu();//chiudo il menu percé rimango sulla stessa pagina
                else {
                    this.startActivity(new Intent(getApplicationContext(), HomePageDrawer.class));
                    this.finish();
                }
                break;

            case R.id.profilo:
                this.startActivity(new Intent(getApplicationContext(), ProfiloDrawer.class));
                this.finish();
                break;

            case R.id.myJobs:
                if (this.layout == R.layout.activity_drawer_lavori)
                    this.closeMenu();
                else {
                    this.startActivity(new Intent(getApplicationContext(), LavoriDrawer.class));
                    finish();
                }
                break;

            case R.id.myPrenotations:
                if (this.layout == R.layout.activity_drawer_prenotazioni)
                    this.closeMenu();
                else {
                    this.startActivity(new Intent(getApplicationContext(), PrenotazioniDrawer.class));
                    finish();
                }
                break;

            case R.id.logout:
                String email = AUTH_STATUS.getCurrentUser().getEmail();
                Map<String, Object> tokenMapRemove = new HashMap<>();
                tokenMapRemove.put("token_id", "");
                DATA_BASE.collection("UTENTI").document(email).update(tokenMapRemove);

                AUTH_STATUS.signOut();
                this.startActivity(new Intent(getApplicationContext(), HomePageDrawer.class));
                this.finish();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if ((AUTH_STATUS.getCurrentUser() != null) && !this.closeMenu())
            super.onBackPressed();
        else if (AUTH_STATUS.getCurrentUser() == null)
            super.onBackPressed();

    }

    public boolean closeMenu() {
        DrawerLayout drawer = this.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    /**
     * Metodo che si occupa di creare la barra del menu
     */
    private void creaMenu(){
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = this.findViewById(R.id.nav_view);
        this.loadHeaderInfo(navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadHeaderInfo(final NavigationView navigationView) {

        DocumentReference docRef = DATA_BASE.collection("UTENTI")
                .document(AUTH_STATUS.getCurrentUser().getEmail());

        final View headerView = navigationView.getHeaderView(0);
        final ImageView ivProfile = headerView.findViewById(R.id.profile_image);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String nome = document.get("Nome").toString();
                        String cognome = document.get("Cognome").toString();
                        String sesso = document.get("Sesso").toString();
                        String ImageRef = document.get("ProfileImage").toString();
                        if (!ImageRef.equals("")) {
                            try {
                                final File localFile = File.createTempFile("images", "jpeg");

                                STORAGE_REF
                                        .child("images/" + ImageRef)
                                        .getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Bitmap bitmapProfile = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                ivProfile.setImageBitmap(bitmapProfile);
                                            }
                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            if(sesso.equals("D"))
                                ivProfile.setImageResource(R.drawable.avatar_woman);
                            else
                                ivProfile.setImageResource(R.drawable.avatar_man);
                        }

                        TextView navUsername = headerView.findViewById(R.id.nomeHeader);
                        navUsername.setText(nome + " " + cognome);
                    }
                }
            }
        });
    }
}
