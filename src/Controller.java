import ComunicationObjects.User;

public class Controller {
    public static void main(String[] args){
        ClientListener cl = new ClientListener();
        //ComunicationObjects.User a = Model.newUser("Andrea","Rossi", "andrearossi@progtre.it", "andrea1", false);
        User b = Model.newUser("Alessandro","Di Zitti", "alessandrodizitti@progtre.it", "alessandro1", false);
        User c = Model.newUser("Michele","Fiorelli", "michelefiorelli@progtre.it", "michele1", false);

    }
}
