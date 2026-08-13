package controller.grafico;

import bean.EsitoOperazione;
import bean.PacchettoBean;
import control.Catalogo;

public class PacchettoControllerGraficoCLI {

    private static final String MESSAGGIO_NON_TROVATO = "Pacchetto non trovato.";

    private final Catalogo catalogo = Catalogo.getInstance();

    public EsitoOperazione aggiungiPacchetto(PacchettoBean dati) {
        String erroreSintassi = dati.validaSintassi();
        if (erroreSintassi != null) {
            return EsitoOperazione.errore(erroreSintassi);
        }
        int id = catalogo.aggiungiPacchetto(dati);
        return EsitoOperazione.successo("Pacchetto aggiunto con ID #" + id + ".");
    }

    public EsitoOperazione modificaPacchetto(int idPacchetto, PacchettoBean dati) {
        String erroreSintassi = dati.validaSintassi();
        if (erroreSintassi != null) {
            return EsitoOperazione.errore(erroreSintassi);
        }
        return catalogo.modificaPacchetto(idPacchetto, dati)
                ? EsitoOperazione.successo("Pacchetto #" + idPacchetto + " aggiornato.")
                : EsitoOperazione.errore(MESSAGGIO_NON_TROVATO);
    }

    public EsitoOperazione rimuoviPacchetto(int idPacchetto) {
        return catalogo.rimuoviPacchetto(idPacchetto)
                ? EsitoOperazione.successo("Pacchetto rimosso.")
                : EsitoOperazione.errore(MESSAGGIO_NON_TROVATO);
    }
}
