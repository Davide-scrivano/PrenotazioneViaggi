package bean;

public class UtenteVistaBean {

    private int id;
    private String nickname;
    private String nome;
    private String cognome;
    private String email;
    private boolean agenzia;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public boolean isAgenzia() {
        return agenzia;
    }

    public void setAgenzia(boolean agenzia) {
        this.agenzia = agenzia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
