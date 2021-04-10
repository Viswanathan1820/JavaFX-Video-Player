package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private AnchorPane anchor;
    @FXML
    private AnchorPane bottom;
    @FXML
    private HBox box;
    @FXML
    private MenuItem open;
    @FXML
    private MediaView mediaView1;
    @FXML
    private MediaPlayer player;
    @FXML
    private Media media;
    @FXML
    private Slider slider;
    @FXML
    private Slider volume;
    @FXML
    private Label timeLabel;
    private Timeline slideIn = new Timeline();
    private Timeline slideOut = new Timeline();
    private Duration duration;
    private double x;
    private double y;
    private boolean play = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeLabel.setText("--:--");
    }

    @FXML
    public void dragged(MouseEvent e) {
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        stage.setX(e.getScreenX()-x);
        stage.setY(e.getScreenY()-y);
    }
    @FXML
    public void pressed(MouseEvent e) {
        x=e.getSceneX();
        y = e.getSceneY();
    }
    @FXML
    public void closeapp(MouseEvent e) {
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        stage.close();

    }
    @FXML
    public void minimize(MouseEvent e) {
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    public void playVideo(MouseEvent m) {
        if(play) {
            player.play();
            play=false;
        }
        else {
            player.play();
            play=true;
        }
    }
    public void play(ActionEvent e) {
        if(player!=null) {
            player.play();
            player.setRate(1);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("You need to Open a video file");
            alert.setContentText("Press OK to open");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent()&&result.get()==ButtonType.OK) {
                vopen();
            }
        }
    }
    public void pause(ActionEvent e) {
        if(player!=null) {
            player.pause();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("You need to Open a video file");
            alert.setContentText("Press OK to open");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent()&&result.get()==ButtonType.OK) {
                vopen();
            }
        }
    }
    public void vopen() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open Video");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4","*.mp4"));
            File file = chooser.showOpenDialog(anchor.getScene().getWindow());
            if(file!=null) {
                media = new Media(file.toURI().toString());
                player = new MediaPlayer(media);
                mediaView1.setMediaPlayer(player);
                slider.setMin(0);
                playingVideo();
                player.play();
            }
        }
        catch(MediaException m) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("This video player does not suppot this file");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent()&&result.get()==ButtonType.OK) {
                alert.close();
            }
        }
    }
    public void videoopen(ActionEvent e) {
        try {
            if(player!=null) {
                player.stop();
            }
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open Video");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4","*.mp4"));
            File file = chooser.showOpenDialog(anchor.getScene().getWindow());
            if(file!=null) {
                media = new Media(file.toURI().toString());
                player = new MediaPlayer(media);
                mediaView1.setMediaPlayer(player);
                slider.setMin(0);
                playingVideo();
                player.play();
            }
        }
        catch(MediaException m) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("This video player does not suppot this file");
            alert.setContentText("Press Ok to open another file");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent()&&result.get()==ButtonType.OK) {
                vopen();
            }

        }
    }
    public void updateDuration() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                duration = player.getMedia().getDuration();
                Duration currentTime = player.getCurrentTime();
                timeLabel.setText(formatTime(currentTime, duration));
            }

        });
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    public void playingVideo() {
        player.setOnReady(new Runnable() {

            @Override
            public void run() {
                double h = bottom.getHeight();
                int w = player.getMedia().getWidth();
                slider.setMin(0.0);
                slider.setValue(0.0);
                slider.setMax(player.getTotalDuration().toSeconds());
                volume.setValue(player.getVolume()*100);

                updateDuration();
                slideIn.getKeyFrames().addAll(
                        new KeyFrame(new Duration(0),
                                new KeyValue(bottom.translateYProperty(),h),
                                new KeyValue(bottom.opacityProperty(),0)
                        ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(bottom.translateYProperty(),h-100),
                                new KeyValue(bottom.opacityProperty(),0.9)
                        )
                );
                slideOut.getKeyFrames().addAll(
                        new KeyFrame(new Duration(0),
                                new KeyValue(bottom.translateYProperty(),h-100),
                                new KeyValue(bottom.opacityProperty(),0.9)
                        ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(bottom.translateYProperty(),h),
                                new KeyValue(bottom.opacityProperty(),0)
                        )
                );

            }

        });

        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {

            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                slider.setValue(newValue.toSeconds());
                updateDuration();
            }

        });
        slider.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                player.seek(Duration.seconds(slider.getValue()));
            }

        });
        slider.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                player.seek(Duration.seconds(slider.getValue()));
            }

        });
        anchor.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                slideIn.play();
            }

        });
        anchor.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                slideOut.play();
            }

        });
        volume.valueProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                player.setVolume(volume.getValue()/100);
            }

        });
        DoubleProperty width = mediaView1.fitWidthProperty();
        DoubleProperty height = mediaView1.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView1.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView1.sceneProperty(), "height"));

    }
}
