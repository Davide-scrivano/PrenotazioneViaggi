package model;

import java.util.ArrayList;
import java.util.List;

public class Utente {

    private int id;
    private String nickname;
    private String name;
    private String surname;
    private String email;
    private String password;
    private List<Prenotazione> prenotazioniEffettuate;

    public Utente(int id, String nickname, String name, String surname, String email, String password) {
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
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

    public List<Prenotazione> getPrenotazioniEffettuate() {
        return prenotazioniEffettuate;
    }

    public void aggiungiPrenotazione(Prenotazione prenotazione) {
        this.prenotazioniEffettuate.add(prenotazione);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente utente = (Utente) o;
        return id == utente.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}