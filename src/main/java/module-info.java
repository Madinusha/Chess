module com.example.chess {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires java.desktop;
	requires json.simple;

	opens com.example.chess to javafx.fxml;
	exports com.example.chess;
	exports controller;
	opens controller to javafx.fxml;
}