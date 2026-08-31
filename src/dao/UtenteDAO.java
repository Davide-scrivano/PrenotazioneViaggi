package dao;

import exceptions.PersistenzaException;
import model.Utente;

public interface UtenteDAO {

    Utente trovaPerId(int id) throws PersistenzaException;

    Utente trovaPerNickname(String nickname) throws PersistenzaException;
}
