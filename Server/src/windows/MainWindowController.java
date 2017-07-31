package windows;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainWindowController {
    private Stage STAGE;

    private double xOffset;
    private double yOffset;

    @FXML
    private AnchorPane headerPane;

    @FXML
    public ListView lw_Clients;

    @FXML
    public Label l_LocalIP;

    @FXML
    void b_Close_Click() { System.exit(0); }

    @FXML
    void b_Min_Click() { STAGE.setIconified(true); }

    @FXML
    void b_About_Click() { }

    @FXML
    void b_CloseConn_Click() { }

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

        InetAddress iaLocalAddress;
        try {
            iaLocalAddress =
                    InetAddress.getLocalHost();

            byte[] ip;
            ip = iaLocalAddress.getAddress();

            l_LocalIP.setText((0xff & (int)ip[0]) + "." +
                    (0xff & (int)ip[1]) + "." +
                    (0xff & (int)ip[2]) + "." +
                    (0xff & (int)ip[3]));
        } catch(UnknownHostException ex) {
            System.out.println(ex.toString());
        }
    }
}
