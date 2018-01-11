import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Created by andreyserebryanskiy on 10/01/2018.
 */
public class Main extends Application {
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/fonts/CRC35.OTF"), 13);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/RangeChoice.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("МОЗ Генератор случайных чисел");
        primaryStage.show();
    }
}
