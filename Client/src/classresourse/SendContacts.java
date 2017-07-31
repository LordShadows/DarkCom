package classresourse;

/**
 * Created by DELL on 10.02.2017.
 * @author Daniel Sandrutski
 */
public class SendContacts {
    private String id;
    private String name;
    private boolean isOnline;
    private byte[] icon;
    private int numMessage;

    public SendContacts(String name, boolean isOnline, byte[] icon, String id, int numMessage) {
        this.name = name;
        this.isOnline = isOnline;
        this.icon = icon;
        this.id = id;
        this.numMessage = numMessage;
    }

    public String getName() {
        return name;
    }

    public String getID() { return id; }

    public boolean isOnline() {
        return isOnline;
    }

    public byte[] getIcon() {
        return icon;
    }

    public int getNumMessage() { return numMessage; }
}
