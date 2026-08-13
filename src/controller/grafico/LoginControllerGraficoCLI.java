package controller.grafico;

import bean.EsitoLogin;
import bean.EsitoRecuperaPassword;
import bean.LoginBean;
import control.GestoreUtenti;
import exceptions.CredenzialiNonValideException;
import model.Utente;

public class LoginControllerGraficoCLI {

    private final GestoreUtenti gestoreUtenti;

    public LoginControllerGraficoCLI(GestoreUtenti gestoreUtenti) {
        this.gestoreUtenti = gestoreUtenti;
    }

    public EsitoLogin gestisciLogin(String nickname, String password) {
        LoginBean datiLogin = new LoginBean();
        datiLogin.setNickname(nickname == null ? null : nickname.trim());
        datiLogin.setPassword(password);

        String erroreSintassi = datiLogin.validaSintassi();
        if (erroreSintassi != null) {
            return EsitoLogin.errore(erroreSintassi);
        }

        try {
            Utente utente = gestoreUtenti.login(datiLogin.getNickname(), datiLogin.getPassword());
            return EsitoLogin.successo(CostruttoreBeanVista.daUtente(utente));
        } catch (CredenzialiNonValideException e) {
            return EsitoLogin.errore(e.getMessage());
        }
    }

    public EsitoRecuperaPassword recuperaPassword(String email) {
        if (email == null || email.isBlank()) {
            return EsitoRecuperaPassword.errore("Inserisci un'email.");
        }

        try {
            String password = gestoreUtenti.recuperaPassword(email.trim());
            return EsitoRecuperaPassword.successo(password);
        } catch (CredenzialiNonValideException e) {
            return EsitoRecuperaPassword.errore(e.getMessage());
        }
    }

    public void logout() {
        gestoreUtenti.logout();
    }
}
