package classresourse;

public class User {
    private String Login;
    private String Email;
    private String Password;
    private String Create_time;
    private byte[] Avatar;
    private String FIO;
    private String Status;
    private String LastDateAccess;
    private String Country;

    public User(String login, String email, String password, String create_time, byte[] avatar, String FIO, String status, String lastDateAccess, String country) {
        Login = login;
        Email = email;
        Password = password;
        Create_time = create_time;
        Avatar = avatar;
        this.FIO = FIO;
        Status = status;
        LastDateAccess = lastDateAccess;
        Country = country;
    }

    public User() {
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCreate_time() {
        return Create_time;
    }

    public void setCreate_time(String create_time) {
        Create_time = create_time;
    }

    public byte[] getAvatar() {
        return Avatar;
    }

    public void setAvatar(byte[] avatar) {
        Avatar = avatar;
    }

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getLastDateAccess() {
        return LastDateAccess;
    }

    public void setLastDateAccess(String lastDateAccess) {
        LastDateAccess = lastDateAccess;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }
}
