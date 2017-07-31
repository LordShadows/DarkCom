package implementationclasses;

import classresourse.Message;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by DELL on 25.01.2017.
 * @author Daniel Sandrutski
 */

public class JavaServer implements Runnable{
    private final ServerSocket ss; 
    private Thread serverThread;
    public static ListView LIST;

    BlockingQueue<SocketProcessor> q = new LinkedBlockingQueue<>();
    
    /**
     * Конструктор объекта сервера
     */
    public JavaServer(int port) throws IOException {
        ss = new ServerSocket(port);
    }
    
    
    /**
     * Главный цикл прослушивания/ожидания коннекта.
     */
    public void run() {
        serverThread = Thread.currentThread(); 
        while (true) {
            Socket s = getNewConn(); 
            if (serverThread.isInterrupted()) { 
                
                break;
            } else if (s != null){ 
                try {
                    final SocketProcessor processor = new SocketProcessor(s); 
                    final Thread thread = new Thread(processor); 
                    thread.setDaemon(true); 
                    thread.start(); 
                    q.offer(processor); 
                } 
                catch (IOException ignored) {}
            }
        }
    }
    
    /**
     * Ожидает новое подключение.
     */
    private Socket getNewConn() {
        Socket s = null;
        try {
            s = ss.accept();
        } catch (IOException e) {
            shutdownServer(); 
        }
        return s;
    }
    
    /**
     * метод "глушения" сервера
     */
    public synchronized void shutdownServer() {
        q.forEach(SocketProcessor::close);
        if (!ss.isClosed()) {
            try {
                ss.close();
            } catch (IOException ignored) {}
        }
    }
    
    /**
     * Вложенный класс асинхронной обработки одного коннекта.
     */
    public class SocketProcessor implements Runnable{
        Socket s;
        BufferedReader br; 
        BufferedWriter bw;
        Crypt crypt;
        public String name = "";
        public String ip = "";
        public String id = "";
 
        /**
         * Сохраняем сокет, пробуем создать читателя и писателя. Если не получается - вылетаем без создания объекта
         */
        SocketProcessor(Socket socketParam) throws IOException {
            s = socketParam;
            br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8") );
            
        }
        
        public String getName()
        {
            return this.name;
        }

        public String getId()
        {
            return this.id;
        }
 
        /**
         * Главный цикл чтения сообщений/рассылки
         */
        @Override
        @SuppressWarnings("StringEquality")
        public void run() {
            ConnectToMySQL connector = new ConnectToMySQL();
            ServerFunctions functions = new ServerFunctions();
            crypt = new Crypt();
            Gson gson = new Gson();
            String line = null;
            Message message;

            // Принимаем сеансовый ключ
            try {
                line = br.readLine();
            } catch (IOException e) {
                close(); // если не получилось - закрываем сокет.
            }
            if(line != null){
                message = gson.fromJson(crypt.decryptRSA(gson.fromJson(line, byte[].class)), Message.class);
                crypt.getDESKey(gson.fromJson(message.getTextArguments()[0], byte[].class)); // Устанавливаем сеансовый ключ
            }
            // Отправляем сообщение о принятии сеансововго ключа

            byte[] mess = crypt.encodeServerMessage("IOK");

            send(gson.toJson(mess));

            try {
                line = br.readLine();
            } catch (IOException e) {
                close(); // если не получилось - закрываем сокет.
            }
            if(line != null){
                message = gson.fromJson(crypt.decodeServerMessage(gson.fromJson(line, byte[].class)), Message.class);
                ip = message.getTextArguments()[0];
                name = message.getTextArguments()[0] + ":" + Integer.toString((int)Math.floor(Math.random() * 1000)) + Integer.toString((int)Math.floor(Math.random() * 1000));
                Platform.runLater(()->list());
            }

            Platform.runLater(()->connector.History("System", " Был подключен новый клиент. IP-адрес: " + name + "."));
                
            while (!s.isClosed()) { 
                line = null;
                try {
                    line = br.readLine(); // Принимаем сообщение
                } catch (IOException e) {
                    Platform.runLater(()->connector.History("System", " Был отключен клиент. IP-адрес: " + name + "."));
                    Platform.runLater(()->connector.Update("UPDATE users SET LastDateAccess = CURRENT_TIMESTAMP WHERE Login = '" + id + "'"));
                    Platform.runLater(()->sendNotOnline());
                    Platform.runLater(()->list());
                    close(); // если не получилось - закрываем сокет.
                }
                
                if(line == null) return;

                message = gson.fromJson(crypt.decodeServerMessage(gson.fromJson(line, byte[].class)), Message.class);

                switch (message.getKeyword()) {
                    case "UpdateUserAccountData": {
                        functions.UpdateUserAccountData(connector, id, this);
                        break;
                    }
                    case "UpdateUserContactsData": {
                        functions.UpdateUserContactsData(connector, message.getTextArguments()[0], id, this);
                        break;
                    }
                    case "SearchContactsData": {
                        functions.SearchContactsData(connector, message.getTextArguments()[0], id, this);
                        break;
                    }
                    case "TakeInfoAboutUser": {
                        functions.TakeInfoAboutUser(connector, message.getTextArguments()[0], id, this);
                        break;
                    }
                    case "TakeUserMessages": {
                        functions.TakeUserMessages(connector, message.getTextArguments()[0], id, this);
                        break;
                    }
                    case "ReadMessage": {
                        functions.ReadMessage(connector, message.getTextArguments()[0], id, this);
                        break;
                    }
                    case "AddContactUser": {
                        functions.AddContactUser(connector, message.getTextArguments()[0], id, this);
                        break;
                    }
                    case "TakeUserAboutSecretDialog": {
                        functions.TakeUserAboutSecretDialog(connector, message.getTextArguments()[0], id, this);
                        break;
                    }
                    case "OpenSecretDialog": {
                        functions.OpenSecretDialog(connector, message.getTextArguments()[0], id, this);
                        break;
                    }
                    case "AnswerOpenSecretDialog": {
                        functions.AnswerOpenSecretDialog(connector, message.getTextArguments()[0], message.getTextArguments()[1], id, this);
                        break;
                    }
                    case "SendMessagesTo": {
                        functions.SendMessagesTo(connector, message.getTextArguments()[0], message.getTextArguments()[1], id, this);
                        break;
                    }
                    case "CreateNewUser": {
                        functions.CreateNewUser(connector, message.getTextArguments()[0], message.getTextArguments()[1], message.getTextArguments()[2], message.getTextArguments()[3], message.getTextArguments()[4], message.getTextArguments()[5], message.getTextArguments()[6], id, this);
                        break;
                    }
                    case "SendGPLToBob": {
                        functions.SendGPLToBob(connector, message.getTextArguments()[0], message.getTextArguments()[1], message.getTextArguments()[2], message.getTextArguments()[3], message.getTextArguments()[4], id, this);
                        break;
                    }
                    case "SendBobToAlice": {
                        functions.SendBobToAlice(connector, message.getTextArguments()[0], message.getTextArguments()[1], id, this);
                        break;
                    }
                    case "SendMessageTo": {
                        functions.SendMessageTo(connector, message.getTextArguments()[0], message.getTextArguments()[1], id, this);
                        break;
                    }
                    case "Login": {
                        try {
                            ResultSet rs = connector.Select("SELECT * FROM users WHERE Login = '" + message.getTextArguments()[0] + "' AND Password = '" + message.getTextArguments()[1] + "'");
                            rs.last();
                            int size = rs.getRow();
                            if(size == 0) {
                                sendMessage(new Message("Login", new String[] {"No"}, ""));
                                break;
                            } else {
                                sendMessage(new Message("Login", new String[] {"Yes"}, ""));
                                String fio = rs.getString("FIO");
                                Platform.runLater(() -> connector.History("System", name + "Авторизация прошла успешно: " + fio + "."));
                                name += " " + rs.getString("FIO");
                                id = rs.getString("Login");
                                Platform.runLater(()->sendIsOnline());
                                Platform.runLater(()->list());
                            }
                        } catch (SQLException sqlE) {
                            Platform.runLater(() -> connector.History("System", "Error: " +sqlE.toString()));
                            sqlE.printStackTrace();
                        }
                        break;
                    }
                    default:
                        Platform.runLater(() -> connector.History("System", "Ошибка определения функции."));
                        break;
                }
            }
        }

