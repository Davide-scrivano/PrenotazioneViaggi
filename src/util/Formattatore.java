package util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import bean.PartecipanteVistaBean;

public final class Formattatore {

    public static final String FORMATO_LEGGIBILE = "gg/mm/aaaa";

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String DATA_NON_SPECIFICATA = "data di nascita non specificata";
    private static final String CODICE_FISCALE_NON_SPECIFICATO = "codice fiscale non specificato";

    private Formattatore() {
    }

    public static boolean formatoDataValido(String testo) {
        try {
            LocalDate.parse(testo.trim(), FORMATO_DATA);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static long millisDaTesto(String testo) {
        if (testo == null || testo.isBlank() || !formatoDataValido(testo)) {
            return 0L;
        }
        return LocalDate.parse(testo.trim(), FORMATO_DATA)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String testoDaMillis(long millis) {
        return FORMATO_DATA.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public static long millisDaData(LocalDate data) {
        return data.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate dataDaMillis(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String descriviPartecipante(PartecipanteVistaBean partecipante) {
        String dataNascita = partecipante.getDataNascita() > 0
                ? testoDaMillis(partecipante.getDataNascita())
                : DATA_NON_SPECIFICATA;
        String codiceFiscale = partecipante.getCodiceFiscale() == null || partecipante.getCodiceFiscale().isBlank()
                ? CODICE_FISCALE_NON_SPECIFICATO
                : partecipante.getCodiceFiscale();
        return partecipante.getNominativo() + " - " + dataNascita + " - " + codiceFiscale;
    }
}
