package it.adz.prog3.mail.controller;
import it.adz.prog3.mail.comunicationobjects.*;
import it.adz.prog3.mail.model.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable  {
    Socket incoming;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    PrintWriter out;
    Scanner in;

    Model m;
    User u;

    public ClientHandler(Socket incoming, Model m) {
        this.incoming = incoming;
        this.m = m;
        u = null;
    }

    public void run() {
        try {
            outStream = new ObjectOutputStream(incoming.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(incoming.getInputStream());

            Object obj = inStream.readObject();
            //System.out.println(obj.getClass().toString());
            if(obj instanceof RequestSendEmail){
                System.out.println("New SendEmail Request");
                RequestSendEmail r = (RequestSendEmail)obj;
                if(login(r)){
                    outStream.writeObject(Model.send(u, r));
                } else
                    outStream.writeObject(new ReplySendEmail());
            } else if(obj instanceof RequestEmailCancellation){
                System.out.println("New EmailCancellation Request");
                RequestEmailCancellation r = (RequestEmailCancellation)obj;
                if(login(r)){
                    outStream.writeObject(Model.deleteEmail(r, u));
                } else
                    outStream.writeObject(new ReplyEmailCancellation());
            } else if(obj instanceof RequestDownloadEmail){
                System.out.println("New DownloadEmail Request");
                RequestDownloadEmail r = (RequestDownloadEmail)obj;
                if(login(r)){
                    outStream.writeObject(Model.downloadEmail(r, u));
                } else
                    outStream.writeObject(new ReplyDownloadEmail(-1, null));
            } else if(obj instanceof Login){
                System.out.println("New Login Request");
                Login l = (Login)obj;
                System.out.println(l.getUserMail()+" "+l.getEncryptedPassword());
                if((u=Model.authenticate(l.getUserMail(), l.getEncryptedPassword())) != null)
                    outStream.writeObject(new ReplyLogin(u.getName()));
                else
                    outStream.writeObject(new ReplyLogin());
            } else{
                System.out.println("NO");
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
}