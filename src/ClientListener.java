import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//ClientListener si mette in attesa di connessioni.
public class ClientListener extends Thread {
    ThreadGroup clients;


    public ClientListener(){
        start();
    }

    public void run() {
        clients = new ThreadGroup("clients");
        try {
            ServerSocket s = new ServerSocket(8189);

            while (true) {
                Socket incoming = s.accept();
                System.out.println("A new client has just connected. Current clients online: "+(getNumberOfClients()+1));
                Runnable r = new ClientHandler(incoming);
                new Thread(clients, r).start();
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public int getNumberOfClients(){
        //notifica la wiev, da aggiungere.
        return clients.activeCount();
    }
}

