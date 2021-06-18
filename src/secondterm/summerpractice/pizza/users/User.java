package secondterm.summerpractice.pizza.users;

public class User {
    private final String login;
    private final String password;
    private final String nickname;

    public User(String nickname, String login, String password) {
        this.nickname = nickname;
        this.login = login;
        this.password = password;
    }

    public boolean enter(String login, String password) {
        return (this.login.equals(login) && this.password.equals(password));
    }

    public String getNickname() {
        return nickname;
    }
}
