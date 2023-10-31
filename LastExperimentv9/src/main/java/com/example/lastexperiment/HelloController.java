package com.example.lastexperiment;

// import libraries
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
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import static Server.Client.clientSocketList;
import static com.example.lastexperiment.HelloApplication.clientCommandList;


// controller class for our program
public class HelloController {

    //initial helper variables
    private MediaPlayer mediaPlayer;
    private String filePath;
    private boolean changeMute = false;
    private PrintWriter outMessage;
    private Duration duration;



    // message sending to the server
    public void sendStopMessage(String command) {
        outMessage.println(command);
        outMessage.flush();
    }


    // needed FXML variables

    @FXML
    private StackPane mainStackPane;

    @FXML
    private Button playButton;
    @FXML
    private Slider slider;

    @FXML
    private HBox sliderBox;

    @FXML
    private Slider seekSlider;

    @FXML
    private MediaView mediaView;

    @FXML
    protected Button muteButton;

    @FXML
    protected Button pauseButton;

    @FXML
    protected Button stopButton;

    @FXML
    protected Button openfileButton;

    @FXML
    protected Button fasterButton;

    @FXML
    protected Button slowerButton;

    @FXML
    protected BorderPane borderPane;

    @FXML
    protected VBox footer;

    @FXML
    protected Text timetext;

    @FXML
    protected Text hostJoin;




    //play icon
    String playPath = FileSystems.getDefault().getPath("src\\main\\resources\\icons\\play.png").toAbsolutePath().toString();
    Image playit = new Image(playPath);
    ImageView playIt = new ImageView(playit);

    //pause icon
    String pausePath = FileSystems.getDefault().getPath("src\\main\\resources\\icons\\pause.png").toAbsolutePath().toString();
    Image pauseit = new Image(pausePath);
    ImageView pauseIt = new ImageView(pauseit);

    //mute icon
    String mutePath = FileSystems.getDefault().getPath("src\\main\\resources\\icons\\noVolume.png").toAbsolutePath().toString();
    Image muteit = new Image(mutePath);
    ImageView muteIt = new ImageView(muteit);

    //unmute icon
    String unmutePath = FileSystems.getDefault().getPath("src\\main\\resources\\icons\\volume.png").toAbsolutePath().toString();
    Image unmuteit = new Image(unmutePath);
    ImageView unmuteIt = new ImageView(unmuteit);


    // Formating time to text
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


    protected void updateValues()
    {
        Platform.runLater(new Runnable() {
            public void run()
            {
                // Updating to the new time value
                // This will move the slider while running your video
                seekSlider.setValue(mediaPlayer.getCurrentTime().toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100);
                timetext.setText(formatTime(mediaPlayer.getCurrentTime(), duration));
            }
        });
    }

