package windows;

import dialogwindows.ErrorWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by DELL on 08.02.2017.
 * @author Daniel Sandrutski
 */
public class Dialogs {
    static public void ShowError(String error){
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Dialogs.class.getResource("../dialogwindows/ErrorWindow.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image ico = new Image("img/Icon32.png");
        stage.getIcons().add(ico);
        stage.initStyle(StageStyle.UNDECORATED);
        assert root != null;
        Scene scene = new Scene(root, Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("Ошибка - DarkCom © 2017");
        stage.initStyle(StageStyle.TRANSPARENT);
        ErrorWindowController controller = loader.getController();
        controller.Initialization(stage, error);
        stage.showAndWait();
    }
}
