package it.adz.prog3.mail.controller;
import it.adz.prog3.mail.model.Model;

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
            while (true) {
                if(s != null && !s.isClosed()) {
                    Socket incoming = s.accept();
                    System.out.println("A new client has just connected. Current clients online: "+(getNumberOfClients()+1));
                    Runnable r = new ClientHandler(incoming, m);
                    new Thread(clients, r).start();
                }
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public int getNumberOfClients(){
        //notifica la view, da aggiungere.
        return clients.activeCount();
    }
}