    // to remove footer in full screen
    @FXML
    protected  void setFooter(){
        borderPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            private boolean once1 = false;
            @Override
            public void handle(MouseEvent mouseEvent) {
                // if clicked twice
                if(mouseEvent.getClickCount() == 2 ){
                    if(once1 == false) {
                        borderPane.getChildren().removeAll(footer);
                        mainStackPane.getChildren().removeAll(sliderBox);

//                        footer.setMaxHeight(0);
                        once1 = true;
                    }else if(once1 == true){       // if clicked twice next time
                        //footer.setMinHeight(40);
                        borderPane.setBottom(footer);
                        mainStackPane.setAlignment(Pos.BOTTOM_CENTER);
                        mainStackPane.getChildren().addAll(sliderBox);
                        once1 = false;
                    }
                }

            }
        });
    }

    // the first button to press for file opening
    @FXML
    protected void handleButtonAction(ActionEvent event){
        always(event);
        // file choosing
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select mp4 file", "*.mp4");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(null);
        try {
            filePath = file.toURI().toString();
        }
        catch(NullPointerException e) {
            System.out.println("Null Value");
        }

        try{
            // in case if already running the media
            mediaPlayer.stop();
            seekSlider.setValue(0);
        }catch (Exception e)
        {
            System.out.println("ku ku");
        }




        if(filePath != null){
            Media media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            DoubleProperty width = mediaView.fitWidthProperty();
            DoubleProperty height = mediaView.fitHeightProperty();

            width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            slider.setValue(mediaPlayer.getVolume() * 100);
            slider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaPlayer.setVolume(slider.getValue()/100);
                }
            });
            slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    changeMute = false;
                    muteButton.setGraphic(unmuteIt);
                }
            });



            seekSlider.valueProperty().addListener(new InvalidationListener() {
                public void invalidated(Observable ov) {
                    if (seekSlider.isValueChanging()) {
                        // multiply duration by percentage calculated by slider position
                        mediaPlayer.seek(duration.multiply(seekSlider.getValue()/100.0));
                    }
                }
            });


            // for styling slider
            StackPane trackPane = (StackPane) seekSlider.lookup(".track");

            seekSlider.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                    String style = String.format("-fx-background-color: linear-gradient(to right, #8A2BE2 %d%%, #969696 %d%%);",
                            new_val.intValue(), new_val.intValue());
                    trackPane.setStyle(style);
                }
            });

            trackPane.setStyle("-fx-background-color: linear-gradient(to right, #2D819D 0%, #969696 0%);");

            mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                public void invalidated(Observable ov)
                {
                    duration = mediaPlayer.getMedia().getDuration();
                    updateValues();
                }
            });



            // Make all buttons enable
            playButton.setDisable(false);
            slider.setDisable(false);
            seekSlider.setDisable(false);
            muteButton.setDisable(false);
            stopButton.setDisable(false);
            pauseButton.setDisable(false);
            slowerButton.setDisable(false);
            fasterButton.setDisable(false);
            mediaPlayer.play();
            hostJoin.setVisible(false);
        }
    }

    @FXML
    synchronized protected void stopVideo(ActionEvent event){
        try {
                try {
                    outMessage = new PrintWriter(clientSocketList.get(0).getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            mediaPlayer.stop();
            seekSlider.setValue(0);
            seekSlider.setDisable(true);
            sendStopMessage("##session##stop##");
        }catch (Exception e){
            System.out.println("Not running");
        }
    }

    @FXML
    synchronized protected void stopVideoRecieved(ActionEvent event){
        try {
                try {
                    outMessage = new PrintWriter(clientSocketList.get(0).getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            mediaPlayer.stop();
            seekSlider.setValue(0);
            seekSlider.setDisable(true);
        }catch (Exception e){
            System.out.println("Not running");
        }
    }


    @FXML
    synchronized  protected void playVideo(ActionEvent event){
        try{
            seekSlider.setDisable(false);

            mediaPlayer.play();
            mediaPlayer.setRate(1);
            sendStopMessage("##session##play##");
        }catch (Exception e){
            System.out.println("Not running");
        }
    }

    @FXML
    synchronized protected void pauseVideo(ActionEvent event){
        try{

                mediaPlayer.pause();
                sendStopMessage("##session##pause##");
        }catch (Exception e){
            System.out.println("Not running");
        }

    }


    @FXML
    synchronized  protected void playVideoRecieved(ActionEvent event){
        try{
            seekSlider.setDisable(false);
            mediaPlayer.play();
            mediaPlayer.setRate(1);
        }catch (Exception e){
            System.out.println("Not running");
        }
    }

    @FXML
    synchronized protected void pauseVideoRecieved(ActionEvent event){
        try{

            mediaPlayer.pause();
        }catch (Exception e){
            System.out.println("Not running");
        }

    }


    @FXML
    protected void fasterVideo(ActionEvent event){
        try{
            mediaPlayer.play();
            mediaPlayer.setRate(mediaPlayer.getRate()*1.2);
        }catch (Exception e){
            System.out.println("Not running");
        }
    }


    @FXML
    protected void slowerVideo(ActionEvent event){
        try{
            mediaPlayer.play();
            mediaPlayer.setRate(mediaPlayer.getRate()*0.8);
        }catch (Exception e){
            System.out.println("Not running");
        }
    }


    @FXML
    protected void muteSound(ActionEvent event){

        try {
            if (changeMute == false) {
                mediaPlayer.setVolume(0);
                changeMute = true;
                muteButton.setGraphic(muteIt);
            } else if (changeMute == true) {
                mediaPlayer.setVolume(slider.getValue() / 100);
                changeMute = false;
                muteButton.setGraphic(unmuteIt);
            }
        }catch (Exception e){
            System.out.println("Media Player is not running..");
        }
    }

    private void always(ActionEvent event) {
        Runnable checkerCommands = () ->
        {
            do {

                if (clientCommandList.get(0).equals("##session##stop##")) {
                    stopVideoRecieved(event);
                    clientCommandList.clear();
                    clientCommandList.add(0, "");
                } else if (clientCommandList.get(0).equals("##session##pause##")) {
                    pauseVideoRecieved(event);
                    clientCommandList.clear();
                    clientCommandList.add(0, "");
                } else if (clientCommandList.get(0).equals("##session##play##")) {
                    playVideoRecieved(event);
                    clientCommandList.clear();
                    clientCommandList.add(0, "");
                }
                System.out.println("Working");
                try {
                    Thread.sleep(100);        //Приостановка потока на 1 сек.
                } catch (InterruptedException e) {
                }
            }
            while (true);
        };
        new Thread(checkerCommands).start();

    }

}