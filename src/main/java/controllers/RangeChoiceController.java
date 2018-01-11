package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
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

    private boolean disallowRepeats;    // if generated random numbers could repeat

    public void handleProceed(KeyEvent keyEvent) {
        if (minField.getText().isEmpty() || maxField.getText().isEmpty()) return;
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            // ToDo: load next screen
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
            controller.init(new RandomGenerator(min, max, disallowRepeats), disallowRepeats);
            Stage stage = (Stage) ((Node) keyEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.centerOnScreen();
        }
    }

    public void handleChangeAllowRepeats(ActionEvent actionEvent) {
        disallowRepeats = ((CheckBox) actionEvent.getSource()).isSelected();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpField(minField);
        setUpField(maxField);
    }

    private void setUpField(final TextField textField) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
}
