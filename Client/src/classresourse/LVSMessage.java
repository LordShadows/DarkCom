package classresourse;

/**
 * Created by Dell on 07.05.2017.
 * @author Daniel Sandrutski
 */
public class LVSMessage {
    private String text;
    private boolean isMyMessage;

    public LVSMessage(String text, boolean isMyMessage) {
        this.text = text;
        this.isMyMessage = isMyMessage;
    }

    public String getText() {
        return text;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }
}
