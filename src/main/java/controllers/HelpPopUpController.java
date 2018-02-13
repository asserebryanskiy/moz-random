package controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

public class HelpPopUpController {
    @FXML
    private HBox contentRoot;
    @FXML
    private Rectangle background;
    @FXML
    private Text content;

    public void setContent(String message) {
        content.setText(message);
        background.setHeight(contentRoot.getBoundsInParent().getHeight() + 10);
    }

    public void handleChangeColor(MouseEvent event) {
        ((SVGPath)((Pane) event.getSource()).getChildren().get(0)).setFill(Color.GRAY);
    }

    public void handleResetColor(MouseEvent event) {
        ((SVGPath)((Pane) event.getSource()).getChildren().get(0)).setFill(Color.WHITE);
    }

    public void handleClosePopUp(MouseEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
}
