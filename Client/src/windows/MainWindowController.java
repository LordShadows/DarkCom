package windows;

import classresourse.Contacts;
import classresourse.Message;
import dialogwindows.Dialogs;
import implementationclasses.FunctionClient;
import javafx.animation.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Objects;

public class MainWindowController {
    private Stage STAGE;
    private Timeline timeline;

    private boolean isAddContacts = false;

    private double xOffset;
    private double yOffset;

    @FXML
    public Label my_login;

    @FXML
    private Button refresh_contacts;

    @FXML
    private Button add_contacts;

    @FXML
    public ListView lv_Message;

    @FXML
    private AnchorPane headerPane;

    @FXML
    public Label contact_status;

    @FXML
    public ListView lv_Contacts;

    @FXML
    public Label my_FIO;

    @FXML
    public ImageView contact_logo;

    @FXML
    public Label contacts_login;

    @FXML
    public ImageView my_logo;

    @FXML
    private Button close_contact;

    @FXML
    private AnchorPane contactpane;

    @FXML
    public Label contacts_FIO;

    @FXML
    private Label l_contact;

    @FXML
    private TextArea ta_Message;

    @FXML
    public Label contact_mail;

    @FXML
    public Label header_contacts_FIO;

    @FXML
    public Label contact_country;

    @FXML
    private TextField tf_search;

    @FXML
    public Label contact_create_time;

    @FXML
    public Label contact_last_access;

    @FXML
    private AnchorPane myAccountPane;

    @FXML
    private Button b_DeleteAccount;

    @FXML
    private Button b_DeleteContact;

    @FXML
    private Button b_AlterAccount;

    @FXML
    private Button b_AddContact;

    @FXML
    private Label l_message;

    @FXML
    private Button b_Send;

    @FXML
    private AnchorPane pane_close_account;

    @FXML
    private Label l_start_label;

    @FXML
    private Button b_AddSecretDialog;

    @FXML
    void b_Close_Click() { System.exit(0); }

    @FXML
    void b_Min_Click() { STAGE.setIconified(true); }

    public void ContactPaneToX(double toX){
        if(contactpane.getLayoutX() != toX){
            if(timeline == null) {
                KeyFrame keyFrame = new KeyFrame(Duration.millis(200), new KeyValue(contactpane.layoutXProperty(), toX));
                timeline = new Timeline(keyFrame);
                timeline.setOnFinished(event-> timeline = null);
                timeline.play();
                if(toX > 150){
                    l_message.setDisable(true);
                    lv_Message.setDisable(true);
                    b_Send.setDisable(true);
                    b_AddSecretDialog.setDisable(true);
                    header_contacts_FIO.setDisable(true);
                    ta_Message.setDisable(true);
                    pane_close_account.setVisible(true);
                } else {
                    l_message.setDisable(false);
                    lv_Message.setDisable(false);
                    b_Send.setDisable(false);
                    b_AddSecretDialog.setDisable(false);
                    header_contacts_FIO.setDisable(false);
                    ta_Message.setDisable(false);
                    pane_close_account.setVisible(false);
                }
            }
        }
    }

