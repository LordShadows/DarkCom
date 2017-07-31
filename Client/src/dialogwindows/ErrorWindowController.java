package dialogwindows;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ErrorWindowController {
    private Stage STAGE;

    private double xOffset;
    private double yOffset;

    @FXML
    private AnchorPane headerPane;

    @FXML
    private Text TextError;

    @FXML
    void b_Close_Click() { STAGE.close(); }

    @FXML
    void b_Min_Click() { STAGE.setIconified(true); }

    public void Initialization(Stage stage, String error) {
        STAGE = stage;
        TextError.setText(error);
        headerPane.setOnMousePressed(event -> {
            xOffset = STAGE.getX() - event.getScreenX();
            yOffset = STAGE.getY() - event.getScreenY();
        });
        headerPane.setOnMouseDragged(event -> {
            STAGE.setX(event.getScreenX() + xOffset);
            STAGE.setY(event.getScreenY() + yOffset);
        });
    }
}
