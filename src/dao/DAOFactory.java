package dao;

import config.ConfigurazioneGlobale;
import config.TipoPersistenza;
import control.GestoreUtenti;
import dao.cache.UtenteDAOConCache;

public class DAOFactory {

    private final ConfigurazioneGlobale configurazione;

    public DAOFactory(ConfigurazioneGlobale configurazione) {
        this.configurazione = configurazione;
    }

    public UtenteDAO creaUtenteDAO() {
        UtenteDAO daoReale;
        if (configurazione.getPersistenza() == TipoPersistenza.DB) {
            daoReale = new UtenteDAOMySQL(configurazione.getUrlDatabase(),
                    configurazione.getUtenteDatabase(), configurazione.getPasswordDatabase());
        } else {
            daoReale = new UtenteDAOFileSystem(configurazione.getPercorsoFileUtenti());
        }
        return new UtenteDAOConCache(daoReale);
    }

    public PacchettoDAO creaPacchettoDAO() {
        if (configurazione.getPersistenza() == TipoPersistenza.DB) {
            return new PacchettoDAOMySQL(configurazione.getUrlDatabase(),
                    configurazione.getUtenteDatabase(), configurazione.getPasswordDatabase());
        }
        return new PacchettoDAOFileSystem(configurazione.getPercorsoFilePacchetti());
    }

    public PrenotazioneDAO creaPrenotazioneDAO(GestoreUtenti gestoreUtenti) {
        if (configurazione.getPersistenza() == TipoPersistenza.DB) {
            return new PrenotazioneDAOMySQL(configurazione.getUrlDatabase(),
                    configurazione.getUtenteDatabase(), configurazione.getPasswordDatabase(), gestoreUtenti);
        }
        return new PrenotazioneDAOFileSystem(configurazione.getPercorsoFilePrenotazioni(), gestoreUtenti);
    }
}
