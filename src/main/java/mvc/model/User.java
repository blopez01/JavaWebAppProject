package mvc.model;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    public User() {
    }

    public User(String userLogin, String userPassword) {
        this.login = userLogin;
        this.password = userPassword;
    }

    public User(long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public static User convertUserJSONObject(JSONObject json) {
        try {
            User user = new User(json.getLong("id"),json.getString("userLogin"), json.getString("userPassword"));
            return user;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse person from provided json: " + json.toString());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserLogin() {
        return login;
    }

    public void setUserLogin(String userLogin) {
        this.login = userLogin;
    }

    public String getUserPassword() {
        return password;
    }

    public void setUserPassword(String userPassword) {
        this.password = userPassword;
    }


    @Override
    public String toString() {
        return "User{" +
                "userLogin='" + login + '\'' +
                ", userPassword='" + password + '\'' +
                '}';
    }

    //TODO: Remember to hash password before compare to the database info

}
