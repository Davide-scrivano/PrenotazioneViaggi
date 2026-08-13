package bean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class PartecipanteBean {

    private static final DateTimeFormatter FORMATO_DATA_NASCITA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern PATTERN_CODICE_FISCALE =
            Pattern.compile("[A-Za-z]{6}\\d{2}[A-Za-z]\\d{2}[A-Za-z]\\d{3}[A-Za-z]");

    private String nome;
    private String cognome;
    private String dataNascita;
    private String codiceFiscale;

    public String validaSintassi(int numeroPartecipante) {
        if (nome == null || nome.isBlank()) {
            return "Inserisci il nome del partecipante " + numeroPartecipante + ".";
        }
        if (cognome == null || cognome.isBlank()) {
            return "Inserisci il cognome del partecipante " + numeroPartecipante + ".";
        }
        // Data di nascita e codice fiscale restano facoltativi: se lasciati vuoti
        // vengono mostrati come "non specificati" (vedi Formattatore). Se pero'
        // vengono compilati, devono avere un formato valido.
        if (dataNascita != null && !dataNascita.isBlank() && !dataNascitaValida(dataNascita)) {
            return "Data di nascita del partecipante " + numeroPartecipante + " non valida: usa il formato gg/mm/aaaa.";
        }
        if (codiceFiscale != null && !codiceFiscale.isBlank() && !PATTERN_CODICE_FISCALE.matcher(codiceFiscale.trim()).matches()) {
            return "Codice fiscale del partecipante " + numeroPartecipante + " non valido.";
        }
        return null;
    }

    private boolean dataNascitaValida(String testo) {
        try {
            LocalDate.parse(testo.trim(), FORMATO_DATA_NASCITA);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
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
