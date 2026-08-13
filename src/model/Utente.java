package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utente {

    private int id;
    private String nickname;
    private String name;
    private String surname;
    private String email;
    private String password;
    private long dataNascita;
    private String codiceFiscale;
    private TipoUtente tipo;
    private List<Prenotazione> prenotazioniEffettuate;

    public Utente(int id, String nickname, String name, String surname, String email, String password) {
        this(id, nickname, name, surname, email, password, new DatiAnagrafici(0L, ""));
    }

    public boolean isAgenzia() {
        return tipo == TipoUtente.AGENZIA;
    }

    public Utente(int id, String nickname, String name, String surname, String email, String password,
            DatiAnagrafici datiAnagrafici) {
        this(id, nickname, name, surname, email, password, TipoUtente.CONSUMER);
        this.dataNascita = datiAnagrafici.getDataNascita();
        this.codiceFiscale = datiAnagrafici.getCodiceFiscale();
    }

    public Utente(int id, String nickname, String name, String surname, String email, String password,
            TipoUtente tipo) {
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.tipo = tipo;
        this.dataNascita = 0L;
        this.codiceFiscale = "";
        this.prenotazioniEffettuate = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public long getDataNascita() {
        return dataNascita;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public TipoUtente getTipo() {
        return tipo;
    }

    public List<Prenotazione> getPrenotazioniEffettuate() {
        return Collections.unmodifiableList(prenotazioniEffettuate);
    }

    public void aggiungiPrenotazione(Prenotazione prenotazione) {
        this.prenotazioniEffettuate.add(prenotazione);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Utente)) {
            return false;
        }
        Utente utente = (Utente) o;
        return id == utente.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}