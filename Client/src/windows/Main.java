package windows;

import implementationclasses.WriteServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Daniel Sandrutski
 */

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Image ico = new Image("img/Icon32.png");
        // Приветсвуйте форму подключения
        Stage connectStage = new Stage();
        FXMLLoader connectLoader = new FXMLLoader(getClass().getResource("ConnectWindow.fxml"));
        Parent root = connectLoader.load();

        connectStage.getIcons().add(ico);
        connectStage.initStyle(StageStyle.UNDECORATED);
        Scene connectScene = new Scene(root, Color.TRANSPARENT);
        connectStage.setScene(connectScene);
        connectStage.setTitle("Доступ к серверу - DarkCom by Daniel Sandrutski © 2017");
        connectStage.initStyle(StageStyle.TRANSPARENT);
        ConnectWindowController connectController = connectLoader.getController();
        connectController.Initialization(connectStage);

        connectStage.showAndWait();

        // Приветствуйте форму авторизации
        Stage loginStage = new Stage();
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));
        Parent loginRoot = loginLoader.load();

        loginStage.getIcons().add(ico);
        loginStage.initStyle(StageStyle.UNDECORATED);
        Scene newscene = new Scene(loginRoot, Color.TRANSPARENT);
        loginStage.setScene(newscene);
        loginStage.setTitle("Авторизация - DarkCom by Daniel Sandrutski © 2017");
        loginStage.initStyle(StageStyle.TRANSPARENT);
        LoginWindowController loginController = loginLoader.getController();
        loginController.Initialization(loginStage);

        loginStage.showAndWait();

        Stage mainStage = new Stage();
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent mainRoot = mainLoader.load();

        mainStage.getIcons().add(ico);
        mainStage.initStyle(StageStyle.UNDECORATED);
        Scene mainScene = new Scene(mainRoot, Color.TRANSPARENT);
        mainStage.setScene(mainScene);
        mainStage.setTitle("DarkCom by Daniel Sandrutski © 2017");
        mainStage.initStyle(StageStyle.TRANSPARENT);
        MainWindowController mainController = mainLoader.getController();
        mainController.Initialization(mainStage);

        mainStage.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
