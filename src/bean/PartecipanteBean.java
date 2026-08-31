package bean;

import java.util.regex.Pattern;

import util.Formattatore;

public class PartecipanteBean {

    private static final Pattern PATTERN_CODICE_FISCALE =
            Pattern.compile("[A-Za-z]{6}\\d{2}[A-Za-z]\\d{2}[A-Za-z]\\d{3}[A-Za-z]");

    private String nome;
    private String cognome;
    private String dataNascita;
    private String codiceFiscale;

    public String validaSintassi(int numeroPartecipante) {
        if (vuoto(nome)) {
            return "Inserisci il nome del partecipante " + numeroPartecipante + ".";
        }
        if (vuoto(cognome)) {
            return "Inserisci il cognome del partecipante " + numeroPartecipante + ".";
        }
        if (!vuoto(dataNascita) && !Formattatore.formatoDataValido(dataNascita)) {
            return "Data di nascita del partecipante " + numeroPartecipante
                    + " non valida: usa il formato " + Formattatore.FORMATO_LEGGIBILE + ".";
        }
        if (!vuoto(codiceFiscale) && !PATTERN_CODICE_FISCALE.matcher(codiceFiscale.trim()).matches()) {
            return "Codice fiscale del partecipante " + numeroPartecipante + " non valido.";
        }
        return null;
    }

    public long getDataNascitaInMillis() {
        return Formattatore.millisDaTesto(dataNascita);
    }

    private boolean vuoto(String valore) {
        return valore == null || valore.isBlank();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }
}