        /**
         * Метод посылает в сокет полученную строку
         * @param line строка на отсылку
         */
        public synchronized void send(String line) {
            try {
                bw.write(line); 
                bw.write("\n"); 
                bw.flush(); 
            } catch (IOException e) {
                close(); 
            }
        }

        public synchronized void sendMessage(Message message){
            Gson gson = new Gson();
            send(gson.toJson(crypt.encodeServerMessage(gson.toJson(message))));
        }

        public synchronized void list() {
            LIST.getItems().clear();
            for (SocketProcessor sp: q) {
                LIST.getItems().add(sp.getName());
            }
        }

        public synchronized void sendMessageTo(Message message, String id){
            for (SocketProcessor sp: q) {
                if(Objects.equals(sp.getId(), id)){
                    sp.sendMessage(message);
                    break;
                }
            }
        }

        public synchronized void updateSendMessage(String login){
            q.stream().filter(tsp -> Objects.equals(tsp.getId(), login)).forEach(tsp -> Platform.runLater(() -> tsp.sendMessage(new Message("SendMessageToYou", new String[]{ this.id }, ""))));
        }

        public synchronized void updateReadMessage(String login){
            q.stream().filter(tsp -> Objects.equals(tsp.getId(), login)).forEach(tsp -> Platform.runLater(() -> tsp.sendMessage(new Message("ReadYouMessage", new String[]{ this.id }, ""))));
        }

        public synchronized void sendIsOnline() {
            for (SocketProcessor tsp: q) {
                Platform.runLater(()-> tsp.sendMessage(new Message("UpdateContactOnline", new String[]{ this.id }, "")));
            }
        }

        public synchronized void sendNotOnline() {
            for (SocketProcessor tsp: q) {
                Platform.runLater(()-> tsp.sendMessage(new Message("UpdateContactNoOnline", new String[]{ this.id }, "")));
            }
        }

        public synchronized boolean isOnline(String id) {
            boolean flag = false;
            for (SocketProcessor sp: q) {
                if(Objects.equals(sp.getId(), id)){
                    flag = true;
                    break;
                }
            }
            return flag;
        }

 
        /**
         * метод аккуратно закрывает сокет и убирает его со списка активных сокетов
         */
        public synchronized void close() {
            q.remove(this); 
            if (!s.isClosed()) {
                try {
                    s.close(); 
                } catch (IOException ignored) {}
            }
        }
 
        /**
         * Финализатор
         * @throws Throwable
         */
        @Override
        @SuppressWarnings("FinalizeDeclaration")
        protected void finalize() throws Throwable {
            super.finalize();
            close();
        }
    }
}
