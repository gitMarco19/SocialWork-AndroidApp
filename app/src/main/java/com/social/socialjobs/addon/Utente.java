package com.social.socialjobs.addon;

import java.io.Serializable;

/**
 * Created by Marco on 11/01/2020.
 *
 * Java class che identifica un utente.
 */
public class Utente implements Serializable {

    private String email;
    private String nome;
    private String cognome;
    private String sesso;
    private String city;
    private String descrizione;
    private String telefono;
    private String profileImage;
    private String coverImage;
    private String servizi;

    public Utente (String email, String nome, String cognome, String city,
                   String telefono, String sesso, String servizi,
                   String anImage1, String anImage2, String descrizione) {
        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.city = city;
        this.profileImage = anImage1;
        this.coverImage= anImage2;
        this.servizi = servizi;
        this.descrizione = descrizione;
        this.sesso = sesso;
    }

    Utente(Utente anUser) {
        this.email = anUser.email;
        this.nome = anUser.nome;
        this.cognome = anUser.cognome;
        this.telefono = anUser.telefono;
        this.city = anUser.city;
        this.profileImage = anUser.profileImage;
        this.coverImage= anUser.coverImage;
        this.servizi = anUser.servizi;
        this.descrizione = anUser.descrizione;
        this.sesso = anUser.sesso;
    }

    public Utente() {}

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCognome() {
        return this.cognome;
    }

    public String getSesso() {
        return this.sesso;
    }

    public String getCity() {
        return this.city;
    }

    public String getDescrizione() {
        return this.descrizione;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public String getProfileImageToString() {
        return this.profileImage;
    }

    public String getServizi() {
        return this.servizi;
    }
}

