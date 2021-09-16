package com.social.socialjobs.lavori;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.social.socialjobs.R;
import com.social.socialjobs.addon.Utilities;
import com.social.socialjobs.prenotazioni.PrenotazioniDrawer;
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
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;

public class FormAggiuntaLavoro extends Utilities implements AdapterView.OnItemSelectedListener {

    private EditText nome;
    private EditText cognome;
    private EditText city;
    private EditText email;
    private Spinner work;
    private TextView editStartData;
    private TextView editEndData;
    private TextView editStartTime;
    private TextView editEndTime, editEndTimeSett;

    private TextInputLayout nomeError;
    private TextInputLayout cognomeError;
    private TextInputLayout cityError;
    private TextInputLayout emailError;
    private boolean sent = true;

    private LinearLayout settimana;
    private RadioButton rbSettimanale;
    private RadioButton rbOccasionale;
    private String tipoImpegno;
    private CheckBox lun, mar, mer, gio, ven, sab, dom;
    private String selectedDays = "";

    private String selectedStartData = "";
    private String selectedEndData = "";
    private int year;
    private int month;
    private int day;
    private boolean sentStartData = false;
    private boolean sentEndData = false;

    private String selectedStartTime;
    private String selectedEndTime;
    private int hour;
    private int minute;
    private boolean sentStartTime= false;
    private boolean sentEndTime = false;
    private boolean sentEndTimeSett = false;

    boolean isStartD = false;
    boolean isStartT = false;

