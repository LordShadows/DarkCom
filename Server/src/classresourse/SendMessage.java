package classresourse;

/**
 * Created by Dell on 19.04.2017.
 * @author Daniel Sandrutski
 */
public class SendMessage {
    private String login;
    private String message;
    private String dateWrite;
    private byte[] avatar;
    private int isRead;

    public SendMessage(String login, String message, String dateWrite, byte[] avatar, int isRead) {
        this.login = login;
        this.message = message;
        this.dateWrite = dateWrite;
        this.avatar = avatar;
        this.isRead = isRead;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateWrite() {
        return dateWrite;
    }

    public void setDateWrite(String dateWrite) {
        this.dateWrite = dateWrite;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
