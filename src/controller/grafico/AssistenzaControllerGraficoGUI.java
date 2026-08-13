package controller.grafico;

import bean.AssistenzaBean;
import bean.EsitoOperazione;
import control.GestoreAssistenza;
import control.GestoreUtenti;
import model.Utente;

public class AssistenzaControllerGraficoGUI {

    private final GestoreAssistenza gestoreAssistenza;
    private final GestoreUtenti gestoreUtenti;

    public AssistenzaControllerGraficoGUI(GestoreAssistenza gestoreAssistenza, GestoreUtenti gestoreUtenti) {
        this.gestoreAssistenza = gestoreAssistenza;
        this.gestoreUtenti = gestoreUtenti;
    }

    public EsitoOperazione gestisciRichiesta(String nome, String email, String messaggio) {
        AssistenzaBean dati = new AssistenzaBean();
        Utente utente = gestoreUtenti.getUtenteLoggato();
        if (utente != null) {
            dati.setNome(utente.getName());
            dati.setEmail(utente.getEmail());
        } else {
            dati.setNome(nome);
            dati.setEmail(email);
        }
        dati.setMessaggio(messaggio);

        String erroreSintassi = dati.validaSintassi();
        if (erroreSintassi != null) {
            return EsitoOperazione.errore(erroreSintassi);
        }

        gestoreAssistenza.inviaRichiesta(dati);
        return EsitoOperazione.successo("Richiesta inviata, ti risponderemo il prima possibile.");
    }

    public String nomePrecompilato() {
        Utente utente = gestoreUtenti.getUtenteLoggato();
        return utente == null ? "" : utente.getName();
    }

    public String emailPrecompilata() {
        Utente utente = gestoreUtenti.getUtenteLoggato();
        return utente == null ? "" : utente.getEmail();
    }
}
