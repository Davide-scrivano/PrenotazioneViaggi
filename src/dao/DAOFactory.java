package dao;

public abstract class DAOFactory {

    public abstract UtenteDAO creaUtenteDAO();

    public abstract PacchettoDAO creaPacchettoDAO();

    public abstract CatalogoDAO creaCatalogoDAO();

    public abstract PagamentoDAO creaPagamentoDAO();

    public abstract PartecipanteDAO creaPartecipanteDAO();

    public abstract PrenotazioneDAO creaPrenotazioneDAO();
}
