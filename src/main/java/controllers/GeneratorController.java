package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import model.RandomGenerator;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.lang.Thread.sleep;

public class GeneratorController {
    private static final int TOP_PANEL_HEIGHT = 22;     // height of the OS panel with controls such as "close", "full-screen", etc.
    private static final int OFFSET_FROM_BORDER = 20;   // offset from nearest border (either vertical or horizontal)
    private static final String MUTE_SVG_PATH = "M5 17h-5v-10h5v10zm2-10v10l9 5v-20l-9 5zm15.324 4.993l1.646-1.659-1.324-1.324-1.651 1.67-1.665-1.648-1.316 1.318 1.67 1.657-1.65 1.669 1.318 1.317 1.658-1.672 1.666 1.653 1.324-1.325-1.676-1.656z";
    private static final String SOUND_SVG_PATH = "M5 17h-5v-10h5v10zm2-10v10l9 5v-20l-9 5zm17 4h-5v2h5v-2zm-1.584-6.232l-4.332 2.5 1 1.732 4.332-2.5-1-1.732zm1 12.732l-4.332-2.5-1 1.732 4.332 2.5 1-1.732z";
    private static final double TEXT_HEIGHT_RATIO = 75d/190d;  // ratio of text height relative to video height
    private static final String MUSIC_SRC = "/media/fort-boyard-monety.mp3";
    private static final String VIDEO_SRC = "/media/moz.mp4";
    private static final String ABSENT_VALUE = "no_value"; // if we no value for alwaysSameNumber was specified
    private static final int NUMBER_CHANGE_FREQ = 100;   // duration in millis after which random number changes

    public StackPane root;
    public MediaView mediaView;
    public SVGPath backBtn;
    public Label number;
    public Button startBtn;
    public SVGPath muteSvg;
    public HBox audioControls;

    public Slider volumeSlider;         // regulates music volume
    private MediaPlayer video;          // media player, when video is shown (completely bugged!)
    private Media media;                // media with video
    private boolean run;                // if an application now is in a run phase? (video is playing, generator is working)
    private Timeline animation;         // animation of random numbers change
    private RandomGenerator generator;  // generated random numbers
    private boolean disallowRepeats;    // if generator is not allowed to generate the same value twice
    private MediaPlayer audio;          // mediaplayer for background sound
//    private Timer videoTimer;   // is needed because of strange behaviour of MediaPLayer cycleCount
                                // look for details ir prepareVideo() method
    private String alwaysSameNumber = ABSENT_VALUE; // optionable field that is set by the user
                                                    // to make generator always return the same value