    public void Initialization(Stage stage) {
        STAGE = stage;
        FunctionClient.MWC = this;
        headerPane.setOnMousePressed(event -> {
            xOffset = STAGE.getX() - event.getScreenX();
            yOffset = STAGE.getY() - event.getScreenY();
        });
        headerPane.setOnMouseDragged(event -> {
            STAGE.setX(event.getScreenX() + xOffset);
            STAGE.setY(event.getScreenY() + yOffset);
        });

        Tooltip ttAddContacts = new Tooltip();
        ttAddContacts.setText("Добавить новые контакты");
        ttAddContacts.setStyle("-fx-background-color: #353836;");
        add_contacts.setTooltip(ttAddContacts);

        Tooltip ttRefreshContacts = new Tooltip();
        ttRefreshContacts.setText("Обновить контакты");
        ttRefreshContacts.setStyle("-fx-background-color: #353836;");
        refresh_contacts.setTooltip(ttRefreshContacts);

        pane_close_account.setOnMouseClicked(event -> ContactPaneToX(20));

        refresh_contacts.setOnAction(event -> {
            if(isAddContacts){
                SearchContacts();
            } else {
                UpdateContacts();
            }
        });

        add_contacts.setOnAction(event -> {
            if(isAddContacts){
                add_contacts.setStyle("-fx-background-image: url(\"img/contact-add.png\");");
                isAddContacts = false;
                tf_search.setText("");
                lv_Contacts.getItems().clear();
                UpdateContacts();
                l_contact.setText("ВАШИ КОНТАКТЫ");

                Tooltip ttTempAddContacts = new Tooltip();
                ttTempAddContacts.setText("Добавить новые контакты");
                ttTempAddContacts.setStyle("-fx-background-color: #353836;");
                add_contacts.setTooltip(ttTempAddContacts);
            } else{
                add_contacts.setStyle("-fx-background-image: url(\"img/my-contacts.png\");");
                isAddContacts = true;
                tf_search.setText("");
                l_contact.setText("ПОИСК");
                lv_Contacts.getItems().clear();
                SearchContacts();

                lv_Message.getItems().clear();
                l_start_label.setVisible(true);

                Tooltip ttTempAddContacts = new Tooltip();
                ttTempAddContacts.setText("Перейти к вашим контактам");
                ttTempAddContacts.setStyle("-fx-background-color: #353836;");
                add_contacts.setTooltip(ttTempAddContacts);
            }
        });

        tf_search.setOnKeyReleased(event->{
            if (isAddContacts) {
                SearchContacts();
            } else {
                UpdateContacts();
            }
        });

        contacts_FIO.setAlignment(Pos.CENTER);
        contacts_login.setAlignment(Pos.CENTER);
        header_contacts_FIO.setAlignment(Pos.CENTER);

        close_contact.setOnAction(event-> ContactPaneToX(20));
        contactpane.setLayoutX(20);

        UpdateMyData();
        UpdateContacts();

        b_AlterAccount.setOnAction(event -> {
            try {
                Image ico = new Image("img/Icon32.png");
                Stage accountStage = new Stage();
                FXMLLoader connectLoader = new FXMLLoader(getClass().getResource("AccountWindow.fxml"));
                Parent root = connectLoader.load();
                accountStage.getIcons().add(ico);
                accountStage.initStyle(StageStyle.UNDECORATED);
                Scene connectScene = new Scene(root, Color.TRANSPARENT);
                accountStage.setScene(connectScene);
                accountStage.setTitle("Коррекция учетной записи - DarkCom by Daniel Sandrutski © 2017");
                accountStage.initStyle(StageStyle.TRANSPARENT);
                AccountWindowController connectController = connectLoader.getController();
                connectController.Initialization(accountStage, contacts_login.getText(), contacts_FIO.getText(), contact_mail.getText(), contact_country.getText(), contact_status.getText(), contact_logo.getImage());
                accountStage.show();
            } catch (IOException e){
                e.printStackTrace();
            }
        });

        b_AddContact.setOnAction(event -> {
            if(Dialogs.ShowQuestion("Вы действительного хотите добавить этого человека в свои контакты?")){
                Contacts contact = (Contacts) lv_Contacts.getSelectionModel().getSelectedItem();
                FunctionClient.SendMessage(new Message("AddContactUser", new String[]{contact.getID()}, ""));
                ContactPaneToX(20);
            }
        });

        lv_Contacts.setOnMouseClicked(event->{
            if(!isAddContacts) {
                if(lv_Contacts.getSelectionModel().getSelectedItem() != null) {
                    l_start_label.setVisible(false);
                    Contacts contact = (Contacts) lv_Contacts.getSelectionModel().getSelectedItem();
                    if (!Objects.equals(header_contacts_FIO.getText(), contact.getID() + " : " + contact.getName())) {
                        header_contacts_FIO.setText(contact.getID() + " : " + contact.getName());
                        lv_Message.getItems().clear();
                        UpdateMessages();
                        ReadMessage();
                    }
                    if(contactpane.getLayoutX() > 300){
                        FunctionClient.SendMessage(new Message("TakeInfoAboutUser", new String[]{contact.getID(), "false"}, ""));
                        b_DeleteContact.setVisible(true);
                        b_AddContact.setVisible(false);
                        b_AlterAccount.setVisible(false);
                        b_DeleteAccount.setVisible(false);
                    }
                }
            }
            else{
                if(lv_Contacts.getSelectionModel().getSelectedItem() != null) {
                    Contacts contact = (Contacts) lv_Contacts.getSelectionModel().getSelectedItem();
                    FunctionClient.SendMessage(new Message("TakeInfoAboutUser", new String[]{contact.getID(), "false"}, ""));
                    b_DeleteContact.setVisible(false);
                    b_AddContact.setVisible(true);
                    b_AlterAccount.setVisible(false);
                    b_DeleteAccount.setVisible(false);
                }
            }
        });

        header_contacts_FIO.setOnMouseClicked(event->{
            if(!Objects.equals(header_contacts_FIO.getText(), ""))
            FunctionClient.SendMessage(new Message("TakeInfoAboutUser", new String[]{header_contacts_FIO.getText().substring(0, header_contacts_FIO.getText().indexOf(':') - 1), "false"}, ""));
            b_DeleteContact.setVisible(true);
            b_AddContact.setVisible(false);
            b_AlterAccount.setVisible(false);
            b_DeleteAccount.setVisible(false);
        });

        myAccountPane.setOnMouseClicked(event -> {
            FunctionClient.SendMessage(new Message("TakeInfoAboutUser", new String[]{my_login.getText(), "false"}, ""));
            b_DeleteContact.setVisible(false);
            b_AddContact.setVisible(false);
            b_AlterAccount.setVisible(true);
            b_DeleteAccount.setVisible(true);

        });
    }

