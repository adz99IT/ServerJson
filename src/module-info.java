module it.adz.prog3.mail {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires com.google.gson;
    exports it.adz.prog3.mail.model;
    exports it.adz.prog3.mail.controller;
    exports it.adz.prog3.mail.comunicationobjects;
    exports it.adz.prog3.mail to javafx.graphics;
    //exports it.adz.prog3.mail.controller to javafx.fxml;
    opens it.adz.prog3.mail.comunicationobjects to com.google.gson;
    opens it.adz.prog3.mail.controller;
}
