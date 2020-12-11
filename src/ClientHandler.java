import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class ClientHandler implements Runnable  {
    Socket incoming;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    PrintWriter out;
    Scanner in;

    User u;

    public ClientHandler(Socket incoming) {
        this.incoming = incoming;
        u = null;
    }

    public void run() {
        try {
            outStream = new ObjectOutputStream(incoming.getOutputStream());
            inStream = new ObjectInputStream(incoming.getInputStream());

            Date d  = (Date)inStream.readObject();

            System.out.println(d.toString());


        }catch(IOException | ClassNotFoundException e){
            try{e.printStackTrace();incoming.close();}catch(IOException e1){e1.printStackTrace();}
        }finally{
            try{incoming.close();}catch(IOException e){e.printStackTrace();}
        }
    }
}
