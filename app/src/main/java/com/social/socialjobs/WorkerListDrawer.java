package com.social.socialjobs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.social.socialjobs.addon.Utente;
import com.social.socialjobs.addon.Utilities;
import com.social.socialjobs.profilo.ProfiloDrawer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Marco on 10/01/2020.
 *
 * Java class relativa alla lista degli utenti
 * che si sono proposti per una data categoria di lavoro.
 */
public class WorkerListDrawer extends Utilities {

    private ArrayList<Utente> users = new ArrayList<>();
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_list_workers);

        //Per la freccia indietro
        this.setBackArrow();
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle param = getIntent().getExtras();
        final String city = param.getString("Città");
        final int codice = param.getInt("WorkCode");
        final int raggio = param.getInt("Raggio");

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        DATA_BASE
                .collection("UTENTI")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                users.add(new Utente(
                                        document.get("Email").toString(),
                                        document.get("Nome").toString(),
                                        document.get("Cognome").toString(),
                                        document.get("Città").toString(),
                                        document.get("Telefono").toString(),
                                        document.get("Sesso").toString(),
                                        document.get("Servizi").toString(),
                                        document.get("ProfileImage").toString(),
                                        document.get("Sfondo").toString(),
                                        document.get("Descrizione").toString()));
                            }

                            //recupero le coordinate della città del cliente (chi fa la ricerca)
                            Location locationClient = new Location(city);
                            try {
                                List<Address> addressList = geocoder.getFromLocationName(city , 1);
                                if (addressList != null && addressList.size() > 0) {
                                    Address address = addressList.get(0);
                                    double latA = address.getLatitude();
                                    double lngA = address.getLongitude();
                                    locationClient.setLatitude(latA);
                                    locationClient.setLongitude(lngA);
                                }
                            }
                            catch(Exception e) {
                                locationClient = null;
                            }

                            ArrayList<Utente> temp = new ArrayList<>();
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).getServizi().charAt(codice) == '1') {
                                    if (users.get(i).getCity().equalsIgnoreCase(city)) {
                                        temp.add(users.get(i));
                                    }
                                    else {
                                        if (locationClient != null &&
                                                getDistance(locationClient, users.get(i).getCity()) <= raggio) {
                                            temp.add(users.get(i));
                                        }
                                    }
                                }
                            }

                            LinearLayout aLayout = findViewById(R.id.adInfo);
                            if (temp.isEmpty()) {
                                aLayout.setVisibility(View.VISIBLE);
                                TextView aText = findViewById(R.id.testo);
                                aText.setText("Nessun lavoratore disponibile!");
                            } else {
                                aLayout.setVisibility(View.GONE);
                                RecyclerView recyclerView = findViewById(R.id.item_list);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setAdapter(
                                        new SimpleItemRecyclerViewAdapter(temp));
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private double getDistance(Location locationA, String cityB) {
        double distance = 0;
        try {
            //recupero latitudine e longitudine
            List<Address> addressList = geocoder.getFromLocationName(cityB, 1);
            if (addressList != null && addressList.size() > 0) {

                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                double lat2 = address.getLatitude();
                double lng2 = address.getLongitude();

                //calcolo la distanza in linea d'aria
                Location locationB = new Location(cityB);
                locationB.setLatitude(lat2);
                locationB.setLongitude(lng2);
                distance = locationA.distanceTo(locationB);
            }
        } catch (IOException e) {}

        //se la città non viene trovata dal geocoder ritorno 0
        return distance/1000;
    }



    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Utente> utenti;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utente item = (Utente) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, ProfiloDrawer.class);
                intent.putExtra("Email", item.getEmail());
                context.startActivity(intent);
            }
        };

        SimpleItemRecyclerViewAdapter(List<Utente> utenti) {
            this.utenti = utenti;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_list_workers, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String ImageRef = this.utenti.get(position).getProfileImageToString();
            final File localFile;
            try {
                if (!ImageRef.equals("")) {
                    localFile = File.createTempFile("images", "jpeg");
                    STORAGE_REF
                            .child("images/" + ImageRef)
                            .getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    holder.anImageView.setImageBitmap(bitmap);
                                }
                            });
                } else {
                    if (utenti.get(position).getSesso().equals("U"))
                        holder.anImageView.setImageResource(R.drawable.avatar_man);
                    else
                        holder.anImageView.setImageResource(R.drawable.avatar_woman);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            holder.mContentView
                    .setText(this.utenti.get(position).getNome()
                            + " " + this.utenti.get(position).getCognome());
            holder.mContentView2.setText("Città: " + this.utenti.get(position).getCity());

            holder.itemView.setTag(this.utenti.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return this.utenti.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView anImageView;
            final TextView mContentView;
            final TextView mContentView2;

            ViewHolder(View view) {
                super(view);
                this.anImageView = view.findViewById(R.id.image);
                mContentView = view.findViewById(R.id.content);
                mContentView2 = view.findViewById(R.id.content2);
            }
        }
    }
}
