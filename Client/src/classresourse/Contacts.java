package classresourse;

import javafx.scene.image.Image;

/**
 * Created by DELL on 10.02.2017.
 * @author Daniel Sandrutski
 */
public class Contacts {
    private String id;
    private String name;
    private boolean isOnline;
    private Image icon;
    private int numMessage;

    public Contacts(String name, boolean isOnline, Image icon, String id, int numMessage) {
        this.name = name;
        this.isOnline = isOnline;
        this.icon = icon;
        this.id = id;
        this.numMessage = numMessage;
    }

    public String getName() { return name; }

    public String getID() { return id; }

    public boolean isOnline() {
        return isOnline;
    }

    public Image getIcon() {
        return icon;
    }

    public int getNumMessage() { return numMessage; }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
