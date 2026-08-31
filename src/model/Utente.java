package model;

import model.valori.PeriodoViaggio;
import model.valori.TipoUtente;
import java.util.List;

public class Utente {

    private final int id;
    private final String nickname;
    private final String nome;
    private final String cognome;
    private final String email;
    private final String password;
    private final TipoUtente tipo;

    public Utente(int id, String nickname, String nome, String cognome, String email, String password,
            TipoUtente tipo) {
        this.id = id;
        this.nickname = nickname;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.tipo = tipo;
    }

    public boolean credenzialiValide(String passwordInserita) {
        return password.equals(passwordInserita);
    }

    public boolean isAgenzia() {
        return tipo == TipoUtente.AGENZIA;
    }

    public TipoUtente getTipo() {
        return tipo;
    }

    public String nominativo() {
        return nome + " " + cognome;
    }

    public Prenotazione prenota(int idPrenotazione, Pacchetto pacchetto, Pagamento pagamento,
            PeriodoViaggio periodo, List<Partecipante> partecipanti) {
        return Prenotazione.nuova(idPrenotazione, this, pacchetto, pagamento, periodo, partecipanti);
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object altro) {
        if (this == altro) {
            return true;
        }
        if (!(altro instanceof Utente)) {
            return false;
        }
        return id == ((Utente) altro).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
