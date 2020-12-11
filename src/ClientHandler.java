import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable  {
    Socket incoming;
    InputStream inStream;
    Scanner in;
    OutputStream outStream;
    PrintWriter out;

    User u;

    public ClientHandler(Socket incoming) {
        this.incoming = incoming;
        u = null;
    }

    public void run() {
        try {

            inStream = incoming.getInputStream();
            outStream = incoming.getOutputStream();

            in = new Scanner(inStream, "UTF-8");
            out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);


        }
    }
}
