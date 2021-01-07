package it.adz.prog3.mail.controller;

import it.adz.prog3.mail.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

public class Controller {
    Model model;
    ClientsListener cl;

    @FXML
    private TextFlow textflow;

    public void setModel(Model model){
        this.model = model;
        cl = new ClientsListener(this.model, this);
    }





}

