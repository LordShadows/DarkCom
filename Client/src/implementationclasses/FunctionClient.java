package implementationclasses;

import classresourse.*;
import com.google.gson.Gson;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import javafx.application.Platform;
import dialogwindows.Dialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import listviewitems.ConactsListViewController;
import listviewitems.MessageListViewController;
import windows.AccountWindowController;
import windows.LoginWindowController;
import windows.MainWindowController;
import windows.SecretDialogueWindowController;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by DELL on 31.01.2017.
 * @author Daniel Sandrutski
 */

public class FunctionClient {
    static public WriteServer WS;
    static public LoginWindowController LWC;
    static public MainWindowController MWC;
    static public AccountWindowController AWC;
    static public ArrayList<SecretDialogueWindowController> SDWCs = new ArrayList<>();

    static public void SendLoginToServer(String login, String password){
        Message message = new Message("Login", new String[] {login, password}, "");
        Gson gson = new Gson();
        WS.WriteLine(gson.toJson(Crypt.encodeServerMessage(gson.toJson(message))));
    }

    static public void Login(String answer)
    {
        if(answer.equals("Yes"))
        {
            Platform.runLater(()-> LWC.STAGE.close());
        }
        else if(answer.equals("No"))
        {
            Platform.runLater(()-> LWC.loadPane.setVisible(false));
            Platform.runLater(()-> Dialogs.ShowError("Не правильный логин или пароль!!!"));
        }
    }

