package it.adz.prog3.mail.controller;

import it.adz.prog3.mail.model.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    Model model;
    ClientsListener cl;

    @FXML
    private TextFlow textflow;

    public void setModel(Model model){
        this.model = model;
        cl = new ClientsListener(this.model, this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public synchronized void updateLog(String s){
        if(s != null) {
            s += "\n";
            Text t = new Text(new Date()+" - "+s);
            t.setFill(Color.GREEN);
            textflow.getChildren().add(t);
        }
    }
}