    public void init(RandomGenerator generator, boolean disallowRepeats) {
//        initLogo();

        backBtn.setManaged(false);
        audioControls.setManaged(false);
        double height = audioControls.getBoundsInLocal().getHeight();
        audioControls.layoutYProperty().bind(root.maxHeightProperty()
                .subtract(OFFSET_FROM_BORDER + height + TOP_PANEL_HEIGHT));
        audioControls.setLayoutX(OFFSET_FROM_BORDER);
        volumeSlider.setMax(1);
        volumeSlider.setValue(1);
        // to make available pressing space button to start/stop playing
        volumeSlider.setOnMouseReleased(e -> startBtn.requestFocus());

        // set up video
        media = new Media(getClass().getResource(VIDEO_SRC).toExternalForm());
        prepareVideo();

        // set up sound
        audio = new MediaPlayer(new Media(getClass().getResource(MUSIC_SRC).toExternalForm()));
        audio.setStartTime(Duration.millis(15000));
        audio.setCycleCount(MediaPlayer.INDEFINITE);
        audio.volumeProperty().bind(volumeSlider.valueProperty());
        changeMute();   // sets default volume property to mute

        // set up random number generation animation
        this.generator = generator;
        this.disallowRepeats = disallowRepeats;
        animation = new Timeline();
        animation.setCycleCount(Integer.MAX_VALUE);
        animation.getKeyFrames().add(new KeyFrame(Duration.millis(NUMBER_CHANGE_FREQ), e ->
                number.setText(String.valueOf(generator.generate()))));
        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            // this is done here to address strange bug with delay of number change
            // to desired value (e.g, if alwaysSameNumber is turned on)
            if (e.getCode().equals(KeyCode.SPACE) && run) {
                substituteNumberIfNeeded();
            }
        });
        startBtn.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> substituteNumberIfNeeded());

        number.setText(String.valueOf(generator.getMin()));

        // bind root size to window size
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            /*stage.setOnCloseRequest(e -> {
                // shut down videoTimer
                shutDownTimer();
            });*/
            root.maxWidthProperty().bind(stage.widthProperty());
            root.maxHeightProperty().bind(stage.heightProperty());
            stage.heightProperty().addListener((obs, n, o) -> correctVideoSize(media));
            stage.setWidth(stage.getWidth() + 1);
            play();
        });

    }

    private void substituteNumberIfNeeded() {
        if (run) {
            long delay = (long) animation.getCycleDuration().subtract(animation.getCurrentTime()).toMillis();
            try {
                sleep(delay + 10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            animation.stop();
            if      (!alwaysSameNumber.equals(ABSENT_VALUE)) {
                number.setText(alwaysSameNumber);
            }
            else if (disallowRepeats)
                number.setText(String.valueOf(generator.generateWithoutRepeats()));
        }
    }

    private void prepareVideo() {
        media.heightProperty().addListener((observable, oldValue, newValue) -> correctVideoSize(media));
        prepareMediaPlayer();
        mediaView.setManaged(false);
    }

    private void prepareMediaPlayer() {
        video = new MediaPlayer(media);
        video.setMute(true);
        video.setCycleCount(MediaPlayer.INDEFINITE);
        video.setAutoPlay(true);
        mediaView.setMediaPlayer(video);
    }

    private void correctVideoSize(Media media) {
        Window window = root.getScene().getWindow();
        if (window != null) {
            // if both video width and height are bigger we set height and then move video on X axis
            double ratio = window.getHeight() / media.getHeight();
            // scale to the size of the screen
            mediaView.setFitHeight(window.getHeight());
            double newFitWidth = media.getWidth() * ratio;
            // if video width is now smaller than window width, set its width to the window's width
            if (newFitWidth < window.getWidth()) {
                mediaView.setFitWidth(window.getWidth());
                mediaView.setFitHeight(media.getHeight() * (window.getWidth() / media.getWidth()));
                mediaView.setLayoutX(0);
            } else {
                mediaView.setFitWidth(newFitWidth);
                // center on X axis
                mediaView.setLayoutX((window.getWidth() - newFitWidth) / 2);
            }

            // change size of the text
            double newFontSize = window.getHeight() * TEXT_HEIGHT_RATIO;
            number.setStyle("-fx-font-size:" + newFontSize);
            number.setTranslateY(newFontSize * 0.15);   // because it runs into logo if not
        }
    }

    private void play() {
        run = true;
        video.seek(Duration.ZERO);
        video.play();
        audio.play();
        animation.play();
        startBtn.setText("Остановить");
    }

    private void stop() {
        run = false;
        video.pause();
        audio.stop();
        // animation is stopped in the scene event filter, because of strange bug
        // with delay of number change to alwaysSameNumber. Change of number text
        // is also moved to scene event filter. Scene event filter is set in the
        // initialize() method.
//        animation.stop();
        startBtn.setText("Генерировать");
    }

    public void handleGenerate() {
        if (run) stop();
        else     play();
    }

    public void handleBack(MouseEvent mouseEvent) {
        if (run) stop();
        alwaysSameNumber = ABSENT_VALUE;
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/RangeChoice.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setWidth(400);
        stage.centerOnScreen();
    }

    public void handleApplyKeyboardShortcut(KeyEvent keyEvent) {
        if (keyEvent.getCharacter().equals("m") || keyEvent.getCharacter().equals("ь")) {
            changeMute();
        }
    }

    public void handleMuteFromBtn() {
        changeMute();
        startBtn.requestFocus();
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

    public void setSameNumber(String sameNumber) {
        this.alwaysSameNumber = sameNumber;
    }

    /*private void shutDownTimer() {
        if (videoTimer == null) return;
        videoTimer.cancel();
        videoTimer.purge();
        videoTimer = null;
    }*/

    /*private void initLogo() {
        Image image = new Image(getClass().getResource("/media/logo.png").toString());
        ImageView imageView = new ImageView(image);
        imageView.setManaged(false);
        imageView.setPreserveRatio(true);
        root.maxWidthProperty().addListener((observable, oldValue, newValue) -> {
            double newFitWidth = newValue.doubleValue() * 0.3;
            imageView.setFitWidth(newFitWidth);
            imageView.setLayoutX((newValue.doubleValue() - newFitWidth) / 2);
        });
        imageView.setLayoutY(OFFSET_FROM_BORDER);
        root.getChildren().add(imageView);
    }*/
}
