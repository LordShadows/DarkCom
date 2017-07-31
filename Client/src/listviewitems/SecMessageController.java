package listviewitems;

import classresourse.LVSMessage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by DELL on 07.04.2017.
 * @author Daniel Sandrutski
 */
public class SecMessageController extends ListCell<LVSMessage> {
    @FXML
    private Label l_message;

    @FXML
    private VBox mainpane;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(LVSMessage message, boolean empty) {
        super.updateItem(message, empty);

        if(empty || message == null) {
            setText(null);
            setGraphic(null);
        } else {
            if(!message.isMyMessage()) {
                mLLoader = new FXMLLoader(getClass().getResource("../listviewitems/LeftMessageCell.fxml"));
                setStyle("-fx-alignment: top-left;");
            } else {
                mLLoader = new FXMLLoader(getClass().getResource("../listviewitems/RightMessageCell.fxml"));
                setStyle("-fx-alignment: top-right;");
            }
            mLLoader.setController(this);
            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            l_message.setText(message.getText());

            int rowNumber = 1;
            String[] words = message.getText().split(" ");
            int symbolsNow = 0;
            double heightText = l_message.getFont().getSize() + l_message.getLineSpacing() + 3.5;
            for (String word: words) {
                rowNumber += searchWord(word, "\n");
                if(symbolsNow + word.length() + 1 > 90){
                    ++rowNumber;
                    symbolsNow = word.length() + 1;
                }
                else {
                    symbolsNow += word.length() + 1;
                }
            }
            l_message.setMaxHeight(rowNumber * heightText + 10.0);
            l_message.setMinHeight(rowNumber * heightText + 10.0);

            setText(null);
            setGraphic(mainpane);
        }

    }

    public static int searchWord(String message, String word) {
        message = message.toLowerCase();
        word = word.toLowerCase();
        int i = message.indexOf(word);
        int count = 0;
        while (i >= 0) {
            count++;
            i = message.indexOf(word, i + 1);
        }
        return count;
    }
}
