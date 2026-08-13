package model;

public class DettagliRicostruzionePrenotazione {

    private final long dataPartenzaViaggio;
    private final long dataRientroViaggio;
    private final long dataPrenotazione;
    private final StatoPrenotazione stato;

    public DettagliRicostruzionePrenotazione(long dataPartenzaViaggio, long dataRientroViaggio,
            long dataPrenotazione, StatoPrenotazione stato) {
        this.dataPartenzaViaggio = dataPartenzaViaggio;
        this.dataRientroViaggio = dataRientroViaggio;
        this.dataPrenotazione = dataPrenotazione;
        this.stato = stato;
    }

    public long getDataPartenzaViaggio() {
        return dataPartenzaViaggio;
    }

    public long getDataRientroViaggio() {
        return dataRientroViaggio;
    }

    public long getDataPrenotazione() {
        return dataPrenotazione;
    }

    public StatoPrenotazione getStato() {
        return stato;
    }
}