    ArrayList<String> categories = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add_lavoro);
        this.setBackArrow();
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        ScrollView layout = this.findViewById(R.id.activity_lavoro);
        layout.setOnTouchListener(this);

        this.setInputID();
        this.setErrorsID();

        this.work.setOnItemSelectedListener(this);




        DocumentReference docRef = DATA_BASE.collection("UTENTI").document(AUTH_STATUS.getCurrentUser().getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String servizi = document.get("Servizi").toString();
                        if (servizi.charAt(0) == '1')
                            categories.add("Babysitter");
                        if (servizi.charAt(1) == '1')
                            categories.add("Dogsitter");
                        if (servizi.charAt(2) == '1')
                            categories.add("Colf");
                        if (servizi.charAt(3) == '1')
                            categories.add("Infermiere");
                        if (servizi.charAt(4) == '1')
                            categories.add("Badante");
                        if (servizi.charAt(5) == '1')
                            categories.add("Commissioni");
                        if (servizi.charAt(6) == '1')
                            categories.add("Compagnia");
                        if (servizi.charAt(7) == '1')
                            categories.add("Ripetizioni");

                        // Creating adapter for spinner
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.my_spinner, categories);

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(R.layout.my_spinner);

                        // attaching data adapter to spinner
                        work.setAdapter(dataAdapter);
                    }
                }
            }
        });

        this.rbOccasionale = findViewById(R.id.occ);
        this.rbOccasionale.setOnClickListener(new View.OnClickListener() {
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
                else
                    editStartData.setText(" Data inizio: " + selectedStartData);
                if (selectedEndData.equals(""))
                    editEndData.setText(" Data fine ");
                else
                    editEndData.setText(" Data fine : " + selectedEndData);

            }
        });

        this.rbSettimanale = findViewById(R.id.sett);
        this.rbSettimanale.setOnClickListener(new View.OnClickListener() {
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
                else
                    editStartData.setText(" Data inizio servizio : " + selectedStartData);
                if (selectedEndData.equals(""))
                    editEndData.setText(" Data fine servizio ");
                else
                    editEndData.setText(" Data fine servizio: " + selectedEndData);

            }
        });

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

        Button btnAdd = this.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
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

                inputValidation();
            }
        });
    }

    //Da gestire il fatto se il lavoro è occasionale o settimanale come in AddReservation
    private void addPrenotazione() {
        final String emailWorker = AUTH_STATUS.getCurrentUser().getEmail();
        DocumentReference docRef = DATA_BASE.collection("UTENTI").document(emailWorker);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> newJob = new HashMap<>();
                        newJob.put("Email lavoratore", emailWorker.trim());
                        newJob.put("Nome lavoratore", document.get("Nome").toString());
                        newJob.put("Cognome lavoratore", document.get("Cognome").toString());
                        newJob.put("Nome cliente", nome.getText().toString().trim());
                        newJob.put("Cognome cliente", cognome.getText().toString().trim());
                        newJob.put("Email cliente", email.getText().toString().trim());
                        newJob.put("Città", city.getText().toString().trim());
                        newJob.put("Lavoro", work.getSelectedItem().toString());
                        newJob.put("DataInizio", selectedStartData);
                        newJob.put("DataFine", selectedEndData);
                        newJob.put("OraInizio", selectedStartTime);
                        newJob.put("OraFine", selectedEndTime);
                        newJob.put("Giorno", selectedDays);
                        newJob.put("TipoImpegno", tipoImpegno);
                        newJob.put("Conferma", "IN_ATTESA");

                        DATA_BASE
                                .collection("PRENOTAZIONI")
                                .document()
                                .set(newJob);
                    }
                }
            }
        });
    }

    private void inputValidation() {
        final boolean validName = this.checkFieldName(this.nome, this.nomeError);

        final boolean validSurname = this.checkFieldName(this.cognome, this.cognomeError);

        final boolean validCity = this.checkFieldName(this.city, this.cityError);

        final boolean validEmail = this.checkEmail(this.email, this.emailError);

        if (validEmail) {
            DocumentReference docRef = DATA_BASE.collection("UTENTI")
                    .document(this.email.getText().toString().trim());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            emailError.setError("L'email del cliente non esiste");
                            sent = false;
                        } else {
                            sent = true;
                            if (sent && validName && validSurname && validCity && sentStartData && sentStartTime && sentEndData &&
                                    ((tipoImpegno.equals("Settimanale") && sentEndTimeSett && (selectedDays.length() > 0)) ||
                                            (tipoImpegno.equals("Occasionale") && sentEndTime))) {

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                Date di;
                                Date de;
                                Boolean timeOk = false;
                                try {
                                    di = sdf.parse(selectedStartData + " " + selectedStartTime);
                                    de = sdf.parse(selectedEndData + " " + selectedEndTime);
                                    if (de.compareTo(di) < 0) {
                                        Toast.makeText(getApplicationContext(), "Data e ora di fine devono essere posteriori a quella di inizio", Toast.LENGTH_LONG).show();
                                    } else {
                                        timeOk = true;
                                    }
                                } catch (ParseException e) {
                                }
                                if (timeOk) {
                                    addPrenotazione();
                                    Toast.makeText(getApplicationContext(), "Lavoro aggiunto correttamente", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), PrenotazioniDrawer.class));
                                    finish();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void setInputID() {
        this.nome = this.findViewById(R.id.input_nome);
        this.cognome = this.findViewById(R.id.input_cognome);
        this.city = this.findViewById(R.id.input_city);
        this.email = this.findViewById(R.id.input_email);
        this.work = findViewById(R.id.spinner_lavori);

        this.editStartData = findViewById(R.id.input_data);
        this.editEndData = findViewById(R.id.input_data2);
        this.editStartTime = findViewById(R.id.input_orario);
        this.editEndTime = findViewById(R.id.input_orario2);
        this.editEndTimeSett = findViewById(R.id.input_orario2_sett);
        this.lun = findViewById(R.id.lun);
        this.mar = findViewById(R.id.mar);
        this.mer = findViewById(R.id.mer);
        this.gio = findViewById(R.id.gio);
        this.ven = findViewById(R.id.ven);
        this.sab = findViewById(R.id.sab);
        this.dom = findViewById(R.id.dom);
        this.settimana = findViewById(R.id.layoutSettimana);
    }

    private void setErrorsID() {
        this.nomeError = this.findViewById(R.id.name_err);
        this.cognomeError = this.findViewById(R.id.cognome_err);
        this.cityError = this.findViewById(R.id.address_err);
        this.emailError = this.findViewById(R.id.email_err);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }
    public void onNothingSelected(AdapterView<?> arg0) { }

    private void createDataPicker() {
        Calendar mcurrentDate = Calendar.getInstance();
        year = mcurrentDate.get(Calendar.YEAR);
        month = mcurrentDate.get(Calendar.MONTH);
        day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDatePicker
                = new DatePickerDialog(FormAggiuntaLavoro.this,
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
                = new TimePickerDialog(FormAggiuntaLavoro.this,
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
                        } else {
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

