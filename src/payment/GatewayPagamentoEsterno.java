package payment;

class GatewayPagamentoEsterno {

    private static final String ESITO_NEGATIVO = "KO";
    private static final String PREFISSO_AUTORIZZAZIONE = "AUTH-";
    private static final String STRUMENTO_NON_ACCETTATO = "0000";

    public String richiediAutorizzazione(long importoInCentesimi, String riferimento,
            String strumento, String codiceSicurezza) {
        if (importoInCentesimi <= 0 || !strumentoAccettato(strumento) || vuoto(codiceSicurezza)) {
            return ESITO_NEGATIVO;
        }
        return PREFISSO_AUTORIZZAZIONE + riferimento;
    }

    public boolean confermaAddebito(String codiceAutorizzazione) {
        return codiceAutorizzazione != null && codiceAutorizzazione.startsWith(PREFISSO_AUTORIZZAZIONE);
    }

    public static boolean autorizzazioneNegata(String codice) {
        return ESITO_NEGATIVO.equals(codice);
    }

    private static boolean strumentoAccettato(String strumento) {
        return !vuoto(strumento) && !strumento.endsWith(STRUMENTO_NON_ACCETTATO);
    }

    private static boolean vuoto(String valore) {
        return valore == null || valore.isBlank();
    }
}
