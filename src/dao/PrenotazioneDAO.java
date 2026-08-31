package dao;

import exceptions.PersistenzaException;
import model.Prenotazione;

public interface PrenotazioneDAO {

    int prossimoId() throws PersistenzaException;

    void inserisci(Prenotazione prenotazione) throws PersistenzaException;

    Prenotazione trovaPerId(int id) throws PersistenzaException;
}
