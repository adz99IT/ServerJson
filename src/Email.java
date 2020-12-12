import java.util.ArrayList;
import java.util.Date;

public class Email {
    private String uuid;
    private Date date;
    private String from;
    private ArrayList<String> to;
    private String subject;
    private String text;

    public Email(String uuid, Date date, String from, ArrayList<User> to, String subject, String text) {
        this.uuid = uuid;
        this.date = date;
        this.from = from;
        this.subject = subject;
        this.text = text;
        this.to = new ArrayList<String>();
        for(User u : to){
            this.to.add(u.getEmail());
        }
    }

    public Date getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public ArrayList<String> getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public String getUuid() {
        return uuid;
    }
}
