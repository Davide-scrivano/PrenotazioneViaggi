package bean;

public class RegistrazioneBean {

    private String nickname;
    private String nome;
    private String cognome;
    private String email;
    private String password;

    public String validaSintassi() {
        if (nickname == null || nickname.isBlank()) {
            return "Inserisci il nickname.";
        }
        if (nome == null || nome.isBlank()) {
            return "Inserisci il nome.";
        }
        if (cognome == null || cognome.isBlank()) {
            return "Inserisci il cognome.";
        }
        if (email == null || email.isBlank()) {
            return "Inserisci l'email.";
        }
        if (password == null || password.isBlank()) {
            return "Inserisci la password.";
        }
        return null;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
