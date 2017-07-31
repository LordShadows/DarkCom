package classresourse;

import javafx.scene.image.Image;

/**
 * Created by DELL on 10.02.2017.
 * @author Daniel Sandrutski
 */
public class LVMessage {
    private String date;
    private String text;
    private Image icon;
    private int isRead;
    private boolean isDelimiter;

    public LVMessage(String date, String text, Image icon, int isRead, boolean isDelimiter) {
        this.date = date;
        this.text = text;
        this.icon = icon;
        this.isRead = isRead;
        this.isDelimiter = isDelimiter;
    }

    public String getDate() { return date; }

    public String getText() {
        return text;
    }

    public Image getIcon() {
        return icon;
    }

    public int getIsRead() {
        return isRead;
    }

    public boolean getIsDelimiter() { return isDelimiter; }
}
