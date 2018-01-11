package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import model.RandomGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by andreyserebryanskiy on 10/01/2018.
 */
public class GeneratorController {
    private final int OFFSET_FROM_BORDER = 20;
    private final String MUTE_SVG_PATH = "M5 17h-5v-10h5v10zm2-10v10l9 5v-20l-9 5zm15.324 4.993l1.646-1.659-1.324-1.324-1.651 1.67-1.665-1.648-1.316 1.318 1.67 1.657-1.65 1.669 1.318 1.317 1.658-1.672 1.666 1.653 1.324-1.325-1.676-1.656z";
    private final String SOUND_SVG_PATH = "M5 17h-5v-10h5v10zm2-10v10l9 5v-20l-9 5zm17 4h-5v2h5v-2zm-1.584-6.232l-4.332 2.5 1 1.732 4.332-2.5-1-1.732zm1 12.732l-4.332-2.5-1 1.732 4.332 2.5 1-1.732z";

    public StackPane root;
    public MediaView mediaView;
    public Label number;
    public SVGPath backBtn;
    public Button startBtn;
    public SVGPath muteSvg;

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
        root.maxWidthProperty().addListener((observable, oldValue, newValue) -> {
            double newFitWidth = newValue.doubleValue() * 0.3;
            imageView.setFitWidth(newFitWidth);
            imageView.setLayoutX((newValue.doubleValue() - newFitWidth) / 2);
            System.out.println(imageView.getFitWidth());
        });
        imageView.setLayoutY(OFFSET_FROM_BORDER);
        root.getChildren().add(imageView);

        backBtn.setManaged(false);
        muteSvg.setManaged(false);
        muteSvg.setLayoutY(OFFSET_FROM_BORDER);
        muteSvg.layoutXProperty().bind(root.maxWidthProperty()
                .subtract(OFFSET_FROM_BORDER + muteSvg.getBoundsInLocal().getWidth()));

        // set up video
        Media media = new Media(getClass().getResource("/media/video.mp4").toExternalForm());
        media.heightProperty().addListener((observable, oldValue, newValue) -> {
            correctVideoSize(media, newValue.doubleValue());
        });
        /*Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();

        });*/
        video = new MediaPlayer(media);
        video.setMute(true);
        video.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView.setMediaPlayer(video);
        mediaView.setManaged(false);

        // set up sound
        audio = new MediaPlayer(new Media(getClass().getResource("/media/waltz.mp3").toExternalForm()));
//        audio.setStopTime(Duration.millis(6500));
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

        // bind root size to window size
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            root.maxWidthProperty().bind(stage.widthProperty());
            root.maxHeightProperty().bind(stage.heightProperty());
            stage.heightProperty().addListener((obs, n, o) -> correctVideoSize(media, media.getHeight()));
            stage.setWidth(stage.getWidth() + 1);
            play();
        });

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
        stage.setScene(new Scene(root));
        stage.setWidth(400);
        stage.centerOnScreen();
    }

    public void handleMute(KeyEvent keyEvent) {
        if (keyEvent.getCharacter().equals("m") || keyEvent.getCharacter().equals("ь")) {
            changeMute();
        }
    }

    public void handleMuteFromBtn() {
        changeMute();
    }

    private void changeMute() {
        if (audio.isMute()) {
            audio.setMute(false);
            muteSvg.setContent(MUTE_SVG_PATH);
        } else {
            audio.setMute(true);
            muteSvg.setContent(SOUND_SVG_PATH);
        }
    }
}
