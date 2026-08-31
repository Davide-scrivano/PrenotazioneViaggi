package model;

import model.valori.DatiAnagrafici;

public class Partecipante {

    private final int id;
    private final String nome;
    private final String cognome;
    private final DatiAnagrafici datiAnagrafici;

    public Partecipante(int id, String nome, String cognome, DatiAnagrafici datiAnagrafici) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.datiAnagrafici = datiAnagrafici;
    }

    public String nominativo() {
        return nome + " " + cognome;
    }

    public boolean haDataNascita() {
        return datiAnagrafici.getDataNascita() > 0;
    }

    public boolean haCodiceFiscale() {
        return !datiAnagrafici.getCodiceFiscale().isBlank();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public long getDataNascita() {
        return datiAnagrafici.getDataNascita();
    }

    public String getCodiceFiscale() {
        return datiAnagrafici.getCodiceFiscale();
    }

    @Override
    public boolean equals(Object altro) {
        if (this == altro) {
            return true;
        }
        if (!(altro instanceof Partecipante)) {
            return false;
        }
        return id == ((Partecipante) altro).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
