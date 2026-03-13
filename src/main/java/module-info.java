module org.cheesy.randomroulettepicker {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens org.cheesy.randomroulettepicker to javafx.fxml;
    exports org.cheesy.randomroulettepicker;
    exports org.cheesy.randomroulettepicker.controller;
    opens org.cheesy.randomroulettepicker.controller to javafx.fxml;
    opens org.cheesy.randomroulettepicker.model to com.google.gson;
}