    private void UpdateMyData() {
        FunctionClient.SendMessage(new Message("UpdateUserAccountData", new String[]{}, ""));
    }

    public void UpdateContacts() { FunctionClient.SendMessage(new Message("UpdateUserContactsData", new String[]{ tf_search.getText() }, "")); }

    public void UpdateMessages() { FunctionClient.SendMessage(new Message("TakeUserMessages", new String[]{ header_contacts_FIO.getText().substring(0, header_contacts_FIO.getText().indexOf(':') - 1) }, "")); }

    public void ReadMessage() { FunctionClient.SendMessage(new Message("ReadMessage", new String[]{ header_contacts_FIO.getText().substring(0, header_contacts_FIO.getText().indexOf(':') - 1) }, "")); }

    private void SearchContacts() { FunctionClient.SendMessage(new Message("SearchContactsData", new String[]{ tf_search.getText() }, "")); }

    @FXML
    void b_About_Click() {
        Stage mainStage = new Stage();
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("AboutWindow.fxml"));
        Parent mainRoot = null;
        try {
            mainRoot = mainLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image ico = new Image("img/Icon32.png");
        mainStage.getIcons().add(ico);
        mainStage.initStyle(StageStyle.UNDECORATED);
        assert mainRoot != null;
        Scene mainScene = new Scene(mainRoot, Color.TRANSPARENT);
        mainStage.setScene(mainScene);
        mainStage.setTitle("О программе - DarkCom by Daniel Sandrutski © 2017");
        mainStage.initStyle(StageStyle.TRANSPARENT);
        AboutWindowController loginController = mainLoader.getController();
        loginController.Initialization(mainStage);

        mainStage.showAndWait();
    }

    @FXML
    void b_SendMessage_Click() {
        if(!Objects.equals(header_contacts_FIO.getText(), "") && !Objects.equals(ta_Message.getText(), "")){
            FunctionClient.SendMessage(new Message("SendMessagesTo", new String[]{ ta_Message.getText(), header_contacts_FIO.getText().substring(0, header_contacts_FIO.getText().indexOf(':') - 1) }, ""));
            UpdateMessages();
            ta_Message.setText("");
        }
    }

    @FXML
    void b_AddSecretDialog_Click() {
        if(!Objects.equals(header_contacts_FIO.getText(), "")) {
            for (SecretDialogueWindowController sdwc: FunctionClient.SDWCs) {
                if(Objects.equals(sdwc.getContact(), header_contacts_FIO.getText().substring(0, header_contacts_FIO.getText().indexOf(':') - 1))) {
                    Dialogs.ShowAlert("Невозможно создать секретный диалог!!! С этим контактом уже ведется секретный диалог!!!");
                    return;
                }
            }
            FunctionClient.SendMessage(new Message("OpenSecretDialog", new String[]{header_contacts_FIO.getText().substring(0, header_contacts_FIO.getText().indexOf(':') - 1)}, ""));
        }

    }
}
