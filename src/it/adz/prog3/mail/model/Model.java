package it.adz.prog3.mail.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.adz.prog3.mail.comunicationobjects.*;
import it.adz.prog3.mail.controller.Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

//questa classe dispone dei metodi per controllare i dati
public class Model {

    public static User authenticate(String email, String password) {
        File file = new File(System.getProperty("user.dir") + "/files/users.json");
        String s;
        if(!isEmailValid(email))
            return null;
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
        int exitCode = notDelivered.size() > 0 ? -2 : 1;
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

    public static ReplyEmailCancellation deleteEmail(RequestEmailCancellation r, User u){
        File file = null;
        String s = null;
        int exitCode = 1;
        Gson gson = new Gson();
        ArrayList<UUID> deleted = new ArrayList<>();
        FileWriter fw = null;

        if(r == null || r.getIdMail().size() < 1 || u == null) {
            System.out.println("Err1");
            return new ReplyEmailCancellation(-3, null);
        }

        file = new File(System.getProperty("user.dir") + "/files/mails/" + u.getFile());
        if (!file.exists()) {
            System.out.println("Err2");
            return new ReplyEmailCancellation(-3, null);
        }
        else {
            try {
                byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                s = new String(encoded, StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.out.println("Err3");
                return new ReplyEmailCancellation(-3, null);
            }

            ArrayList<Email> allEmails = gson.fromJson(s, new TypeToken<ArrayList<Email>>() {
            }.getType());
            if(allEmails.size() == 0){
                System.out.println("Err4");
                return new ReplyEmailCancellation(-3, null);
            }
            b:for(UUID id : r.getIdMail()){
                for(Email e : allEmails){
                    if(id.toString().equals(e.getUuid().toString())){
                        allEmails.remove(e);
                        deleted.add(id);
                        continue b;
                    }
                }
                exitCode = -2;
            }
            System.out.println(exitCode);
            exitCode = deleted.size() == 0 ? -3 : exitCode;
            System.out.println(exitCode);

            try {
                fw = new FileWriter(file);
                fw.write(gson.toJson(allEmails));
            } catch (IOException e) {
                return new ReplyEmailCancellation(-3, null);
            } finally {
                try {
                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new ReplyEmailCancellation(exitCode, deleted);
        }
    }

    public static ReplyDownloadEmail downloadEmail(RequestDownloadEmail r, User u) {
        File file = null;
        String s = null;
        int exitCode = 1;
        Gson gson = new Gson();
        ArrayList<UUID> deleted = new ArrayList<>();
        FileWriter fw = null;

        if (r == null || u == null) {
            System.out.println("Err1");
            return new ReplyDownloadEmail(-3, null);
        }

        file = new File(System.getProperty("user.dir") + "/files/mails/" + u.getFile());
        if (!file.exists()) {
            return new ReplyDownloadEmail(1, new ArrayList<Email>());
        } else {
            try {
                byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                s = new String(encoded, StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.out.println("Err3");
                return new ReplyDownloadEmail(-3, null);
            }

            ArrayList<Email> allEmails = gson.fromJson(s, new TypeToken<ArrayList<Email>>() {
            }.getType());
            if (allEmails.size() == 0) {
                System.out.println("Err4");
                return new ReplyDownloadEmail(1, new ArrayList<Email>());
            }

            if (r.getSinceDate() != null)
                System.out.println(allEmails.removeIf(e -> e.getDate().before(r.getSinceDate())));

            System.out.println(allEmails.size() + " mails trovate.");
            return new ReplyDownloadEmail(1, allEmails);
        }
    }

    public static User newUser(String name, String surname, String email, String password, Boolean passwordIsEncrypted){
        User u = null;
        File file = null;
        FileWriter fw = null;
        String s;

        if(name == null || surname == null || email == null || !isEmailValid(email) || password == null || password.length() < 1 || passwordIsEncrypted == null)
            return null;
        u = new User(name, surname, email, passwordIsEncrypted ? password : digest("SHA-256", password), UUID.randomUUID().toString()+".json");

        file = new File(System.getProperty("user.dir") + "/files/users.json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            return null;
        }

        try {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            s = new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Gson gson = new Gson();
        ArrayList<User> allUsers = gson.fromJson(s, new TypeToken<ArrayList<User>>() {
        }.getType());
        if(allUsers != null) {
            for (User user : allUsers) {
                if (user.getEmail().equals(email)) {
                    return user;
                }
            }
            allUsers.add(u);
        }
        else
            allUsers = new ArrayList<User>();

        try {
            fw = new FileWriter(file);
            fw.write(gson.toJson(allUsers));
        } catch (IOException e) {
            u = null;
        } finally {
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return u;
    }

    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    //I seguenti due metodi criptano le pass (quando si genera un nuovo utente)
    public static String digest(String alg, String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(alg);
            byte[] buffer = input.getBytes("UTF-8");
            md.update(buffer);
            byte[] digest = md.digest();
            return encodeHex(digest);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private static String encodeHex(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}