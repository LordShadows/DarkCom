package listviewitems;

import classresourse.LVMessage;
import classresourse.Message;
import implementationclasses.FunctionClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

/**
 * Created by DELL on 10.02.2017.
 * @author Daniel Sandrutski
 */
public class MessageListViewController extends ListCell<LVMessage> {
    @FXML
    private Label date;

    @FXML
    private Label text;

    @FXML
    private ImageView icon;

    @FXML
    private GridPane gridPane;

    @FXML
    private AnchorPane textpane;

    @FXML
    private AnchorPane mainpane;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(LVMessage message, boolean empty) {
        super.updateItem(message, empty);

        if(empty || message == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (!message.getIsDelimiter()) {
                mLLoader = new FXMLLoader(getClass().getResource("../listviewitems/MessageCell.fxml"));
            }
            else
            {
                mLLoader = new FXMLLoader(getClass().getResource("../listviewitems/DelimiterMessageCell.fxml"));
            }

            mLLoader.setController(this);
            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            text.setText(message.getText());

            if(!message.getIsDelimiter()) {
                date.setText(message.getDate());
                icon.setImage(message.getIcon());

                Rectangle clip = new Rectangle(
                        icon.getFitWidth(), icon.getFitHeight()
                );
                clip.setArcWidth(35);
                clip.setArcHeight(35);
                icon.setClip(clip);

                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                WritableImage image = icon.snapshot(parameters, null);
                icon.setClip(null);
                icon.setImage(image);

                int rowNumber = 1;
                String[] words = message.getText().split(" ");
                int symbolsNow = 0;
                double heightText = text.getFont().getSize() + text.getLineSpacing() + 2.0;
                for (String word: words) {
                    rowNumber += searchWord(word, "\n");
                    if(symbolsNow + word.length() + 1 > 100){
                        ++rowNumber;
                        symbolsNow = word.length() + 1;
                    }
                    else {
                        symbolsNow += word.length() + 1;
                    }
                }
                gridPane.setMaxHeight(rowNumber * heightText + 35.0);
                gridPane.setMinHeight(rowNumber * heightText + 35.0);
                gridPane.setPrefHeight(rowNumber * heightText + 35.0);
                textpane.setMaxHeight(rowNumber * heightText + 35.0);
                textpane.setMinHeight(rowNumber * heightText + 35.0);
                textpane.setPrefHeight(rowNumber * heightText + 35.0);
            }
            else {
                text.setAlignment(Pos.CENTER);
            }

            setText(null);
            if(!message.getIsDelimiter())
                setGraphic(gridPane);
            else
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
