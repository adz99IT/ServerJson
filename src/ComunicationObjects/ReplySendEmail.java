package ComunicationObjects;

import java.io.Serializable;
import java.util.ArrayList;

public class ReplySendEmail implements Reply, Serializable {
    int exitCode; //-1 invalid login //-2 some failed //-3 all failed //1- all sent
    ArrayList<String> notDelivered;

    public ReplySendEmail(){
        this.exitCode = -1;
        notDelivered = null;
    }

    public ReplySendEmail(int exitCode, ArrayList<String> notDelivered) {
        this.exitCode = exitCode;
        this.notDelivered = notDelivered;
    }

    public int getExitCode(){
        return exitCode;
    }
}
