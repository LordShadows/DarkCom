package windows;

import implementationclasses.FunctionClient;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.regex.Pattern;

public class AccountWindowController {
    private Stage STAGE;

    private String LOGIN;
    private String FIO;
    private String EMAIL;
    private String COUNTRY;
    private String ABOUT;
    private javafx.scene.image.Image AVATAR;

    private double xOffset;
    private double yOffset;

    private BufferedImage image;

    @FXML
    private TextField tf_FIO;

    @FXML
    private ImageView iv_avatar;

    @FXML
    private TextField tf_about;

    @FXML
    private AnchorPane headerPane;

    @FXML
    public TextField tf_login;

    @FXML
    private PasswordField tf_password;

    @FXML
    private TextField tf_email;

    @FXML
    public Label l_erro;

    @FXML
    private ComboBox cb_Country;

    @FXML
    private Button b_ChangePassword;

    @FXML
    private Button b_SaveChange;

    @FXML
    private Button b_Create;

    @FXML
    public void b_Close_Click() { STAGE.close(); }

    @FXML
    void b_Min_Click() { STAGE.setIconified(true); }

    @FXML
    void b_Upload_Click() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузка вашего аватара...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File avatar = fileChooser.showOpenDialog(STAGE);
        if (avatar != null) {
            image = ImageIO.read(avatar);
            image = SubImage(image);
            iv_avatar.setImage(SwingFXUtils.toFXImage(image, null));
            Rectangle clip = new Rectangle(
                    iv_avatar.getFitWidth(), iv_avatar.getFitHeight()
            );
            clip.setArcWidth(150);
            clip.setArcHeight(150);
            iv_avatar.setClip(clip);

            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            WritableImage image = iv_avatar.snapshot(parameters, null);
            iv_avatar.setClip(null);
            iv_avatar.setImage(image);
        }
    }

    @FXML
    void b_Create_Click() throws NoSuchAlgorithmException {
        if(!Pattern.matches("^[a-zA-ZА-Яа-я_\\s]+$", tf_FIO.getText()) || !Pattern.matches("^.+@.+\\..+$", tf_email.getText()) || !Pattern.matches("^[a-zA-Z0-9_]+$", tf_login.getText()) || !Pattern.matches("^[a-zA-Z0-9_]+$", tf_password.getText())){
            l_erro.setText("Заполните все поля корректно!!!");
            return;
        }
        FunctionClient.CreateNewUser(tf_login.getText(), tf_password.getText(), tf_FIO.getText(), tf_email.getText(), cb_Country.getSelectionModel().getSelectedItem().toString(), tf_about.getText(), image);
    }

    @FXML
    void b_SaveChange_Click() {

    }

    private BufferedImage SubImage(BufferedImage image){
        BufferedImage resizedImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        Image scaled;
        if(image.getWidth() > image.getHeight()){
            scaled = image.getSubimage((image.getWidth() - image.getHeight()) / 2, 0, image.getHeight(), image.getHeight()).getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        }
        else {
            scaled = image.getSubimage(0, (image.getHeight() - image.getWidth()) / 2, image.getWidth(), image.getWidth()).getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        }
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        return resizedImage;
    }

    void Initialization(Stage stage, String login, String FIO, String email, String country, String about, javafx.scene.image.Image avatar) {
        STAGE = stage;
        LOGIN = login;
        this.FIO = FIO;
        EMAIL = email;
        COUNTRY = country;
        ABOUT = about;
        AVATAR = avatar;
        initializationComponent();
        tf_login.setText(login);
        tf_FIO.setText(FIO);
        tf_email.setText(email);
        tf_about.setText(about);
        iv_avatar.setImage(avatar);
        cb_Country.getSelectionModel().select(country);
        b_ChangePassword.setVisible(true);
        tf_password.setVisible(false);
        b_Create.setVisible(false);
        b_SaveChange.setVisible(true);
    }

    void Initialization(Stage stage) {
        STAGE = stage;
        initializationComponent();
        cb_Country.getSelectionModel().select("Соединённые Штаты Америки");

    }

    private void initializationComponent(){
        headerPane.setOnMousePressed(event -> {
            xOffset = STAGE.getX() - event.getScreenX();
            yOffset = STAGE.getY() - event.getScreenY();
        });
        headerPane.setOnMouseDragged(event -> {
            STAGE.setX(event.getScreenX() + xOffset);
            STAGE.setY(event.getScreenY() + yOffset);
        });
        FunctionClient.AWC = this;

        tf_FIO.setOnKeyReleased(event -> {
            if(Pattern.matches("^[a-zA-ZА-Яа-я_\\s]+$", tf_FIO.getText())){
                tf_FIO.setStyle("-fx-border-color: transparent transparent white transparent;");
                if(FIO != null)
                    if(!Objects.equals(tf_FIO.getText(), FIO))
                        tf_FIO.setStyle("-fx-border-color: transparent transparent #f5b74f transparent;");
            }
            else{
                tf_FIO.setStyle("-fx-border-color: transparent transparent #dd6660 transparent;");
            }
        });

        tf_email.setOnKeyReleased(event -> {
            if(Pattern.matches("^.+@.+\\..+$", tf_email.getText())){
                tf_email.setStyle("-fx-border-color: transparent transparent white transparent;");
                if(EMAIL != null)
                    if(!Objects.equals(tf_email.getText(), EMAIL))
                        tf_email.setStyle("-fx-border-color: transparent transparent #f5b74f transparent;");
            }
            else{
                tf_email.setStyle("-fx-border-color: transparent transparent #dd6660 transparent;");
            }
        });

        tf_login.setOnKeyReleased(event -> {
            if(Pattern.matches("^[a-zA-Z0-9_]+$", tf_login.getText())){
                tf_login.setStyle("-fx-border-color: transparent transparent white transparent;");
                if(LOGIN != null)
                    if(!Objects.equals(tf_login.getText(), LOGIN))
                        tf_login.setStyle("-fx-border-color: transparent transparent #f5b74f transparent;");
            }
            else{
                tf_login.setStyle("-fx-border-color: transparent transparent #dd6660 transparent;");
            }
        });

        tf_password.setOnKeyReleased(event -> {
            if(Pattern.matches("^[a-zA-Z0-9_]+$", tf_password.getText())){
                tf_password.setStyle("-fx-border-color: transparent transparent white transparent;");
            }
            else{
                tf_password.setStyle("-fx-border-color: transparent transparent #dd6660 transparent;");
            }
        });

        tf_about.setOnKeyReleased(event -> {
            if(ABOUT != null) {
                if (!Objects.equals(tf_about.getText(), ABOUT))
                    tf_about.setStyle("-fx-border-color: transparent transparent #f5b74f transparent;");
                else tf_about.setStyle("-fx-border-color: transparent transparent white transparent;");
            }

        });

        cb_Country.setOnAction(event -> {
            if(COUNTRY != null) {
                if (!Objects.equals(cb_Country.getSelectionModel().getSelectedItem().toString(), COUNTRY))
                    cb_Country.setStyle("-fx-border-color: transparent transparent #f5b74f transparent;");
                else cb_Country.setStyle("-fx-border-color: transparent transparent white transparent;");
            }
        });

        l_erro.setStyle("-fx-text-fill: #dd6660");

        cb_Country.getItems().addAll(
                "Абхазия",
                "Австралия",
                "Австрия",
                "Азербайджан",
                "Албания",
                "Алжир",
                "Ангола",
                "Андорра",
                "Антигуа и Барбуда",
                "Аргентина",
                "Армения",
                "Афганистан",
                "Багамские Острова",
                "Бангладеш",
                "Барбадос",
                "Бахрейн",
                "Беларусь",
                "Белиз",
                "Бельгия",
                "Бенин",
                "Болгария",
                "Боливия",
                "Босния и Герцеговина",
                "Ботсвана",
                "Бразилия",
                "Бруней",
                "Буркина Фасо",
                "Бурунди",
                "Бутан",
                "Вануату",
                "Ватикан",
                "Великобритания",
                "Венгрия",
                "Венесуэла",
                "Восточный Тимоp",
                "Вьетнам",
                "Габон",
                "Гаити",
                "Гайана",
                "Гамбия",
                "Гана",
                "Гватемала",
                "Гвинея",
                "Гвинея-Бисау",
                "Германия",
                "Гондурас",
                "Гренада",
                "Греция",
                "Грузия",
                "Дания",
                "Демократическая Республика Конго",
                "Джибути",
                "Доминиканская Республика",
                "Доминикана",
                "Египет",
                "Замбия",
                "Зимбабве",
                "Израиль",
                "Индия",
                "Индонезия",
                "Иордания",
                "Ирак",
                "Иран",
                "Ирландия",
                "Исландия",
                "Испания",
                "Италия",
                "Йемен",
                "Кабо-Верде",
                "Казахстан",
                "Камбоджа",
                "Камерун",
                "Канада",
                "Катар",
                "Кения",
                "Кипр",
                "Киргизия",
                "Кирибати",
                "Китай",
                "Колумбия",
                "Коморские острова",
                "КНДР",
                "Коста-Рика",
                "Кот-д’Ивуар",
                "Куба",
                "Кувейт",
                "Лаос",
                "Латвия",
                "Лесото",
                "Либерия",
                "Ливан",
                "Ливия",
                "Литва",
                "Лихтенштейн",
                "Люксембург",
                "Маврикий",
                "Мавритания",
                "Мадагаскар",
                "Македония",
                "Малави",
                "Малайзия",
                "Мали",
                "Мальдивы",
                "Мальта",
                "Марокко",
                "Маршалловы Острова",
                "Мексика",
                "Микронезия",
                "Мозамбик",
                "Молдова",
                "Монако",
                "Монголия",
                "Мьянма",
                "Намибия",
                "Науру",
                "Непал",
                "Нигер",
                "Нигерия",
                "Нидерланды",
                "Никарагуа",
                "Новая Зеландия",
                "Норвегия",
                "ОАЭ",
                "Оман",
                "Пакистан",
                "Палау",
                "Панама",
                "Папуа-Новая Гвинея",
                "Парагвай",
                "Перу",
                "Польша",
                "Португалия",
                "Республика Конго",
                "Республика Корея",
                "Россия",
                "Руанда",
                "Румыния",
                "Сальвадор",
                "Самоа",
                "Сан-Марино",
                "Сан-Томе и Принсипи",
                "Саудовская Аравия",
                "Свазиленд",
                "Северные Марианские острова",
                "Сейшелы",
                "Сенегал",
                "Сент-Винсент и Гренадины",
                "Сент-Китс и Невис",
                "Сент-Люсия",
                "Сербия",
                "Сингапур",
                "Сирия",
                "Словакия",
                "Словения",
                "Соединённые Штаты Америки",
                "Соломоновы Острова",
                "Сомали",
                "Судан",
                "Сьерра-Леоне",
                "Таджикистан",
                "Таиланд",
                "Танзания",
                "Того",
                "Тонга",
                "Тринидад и Тобаго",
                "Тувалу",
                "Тунис",
                "Туркмения",
                "Турция",
                "Уганда",
                "Узбекистан",
                "Украина",
                "Уругвай",
                "Фиджи",
                "Филиппины",
                "Финляндия",
                "Франция",
                "Хорватия",
                "Центральноафриканская Республика",
                "Чад",
                "Черногория",
                "Чехия",
                "Чили",
                "Швейцария",
                "Швеция",
                "Шри-Ланка",
                "Эквадор",
                "Экваториальная Гвинея",
                "Эритрея",
                "Эстония",
                "Эфиопия",
                "Южно-Африканская Республика",
                "Южный Судан",
                "Ямайка",
                "Япония");
    }
}
