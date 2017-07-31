package windows;

import implementationclasses.FunctionClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginWindowController {
    public Stage STAGE;

    private double xOffset;
    private double yOffset;

    @FXML
    private TextField tf_Password;

    @FXML
    public AnchorPane loadPane;

    @FXML
    private AnchorPane headerPane;

    @FXML
    private TextField tf_Login;

    @FXML
    private Label l_newAccount;

    public void setTf_Login(String tf_Login) {
        this.tf_Login.setText(tf_Login);
    }

    public void setTf_Password(String tf_Password) {
        this.tf_Password .setText(tf_Password);
    }

    @FXML
    void b_Login_Click() {
        loadPane.setVisible(true);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        md.update(tf_Password.getText().getBytes());
        FunctionClient.SendLoginToServer(tf_Login.getText(), DatatypeConverter.printHexBinary(md.digest()));
    }

    @FXML
    void b_Close_Click() { System.exit(0); }

    @FXML
    void b_Min_Click() { STAGE.setIconified(true); }

    public void Initialization(Stage stage){
        STAGE = stage;
        headerPane.setOnMousePressed(event -> {
            xOffset = STAGE.getX() - event.getScreenX();
            yOffset = STAGE.getY() - event.getScreenY();
        });
        headerPane.setOnMouseDragged(event -> {
            STAGE.setX(event.getScreenX() + xOffset);
            STAGE.setY(event.getScreenY() + yOffset);
        });
        FunctionClient.LWC = this;


        l_newAccount.setOnMouseClicked(event -> {
            try {
                Image ico = new Image("img/Icon32.png");
                Stage accountStage = new Stage();
                FXMLLoader connectLoader = new FXMLLoader(getClass().getResource("AccountWindow.fxml"));
                Parent root = connectLoader.load();
                accountStage.getIcons().add(ico);
                accountStage.initStyle(StageStyle.UNDECORATED);
                Scene connectScene = new Scene(root, Color.TRANSPARENT);
                accountStage.setScene(connectScene);
                accountStage.setTitle("Создание учетной записи - DarkCom by Daniel Sandrutski © 2017");
                accountStage.initStyle(StageStyle.TRANSPARENT);
                AccountWindowController connectController = connectLoader.getController();
                connectController.Initialization(accountStage);
                accountStage.show();
            } catch (IOException e){
                e.printStackTrace();
            }

        });
    }
}
