package implementationclasses;

import classresourse.SendContacts;
import classresourse.Message;
import classresourse.SendMessage;
import classresourse.User;
import com.google.gson.Gson;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import javafx.application.Platform;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class ServerFunctions {

    public void UpdateUserAccountData(ConnectToMySQL connector, String id, JavaServer.SocketProcessor sp){
        try {
            ResultSet rs = connector.Select("SELECT * FROM users WHERE Login = '" +  id + "'");
            rs.last();
            if( rs.getRow() > 0) {
                byte[] byteImage = extractBytes2("/upload/" + rs.getString("Avatar") + "60.png");
                Message message = new Message("UpdateUserAccountData", new String[] { rs.getString("Login"), rs.getString("FIO"), new Gson().toJson(byteImage)}, "");
                sp.sendMessage(message);
                Platform.runLater(() -> connector.History(id, "Запрос на полную информацию о себе. Отправлено."));
            }
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
            Platform.runLater(() -> connector.History("System", id + " : " + e.toString()));
        }

    }

    public void UpdateUserContactsData(ConnectToMySQL connector, String search, String id, JavaServer.SocketProcessor sp) {
        try {
            ResultSet rs = connector.Select("SELECT Us, ID, Login, FIO, Avatar, " +
                    "(SELECT count(*) FROM message WHERE IsRead = 0 AND message.Contact = Cont.ID AND message.User = Cont.Login) AS 'MessageNumber' " +
                    "FROM (" +
                    "SELECT * FROM ( SELECT DISTINCT User1 'Us', ID  FROM contacts WHERE User2 = '" + id + "' UNION SELECT DISTINCT User2 'Us', ID FROM contacts WHERE User1 = '" + id + "') CU " +
                    "INNER JOIN users ON CU.Us = users.Login " +
                    ") Cont " +
                    (!Objects.equals(search, "") ? "WHERE Login LIKE '%" + search + "%' OR FIO LIKE '%" + search + "%'" : "") +
                    "ORDER BY MessageNumber DESC, FIO");
            ArrayList<SendContacts> contacts = new ArrayList<>();
            if(rs!= null){
                rs.last();
                long size = rs.getRow();
                if( size > 0) {
                    rs.first();
                    for(long i = 0; i < size; i++) {
                        byte[] byteImage = extractBytes2("/upload/" + rs.getString("Avatar") + "35.png");
                        contacts.add(
                                new SendContacts(rs.getString("FIO"), sp.isOnline(rs.getString("Login")), byteImage, rs.getString("Login"), rs.getInt("MessageNumber"))
                        );
                        rs.next();
                    }
                }
            }
            Message message = new Message("UpdateUserContactsData", new String[]{ new Gson().toJson(contacts) }, "");
            sp.sendMessage(message);
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
            Platform.runLater(() -> connector.History("System", id + " : " + e.toString()));
        }
    }

    public static void SearchContactsData(ConnectToMySQL connector, String search, String id, JavaServer.SocketProcessor sp){
        try {
            ResultSet rs = connector.Select("SELECT Login, FIO, Avatar \n" +
                    "FROM (SELECT DISTINCT * FROM users WHERE NOT (SELECT COUNT(*) FROM contacts WHERE (User1 = '" + id + "' AND User2 = users.Login) OR (User2 = '" + id + "' AND User1 = users.Login)) AND NOT users.Login = '" + id + "') CU\n" +
                    (!Objects.equals(search, "") ? "WHERE Login LIKE '%" + search + "%' OR FIO LIKE '%" + search + "%'" : "") +
                    "ORDER BY FIO");
            ArrayList<SendContacts> contacts = new ArrayList<>();
            if(rs!= null){
                rs.last();
                long size = rs.getRow();
                if( size > 0) {
                    rs.first();
                    for(long i = 0; i < size; i++) {
                        byte[] byteImage = extractBytes2("/upload/" + rs.getString("Avatar") + "35.png");
                        contacts.add(
                                new SendContacts(rs.getString("FIO"), sp.isOnline(rs.getString("Login")), byteImage, rs.getString("Login"), 0)
                        );
                        rs.next();
                    }
                }
            }
            Message message = new Message("SearchContactsData", new String[]{ new Gson().toJson(contacts) }, "");
            sp.sendMessage(message);
            Platform.runLater(() -> connector.History(id, "Запрос на полную информацию о собственных контактах. Отправлено."));
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
            Platform.runLater(() -> connector.History("System", id + " : " + e.toString()));
        }
    }

    public static void TakeInfoAboutUser(ConnectToMySQL connector, String login, String id, JavaServer.SocketProcessor sp){
        try {
            ResultSet rs = connector.Select("SELECT * FROM users WHERE Login = '" + login + "'");
            User user = new User();
            if(rs != null){
                rs.first();
                user.setLogin(rs.getString("Login"));
                user.setCountry(rs.getString("Country"));
                user.setCreate_time(rs.getString("Create_time"));
                user.setEmail(rs.getString("Email"));
                user.setFIO(rs.getString("FIO"));
                user.setStatus(rs.getString("Status"));
                if(sp.isOnline(login)){
                    user.setLastDateAccess("Онлайн");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                    Date date = sdf.parse(rs.getString("LastDateAccess"));
                    Date dateNow = new Date();
                    user.setLastDateAccess((dateNow.getYear() - date.getYear() == 0 ? (dateNow.getMonth() - date.getMonth() == 0 ? (dateNow.getDay() - date.getDay() == 0 ? (dateNow.getHours() - date.getHours() == 0 ? (dateNow.getMinutes() - date.getMinutes() == 0 ? "30 сек. назад" : (String.valueOf(dateNow.getMinutes() - date.getMinutes()) + " мин. назад")) : (String.valueOf(dateNow.getHours() - date.getHours()) + " ч. назад")) : (String.valueOf(dateNow.getDay() - date.getDay()) + " дн. назад")) : (String.valueOf(dateNow.getMonth() - date.getMonth()) + " мес. назад")) : (String.valueOf(dateNow.getYear() - date.getYear() ) + " год(а) назад")));
                }
                byte[] byteImage = extractBytes2("/upload/" + rs.getString("Avatar") + "150.png");
                user.setAvatar(byteImage);
            }
            Message message = new Message("TakeInfoAboutUser", new String[]{ new Gson().toJson(user) }, "");
            sp.sendMessage(message);
            Platform.runLater(() -> connector.History(id, "Запрос на полную информацию о собственных контактах. Отправлено."));
        } catch (SQLException | IOException | URISyntaxException | ParseException e) {
            e.printStackTrace();
            Platform.runLater(() -> connector.History("System", id + " : " + e.toString()));
        }
    }

    public static void TakeUserAboutSecretDialog(ConnectToMySQL connector, String login, String id, JavaServer.SocketProcessor sp){
        try {
            ResultSet rsCon = connector.Select("SELECT * FROM users WHERE Login = '" + login + "'");
            ResultSet rsMy = connector.Select("SELECT * FROM users WHERE Login = '" + id + "'");
            if(rsCon != null && rsMy != null){
                rsCon.first();
                rsMy.first();
                byte[] byteImageCon = extractBytes2("/upload/" + rsCon.getString("Avatar") + "35.png");
                byte[] byteImageMy = extractBytes2("/upload/" + rsMy.getString("Avatar") + "35.png");
                Message message = new Message("TakeUserAboutSecretDialog", new String[]{ login, rsCon.getString("FIO"), new Gson().toJson(byteImageCon), rsMy.getString("FIO"), new Gson().toJson(byteImageMy) }, "");
                sp.sendMessage(message);
            }
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
            Platform.runLater(() -> connector.History("System", id + " : " + e.toString()));
        }
    }

    public void TakeUserMessages(ConnectToMySQL connector, String login, String id, JavaServer.SocketProcessor sp) {
        try {
            ResultSet rs = connector.Select("SELECT Text, DateWrite, FIO, Avatar, IsRead " +
                    "FROM message " +
                    "INNER JOIN users ON message.User = users.Login " +
                    "WHERE message.Contact = ANY (SELECT ID FROM ( SELECT ID  FROM contacts WHERE User2 = '" + id + "' AND User1 = '" + login + "' UNION SELECT ID FROM contacts WHERE User1 = '" + id + "' AND User2 = '" + login + "') TT) " +
                    "ORDER BY DateWrite");

            ArrayList<SendMessage> messages = new ArrayList<>();
            if(rs!= null){
                rs.last();
                long size = rs.getRow();
                if( size > 0) {
                    rs.first();
                    for(long i = 0; i < size; i++) {
                        byte[] byteImage = extractBytes2("/upload/" + rs.getString("Avatar") + "35.png");
                        messages.add(
                                new SendMessage(rs.getString("FIO"), rs.getString("Text"), rs.getString("DateWrite"), byteImage, rs.getInt("IsRead"))
                        );
                        rs.next();
                    }
                }
            }
            Message message = new Message("TakeUserMessages", new String[]{ new Gson().toJson(messages) }, "");
            sp.sendMessage(message);
            UpdateUserContactsData(connector, "", id, sp);
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
            Platform.runLater(() -> connector.History("System", id + " : " + e.toString()));
        }
    }

    public void ReadMessage(ConnectToMySQL connector, String login, String id, JavaServer.SocketProcessor sp) {
            connector.Update("UPDATE message " +
                    "SET IsRead = 1 " +
                    "WHERE message.Contact = ANY (SELECT ID FROM ( SELECT ID  FROM contacts WHERE User2 = '" + id + "' AND User1 = '" + login + "' UNION SELECT ID FROM contacts WHERE User1 = '" + id + "' AND User2 = '" + login + "') TT) " +
                    "AND NOT User = '" + id + "'");

            sp.updateReadMessage(login);
    }

    public void AddContactUser(ConnectToMySQL connector, String login, String id, JavaServer.SocketProcessor sp) {
        connector.Update("INSERT INTO contacts (`User1`, `User2`) " +
                "VALUES ('" + login + "', '" + id + "')");
        SearchContactsData(connector, "", id, sp);
    }

    static public void AnswerOpenSecretDialog(ConnectToMySQL connector, String message, String login, String id, JavaServer.SocketProcessor sp){
        switch (message){
            case "No": {
                sp.sendMessageTo(new Message("AnswerOpenSecretDialog", new String[]{"No", id}, ""), login);
                break;
            }
            case "Yes": {
                sp.sendMessageTo(new Message("AnswerOpenSecretDialog", new String[]{"Yes", id}, ""), login);
                break;
            }
        }
    }

    public void OpenSecretDialog(ConnectToMySQL connector, String login, String id, JavaServer.SocketProcessor sp) {
        if(sp.isOnline(login)){
            try {
                ResultSet rs = connector.Select("SELECT * FROM users WHERE Login = '" + id + "'");
                if(rs != null){
                    rs.first();
                    Message message = new Message("AgreementOpenSecretDialog", new String[]{ id, rs.getString("FIO") }, "");
                    sp.sendMessageTo(message, login);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> connector.History("System", id + " : " + e.toString()));
            }
        }
        else {
            Message message = new Message("AnswerOpenSecretDialog", new String[]{ "NoOnline", login }, "");
            sp.sendMessage(message);
        }
    }

    public void SendMessagesTo(ConnectToMySQL connector, String text,  String login, String id, JavaServer.SocketProcessor sp) {
        try {
            ResultSet rs = connector.Select("SELECT ID FROM ( SELECT ID  FROM contacts WHERE User2 = '" + id + "' AND User1 = '" + login + "' UNION SELECT ID FROM contacts WHERE User1 = '" + id + "' AND User2 = '" + login + "') UT");
            if(rs!= null){
                rs.last();
                long size = rs.getRow();
                if( size > 0) {
                    connector.Update("INSERT INTO `message` (`Text`,`Contact`,`User`) " +
                            "VALUES ('" + text + "', " +
                            "'" + rs.getString("ID") + "', " +
                            "'" + id + "')");
                }
                sp.updateSendMessage(login);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> connector.History("System", id + " : " + e.toString()));
        }
    }

    public void CreateNewUser(ConnectToMySQL connector, String login, String password, String FIO, String email, String country,  String about, String avatar, String id, JavaServer.SocketProcessor sp) {
        try {
            ResultSet rs = connector.Select("SELECT * FROM users WHERE Login = '" + login + "'");
            if(rs!= null){
                rs.last();
                long size = rs.getRow();
                if( size == 0) {
                    connector.Update("INSERT INTO `users` (`Login`, `Email`, `Password`, `Avatar`, `FIO`, `Status`, `Country`) " +
                            "VALUES ('" + login + "', " +
                            "'" + email + "', " +
                            "'" + password + "', " +
                            "'" + SaveAvatar(avatar) + "', " +
                            "'" + FIO + "', " +
                            "'" + about + "', " +
                            "'" + country + "')");
                    sp.sendMessage(new Message("CreateNewUser", new String[]{"OK"}, ""));
                }
                else{
                    sp.sendMessage(new Message("CreateNewUser", new String[]{"BadLogin"}, ""));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sp.sendMessage(new Message("CreateNewUser", new String[]{"Error"}, ""));
            Platform.runLater(() -> connector.History("System", id + " : " + e.toString()));
        }
    }

    public void SendGPLToBob(ConnectToMySQL connector, String login, String aliceP, String aliceG, String aliceL, String alice, String id, JavaServer.SocketProcessor sp) {
        sp.sendMessageTo(new Message("SendGPLToBob", new String[]{ id, aliceP, aliceG, aliceL, alice }, ""), login);
    }

    public void SendBobToAlice(ConnectToMySQL connector, String login, String bob, String id, JavaServer.SocketProcessor sp) {
        sp.sendMessageTo(new Message("SendBobToAlice", new String[]{ id, bob }, ""), login);
    }

    public void SendMessageTo(ConnectToMySQL connector, String login, String message, String id, JavaServer.SocketProcessor sp) {
        sp.sendMessageTo(new Message("TakeSecretMessageYou", new String[]{ id, message }, ""), login);
    }

    private static String SaveAvatar(String avatar){
        if(avatar == null) return "Default";
        Gson gson = new Gson();
        String nameAvatar = RandomAvatarName();
        byte[] byteAvatar = gson.fromJson(avatar, byte[].class);
        try {
            BufferedImage fromBytes150 = ImageIO.read(new ByteInputStream(byteAvatar, byteAvatar.length));
            BufferedImage fromBytes60 = SubImage(fromBytes150, 60);
            BufferedImage fromBytes35 = SubImage(fromBytes150, 35);
            ImageIO.write(fromBytes150, "png", new File(new File("").getAbsolutePath() + "/upload/" + nameAvatar + "150.png"));
            ImageIO.write(fromBytes60, "png", new File(new File("").getAbsolutePath() + "/upload/" + nameAvatar + "60.png"));
            ImageIO.write(fromBytes35, "png", new File(new File("").getAbsolutePath() + "/upload/" + nameAvatar + "35.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nameAvatar;
    }

    private static String RandomAvatarName(){
        String filename;
            do {
                UUID id = UUID.randomUUID();
                filename = id.toString().replaceAll("-", "");
            } while (new File(new File("").getAbsolutePath() + "/upload/" + filename + "150.png").exists());
        return filename;
    }

    public static byte[] extractBytes2(String ImageName) throws IOException, URISyntaxException {
        File imgPath = new File(new File("").getAbsolutePath() + ImageName);
        BufferedImage bufferedImage = ImageIO.read(imgPath);

        ByteOutputStream bos = null;
        try {
            bos = new ByteOutputStream();
            ImageIO.write(bufferedImage, "png", bos);
        } finally {
            try {
                assert bos != null;
                bos.close();
            } catch (Exception ignored) {
            }
        }
        return bos.getBytes();
    }

    private static BufferedImage SubImage(BufferedImage image, int size){
        BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        Image scaled;
        if(image.getWidth() > image.getHeight()){
            scaled = image.getSubimage((image.getWidth() - image.getHeight()) / 2, 0, image.getHeight(), image.getHeight()).getScaledInstance(size, size, Image.SCALE_SMOOTH);
        }
        else {
            scaled = image.getSubimage(0, (image.getHeight() - image.getWidth()) / 2, image.getWidth(), image.getWidth()).getScaledInstance(size, size, Image.SCALE_SMOOTH);
        }
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        return resizedImage;
    }

}