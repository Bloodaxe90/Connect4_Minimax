package com.example.connect4_minimax;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Connect4Application extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Connect4Application.class.getResource("Connect4.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(getClass().getResource("Connect4.css").toExternalForm());
            stage.setTitle("Connect4");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }

        stage.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}