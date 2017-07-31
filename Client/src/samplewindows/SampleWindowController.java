package samplewindows;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SampleWindowController {
    private Stage STAGE;

    private double xOffset;
    private double yOffset;

    @FXML
    private AnchorPane headerPane;

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
