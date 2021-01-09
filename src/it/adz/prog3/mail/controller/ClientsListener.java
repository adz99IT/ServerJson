package it.adz.prog3.mail.controller;
import it.adz.prog3.mail.model.Model;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//ClientListener si mette in attesa di connessioni.
public class ClientsListener extends Thread {
    private ThreadGroup clients;
    public ServerSocket s;

    Model m;
    Controller c;


    public ClientsListener(Model m, Controller c){
        s = null;
        this.m = m;
        this.c = c;
        start();
    }

    public void run() {
        clients = new ThreadGroup("clients");
        try {
            s = new ServerSocket(8189);
            Platform.runLater(() ->{
                c.updateLog("Server started.");
            });
            while (true) {
                if(s != null && !s.isClosed()) {
                    Socket incoming = s.accept();
                    Runnable r = new ClientHandler(incoming, m, c);
                    new Thread(clients, r).start();
                }
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }
}