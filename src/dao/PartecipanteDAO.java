package dao;

import java.util.List;

import exceptions.PersistenzaException;
import model.Partecipante;

public interface PartecipanteDAO {

    int prossimoId() throws PersistenzaException;

    void inserisci(Partecipante partecipante, int idPrenotazione) throws PersistenzaException;

    List<Partecipante> trovaPerPrenotazione(int idPrenotazione) throws PersistenzaException;
}
