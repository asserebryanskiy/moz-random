package model;

import controllers.HelpPopUpController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Popup;
import javafx.stage.Screen;

import java.io.IOException;

/**
 * HelpPopUp has custom design.
 */
public class HelpPopUp extends Popup {
    private String parentsNodeId;

    public HelpPopUp(Node node, String message) {
        parentsNodeId = node.getId();
        preparePopup(message);
    }

    public HelpPopUp(String message) {
        preparePopup(message);
    }

    private void preparePopup(String message) {
        setAutoHide(true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HelpPopUp.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        HelpPopUpController controller = loader.getController();
        controller.setContent(message);

        getContent().add(root);

        setOnShown(event -> {
            HelpPopUp source = (HelpPopUp) event.getSource();
            double screenX = source.getX() + source.getWidth();
            double screenRightBorder = Screen.getPrimary().getBounds().getMaxX();
            if(screenX + 100 > screenRightBorder) setX(screenX - 2 * getWidth());
        });
    }

    public String getParentsNodeId() {
        return parentsNodeId;
    }
}
