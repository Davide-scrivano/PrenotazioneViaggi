package dao;

import java.util.List;

import exceptions.PersistenzaException;
import model.Pacchetto;

public interface PacchettoDAO {

    List<Pacchetto> trovaTutti() throws PersistenzaException;

    Pacchetto trovaPerId(int id) throws PersistenzaException;

    void aggiorna(Pacchetto pacchetto) throws PersistenzaException;
}
