package bean;

public class LoginBean {

    private String nickname;
    private String password;

    public String validaSintassi() {
        if (nickname == null || nickname.isBlank()) {
            return "Inserisci il nickname.";
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
