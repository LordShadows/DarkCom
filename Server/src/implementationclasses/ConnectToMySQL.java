package implementationclasses;

import windows.Dialogs;
import java.sql.*;
import java.text.SimpleDateFormat;
import javafx.application.Platform;

public class ConnectToMySQL {

    static private final String URL = "jdbc:mysql://localhost:3306/darkcom?zeroDateTimeBehavior=convertToNull";
    static private final String USER = "root";
    static private final String PASSWORD = "root";

    private Connection con;

    ConnectToMySQL(){
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static public void Test()
    {
        try 
        {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            con.createStatement();
            con.close();
        }
        catch (SQLException sqlE) 
        {
            Platform.runLater(() -> {
                Dialogs.ShowError("Тест на корректное подключение к базе данных провален!!! Приложение будет закрыто!!!");
                System.exit(0);
            });
        }
    }
    
    void Update(String query)
    {
        try
        {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        }
        catch (SQLException sqlE)
        {
            sqlE.printStackTrace();
            Platform.runLater(()->Dialogs.ShowError("Ошибка соединения с БД: " + sqlE.getMessage()));
        }
    }
    
    ResultSet Select(String query)
    {
        try {
            Statement stmt = con.createStatement();
            return stmt.executeQuery(query);
        }
        catch (SQLException sqlE)
        {
            sqlE.printStackTrace();
            Platform.runLater(()->Dialogs.ShowError("Ошибка соединения с БД: " + sqlE.getMessage()));
            return null;
        }
    }

    void History(String user, String message){
        try
        {
            Statement stmt = con.createStatement();
            java.util.Date date = new java.util.Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            stmt.executeUpdate("INSERT INTO History (Date, User, Text) VALUES ('" + format.format(date) + "', '" + user + "', '" + message + "')");
            stmt.close();
        }
        catch (SQLException sqlE)
        {
            Platform.runLater(()->Dialogs.ShowError("Ошибка соединения с базой данных!!!"));
        }
    }
}
