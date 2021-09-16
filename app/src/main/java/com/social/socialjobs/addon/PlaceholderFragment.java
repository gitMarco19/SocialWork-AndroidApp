package com.social.socialjobs.addon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.social.socialjobs.InfoPanel;
import com.social.socialjobs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Marco on 23/01/2020.
 *
 * Java class che si occupa della gestione dei contenuti dei fragment.
 */
public class PlaceholderFragment extends Fragment {

    private static Integer sezione;
    private static String tipo;
    private ArrayList<Prenotation> myDates = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.readBundle(getArguments());

        View aView = null;
        switch (sezione) {
            case 0: //appuntamenti
                aView  = inflater.inflate(R.layout.fragment_dates, container, false);
                stampaImpegni(aView, 0);
                break;

            case 1: //richieste
                aView = inflater.inflate(R.layout.fragment_request, container, false);
                stampaImpegni(aView, 1);
                break;

            case 2: //richieste in attesa di conferma
                aView = inflater.inflate(R.layout.fragment_request, container, false);
                stampaImpegni(aView, 2);
                break;
        }

        return aView;
    }

    /**
     * Metodo per creare un fragment.
     */
    public static PlaceholderFragment newInstance(int index, String tipo) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Indice", index);
        bundle.putString("Tipo", tipo);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Metodo per recuperare i parametri passati dall'activity al fragment.
     */
    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            sezione = bundle.getInt("Indice");
            tipo = bundle.getString("Tipo");
        }
    }

    private void stampaImpegni(final View aView, final int pagina) {
        this.myDates.clear();
        Utilities.DATA_BASE
                .collection("PRENOTAZIONI")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Utente lavoratore = new Utente();
                                lavoratore.setNome(document.get("Nome lavoratore").toString());
                                lavoratore.setCognome(document.get("Cognome lavoratore").toString());
                                lavoratore.setEmail(document.get("Email lavoratore").toString());

                                Utente cliente = new Utente();
                                cliente.setNome(document.get("Nome cliente").toString());
                                cliente.setCognome(document.get("Cognome cliente").toString());
                                cliente.setEmail(document.get("Email cliente").toString());

                                String dataInizio = document.get("DataInizio").toString();
                                String dataFine = document.get("DataFine").toString();
                                String oraInizio = document.get("OraInizio").toString();
                                String oraFine = document.get("OraFine").toString();

                                String lavoro = document.get("Lavoro").toString();
                                String city = document.get("Città").toString();
                                String tipoImpegno = document.get("TipoImpegno").toString();
                                String giorno = document.get("Giorno").toString();
                                String conferma = document.get("Conferma").toString();
                                String codice = document.getId();

                                myDates.add(new Prenotation(codice, lavoratore, cliente,
                                        dataInizio, dataFine, oraInizio, oraFine,
                                        lavoro, city, tipoImpegno, giorno, conferma));
                            }

                            Collections.sort(myDates, new SortbyData());
                            ArrayList<Prenotation> temp = new ArrayList<>();

                            filterImpegni(temp, pagina);

                            LinearLayout aLayout = aView.findViewById(R.id.adInfo);
                            if (!temp.isEmpty()) {
                                temp = sortbyTime(temp);
                                aLayout.setVisibility(View.GONE);
                                RecyclerView recyclerView = aView.findViewById(R.id.item_list);
                                recyclerView.setHasFixedSize(true);
                                if (pagina == 0)
                                    recyclerView.setAdapter(
                                            new PrenotationRecyclerViewAdapter(temp));
                                else
                                    recyclerView.setAdapter(
                                            new RequestRecyclerViewAdapter(temp));
                            } else {
                                aLayout.setVisibility(View.VISIBLE);
                                TextView aText = aView.findViewById(R.id.testo);
                                if (pagina == 0)
                                    aText.setText("Non ci sono impegni in agenda");
                                else if (pagina == 1)
                                    aText.setText("Non ci sono prenotazioni da confermare");
                                else
                                    aText.setText("Non ci sono prenotazioni in attesa di conferma");
                            }
                        }
                    }
                });
    }

    /**
     * Metodo per filtrare gli impegni in base al tipo e agli orari
     */
    private void filterImpegni(ArrayList<Prenotation> temp, int pagina) {
        String email;
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        String todayDate = sdf.format(new Date());
        Date data = new Date();
        try {
            data = new SimpleDateFormat("dd/MM/yyyy").parse(todayDate);
        } catch (ParseException eccezione) {
            eccezione.getCause();
        }

        for (int i = 0; i < this.myDates.size(); i++) {
            if (tipo.equals("lavori"))
                email = this.myDates.get(i).getLavoratore().getEmail();
            else
                email = this.myDates.get(i).getCliente().getEmail();

            if (email.equals(Utilities.AUTH_STATUS.getCurrentUser().getEmail())) {
                if (pagina == 0 && this.myDates.get(i).getConferma().equals("CONFERMATA")) {
                    if (this.myDates.get(i).getDataFine().after(data) ||
                            this.myDates.get(i).getDataFine().equals(data))
                        temp.add(this.myDates.get(i));
                    else
                        this.deletePrenotation(i);
                } else if (pagina == 1) {
                    if (tipo.equals("lavori") && this.myDates.get(i).getConferma().equals("DA_CONFERMARE")) {
                        if (this.myDates.get(i).getDataInizio().after(data) ||
                                this.myDates.get(i).getDataInizio().equals(data))
                            temp.add(myDates.get(i));
                        else
                            this.deletePrenotation(i);
                    } else if (tipo.equals("prenotazioni") && this.myDates.get(i).getConferma().equals("IN_ATTESA")) {
                        if (this.myDates.get(i).getDataInizio().after(data) ||
                                this.myDates.get(i).getDataInizio().equals(data))
                            temp.add(myDates.get(i));
                        else
                            this.deletePrenotation(i);
                    }
                } else if (pagina == 2) {
                    if (tipo.equals("lavori") && this.myDates.get(i).getConferma().equals("IN_ATTESA")) {
                        if (this.myDates.get(i).getDataInizio().after(data) ||
                                this.myDates.get(i).getDataInizio().equals(data))
                            temp.add(myDates.get(i));
                        else
                            this.deletePrenotation(i);
                    } else if (tipo.equals("prenotazioni") && this.myDates.get(i).getConferma().equals("DA_CONFERMARE")) {
                        if (this.myDates.get(i).getDataInizio().after(data) ||
                                this.myDates.get(i).getDataInizio().equals(data))
                            temp.add(this.myDates.get(i));
                        else
                            this.deletePrenotation(i);
                    }
                }
            }
        }
    }

    private void deletePrenotation(int index) {
        Utilities.DATA_BASE
                .collection("PRENOTAZIONI")
                .document(this.myDates.get(index).getCodice())
                .delete();
    }

    /**
     * Metodo per ordinare gli impegni in base all'ora.
     */
    private ArrayList<Prenotation> sortbyTime(ArrayList<Prenotation> temp) {
        ArrayList<ArrayList<Prenotation>> a = new ArrayList<>();
        a.add(new ArrayList<Prenotation>());
        int k = 0;
        Date t = temp.get(0).getDataInizio();
        for (int i = 0; i < temp.size(); i++) {
            if (!temp.get(i).getDataInizio().equals(t)) {
                t = temp.get(i).getDataInizio();
                k++;
                a.add(new ArrayList<Prenotation>());
                a.get(k).add(temp.get(i));
            } else {
                a.get(k).add(temp.get(i));
            }
        }

        for (int i = 0; i < a.size(); i++)
            Collections.sort(a.get(i), new SortbyTime());

        temp = new ArrayList<>();

        for (int i = 0; i < a.size(); i++)
            temp.addAll(a.get(i));

        return temp;
    }

    public class PrenotationRecyclerViewAdapter
            extends RecyclerView.Adapter<PrenotationRecyclerViewAdapter.ViewHolder> {

        private final List<Prenotation> prenotazioni;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prenotation item = (Prenotation) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, InfoPanel.class);
                intent.putExtra("Tipo", tipo);
                intent.putExtra("Lavoratore", item.getLavoratore());
                intent.putExtra("Cliente", item.getCliente());
                intent.putExtra("City", item.getCity());
                intent.putExtra("StartData", item.getStartDatatoSring());
                intent.putExtra("EndData", item.getEndDatatoSring());
                intent.putExtra("StartTime", item.getStartTimetoString());
                intent.putExtra("EndTime", item.getEndTimetoString());
                intent.putExtra("TipoLavoro", item.getTipoLavoro());
                intent.putExtra("TipoImpegno", item.getTipoImpegno());
                intent.putExtra("Giorni", item.getGiorno());
                context.startActivity(intent);
            }
        };

        PrenotationRecyclerViewAdapter(List<Prenotation> prenot) {
            this.prenotazioni = prenot;
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_list_prenotazioni, parent, false);
            return new PrenotationRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final PrenotationRecyclerViewAdapter.ViewHolder holder,
                                     final int position) {
            String content1;
            if (tipo.equals("lavori"))
                content1 = "Cliente: " + this.prenotazioni.get(position).getCliente().getNome()
                        + " " + this.prenotazioni.get(position).getCliente().getCognome();
            else
                content1 = "Lavoratore: " + this.prenotazioni.get(position).getLavoratore().getNome()
                        + " " + this.prenotazioni.get(position).getLavoratore().getCognome();

            holder.content1.setText(content1);

            holder.content2
                    .setText("Servizio: " + this.prenotazioni.get(position).getTipoLavoro()
                            + " - " + this.prenotazioni.get(position).getTipoImpegno());

            holder.content3.setText("CLICK PER MAGGIORI INFO");

            holder.calendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Date d = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    try {
                        d = sdf.parse(prenotazioni.get(position).getStartDatatoSring() +
                                " " + prenotazioni.get(position).getStartTimetoString());
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }

                    Date d2 = new Date();
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    try {
                        d2 = sdf2.parse(prenotazioni.get(position).getStartDatatoSring() +
                                " " +prenotazioni.get(position).getEndTimetoString());
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }

                    Intent aIntent = new Intent();
                    aIntent.setType("vnd.android.cursor.item/event");
                    aIntent.putExtra("title", "SOCIALJOBS: "
                            + prenotazioni.get(position).getTipoLavoro());
                    String name = holder.content1.getText().toString();
                    aIntent.putExtra("description",name.substring(name.indexOf(":") + 2));
                    aIntent.putExtra("beginTime", d.getTime());
                    aIntent.putExtra("endTime", d2.getTime());

                    String day = prenotazioni.get(position).getEndDatatoSring().substring(0, 2);
                    String month = prenotazioni.get(position).getEndDatatoSring().substring(3, 5);
                    String year = prenotazioni.get(position).getEndDatatoSring().substring(6, 10);
                    String endDate = year + month + day;
                    aIntent.setAction(Intent.ACTION_EDIT);

                    if (prenotazioni.get(position).getTipoImpegno().equals("Settimanale")) {
                        if (prenotazioni.get(position).getGiorno().contains("Lunedi")) {
                            aIntent.putExtra(CalendarContract.Events.RRULE,
                                    "FREQ=WEEKLY;BYDAY=MO;UNTIL=" + endDate);
                            startActivity(aIntent);
                        }

                        if (prenotazioni.get(position).getGiorno().contains("Martedi")) {
                            aIntent.putExtra(CalendarContract.Events.RRULE,
                                    "FREQ=WEEKLY;BYDAY=TU;UNTIL=" + endDate);
                            startActivity(aIntent);
                        }

                        if (prenotazioni.get(position).getGiorno().contains("Mercoledi")) {
                            aIntent.putExtra(CalendarContract.Events.RRULE,
                                    "FREQ=WEEKLY;BYDAY=WE;UNTIL=" + endDate);
                            startActivity(aIntent);
                        }

                        if (prenotazioni.get(position).getGiorno().contains("Giovedi")) {
                            aIntent.putExtra(CalendarContract.Events.RRULE,
                                    "FREQ=WEEKLY;BYDAY=TH;UNTIL=" + endDate);
                            startActivity(aIntent);
                        }

                        if (prenotazioni.get(position).getGiorno().contains("Venerdi")) {
                            aIntent.putExtra(CalendarContract.Events.RRULE,
                                    "FREQ=WEEKLY;BYDAY=FR;UNTIL=" + endDate);
                            startActivity(aIntent);
                        }

                        if (prenotazioni.get(position).getGiorno().contains("Sabato")) {
                            aIntent.putExtra(CalendarContract.Events.RRULE,
                                    "FREQ=WEEKLY;BYDAY=SA;UNTIL=" + endDate);
                            startActivity(aIntent);
                        }

                        if (prenotazioni.get(position).getGiorno().contains("Domenica")) {
                            aIntent.putExtra(CalendarContract.Events.RRULE,
                                    "FREQ=WEEKLY;BYDAY=SU;UNTIL=" + endDate);
                            startActivity(aIntent);
                        }
                    } else
                        startActivity(aIntent);
                }
            });

            holder.itemView.setTag(this.prenotazioni.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return this.prenotazioni.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView content1;
            final TextView content2;
            final TextView content3;
            final ImageView calendar;

            ViewHolder(View view) {
                super(view);
                content1 = view.findViewById(R.id.content);
                content2 = view.findViewById(R.id.content2);
                content3 = view.findViewById(R.id.content3);
                calendar = view.findViewById(R.id.buttonCalendar);

            }
        }
    }

    public static class RequestRecyclerViewAdapter
            extends RecyclerView.Adapter<RequestRecyclerViewAdapter.ViewHolder> {

        private final List<Prenotation> richieste;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prenotation item = (Prenotation) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, InfoPanel.class);
                intent.putExtra("Tipo", tipo);
                intent.putExtra("Lavoratore", item.getLavoratore());
                intent.putExtra("Cliente", item.getCliente());
                intent.putExtra("City", item.getCity());
                intent.putExtra("StartData", item.getStartDatatoSring());
                intent.putExtra("EndData", item.getEndDatatoSring());
                intent.putExtra("StartTime", item.getStartTimetoString());
                intent.putExtra("EndTime", item.getEndTimetoString());
                intent.putExtra("TipoLavoro", item.getTipoLavoro());
                intent.putExtra("TipoImpegno", item.getTipoImpegno());
                intent.putExtra("Giorni", item.getGiorno());
                context.startActivity(intent);
            }
        };

        RequestRecyclerViewAdapter(List<Prenotation> request) {
            this.richieste = request;
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_list_richieste, parent, false);
            return new RequestRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RequestRecyclerViewAdapter.ViewHolder holder,
                                     final int position) {
            String content1;
            if (tipo.equals("lavori"))
                content1 = "Cliente: " + this.richieste.get(position).getCliente().getNome()
                        + " " + this.richieste.get(position).getCliente().getCognome();
            else
                content1 = "Lavoratore: " + this.richieste.get(position).getLavoratore().getNome()
                        + " " + this.richieste.get(position).getLavoratore().getCognome();

            holder.content1.setText(content1);

            holder.content2.setText("Servizio: " + this.richieste.get(position).getTipoLavoro()
                    + " - " + this.richieste.get(position).getTipoImpegno());

            holder.content3.setText("CLICK PER MAGGIORI INFO");

            if (sezione == 1) {
                holder.btnAccetta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.btnAccetta.setVisibility(View.GONE);
                        holder.btnRifiuta.setVisibility(View.GONE);
                        holder.content4.setVisibility(View.VISIBLE);
                        holder.content4.setText("RICHIESTA ACCETTATA!");
                        Utilities.DATA_BASE
                                .collection("PRENOTAZIONI")
                                .document(richieste.get(position).getCodice())
                                .update("Conferma", "CONFERMATA");

                        String currentUserMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        String email;
                        if (tipo.equals("lavori"))
                            email = richieste.get(position).getCliente().getEmail();
                        else
                            email = richieste.get(position).getLavoratore().getEmail();

                        Map<String, Object> notificationMessage = new HashMap<>();
                        notificationMessage.put("message", "ciao");
                        notificationMessage.put("from", currentUserMail);

                        FirebaseFirestore.getInstance().collection("UTENTI/"+email+"/NOTIFICHE").add(notificationMessage);


                    }
                });

                holder.btnRifiuta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.btnAccetta.setVisibility(View.GONE);
                        holder.btnRifiuta.setVisibility(View.GONE);
                        holder.content4.setVisibility(View.VISIBLE);
                        holder.content4.setText("RICHIESTA RIFIUTATA!");
                        Utilities.DATA_BASE
                                .collection("PRENOTAZIONI")
                                .document(richieste.get(position).getCodice())
                                .update("Conferma", "RIFIUTATA");
                    }
                });
            } else if (sezione == 2) {
                holder.btnAccetta.setVisibility(View.GONE);
                holder.btnRifiuta.setVisibility(View.GONE);
                holder.content4.setText("IN ATTESA DI CONFERMA");
            }

            holder.itemView.setTag(this.richieste.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return this.richieste.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView content1;
            final TextView content2;
            final TextView content3;
            final TextView content4;
            final Button btnAccetta;
            final Button btnRifiuta;

            ViewHolder(View view) {
                super(view);
                content1 = view.findViewById(R.id.content);
                content2 = view.findViewById(R.id.content2);
                content3 = view.findViewById(R.id.content3);
                content4 = view.findViewById(R.id.content4);
                btnAccetta = view.findViewById(R.id.btn1);
                btnRifiuta = view.findViewById(R.id.btn2);
            }
        }
    }
}
