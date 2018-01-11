package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.media.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import model.RandomGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Created by andreyserebryanskiy on 10/01/2018.
 */
public class GeneratorController {
    public StackPane root;
    public MediaView mediaView;
    public Label number;
    public SVGPath backBtn;
    public Button startBtn;

    private MediaPlayer video;
    private boolean run;
    private Timeline animation;
    private RandomGenerator generator;
    private boolean disallowRepeats;
    private MediaPlayer audio;

    public void init(RandomGenerator generator, boolean disallowRepeats) {
        Image image = new Image(getClass().getResource("/media/logo.png").toString());
        ImageView imageView = new ImageView(image);
        imageView.setManaged(false);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(200);
        int OFFSET_FROM_BORDER = 20;
        root.maxWidthProperty().addListener((observable, oldValue, newValue) -> {
            double newFitWidth = newValue.doubleValue() * 0.3;
            imageView.setFitWidth(newFitWidth);
            imageView.setLayoutX((newValue.doubleValue() - newFitWidth) / 2);
        });
        /*imageView.layoutXProperty().bind(root.maxWidthProperty()
                .divide(2)
                .subtract(imageView.getFitWidth() / 2));*/
        imageView.setLayoutY(OFFSET_FROM_BORDER);
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            root.maxWidthProperty().bind(stage.widthProperty());
            root.maxHeightProperty().bind(stage.heightProperty());
        });
        root.getChildren().add(imageView);

        backBtn.setManaged(false);

        // set up video
        Media media = new Media(getClass().getResource("/media/video.mp4").toExternalForm());
        media.heightProperty().addListener((observable, oldValue, newValue) -> {
            correctVideoSize(media, newValue.doubleValue());
        });
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.heightProperty().addListener((obs, n, o) -> correctVideoSize(media, media.getHeight()));
            play();
        });
        video = new MediaPlayer(media);
        video.setMute(true);
        video.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView.setMediaPlayer(video);
        mediaView.setManaged(false);

        // set up sound
        audio = new MediaPlayer(new Media(getClass().getResource("/media/sound.mp3").toExternalForm()));
        audio.setStopTime(Duration.millis(6500));
        audio.setCycleCount(MediaPlayer.INDEFINITE);


        // set up random number generation animation
        this.generator = generator;
        this.disallowRepeats = disallowRepeats;
        animation = new Timeline();
        animation.setAutoReverse(true);
        animation.setCycleCount(Integer.MAX_VALUE);
        animation.getKeyFrames().add(new KeyFrame(Duration.millis(50), e ->
                number.setText(String.valueOf(generator.generate()))));

        number.setText(String.valueOf(generator.getMin()));
    }

    private void correctVideoSize(Media media, double newValue) {
        Window window = root.getScene().getWindow();
        if (window != null) {
            double newHeight = Math.min(window.getHeight(), newValue);
            mediaView.setFitHeight(newHeight);

            double newWidth = media.getWidth() * (newHeight / newValue);
            mediaView.setFitWidth(newWidth);
        }
    }

    private void play() {
        run = true;
        video.play();
        audio.play();
        animation.play();
        startBtn.setText("Остановить");
    }

    private void stop() {
        run = false;
        video.pause();
        audio.stop();
        animation.stop();
        startBtn.setText("Генерировать");

        if (disallowRepeats) number.setText(String.valueOf(generator.generateWithoutRepeats()));
    }

    public void handleGenerate() {
        if (run) stop();
        else     play();
    }

    public void handleBack(MouseEvent mouseEvent) {
        if (run) stop();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/RangeChoice.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        stage.setMaximized(false);
        stage.setFullScreen(false);
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }

    public void handleMute(KeyEvent keyEvent) {
        if (keyEvent.getCharacter().equals("m") || keyEvent.getCharacter().equals("ь")) {
            audio.setMute(!audio.isMute());
        }
    }
}
