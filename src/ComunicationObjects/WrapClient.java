package ComunicationObjects;

public class WrapClient {
    private String fromMail;
    private String encryptedPassword;
    private Object payload;

    public WrapClient(String fromMail, String encryptedPassword, Object payload) {
        this.fromMail = fromMail;
        this.encryptedPassword = encryptedPassword;
        this.payload = payload;
    }

    public String getFromMail() {
        return fromMail;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public Object getPayload() {
        return payload;
    }
}
