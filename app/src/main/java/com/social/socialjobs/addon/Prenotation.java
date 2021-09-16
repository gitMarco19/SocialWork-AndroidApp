package com.social.socialjobs.addon;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Marco on 23/01/2020.
 *
 * Java class che identifica una prenotazione.
 */
public class Prenotation {

    private String codice;
    private Utente lavoratore;
    private Utente cliente;
    private Date dataInizio;
    private Date dataFine;
    private Date oraInizio;
    private Date oraFine;
    private String tipoLavoro;
    private String city;
    private String tipoImpegno;
    private String giorno;
    private String conferma;

    public Prenotation(String codice, Utente user1, Utente user2, String dataInizio, String dataFine, String oraInizio, String oraFine,
                       String lavoro, String city, String tipoImpegno, String giorno, String conferma) {
        this.lavoratore = new Utente(user1);
        this.cliente = new Utente(user2);
        try {
            this.dataInizio = new SimpleDateFormat("dd/MM/yyyy").parse(dataInizio);
        } catch (ParseException eccezione) {
            eccezione.getCause();
        }

        try {
            this.dataFine = new SimpleDateFormat("dd/MM/yyyy").parse(dataFine);
        } catch (ParseException eccezione) {
            eccezione.getCause();
        }

        try {
            this.oraInizio = new SimpleDateFormat("HH:mm").parse(oraInizio);
        } catch (ParseException eccezione) {
            eccezione.getCause();
        }

        try {
            this.oraFine = new SimpleDateFormat("HH:mm").parse(oraFine);
        } catch (ParseException eccezione) {
            eccezione.getCause();
        }
        this.tipoLavoro = lavoro;
        this.city = city;
        this.conferma = conferma;
        this.tipoImpegno = tipoImpegno;
        this.giorno = giorno;
        this.codice = codice;
    }

    public Utente getLavoratore() {
        return this.lavoratore;
    }

    public Utente getCliente() {
        return cliente;
    }

    public Date getDataInizio() {
        return this.dataInizio;
    }

    public Date getDataFine() {
        return this.dataFine;
    }

    public String getStartDatatoSring() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        return sdf.format(this.dataInizio);
    }

    public String getEndDatatoSring() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        return sdf.format(this.dataFine);
    }


    public Date getStartTime() {
        return this.oraInizio;
    }
    public Date getEndTime() {
        return this.oraFine;
    }



    public String getStartTimetoString() {
        String myFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        return sdf.format(oraInizio);
    }

    public String getEndTimetoString() {
        String myFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        return sdf.format(oraFine);
    }

    public String getTipoLavoro() {
        return this.tipoLavoro;
    }

    public String getCity() {
        return this.city;
    }

    public String getTipoImpegno() {
        return this.tipoImpegno;
    }

    public String getGiorno() {
        return this.giorno;
    }

    public void setConferma(String conferma) {
        this.conferma = conferma;
    }

    public String getConferma() {
        return this.conferma;
    }

    public String getCodice() {
        return this.codice;
    }
}

