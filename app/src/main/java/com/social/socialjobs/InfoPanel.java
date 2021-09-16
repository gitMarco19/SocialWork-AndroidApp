package com.social.socialjobs;

import android.os.Bundle;
import android.widget.TextView;

import com.social.socialjobs.addon.Utente;
import com.social.socialjobs.addon.Utilities;

import java.util.ArrayList;

public class InfoPanel extends Utilities {

    private TextView utente;
    private TextView lavoro;
    private TextView giorni;
    private TextView dataStart;
    private TextView dataEnd;
    private TextView city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("INFO");
        this.setContentView(R.layout.activity_info_panel);

        this.setViewID();

        Bundle param = getIntent().getExtras();
        String tipo = param.getString("Tipo");
        String aString;
        Utente user;
        if (tipo.equals("lavori")) {
            user = (Utente) param.getSerializable("Cliente");
            aString = "Cliente: ";
            aString += user.getNome();
            aString += " " + user.getCognome();
        }
        else {
            user = (Utente) param.getSerializable("Lavoratore");
            aString = "Lavoratore: ";
            aString += user.getNome();
            aString += " " + user.getCognome();
        }

        this.utente.setText(aString);

        String tipoLavoro = "Lavoro: " + param.getString("TipoLavoro");
        tipoLavoro += " - " + param.getString("TipoImpegno");

        this.lavoro.setText(tipoLavoro);

        String giorni = param.getString("Giorni");
        if (giorni != null && !giorni.equals("")) {
            ArrayList<String> days = new ArrayList<>();
            this.giorni.setText("Giorni: \n");
            int k = -1;
            for (int i = 0; i < giorni.length(); i++) {
                if (Character.isUpperCase(giorni.charAt(i))) {
                    k++;
                    days.add("" + giorni.charAt(i));
                } else {
                    days.set(k, days.get(k).concat("" + giorni.charAt(i)));
                }
            }
            for (int i = 0; i < days.size(); i++)
                if (i != days.size() - 1)
                    this.giorni.append(" - " + days.get(i) + "\n");
                else
                    this.giorni.append(" - " + days.get(i));
        }

        String data = "Data inizio: " + param.getString("StartData");
        data += " - " + param.getString("StartTime");

        this.dataStart.setText(data);

        data = "Data fine: " + param.getString("EndData");
        data += " - " + param.getString("EndTime");

        this.dataEnd.setText(data);

        String luogo = "Città: " + param.getString("City");
        this.city.setText(luogo);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setViewID() {
        this.utente = this.findViewById(R.id.nome);
        this.lavoro = this.findViewById(R.id.lavoro);
        this.giorni = this.findViewById(R.id.giorni);
        this.dataStart = this.findViewById(R.id.data_ora_start);
        this.dataEnd = this.findViewById(R.id.data_ora_end);
        this.city = this.findViewById(R.id.city);
    }
}

