package controller.grafico;

import bean.EsitoRegistrazione;
import bean.RegistrazioneBean;
import control.GestoreUtenti;
import exceptions.RegistrazioneNonConsentitaException;
import model.Utente;

public class RegistrazioneControllerGraficoGUI {

    private final GestoreUtenti gestoreUtenti;

    public RegistrazioneControllerGraficoGUI(GestoreUtenti gestoreUtenti) {
        this.gestoreUtenti = gestoreUtenti;
    }

    public EsitoRegistrazione gestisciRegistrazione(String nickname, String nome, String cognome, String email,
            String password) {

        RegistrazioneBean dati = new RegistrazioneBean();
        dati.setNickname(nickname == null ? null : nickname.trim());
        dati.setNome(nome);
        dati.setCognome(cognome);
        dati.setEmail(email);
        dati.setPassword(password);

        String erroreSintassi = dati.validaSintassi();
        if (erroreSintassi != null) {
            return EsitoRegistrazione.errore(erroreSintassi);
        }

        try {
            Utente nuovoUtente = gestoreUtenti.registraUtente(dati);
            return EsitoRegistrazione.successo(CostruttoreBeanVista.daUtente(nuovoUtente));
        } catch (RegistrazioneNonConsentitaException e) {
            return EsitoRegistrazione.errore(e.getMessage());
        }
    }
}
