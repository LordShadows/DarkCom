package windows;

import dialogwindows.Dialogs;
import implementationclasses.FunctionClient;
import implementationclasses.WriteServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Objects;

public class ConnectWindowController {
    public Stage STAGE;
    private WriteServer WS;

    private double xOffset;
    private double yOffset;

    @FXML
    public AnchorPane loadPane;

    @FXML
    private TextField tf_IP;

    @FXML
    private AnchorPane headerPane;

    @FXML
    void b_Login_Click() {
        loadPane.setVisible(true);
        if(Objects.equals(tf_IP.getText(), ""))
        {
            Platform.runLater(()-> {
                Dialogs.ShowError("Введите IP-адрес!");
                loadPane.setVisible(false);
            });
            return;
        }
        WS = new WriteServer(tf_IP.getText(), 45000, this);
        FunctionClient.WS = WS;
    }

    @FXML
    void b_Close_Click() { System.exit(0); }

    @FXML
    void b_Min_Click() { STAGE.setIconified(true); }

    public void Initialization(Stage stage) {
        STAGE = stage;
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
