package control;

import model.Pacchetto;

public interface OsservatorePosti {

    void postiLiberati(Pacchetto pacchetto, int numeroPostiLiberati);
}
