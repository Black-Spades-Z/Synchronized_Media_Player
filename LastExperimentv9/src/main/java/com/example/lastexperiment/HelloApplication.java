package com.example.lastexperiment;

import Server.MainWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;

public class HelloApplication extends Application {


    public static final ArrayList<String> clientCommandList = new ArrayList<>(1);


    @Override
    public void start(Stage stage) throws IOException {

        clientCommandList.add("");

        // Get resource from fxml file
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        //load scene from fxml
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("HeyMedia");
        // get absolute path from relative one
        String logoPath = FileSystems.getDefault().getPath("src\\main\\resources\\icons\\logo.png").toAbsolutePath().toString();
        // get icon with absolute path
        stage.getIcons().add(new Image(logoPath));
        // set sizes
        stage.setMinHeight(440);
        stage.setMinWidth(600);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);
            }
        });

        // Event while mouse clicked
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            private boolean once1 = false;
            @Override
            public void handle(MouseEvent mouseEvent) {
                // if clicked twice
                if(mouseEvent.getClickCount() == 2 ){
                    if(once1 == false) {
                        stage.setFullScreen(true); //set full screen
                        once1 = true;
                    }else if(once1 == true){       // if clicked twice next time
                        stage.setFullScreen(false); //set small screen
                        once1 = false;
                    }
                }

            }
        });
        //Stage to the scene
        stage.setScene(scene);
        stage.show(); // show the stage
    }

    public static void main(String[] args) {

        Runnable task = () ->
        {
            new MainWindow();
        };

        Thread thread = new Thread(task);
        thread.start();

        launch();
    }
}