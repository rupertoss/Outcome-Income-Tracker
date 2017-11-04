package outcomeIncomeJavaFX;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	public static void main(String[] args) {
		
		launch(args);
		
		try {
		Connection conn = DriverManager.getConnection("jdbc:sqlite:oidata.db");
		Statement statement = conn.createStatement();
		statement.executeQuery("CREATE TABLE outcomeIncomeData (_id INTEGER PRIMARY KEY, incomeFlag TEXT NOT NULL, value TEXT NOT NULL, source TEXT NOT NULL, notes TEXT NOT NULL)");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		FXMLLoader fxmlLoaderMain = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
		Parent root = (Parent) fxmlLoaderMain.load();
		primaryStage.setTitle("Spendings Tracker!");
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.show();
		
		//showing save data prompt on closing
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
					Controller controller = fxmlLoaderMain.getController();
					controller.handleExit();
				}
			}
		});
		
		

	}

}