    static public void UpdateUserAccountData(String login, String FIO, byte[] byteImage){
        Platform.runLater(()-> MWC.my_login.setText(login));
        Platform.runLater(()-> MWC.my_FIO.setText(FIO));
        if (byteImage != null) {
            BufferedImage fromBytes;
            try {
                fromBytes = ImageIO.read(new ByteInputStream(byteImage, byteImage.length));
                Platform.runLater(()-> {
                    MWC.my_logo.setImage(SwingFXUtils.toFXImage(fromBytes, null));
                    Rectangle clip = new Rectangle(
                            MWC.my_logo.getFitWidth(), MWC.my_logo.getFitHeight()
                    );
                    clip.setArcWidth(60);
                    clip.setArcHeight(60);
                    MWC.my_logo.setClip(clip);

                    SnapshotParameters parameters = new SnapshotParameters();
                    parameters.setFill(Color.TRANSPARENT);
                    WritableImage image = MWC.my_logo.snapshot(parameters, null);
                    MWC.my_logo.setClip(null);
                    MWC.my_logo.setImage(image);
                });
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
    }

    static public void TakeInfoAboutUser(User user){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        SimpleDateFormat cdsdf = new SimpleDateFormat("dd MMMM yyyy г.");
        Date date = null;
        try {
            date = sdf.parse(user.getCreate_time());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Platform.runLater(()-> MWC.contacts_login.setText(user.getLogin()));
        Platform.runLater(()-> MWC.contacts_FIO.setText(user.getFIO()));
        Platform.runLater(()-> MWC.contact_mail.setText(user.getEmail()));
        Platform.runLater(()-> MWC.contact_country.setText(user.getCountry()));
        Platform.runLater(()-> MWC.contacts_login.setText(user.getLogin()));
        Date finalDate = date;
        Platform.runLater(()-> MWC.contact_create_time.setText(cdsdf.format(finalDate)));
        Platform.runLater(()-> MWC.contact_last_access.setText(user.getLastDateAccess()));
        Platform.runLater(()-> MWC.contact_status.setText(user.getStatus()));
        if (user.getAvatar() != null) {
            BufferedImage fromBytes;
            try {
                fromBytes = ImageIO.read(new ByteInputStream(user.getAvatar(), user.getAvatar().length));
                Platform.runLater(()-> {
                    MWC.contact_logo.setImage(SwingFXUtils.toFXImage(fromBytes, null));
                    Rectangle clip = new Rectangle(
                            MWC.contact_logo.getFitWidth(), MWC.contact_logo.getFitHeight()
                    );
                    clip.setArcWidth(150);
                    clip.setArcHeight(150);
                    MWC.contact_logo.setClip(clip);

                    SnapshotParameters parameters = new SnapshotParameters();
                    parameters.setFill(Color.TRANSPARENT);
                    WritableImage image = MWC.contact_logo.snapshot(parameters, null);
                    MWC.contact_logo.setClip(null);
                    MWC.contact_logo.setImage(image);
                });
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
        Platform.runLater(()->MWC.ContactPaneToX(334));
    }

    static public void UpdateUserContactsData(SendContacts[] sendContacts) {
        try {
            ObservableList<Contacts> contactObservableList = FXCollections.observableArrayList();
            for (SendContacts sc : sendContacts) {
                BufferedImage fromBytes = ImageIO.read(new ByteInputStream(sc.getIcon(), sc.getIcon().length));
                contactObservableList.add(new Contacts(sc.getName(), sc.isOnline(), SwingFXUtils.toFXImage(fromBytes, null), sc.getID(), sc.getNumMessage()));
            }
            Platform.runLater(()-> {
                MWC.lv_Contacts.setItems(contactObservableList);
                MWC.lv_Contacts.setCellFactory(slw -> new ConactsListViewController());
            });
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    static public void TakeUserMessages(SendMessage[] sendMessages) {
        try {
            ObservableList<LVMessage> messageObservableList = FXCollections.observableArrayList();
            boolean isRead = true;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            SimpleDateFormat fsdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            for (SendMessage sendMessage : sendMessages) {
                if(isRead && sendMessage.getIsRead() == 0 && !Objects.equals(sendMessage.getLogin(), MWC.my_FIO.getText())){
                    messageObservableList.add(new LVMessage(null, "Новые сообщения", null, 1, true));
                    isRead = false;
                }
                if(isRead && sendMessage.getIsRead() == 0 && Objects.equals(sendMessage.getLogin(), MWC.my_FIO.getText())){
                    messageObservableList.add(new LVMessage(null, "Еще не прочитанные", null, 1, true));
                    isRead = false;
                }
                BufferedImage fromBytes = ImageIO.read(new ByteInputStream(sendMessage.getAvatar(), sendMessage.getAvatar().length));
                Date date = sdf.parse(sendMessage.getDateWrite());
                messageObservableList.add(new LVMessage(sendMessage.getLogin() + "  " + fsdf.format(date), sendMessage.getMessage(), SwingFXUtils.toFXImage(fromBytes, null), sendMessage.getIsRead(), false));
            }
            Platform.runLater(()-> {
                MWC.lv_Message.setCellFactory(messageListView -> new MessageListViewController());
                MWC.lv_Message.setItems(messageObservableList);
                MWC.lv_Message.refresh();
                MWC.lv_Message.scrollTo(messageObservableList.size() - 1);
            });
        } catch (IOException | ParseException exp) {
            exp.printStackTrace();
        }
    }

    static public void UpdateContactOnline(String id){
        if(MWC != null) {
            Platform.runLater(()-> {
                ObservableList<Contacts> contactObservableList = MWC.lv_Contacts.getItems();
                contactObservableList.stream().filter(contact -> Objects.equals(contact.getID(), id)).forEach(contact -> contact.setOnline(true));
                MWC.lv_Contacts.setCellFactory(slw -> new ConactsListViewController());
                Platform.runLater(()->MWC.lv_Contacts.setCellFactory(slw -> new ConactsListViewController()));
            });
        }
    }

    static public void UpdateContactNoOnline(String id) {
        if(MWC != null) {
            Platform.runLater(()-> {
                ObservableList<Contacts> contactObservableList = MWC.lv_Contacts.getItems();
                contactObservableList.stream().filter(contact -> Objects.equals(contact.getID(), id)).forEach(contact -> contact.setOnline(false));
                MWC.lv_Contacts.setCellFactory(slw -> new ConactsListViewController());
                Platform.runLater(()->MWC.lv_Contacts.setCellFactory(slw -> new ConactsListViewController()));
            });
        }
    }

    static public void SendMessageToYou(String login){
        if(!Objects.equals(MWC.header_contacts_FIO.getText(), ""))
            if(Objects.equals(MWC.header_contacts_FIO.getText().substring(0, MWC.header_contacts_FIO.getText().indexOf(':') - 1), login)) {
                MWC.UpdateMessages();
                MWC.ReadMessage();
            }
        MWC.UpdateContacts();
    }
    static public void ReadYouMessage(String login){
        if(!Objects.equals(MWC.header_contacts_FIO.getText(), ""))
            if(Objects.equals(MWC.header_contacts_FIO.getText().substring(0, MWC.header_contacts_FIO.getText().indexOf(':') - 1), login))
                MWC.UpdateMessages();
    }

    static public void TakeUserAboutSecretDialog(String login, String nameCon, byte[] avatarCon, String nameMy, byte[] avatarMy){
        for (SecretDialogueWindowController SDWC: SDWCs) {
            if(Objects.equals(SDWC.getContact(), login)){
                Platform.runLater(()-> SDWC.l_con_name.setText(nameCon));
                Platform.runLater(()-> SDWC.l_my_name.setText(nameMy));
                Platform.runLater(()-> {
                    try {
                        SDWC.iv_con_logo.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteInputStream(avatarCon, avatarCon.length)), null));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Rectangle clip = new Rectangle(
                            SDWC.iv_con_logo.getFitWidth(), SDWC.iv_con_logo.getFitHeight()
                    );
                    clip.setArcWidth(35);
                    clip.setArcHeight(35);
                    SDWC.iv_con_logo.setClip(clip);

                    SnapshotParameters parameters = new SnapshotParameters();
                    parameters.setFill(Color.TRANSPARENT);
                    WritableImage image = SDWC.iv_con_logo.snapshot(parameters, null);
                    SDWC.iv_con_logo.setClip(null);
                    SDWC.iv_con_logo.setImage(image);
                });
                Platform.runLater(()-> {
                    try {
                        SDWC.iv_my_logo.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteInputStream(avatarMy, avatarMy.length)), null));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Rectangle clip = new Rectangle(
                            SDWC.iv_my_logo.getFitWidth(), SDWC.iv_my_logo.getFitHeight()
                    );
                    clip.setArcWidth(35);
                    clip.setArcHeight(35);
                    SDWC.iv_my_logo.setClip(clip);

                    SnapshotParameters parameters = new SnapshotParameters();
                    parameters.setFill(Color.TRANSPARENT);
                    WritableImage image = SDWC.iv_my_logo.snapshot(parameters, null);
                    SDWC.iv_my_logo.setClip(null);
                    SDWC.iv_my_logo.setImage(image);
                });
                break;
            }
        }
    }

    static public void CreateNewUser(String value){
        switch (value) {
            case "OK":
                Platform.runLater(()->LWC.setTf_Login(AWC.tf_login.getText()));
                Platform.runLater(()->LWC.setTf_Password(""));
                Platform.runLater(()->AWC.b_Close_Click());
                break;
            case "BadLogin":
                Platform.runLater(()->AWC.l_erro.setText("Данный логин уже имеется!!!"));
                break;
            case "Error":
                Platform.runLater(()->AWC.l_erro.setText("Произошла ошибка!!! Попробуйте еще."));
                break;
        }
    }

    static public void SendGPLToBob( String login, String aliceP, String aliceG, String aliceL, String alice) {
        SDWCs.stream().filter(SDWC -> Objects.equals(SDWC.getContact(), login)).forEach(SDWC -> {
            String bob = SDWC.doBob(aliceP, aliceG, aliceL, alice);
            SendMessage(new Message("SendBobToAlice", new String[]{ login, bob}, ""));
        });
    }

    static public void SendBobToAlice(String login, String bob) {
        SDWCs.stream().filter(SDWC -> Objects.equals(SDWC.getContact(), login)).forEach(SDWC -> SDWC.doAlice(bob));
    }

    static public void TakeSecretMessageYou(String login, String message) {
        SDWCs.stream().filter(SDWC -> Objects.equals(SDWC.getContact(), login)).forEach(SDWC -> Platform.runLater(()->SDWC.SetMessage(message)));
    }

    static public void SendMessage(Message message){
        Gson gson = new Gson();
        WS.WriteLine(gson.toJson(Crypt.encodeServerMessage(gson.toJson(message))));
    }

    static public void CloseSecDialog(String login){
        for (Iterator<SecretDialogueWindowController> iterator = SDWCs.iterator(); iterator.hasNext(); ) {
            SecretDialogueWindowController value = iterator.next();
            if (Objects.equals(value.getContact(), login)) {
                iterator.remove();
            }
        }
    }

    static public void SendMessageTo(String message, String login){
        SendMessage(new Message("SendMessageTo", new String[]{ login, message }, ""));
    }

    static public void AnswerOpenSecretDialog(String message, String id){
        switch (message){
            case "NoOnline": {
                Platform.runLater(()-> Dialogs.ShowAlert("Данный пользователь сейчас не онлайн!!!"));
                break;
            }
            case "No": {
                Platform.runLater(()-> Dialogs.ShowAlert("Пользователь " + id + " отказал в создании секретного диалога!!!"));
                break;
            }
            case "Yes": {
                Platform.runLater(()-> {
                    try {
                        Image ico = new Image("img/Icon32.png");
                        Stage accountStage = new Stage();
                        FXMLLoader connectLoader = new FXMLLoader(FunctionClient.class.getResource("../windows/SecretDialogueWindow.fxml"));
                        Parent root = connectLoader.load();
                        accountStage.getIcons().add(ico);
                        accountStage.initStyle(StageStyle.UNDECORATED);
                        Scene connectScene = new Scene(root, Color.TRANSPARENT);
                        accountStage.setScene(connectScene);
                        accountStage.setTitle("Секретный диалог - DarkCom by Daniel Sandrutski © 2017");
                        accountStage.initStyle(StageStyle.TRANSPARENT);
                        SecretDialogueWindowController connectController = connectLoader.getController();
                        SDWCs.add(connectController);
                        connectController.Initialization(accountStage, id, MWC.my_login.getText(), true);
                        accountStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            }
        }
    }

    static public void AgreementOpenSecretDialog(String id, String name){
        Platform.runLater(()-> {
            if(Dialogs.ShowQuestion("Пользователь " + name + " хочет  начать с вами секретный диалог. Нажмите \"Да\" если вы согласны.")){
                Platform.runLater(()-> {
                    try {
                        Image ico = new Image("img/Icon32.png");
                        Stage accountStage = new Stage();
                        FXMLLoader connectLoader = new FXMLLoader(FunctionClient.class.getResource("../windows/SecretDialogueWindow.fxml"));
                        Parent root = connectLoader.load();
                        accountStage.getIcons().add(ico);
                        accountStage.initStyle(StageStyle.UNDECORATED);
                        Scene connectScene = new Scene(root, Color.TRANSPARENT);
                        accountStage.setScene(connectScene);
                        accountStage.setTitle("Секретный диалог - DarkCom by Daniel Sandrutski © 2017");
                        accountStage.initStyle(StageStyle.TRANSPARENT);
                        SecretDialogueWindowController connectController = connectLoader.getController();
                        SDWCs.add(connectController);
                        connectController.Initialization(accountStage, id, MWC.my_login.getText(), false);
                        accountStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                SendMessage(new Message("AnswerOpenSecretDialog", new String[] { "Yes", id }, ""));
            } else {
                SendMessage(new Message("AnswerOpenSecretDialog", new String[] { "No", id }, ""));
            }
        });
    }

    static public void CreateNewUser(String login, String password, String FIO, String email, String country,  String about, BufferedImage avatar) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        SendMessage(new Message("CreateNewUser", new String[] {login, DatatypeConverter.printHexBinary(md.digest()), FIO, email, country, about, avatar == null ? null : extractBytes2(avatar)}, ""));
    }

    public static String extractBytes2(BufferedImage bufferedImage) {
        ByteOutputStream bos = null;
        try {
            bos = new ByteOutputStream();
            ImageIO.write(bufferedImage, "png", bos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bos != null;
                bos.close();
            } catch (Exception ignored) {
            }
        }
        Gson gson = new Gson();
        return gson.toJson(bos.getBytes());
    }

}
