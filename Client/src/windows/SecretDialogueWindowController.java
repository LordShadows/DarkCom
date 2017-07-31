package windows;

import classresourse.LVMessage;
import classresourse.LVSMessage;
import classresourse.Message;
import implementationclasses.DiffieHellman;
import implementationclasses.FunctionClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import listviewitems.SecMessageController;

import javax.swing.text.PlainDocument;
import java.util.Objects;

public class SecretDialogueWindowController {
    private Stage STAGE;
    private String CONTACTLOGIN;
    private String MYLOGIN;

    private double xOffset;
    private double yOffset;

    private ObservableList<LVSMessage> messageObservableList = FXCollections.observableArrayList();
    private DiffieHellman DH;

    @FXML
    public ListView lv_message;

    @FXML
    public ImageView iv_my_logo;

    @FXML
    public Label l_my_name;

    @FXML
    public Label l_con_name;

    @FXML
    private AnchorPane headerPane;

    @FXML
    private TextField tf_message;

    @FXML
    public ImageView iv_con_logo;

    @FXML
    private Button b_send;

    @FXML
    void b_Close_Click() { STAGE.close(); FunctionClient.CloseSecDialog(CONTACTLOGIN); FunctionClient.SendMessageTo("_###$%&GBCLOSE", CONTACTLOGIN);}

    @FXML
    void b_Min_Click() { STAGE.setIconified(true); }

    public String getContact(){
        return CONTACTLOGIN;
    }

    public void Initialization(Stage stage, String contactLogin, String myLogin, boolean isCommand) {
        STAGE = stage;
        CONTACTLOGIN = contactLogin;
        MYLOGIN = myLogin;
        headerPane.setOnMousePressed(event -> {
            xOffset = STAGE.getX() - event.getScreenX();
            yOffset = STAGE.getY() - event.getScreenY();
        });
        headerPane.setOnMouseDragged(event -> {
            STAGE.setX(event.getScreenX() + xOffset);
            STAGE.setY(event.getScreenY() + yOffset);
        });

        STAGE.setOnCloseRequest(we -> FunctionClient.CloseSecDialog(CONTACTLOGIN));

        FunctionClient.SendMessage(new Message("TakeUserAboutSecretDialog", new String[] { CONTACTLOGIN }, ""));

        DH = new DiffieHellman();

        lv_message.setCellFactory(messageListView -> new SecMessageController());
        lv_message.setItems(messageObservableList);
        lv_message.refresh();

        b_send.setOnAction(event -> {
            messageObservableList.add(new LVSMessage(tf_message.getText(), true));
            lv_message.setItems(messageObservableList);
            lv_message.refresh();
            lv_message.scrollTo(messageObservableList.size() - 1);
            FunctionClient.SendMessageTo(DH.encrypt(tf_message.getText()) , CONTACTLOGIN);
            tf_message.clear();
        });

        tf_message.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER){
                messageObservableList.add(new LVSMessage(tf_message.getText(), true));
                lv_message.setItems(messageObservableList);
                lv_message.refresh();
                FunctionClient.SendMessageTo(DH.encrypt(tf_message.getText()) , CONTACTLOGIN);
                tf_message.clear();
            }
        });

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(isCommand){
            DH.AliceInit();
            FunctionClient.SendMessage(new Message("SendGPLToBob", new String[] { CONTACTLOGIN, DH.getAliceP(), DH.getAliceG(), DH.getAliceL(), DH.getAlice() }, ""));
        }
    }

    public void doAlice(String bob){
        DH.AliceGenKey(bob);
        messageObservableList.add(new LVSMessage("Приветствую!", false));
        messageObservableList.add(new LVSMessage("Приветствую!", true));
        lv_message.setItems(messageObservableList);
        lv_message.refresh();
    }

    public String doBob(String aliceP, String aliceG, String aliceL, String alice){
        String temp = DH.BobInit(aliceP, aliceG, aliceL, alice);
        messageObservableList.add(new LVSMessage("Приветствую!", false));
        messageObservableList.add(new LVSMessage("Приветствую!", true));
        lv_message.setItems(messageObservableList);
        lv_message.refresh();
        return temp;
    }

    public void SetMessage (String message){
        if(Objects.equals(message, "_###$%&GBCLOSE")) {
            messageObservableList.add(new LVSMessage("Я покинул секретный диалог!", false));
            lv_message.setItems(messageObservableList);
            lv_message.refresh();
            lv_message.scrollTo(messageObservableList.size() - 1);
            b_send.setVisible(false);
            tf_message.setVisible(false);
        } else {
            messageObservableList.add(new LVSMessage(DH.decrypt(message), false));
            lv_message.setItems(messageObservableList);
            lv_message.refresh();
            lv_message.scrollTo(messageObservableList.size() - 1);
        }
    }
}
