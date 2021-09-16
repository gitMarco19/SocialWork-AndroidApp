package com.social.socialjobs.prenotazioni;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.social.socialjobs.profilo.FromProfileToUpdate;
import com.social.socialjobs.HomePageDrawer;
import com.social.socialjobs.R;
import com.social.socialjobs.addon.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddReservation extends Utilities implements AdapterView.OnItemSelectedListener {
    private static final int REQUEST_CALL = 1;

    private TextView titolo, editStartData, editEndData, contatta, editStartTime, editEndTime, editEndTimeSett;

    private String email;

    private RadioButton rbSettimanale, rbOccasionale;
    String tipoImpegno;

    private CheckBox lun, mar, mer, gio, ven, sab, dom;
    private String selectedDays = "";

    private Spinner spinner;

    private Button buttonSendReservation;

    private LinearLayout settimana;

    private String selectedStartData = "";
    private String selectedEndData = "";

    private int year;
    private int month;
    private int day;
    private boolean sentStartData = false;
    private boolean sentEndData = false;
    private boolean sentStartTime= false;
    private boolean sentEndTime = false;
    private boolean sentEndTimeSett = false;


    private String selectedStartTime, selectedEndTime;
    private int hour;
    private int minute;

    Boolean isStartD = false;
    Boolean isStartT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.add_reservation);

        this.setBackArrow();
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        titolo = (TextView) findViewById(R.id.title);
        titolo.setText("Richiedi una prenotazione!");

        contatta = (TextView) findViewById(R.id.contatta);
        contatta.setText("Ti consigliamo di contattare " + FromProfileToUpdate.nome + " prima di prenotare");

        ImageButton call = (ImageButton) findViewById(R.id.buttonCall);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        email = getIntent().getStringExtra("Email");

        ImageButton sendMail = (ImageButton) findViewById(R.id.buttonMail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

        //Aggiunto
        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner_lavori);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();

        if (FromProfileToUpdate.servizi.charAt(0) == '1')
            categories.add("Babysitter");
        if (FromProfileToUpdate.servizi.charAt(1) == '1')
            categories.add("Dogsitter");
        if (FromProfileToUpdate.servizi.charAt(2) == '1')
            categories.add("Colf");
        if (FromProfileToUpdate.servizi.charAt(3) == '1')
            categories.add("Infermiere");
        if (FromProfileToUpdate.servizi.charAt(4) == '1')
            categories.add("Badante");
        if (FromProfileToUpdate.servizi.charAt(5) == '1')
            categories.add("Commissioni");
        if (FromProfileToUpdate.servizi.charAt(6) == '1')
            categories.add("Compagnia");
        if (FromProfileToUpdate.servizi.charAt(7) == '1')
            categories.add("Ripetizioni");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.my_spinner, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.my_spinner);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        rbOccasionale = (RadioButton) findViewById(R.id.occ);
        rbOccasionale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartD = false;
                isStartT = false;
                rbSettimanale.setChecked(false);
                editEndTimeSett.setVisibility(View.GONE);
                editEndTime.setVisibility(View.VISIBLE);
                settimana.setVisibility(View.GONE);
                if (selectedStartData.equals(""))
                    editStartData.setText(" Data inizio ");
                else if (!selectedStartData.equals(""))
                    editStartData.setText(" Data inizio: " + selectedStartData);
                if (selectedEndData.equals(""))
                    editEndData.setText(" Data fine ");
                else if (!selectedEndData.equals(""))
                    editEndData.setText(" Data fine : " + selectedEndData);

            }
        });



        rbSettimanale = (RadioButton) findViewById(R.id.sett);
        rbSettimanale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartD = false;
                isStartT = false;
                rbOccasionale.setChecked(false);
                editEndTimeSett.setVisibility(View.VISIBLE);
                settimana.setVisibility(View.VISIBLE);
                editEndTime.setVisibility(View.GONE);
                if (selectedStartData.equals(""))
                    editStartData.setText(" Data inizio servizio ");
                else if (!selectedStartData.equals(""))
                    editStartData.setText(" Data inizio servizio : " + selectedStartData);
                if (selectedEndData.equals(""))
                    editEndData.setText(" Data fine servizio ");
                else
                    editEndData.setText(" Data fine servizio: " + selectedEndData);

            }
        });


        this.editStartData = (TextView) findViewById(R.id.input_data);
        this.editEndData = (TextView) findViewById(R.id.input_data2);
        this.editStartTime = (TextView) findViewById(R.id.input_orario);
        this.editEndTime = (TextView) findViewById(R.id.input_orario2);
        this.editEndTimeSett = (TextView)  findViewById(R.id.input_orario2_sett);
        this.lun = (CheckBox) findViewById(R.id.lun);
        this.mar = (CheckBox) findViewById(R.id.mar);
        this.mer = (CheckBox) findViewById(R.id.mer);
        this.gio = (CheckBox) findViewById(R.id.gio);
        this.ven = (CheckBox) findViewById(R.id.ven);
        this.sab = (CheckBox) findViewById(R.id.sab);
        this.dom = (CheckBox) findViewById(R.id.dom);
        this.settimana = (LinearLayout) findViewById(R.id.layoutSettimana);

        if (rbOccasionale.isChecked()) {
            settimana.setVisibility(View.GONE);
            editEndTimeSett.setVisibility(View.GONE);
            editEndTime.setVisibility(View.VISIBLE);
        }
        else {
            editEndTime.setVisibility(View.GONE);
        }


        this.editStartData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartD = true;
                createDataPicker();
            }
        });

        this.editEndData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartD = false;
                createDataPicker();
            }
        });

        this.editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartT = true;
                createTimePicker();
            }
        });

        this.editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartT = false;
                createTimePicker();
            }
        });

        this.editEndTimeSett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartT = false;
                createTimePicker();
            }
        });



        buttonSendReservation = (Button) findViewById(R.id.btnAdd);
        buttonSendReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbSettimanale.isChecked()) {
                    tipoImpegno = "Settimanale";
                    if (!sentEndTimeSett)
                        editEndTimeSett.setText("Orario fine obbligatorio!");
                    selectedDays = "";
                    if (lun.isChecked())
                        selectedDays = selectedDays + "Lunedi";
                    if (mar.isChecked())
                        selectedDays = selectedDays + "Martedi";
                    if (mer.isChecked())
                        selectedDays = selectedDays + "Mercoledi";
                    if (gio.isChecked())
                        selectedDays = selectedDays + "Giovedi";
                    if (ven.isChecked())
                        selectedDays = selectedDays + "Venerdi";
                    if (sab.isChecked())
                        selectedDays = selectedDays + "Sabato";
                    if (dom.isChecked())
                        selectedDays = selectedDays + "Domenica";
                } else {
                    tipoImpegno = "Occasionale";
                    if (!sentEndTime)
                        editEndTime.setText("Orario fine obbligatorio!");
                }
                if (tipoImpegno.equals("Settimanale") && selectedDays.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Selezionare un giorno della settimana", Toast.LENGTH_SHORT).show();
                }
                if (!sentStartData)
                    editStartData.setText("Data inizio obbligatoria!");
                if (!sentStartTime)
                    editStartTime.setText("Orario inizio obbligatorio!");
                if (!sentEndData)
                    editEndData.setText("Data fine obbligatoria!");

                if (sentStartData && sentStartTime && sentEndData &&
                        ((tipoImpegno.equals("Settimanale") && sentEndTimeSett && (selectedDays.length()>0)) ||
                                (tipoImpegno.equals("Occasionale") && sentEndTime))) {

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date di = null;
                    Date de = null;
                    Boolean timeOk = false;
                    try {
                        di = sdf.parse(selectedStartData+ " "+selectedStartTime);
                        de = sdf.parse(selectedEndData+ " "+selectedEndTime);
                        if (de.compareTo(di) < 0) {
                            Toast.makeText(getApplicationContext(), "Data e ora di fine devono essere posteriori a quella di inizio", Toast.LENGTH_LONG).show();
                        }
                        else {
                            timeOk = true;
                        }
                    } catch (ParseException e) {}
                    if (timeOk) {
                        addRichiesta();
                        Toast.makeText(getApplicationContext(), "Richiesta di prenotazione inviata", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomePageDrawer.class));
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }
            else{
                Toast.makeText(this, "Permesso negato", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makePhoneCall(){
        if (FromProfileToUpdate.telefono.length() > 0) {
            if (ContextCompat.checkSelfPermission(AddReservation.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddReservation.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + FromProfileToUpdate.telefono;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(AddReservation.this, "Numero di telefono non presente", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EMAIL DA SOCIALJOBS");
        startActivity(Intent.createChooser(emailIntent, "Invia con"));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView<?> arg0) { }

    private void addRichiesta() {
        final String emailClient = AUTH_STATUS.getCurrentUser().getEmail();
        DocumentReference docRef = DATA_BASE.collection("UTENTI").document(emailClient);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> richiesta = new HashMap<>();
                        richiesta.put("Email lavoratore", email);
                        richiesta.put("Nome lavoratore", FromProfileToUpdate.nome);
                        richiesta.put("Cognome lavoratore", FromProfileToUpdate.cognome);
                        richiesta.put("Nome cliente", document.get("Nome").toString());
                        richiesta.put("Cognome cliente", document.get("Cognome").toString());
                        richiesta.put("Email cliente", emailClient);
                        richiesta.put("Città", document.get("Città").toString());
                        richiesta.put("Lavoro", spinner.getSelectedItem().toString());
                        richiesta.put("DataInizio", selectedStartData);
                        richiesta.put("DataFine", selectedEndData);
                        richiesta.put("OraInizio", selectedStartTime);
                        richiesta.put("OraFine", selectedEndTime);
                        richiesta.put("Giorno", selectedDays);
                        richiesta.put("TipoImpegno", tipoImpegno);
                        richiesta.put("Conferma", "DA_CONFERMARE");

                        DATA_BASE
                                .collection("PRENOTAZIONI")
                                .document()
                                .set(richiesta);
                    }
                }
            }
        });
    }

    private void createDataPicker() {
        Calendar mcurrentDate = Calendar.getInstance();
        year = mcurrentDate.get(Calendar.YEAR);
        month = mcurrentDate.get(Calendar.MONTH);
        day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDatePicker
                = new DatePickerDialog(AddReservation.this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
                        if (isStartD)
                            selectedStartData = sdf.format(myCalendar.getTime());
                        else
                            selectedEndData = sdf.format(myCalendar.getTime());

                        if (rbOccasionale.isChecked())
                            if (isStartD)
                                editStartData.setText(" Data inizio: " + selectedStartData);
                            else
                                editEndData.setText(" Data fine: " + selectedEndData);
                        else
                        if (isStartD)
                            editStartData.setText(" Data inizio servizio: " + selectedStartData);
                        else
                            editEndData.setText(" Data fine servizio: " + selectedEndData);


                        day = selectedday;
                        month = selectedmonth;
                        year = selectedyear;
                        if (isStartD)
                            sentStartData = true;
                        else
                            sentEndData = true;
                    }
                }, year, month, day);
        mDatePicker.show();
    }

    private void createTimePicker() {
        Calendar currentTime = Calendar.getInstance();
        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timePicker
                = new TimePickerDialog(AddReservation.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker aTimePicker, int selectedHour, int selectedMinute) {
                        Calendar myTime = Calendar.getInstance();
                        myTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myTime.set(Calendar.MINUTE, selectedMinute);

                        String myFormat = "HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);

                        if (isStartT) {
                            selectedStartTime = sdf.format(myTime.getTime());
                            editStartTime.setText(" Orario inizio: " + selectedStartTime);
                        }
                        else {
                            selectedEndTime = sdf.format(myTime.getTime());
                            if (rbOccasionale.isChecked())
                                editEndTime.setText(" Orario fine: " + selectedEndTime);
                            else
                                editEndTimeSett.setText("Orario fine: " + selectedEndTime);
                        }
                        hour = selectedHour;
                        minute = selectedMinute;
                        if (isStartT)
                            sentStartTime = true;
                        else {
                            if (rbOccasionale.isChecked())
                                sentEndTime = true;
                            else
                                sentEndTimeSett = true;
                        }
                    }
                }, hour, minute, true);
        timePicker.show();
    }
}
