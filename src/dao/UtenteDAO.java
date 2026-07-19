package dao;

import java.util.List;

import exceptions.PersistenzaException;
import model.Utente;

/**
 * Interfaccia comune per il salvataggio/caricamento degli utenti,
 * a prescindere dal tipo di persistenza usata (file system o database).
 * Richiesta del progetto: "At least one DAO shall be provided in two
 * versions DBMS and file system".
 */
public interface UtenteDAO {

    void salva(List<Utente> utenti) throws PersistenzaException;

    List<Utente> carica() throws PersistenzaException;
}
