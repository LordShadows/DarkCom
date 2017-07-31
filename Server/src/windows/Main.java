package windows;

import implementationclasses.ConnectToMySQL;
import implementationclasses.JavaServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = loader.load();

        Image ico = new Image("img/Icon32.png");
        primaryStage.getIcons().add(ico);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root, Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server - DarkCom © 2017");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        MainWindowController controller = loader.getController();
        controller.Initialization(primaryStage);

        primaryStage.show();

        JavaServer.LIST = controller.lw_Clients;

        new Thread(() -> ConnectToMySQL.Test()).start();

        try
        {
            final JavaServer processor = new JavaServer(45000);
            final Thread thread = new Thread(processor);
            thread.setDaemon(true);
            thread.start();
        }
        catch (IOException ignore){}
    }


    public static void main(String[] args) {
        launch(args);
    }
}
