package listviewitems;

import classresourse.Contacts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

/**
 * Created by DELL on 10.02.2017.
 * @author Daniel Sandrutski
 */
public class ConactsListViewController  extends ListCell<Contacts> {
    public String ID;

    @FXML
    private Label name;

    @FXML
    private Label isOnline;

    @FXML
    private Label numMess;

    @FXML
    private ImageView icon;

    @FXML
    private GridPane gridPane;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Contacts contact, boolean empty) {
        super.updateItem(contact, empty);

        if(empty || contact == null) {

            setText(null);
            setGraphic(null);

        } else {
            mLLoader = new FXMLLoader(getClass().getResource("ListCell.fxml"));
            mLLoader.setController(this);

            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            name.setText(String.valueOf(contact.getName()));
            isOnline.setText(contact.isOnline()?"Онлайн":"Не в сети");
            ID = contact.getID();

            icon.setImage(contact.getIcon());

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

            numMess.setText(contact.getNumMessage() == 0 ? "" : "+" + String.valueOf(contact.getNumMessage()));
            numMess.setAlignment(Pos.CENTER);

            setText(null);
            setGraphic(gridPane);
        }

    }
}
