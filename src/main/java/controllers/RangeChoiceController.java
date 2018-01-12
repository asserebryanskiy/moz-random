package controllers;

import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.RandomGenerator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by andreyserebryanskiy on 10/01/2018.
 */
public class RangeChoiceController implements Initializable{
    public TextField minField;
    public TextField maxField;
    public VBox root;

    private boolean disallowRepeats;    // if generated random numbers could repeat
    private Line minFieldLine;
    private Line maxFieldLine;

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
        Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.getIcons().addAll(oldStage.getIcons());
        stage.setTitle(oldStage.getTitle());
        oldStage.close();
        controller.init(new RandomGenerator(min, max, disallowRepeats), disallowRepeats);
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
        double rootWidth = 400;
        double rootHeight = 332;
        System.out.println(rootHeight);

        setUpField(minField);
        setUpField(maxField);

        double lineWidth = 100;
        double y = 150;
        double x = rootWidth / 2 - lineWidth / 2;
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
        btnNext.setLayoutX(rootWidth - btnSize - 20);
        btnNext.setLayoutY(rootHeight - btnSize - 20);
        btnNext.setOnMouseClicked(this::proceed);
        root.getChildren().add(btnNext);
    }

    private void setUpField(final TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
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

    private void scale(Line line, int newV, int defaultV) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(500), line);
        transition.setByX(line.getScaleX() != 0 ? newV : defaultV);
        transition.play();
    }
}
