package util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import bean.PartecipanteVistaBean;
import bean.PrenotazioneVistaBean;

public final class Formattatore {

    private static final String NON_SPECIFICATA = "data di nascita non specificata";
    private static final String CF_NON_SPECIFICATO = "codice fiscale non specificato";

    private Formattatore() {
        // Utility class: non deve essere istanziata.
    }

    public static String formattaData(long millis, DateTimeFormatter formato) {
        return formato.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public static String descriviPartecipante(PartecipanteVistaBean partecipante, DateTimeFormatter formatoData) {
        String dataNascita = partecipante.hasDataNascita()
                ? formattaData(partecipante.getDataNascita(), formatoData)
                : NON_SPECIFICATA;
        String codiceFiscale = partecipante.hasCodiceFiscale()
                ? partecipante.getCodiceFiscale()
                : CF_NON_SPECIFICATO;
        return partecipante.getNome() + " " + partecipante.getCognome() + " - " + dataNascita + " - " + codiceFiscale;
    }

    public static String descriviPagamento(PrenotazioneVistaBean prenotazione) {
        return prenotazione.getDescrizionePagamento() + " (" + prenotazione.getImportoPagato() + " euro)";
    }
}
