package it.adz.prog3.mail;

import it.adz.prog3.mail.controller.Controller;
import it.adz.prog3.mail.model.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    Model model;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        model = new Model();
        Controller controller;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ServerUI.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        controller = loader.getController();
        controller.setModel(model);

        primaryStage.setTitle("Server Console");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop(){
        model.setStopped();
    }
}
