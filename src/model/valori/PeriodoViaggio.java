package model.valori;

public class PeriodoViaggio {

    private final long dataPartenza;
    private final long dataRientro;

    public PeriodoViaggio(long dataPartenza, long dataRientro) {
        this.dataPartenza = dataPartenza;
        this.dataRientro = dataRientro;
    }

    public static PeriodoViaggio daPartenzaEDurata(long dataPartenza, DurataViaggio durata) {
        return new PeriodoViaggio(dataPartenza, dataPartenza + durata.getDurataInMillisecondi());
    }

    public boolean contiene(PeriodoViaggio periodo) {
        return periodo.dataPartenza >= this.dataPartenza && periodo.dataRientro <= this.dataRientro;
    }

    public long getDataPartenza() {
        return dataPartenza;
    }

    public long getDataRientro() {
        return dataRientro;
    }
}
