import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.stage.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ResourceBundle;

public class View extends Application implements Initializable {

    private Scene s;
    private Parent root;
    private String port, ip;

    @FXML
    private Button button;

    @Override
    public void start(Stage primaryStage){
        Parameters params = getParameters();
        List<String> list = params.getRaw();
        if(list.size() == 2){
            this.ip = list.get(0);
            this.port = list.get(1);
        }

        try {
            root = FXMLLoader.load(getClass().getResource("FXML.fxml"));
        }catch(IOException e){
            e.printStackTrace();
        }

        s = new Scene(root);
        primaryStage.setScene(s);
        primaryStage.setTitle("Server Mail");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(",,");
        try {
            System.out.println("IP: "+ InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void buttonHandler(ActionEvent actionEvent) {


    }

}
