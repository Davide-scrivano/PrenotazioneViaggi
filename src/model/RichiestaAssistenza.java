package model;

public class RichiestaAssistenza {

    private int id;
    private String nomeRichiedente;
    private String emailRichiedente;
    private String messaggio;
    private long dataRichiesta;

    public RichiestaAssistenza(int id, String nomeRichiedente, String emailRichiedente, String messaggio) {
        this.id = id;
        this.nomeRichiedente = nomeRichiedente;
        this.emailRichiedente = emailRichiedente;
        this.messaggio = messaggio;
        this.dataRichiesta = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public String getNomeRichiedente() {
        return nomeRichiedente;
    }

    public String getEmailRichiedente() {
        return emailRichiedente;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public long getDataRichiesta() {
        return dataRichiesta;
    }
}
