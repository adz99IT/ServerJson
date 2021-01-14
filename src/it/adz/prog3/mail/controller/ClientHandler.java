package it.adz.prog3.mail.controller;
import it.adz.prog3.mail.comunicationobjects.*;
import it.adz.prog3.mail.model.*;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable  {
    Socket incoming;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;

    Model m;
    User u;
    Controller c;

    public ClientHandler(Socket incoming, Model m, Controller c) {
        this.incoming = incoming;
        this.m = m;
        this.c = c;
        u = null;
    }

    public void run() {
        try {
            outStream = new ObjectOutputStream(incoming.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(incoming.getInputStream());

            Object obj = inStream.readObject();

            if(obj instanceof RequestSendEmail){
                RequestSendEmail r = (RequestSendEmail)obj;
                if(login(r)){
                    outStream.writeObject(Model.send(u, r));
                    updateLog(u.getName() + " " + u.getSurname() + " has sent a mail.");
                } else {
                    outStream.writeObject(new ReplySendEmail());
                    updateLog("WARNING: a request (RequestSendEmail) was received with wrong credentials.");
                }
            } else if(obj instanceof RequestEmailCancellation){
                System.out.println("New EmailCancellation Request");
                RequestEmailCancellation r = (RequestEmailCancellation)obj;
                if(login(r)){
                    outStream.writeObject(Model.deleteEmail(r, u));
                    updateLog(u.getName() + " " + u.getSurname() + " has deleted some mails.");
                } else {
                    outStream.writeObject(new ReplyEmailCancellation());
                    updateLog("WARNING: a request (RequestEmailCancellation) was received with wrong credentials.");
                }
            } else if(obj instanceof RequestDownloadEmail){
                System.out.println("New DownloadEmail Request "+new Date());
                RequestDownloadEmail r = (RequestDownloadEmail)obj;
                if(login(r)){
                    ReplyDownloadEmail p = Model.downloadEmail(r, u);
                    outStream.writeObject(p);
                    if(p.getEmails() != null && p.getEmails().size() > 0)
                        updateLog(u.getName() + " " + u.getSurname() + " has downloaded his mail"+ (r.getSinceDate() != null ? " since "+r.getSinceDate() : "") +".");
                } else {
                    outStream.writeObject(new ReplyDownloadEmail(-1, null));
                    updateLog("WARNING: a request (RequestDownloadEmail) was received with wrong credentials.");
                }
            } else if(obj instanceof Login){
                Login l = (Login)obj;
                System.out.println(l.getUserMail()+" "+l.getEncryptedPassword());
                if((u=Model.authenticate(l.getUserMail(), l.getEncryptedPassword())) != null) {
                    outStream.writeObject(new ReplyLogin(u.getName()));
                    updateLog(u.getName() + " " + u.getSurname() + " has just logged.");
                }
                else {
                    outStream.writeObject(new ReplyLogin());
                    updateLog("WARNING: login attempt failed.");
                }
            } else{
                updateLog("WARNING: an unknown object has been received. It will be ignored.");
            }

        }catch(IOException | ClassNotFoundException e){
            try{e.printStackTrace();incoming.close();}catch(IOException e1){e1.printStackTrace();}
        }finally{
            try{incoming.close();}catch(IOException e){e.printStackTrace();}
        }
    }

    private boolean login(Login l){
        try {
            if ((u = Model.authenticate(l.getUserMail(), l.getEncryptedPassword())) != null) {
                return true;
            }
            else {
                return false;
            }
        }catch(Exception e){return false;}
    }

    private void updateLog(String s){
        Platform.runLater(() ->{
            c.updateLog(s);
        });
    }
}