module com.example.chess {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;

	opens com.example.chess to javafx.fxml;
	exports com.example.chess;
}