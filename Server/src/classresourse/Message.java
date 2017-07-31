package classresourse;

/**
 * Created by DELL on 31.01.2017.
 * @author Daniel Sandrutski
 */

public class Message {
    private String keyword;
    private String[] textArguments;
    private String signature;

    public Message(String keyword, String[] textArguments, String signature) {
        this.keyword = keyword;
        this.textArguments = textArguments;
        this.signature = signature;
    }

    public String getKeyword() {
        return keyword;
    }

    public String[] getTextArguments() {
        return textArguments;
    }

    public String getSignature() {
        return signature;
    }
}
