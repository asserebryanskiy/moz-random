import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Created by andreyserebryanskiy on 10/01/2018.
 */
public class Main extends Application {
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/fonts/CRC35.OTF"), 13);
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/CIRCE-EXTRABOLD.OTF"), 13);
        System.out.println(font.getName());
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/RangeChoice.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(getClass()
                .getResource("/media/logo.png").toExternalForm()));
        primaryStage.setTitle("МОЗ Генератор случайных чисел");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
