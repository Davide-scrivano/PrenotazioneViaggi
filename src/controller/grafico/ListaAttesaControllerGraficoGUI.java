package controller.grafico;

import bean.EsitoOperazione;
import bean.ListaAttesaBean;
import control.GestoreListaAttesa;
import control.GestoreUtenti;
import exceptions.IscrizioneListaAttesaNonConsentitaException;
import model.Utente;

public class ListaAttesaControllerGraficoGUI {

    private final GestoreListaAttesa gestoreListaAttesa;
    private final GestoreUtenti gestoreUtenti;

    public ListaAttesaControllerGraficoGUI(GestoreListaAttesa gestoreListaAttesa, GestoreUtenti gestoreUtenti) {
        this.gestoreListaAttesa = gestoreListaAttesa;
        this.gestoreUtenti = gestoreUtenti;
    }

    public EsitoOperazione iscriviListaAttesa(int idPacchetto, int numeroPosti) {
        Utente utente = gestoreUtenti.getUtenteLoggato();
        if (utente == null) {
            return EsitoOperazione.errore("Devi effettuare il login per iscriverti alla lista d'attesa.");
        }

        ListaAttesaBean dati = new ListaAttesaBean();
        dati.setIdPacchetto(idPacchetto);
        dati.setNumeroPosti(numeroPosti);

        String erroreSintassi = dati.validaSintassi();
        if (erroreSintassi != null) {
            return EsitoOperazione.errore(erroreSintassi);
        }

        try {
            gestoreListaAttesa.iscrivi(utente, dati);
            return EsitoOperazione.successo(
                    "Iscrizione alla lista d'attesa effettuata: sarai avvisato appena si liberano posti.");
        } catch (IscrizioneListaAttesaNonConsentitaException e) {
            return EsitoOperazione.errore(e.getMessage());
        }
    }
}
