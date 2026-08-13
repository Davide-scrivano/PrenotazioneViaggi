package dao;

import java.util.List;

import exceptions.PersistenzaException;
import model.Utente;

public interface UtenteDAO {

    void salva(List<Utente> utenti) throws PersistenzaException;

    List<Utente> carica() throws PersistenzaException;
}
