import javafx.application.Application;
import javafx.event.ActionEvent;

public class Controller {
    private Model m;


    public void start(){
        //////SETTINGS
        int port =  8189;
        String ipServer = "poggivpn.ddns.net";
        //////////////

        ClientListener cl = new ClientListener(m, port); //thread del controller che gestisce i socket in ingresso
        Application.launch(View.class, ipServer, Integer.toString(port));
    }

    public void setModel(Model m){
        if(this.m == null) {
            this.m = m;
            start();
        }
    }

    public void buttonHandler(ActionEvent actionEvent) {
        //

    }



    /*@FXML
    public void buttonHandler(ActionEvent actionEvent) {
        m.setState(!m.getState());
        if(m.getState()) {
            button.setText("TURN OFF");
            button.setStyle("-fx-background-color: #42f548; ");
        }else{
            button.setText("TURN ON");
            button.setStyle("-fx-background-color: #fa1100; ");
        }
        System.out.println(m.getState());
    }*/

}
