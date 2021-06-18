package secondterm.summerpractice.pizza.users;

public class Client extends User {
    private final int district;

    public Client(String nickname, String login, String password, int district) {
        super(nickname, login, password);
        this.district = district;
    }

    public int getDist() {
        return district;
    }
}
