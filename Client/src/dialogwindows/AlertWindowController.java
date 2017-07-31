package dialogwindows;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AlertWindowController {
    private Stage STAGE;

    private double xOffset;
    private double yOffset;

    private boolean answer = false;

    public boolean getAnswer(){ return answer; }

    @FXML
    private Button b_No;

    @FXML
    private AnchorPane headerPane;

    @FXML
    private Button b_Yes;

    @FXML
    private ImageView iv_Icon;

    @FXML
    private Label l_text;

    @FXML
    void b_Close_Click() { STAGE.close(); }

    @FXML
    void b_Min_Click() { STAGE.setIconified(true); }

    @FXML
    void b_Yes_Click() {
        answer = true;
        STAGE.close();
    }

    @FXML
    void b_No_Click() {
        answer = false;
        STAGE.close();
    }

    public void Initialization(Stage stage, String text) {
        STAGE = stage;
        headerPane.setOnMousePressed(event -> {
            xOffset = STAGE.getX() - event.getScreenX();
            yOffset = STAGE.getY() - event.getScreenY();
        });
        headerPane.setOnMouseDragged(event -> {
            STAGE.setX(event.getScreenX() + xOffset);
            STAGE.setY(event.getScreenY() + yOffset);
        });
        l_text.setText(text);
    }

    public void InitializationAlert(Stage stage, String text) {
        STAGE = stage;
        headerPane.setOnMousePressed(event -> {
            xOffset = STAGE.getX() - event.getScreenX();
            yOffset = STAGE.getY() - event.getScreenY();
        });
        headerPane.setOnMouseDragged(event -> {
            STAGE.setX(event.getScreenX() + xOffset);
            STAGE.setY(event.getScreenY() + yOffset);
        });
        l_text.setText(text);
        Image imageOk = new Image("img/messages-icon.png");
        iv_Icon.setImage(imageOk);
        b_Yes.setVisible(false);
        b_No.setText("OK");
    }
}
