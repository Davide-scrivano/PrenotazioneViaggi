package dao;

import java.util.List;

import exceptions.PersistenzaException;
import model.Pacchetto;

public interface PacchettoDAO {

    void salva(List<Pacchetto> pacchetti) throws PersistenzaException;

    List<Pacchetto> carica() throws PersistenzaException;
}
