import ComunicationObjects.ReplySendEmail;
import ComunicationObjects.RequestSendEmail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

//questa classe dispone dei metodi per controllare i dati
public class Model {
    private int usersNumber;

    //aggiungere un contatore (esclusivo con semafori tipo) per creare l'id delle mail


    public static User authenticate(String email, String password) {
        File file = new File(System.getProperty("user.dir") + "/files/users.json");
        String s;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            s = new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Gson gson = new Gson();
        User[] users = gson.fromJson(s, User[].class);
        for(User user : users) {
            if(user.getEmail().equals(email) && user.testPassword(password)){
                return user;
            }
        }

        return null;
    }

    public static ReplySendEmail send(User from, RequestSendEmail r){
        String json;
        ArrayList<User> delivered = new ArrayList<>();
        ArrayList<String> notDelivered = new ArrayList<>();

        if(from == null || r == null || r.getTo().size() < 1 || r.getText() == null)
            return new ReplySendEmail(-3, null);

        File file = new File(System.getProperty("user.dir") + "/files/users.json");
        String s;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            s = new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return new ReplySendEmail(-3, null);
        }

        Gson gson = new Gson();
        User[] users = gson.fromJson(s, User[].class);
        a:for(String email : r.getTo()) {
            for(User u : users){
                if(u.getEmail().equals(email)){
                    delivered.add(u);
                    continue a;
                }
            }
            notDelivered.add(email);
        }

        System.out.println("DEST:");
        for(User t : delivered) {
            System.out.println("+"+t.getEmail());
        }

        System.out.println("NOT F:");
        for(String t : notDelivered) {
            System.out.println("-"+t);
        }

        if(delivered.size() > 0){
            ArrayList<String> notFoundEmails2;
            Email e = new Email(UUID.randomUUID().toString(), new Date(), from.getEmail(), delivered, r.getSubject(), r.getText());
            return saveEmail(e, delivered, notDelivered);
        }else
            return new ReplySendEmail(-3, null);
    }

    private static ReplySendEmail saveEmail(Email mail, ArrayList<User> toUsers, ArrayList<String> notDelivered) {
        int exitCode = 1;
        Gson gson = new Gson();

        if (mail == null || toUsers == null || toUsers.size() < 1) {
            System.out.println("Err1");
            return new ReplySendEmail(-3, null);
        }

        a:for (User u : toUsers) {
            File file = new File(System.getProperty("user.dir") + "/files/mails/" + u.getFile());
            try {
                file.createNewFile();
            } catch (IOException e) {
                exitCode = -2;
                toUsers.remove(u);
                notDelivered.add(u.getEmail());
                continue a;
            }

            if (file.exists()) {
                String s;
                try {
                    byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                    s = new String(encoded, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    exitCode = -2;
                    toUsers.remove(u);
                    notDelivered.add(u.getEmail());
                    continue a;
                }

                ArrayList<Email> allEmails = gson.fromJson(s, new TypeToken<ArrayList<Email>>() {
                }.getType());
                if (allEmails == null)
                    allEmails = new ArrayList<Email>();
                allEmails.add(mail);

                FileWriter fw = null;
                try {
                    fw = new FileWriter(System.getProperty("user.dir") + "/files/mails/" + u.getFile());
                    fw.write(gson.toJson(allEmails));
                } catch (IOException e) {
                    System.out.println("Err3");
                    exitCode = -2;
                    toUsers.remove(u);
                    notDelivered.add(u.getEmail());
                    continue a;
                } finally {
                    try {
                        fw.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                System.out.println("Err2");
                exitCode = -2;
                toUsers.remove(u);
                notDelivered.add(u.getEmail());
            }
        }
        if(toUsers.size() < 1)
            exitCode = -3;
        return new ReplySendEmail(exitCode, notDelivered);
    }
}