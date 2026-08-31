package notifica;

import model.Prenotazione;

public class NotificatorePrenotazioni extends SoggettoPrenotazione {

    private Prenotazione prenotazione = null;

    public synchronized Prenotazione getPrenotazione() {
        return prenotazione;
    }

    public void confermaPrenotazione(Prenotazione daConfermare) {
        impostaPrenotazione(daConfermare);
        notificaOsservatori();
    }

    private synchronized void impostaPrenotazione(Prenotazione daConfermare) {
        this.prenotazione = daConfermare;
    }
}
