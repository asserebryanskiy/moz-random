package controllers;

import com.sun.javafx.tk.Toolkit;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import model.AsnStrategy;
import model.RandomGenerator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by andreyserebryanskiy on 10/01/2018.
 */
public class RangeChoiceController implements Initializable {

    private static final int ROOT_WIDTH = 400;
    private static final int ROOT_HEIGHT = 332;
    public TextField minField;
    public TextField maxField;
    public VBox root;
    public TextField sameNumberField;
    public CheckBox disallowRepeatsCheckbox;

    private boolean disallowRepeats;    // if generated random numbers could repeat
    private Line minFieldLine;
    private Line maxFieldLine;

    private static Pane asnStrategyBox = createAsnStrategyBox();
    // indicates if now showing/hiding animation of asnStrategyBox is running
    // is introduced to prevent double launching of animation of fast text input
    private boolean asnAnimationRunning = false;
    private static ToggleGroup asnStrategyToggleGroup;

    public void handleProceed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            proceed(keyEvent);
        }
    }

    private void proceed(Event event) {
        if (minField.getText().isEmpty() || maxField.getText().isEmpty()) return;
        int min = Integer.parseInt(minField.getText());
        int max = Integer.parseInt(maxField.getText());
        if (min >= max) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Верхняя граница должна быть больше нижней");
            alert.show();
            return;
        }
        String sameNumbers = sameNumberField.getText();
        if (!sameNumbers.isEmpty() && !sameNumbers.matches("^(\\d+|\\d+-\\d+)(,?(\\d+|\\d+-\\d+))*$")) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Неправильный формат поля \"Всегда выводить числа\"");
            alert.show();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Generator.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Не удалось загрузить окно генератора случайных чисел");
            alert.show();
            return;
        }
        GeneratorController controller = loader.getController();

        // we need to change stage, because for some strange bug when we load on new stage video is not showed
        // unless we resize the window
        Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.getIcons().addAll(oldStage.getIcons());
        stage.setTitle(oldStage.getTitle());
        oldStage.close();
        controller.init(new RandomGenerator(min, max, disallowRepeats), disallowRepeats);
        if (!sameNumbers.isEmpty()) {
            controller.setSameNumbers(sameNumbers);
            if (asnStrategyBox.isVisible()) {
                AsnStrategy asnStrategy = ((RadioButton) asnStrategyToggleGroup.getSelectedToggle())
                        .getText().equals("Последовательно") ?
                        AsnStrategy.SUBSEQUENT : AsnStrategy.RANDOM;
                controller.setAsnStrategy(asnStrategy);
            }
        }
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
        stage.setResizable(true);
        stage.centerOnScreen();
    }

    public void handleChangeAllowRepeats(ActionEvent actionEvent) {
        disallowRepeats = ((CheckBox) actionEvent.getSource()).isSelected();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpField(minField);
        setUpField(maxField);

        double oneLetterWidth = Toolkit.getToolkit().getFontLoader()
                .computeStringWidth("5", sameNumberField.getFont()) + 2;
        sameNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^(\\d+|\\d+-\\d+)(,?(\\d+|\\d+-\\d+))*$")) {
                sameNumberField.setText(newValue.replaceAll("[а-яА-Яa-zA-Z]", ""));
            }
            String text = sameNumberField.getText();
            if (!text.isEmpty()) {
                disallowRepeatsCheckbox.setSelected(false);
                disallowRepeatsCheckbox.setDisable(true);
            } else disallowRepeatsCheckbox.setDisable(false);
            sameNumberField.setPrefWidth(Math.max(40, text.length() * oneLetterWidth + 10));
            if ((text.contains(",") || text.contains("-"))) {
                if (!asnStrategyBox.isVisible() && !asnAnimationRunning) {
                    showAsnStrategyBox(true);
                }
            } else if (asnStrategyBox.isVisible() && !asnAnimationRunning) {
                showAsnStrategyBox(false);
            }
        });

        double lineWidth = 100;
        double y = 150;
        double x = ROOT_WIDTH / 2 - lineWidth / 2;
        minFieldLine = new Line(x, y, x + lineWidth, y);
        minFieldLine.setManaged(false);
        minFieldLine.setStroke(Color.WHITE);
        root.getChildren().add(minFieldLine);

        y = 280;
        maxFieldLine = new Line(x, y, x + lineWidth, y);
        maxFieldLine.setManaged(false);
        maxFieldLine.setStroke(Color.WHITE);
        root.getChildren().add(maxFieldLine);

        SVGPath btnNext = new SVGPath();
        btnNext.setContent("M24 12l-12-9v5h-12v8h12v5l12-9z");
        btnNext.setId("btnNext");
        btnNext.setManaged(false);
        double scale = 1.5;
        double btnSize = 24 * scale;
        btnNext.setScaleX(scale);
        btnNext.setScaleY(scale);
        btnNext.setLayoutX(ROOT_WIDTH - btnSize - 20);
        btnNext.setLayoutY(ROOT_HEIGHT - btnSize - 20);
        btnNext.setOnMouseClicked(this::proceed);
        root.getChildren().add(btnNext);
    }

    // is static to be created on class load
    private static Pane createAsnStrategyBox() {
        StackPane boxRoot = new StackPane();
        VBox box = new VBox();
        Label label = new Label("Показывать числа из выбранного диапазона");
        label.setId("asnStrategyLabel");
        asnStrategyToggleGroup = new ToggleGroup();
        RadioButton subsequent = new RadioButton("Последовательно");
        RadioButton random = new RadioButton("Случайно");
        subsequent.setToggleGroup(asnStrategyToggleGroup);
        random.setToggleGroup(asnStrategyToggleGroup);
        asnStrategyToggleGroup.selectToggle(subsequent);
        box.getChildren().addAll(label, subsequent, random);

        Rectangle clip = new Rectangle(400 - 40, 72, Color.rgb(78,53,73));
        boxRoot.getChildren().addAll(box, clip);
        boxRoot.setVisible(false);
        boxRoot.setAlignment(Pos.BOTTOM_LEFT);
        return boxRoot;
    }

    private void showAsnStrategyBox(boolean show) {
        if (show) {
            asnStrategyBox.setVisible(true);
            root.getChildren().add(asnStrategyBox);
        }
        double heightIncrease = show ? 72 : -72; // height of the asnStrategyBox
        int duration = 500;         // in milliseconds
        int fps      = 50;          // times in second
        int period   = 1000 / fps;  // in millis
        double shift = heightIncrease / (duration / period);
        Timer timer = new Timer();
        Window window = root.getScene().getWindow();
        Rectangle clip = (Rectangle) asnStrategyBox.getChildren().get(1);
        if (!show) clip.setVisible(true);
        asnAnimationRunning = true;
        double previousHeight = window.getHeight();
        timer.scheduleAtFixedRate(new TimerTask() {
            double heightChange = 0;

            @Override
            public void run() {
                if ((show && heightChange < heightIncrease) || (!show && heightChange > heightIncrease)) {
                    window.setHeight(previousHeight + heightChange);
                    clip.setHeight(clip.getHeight() - shift);
                } else {
                    asnAnimationRunning = false;
                    if (show) {
                        clip.setVisible(false);
                    } else {
                        Platform.runLater(() -> {
                            asnStrategyBox.setVisible(false);
                            root.getChildren().remove(asnStrategyBox);
                        });
                    }
                    timer.cancel();
                }
                heightChange += shift;
            }
        }, 0, period);

    }


    private void setUpField(final TextField textField) {
        setDigitVerification(textField);
        // set up animation
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                String id = textField.getId();
                if (id.startsWith("min")) scale(minFieldLine, -1, 0);
                else                      scale(maxFieldLine, -1, 0);
            } else {
                if (textField.getText().isEmpty()) {
                    String id = textField.getId();
                    if (id.startsWith("min")) scale(minFieldLine, 0, 1);
                    else                      scale(maxFieldLine, 0, 1);
                }
            }
        });
    }

    // checks if input is only digits
    private void setDigitVerification(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^0-9]+", ""));
            }
        });
    }

    private void scale(Line line, int newV, int defaultV) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(500), line);
        transition.setByX(line.getScaleX() != 0 ? newV : defaultV);
        transition.play();
    }
}
