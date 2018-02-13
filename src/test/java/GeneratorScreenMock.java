import controllers.GeneratorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.RandomGenerator;

/**
 * Created by andreyserebryanskiy on 11/01/2018.
 */
public class GeneratorScreenMock extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/fonts/CRC35.OTF"), 13);
        Font.loadFont(getClass().getResourceAsStream("/fonts/CIRCE-EXTRABOLD.OTF"), 13);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Generator.fxml"));
        Parent root = loader.load();
        GeneratorController controller = loader.getController();
        controller.init(new RandomGenerator(0, 100, false), false);
        controller.setSameNumbers("2");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("МОЗ Генератор случайных чисел");
        primaryStage.show();
    }
}
