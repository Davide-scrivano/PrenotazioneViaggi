package dao;

import java.util.List;

import exceptions.PersistenzaException;
import model.Prenotazione;

public interface PrenotazioneDAO {

    void salva(List<Prenotazione> prenotazioni) throws PersistenzaException;

    List<Prenotazione> carica() throws PersistenzaException;
}
