package bean;

public class AssistenzaBean {

    private String nome;
    private String email;
    private String messaggio;

    public String validaSintassi() {
        if (nome == null || nome.isBlank()) {
            return "Inserisci il nome.";
        }
        if (email == null || email.isBlank()) {
            return "Inserisci l'email.";
        }
        if (messaggio == null || messaggio.isBlank()) {
            return "Inserisci il messaggio.";
        }
        return null;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
