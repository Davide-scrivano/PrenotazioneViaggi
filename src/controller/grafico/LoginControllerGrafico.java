package controller.grafico;

import bean.EsitoLoginBean;
import bean.LoginBean;
import control.GestoreLogin;
import dao.DAOFactory;
import exceptions.ApplicazioneException;
import model.Utente;

public class LoginControllerGrafico {

    private final DAOFactory daoFactory;

    public LoginControllerGrafico(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    private GestoreLogin nuovaAttivazione() {
        return new GestoreLogin(daoFactory);
    }

    public EsitoLoginBean effettuaLogin(LoginBean credenziali) {
        EsitoLoginBean esito = new EsitoLoginBean();

        String erroreSintassi = credenziali.validaSintassi();
        if (erroreSintassi != null) {
            esito.setMessaggio(erroreSintassi);
            return esito;
        }

        try {
            Utente utente = nuovaAttivazione().autentica(credenziali);
            esito.setSuccesso(true);
            esito.setUtente(MapperBeanVista.daUtente(utente));
        } catch (ApplicazioneException e) {
            esito.setMessaggio(TraduttoreErrori.perUtente(e));
        }
        return esito;
    }
}
