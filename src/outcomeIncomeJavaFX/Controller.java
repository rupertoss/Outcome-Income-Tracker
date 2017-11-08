package outcomeIncomeJavaFX;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import outcomeIncomeData.OutcomeIncome;
import outcomeIncomeData.OutcomeIncomeData;

public class Controller {

	@FXML
	private BorderPane mainPanel;

	@FXML
	private OutcomeIncomeData data;

	@FXML
	private TableView<OutcomeIncome> outcomeIncomesTable;

	@FXML
	private ContextMenu contextMenu;

	@FXML
	private TableColumn<OutcomeIncome, ?> tableColumnDate;

	@FXML
	private ToggleButton last30daysButton;

	@FXML
	private Label statisticsLabel;

	@FXML
	private Label totalMoneyLabel;

	@FXML
	private Label totalOutcomeLabel;

	@FXML
	private Label totalIncomeLabel;

	@FXML
	private Label nrOfEntriesLabel;

	@FXML
	private Label nrOfOutcomesLabel;

	@FXML
	private Label nrOfIncomesLabel;

	@FXML
	private Label averageOutcomeLabel;

	@FXML
	private Label averageIncomeLabel;

	public void initialize() {
		
		// >> creating singleton
		// >> opening connection to database
		// >> loading data from database
		// >> setting items to TableView
		// >> sorting
		// >> updating statistics
		
		data = OutcomeIncomeData.getInstance();
		
		SQLException sqlException = data.openConnection(data.CONNECTION);
		if (sqlException != null) {
			showErrorAlert(sqlException, "Couldn't connect to the database. File may not exist or may be corrupted.\nThe application will terminate.\n\n\n");
			Platform.exit();
		}
		
		loadDB();
		outcomeIncomesTable.setItems(data.getOutcomeIncomes());
		tableColumnDate.setSortType(TableColumn.SortType.ASCENDING);
		outcomeIncomesTable.getSortOrder().add(tableColumnDate);
		displayStatistics();


		// ContextMenu for TableView entries "edit/delete"
		contextMenu = new ContextMenu();

		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> deleteOutcomeIncome());

		MenuItem editMenuItem = new MenuItem("Edit");
		editMenuItem.setOnAction(event -> showEditingDialog());

		// showing context menu over whole TableView - including empty rows :/
		contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
		outcomeIncomesTable.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.SECONDARY)
				contextMenu.show(outcomeIncomesTable, event.getScreenX(), event.getScreenY());
		});
	}

	// Showing dialog to add new Outcome/Income
	@FXML
	public void showAddingDialog() {
		//showing dialog
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		dialog.initOwner(mainPanel.getScene().getWindow());
		dialog.setTitle("Add new Outcome/Income");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("OutcomeIncomeDialog.fxml"));
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException ioException) {
			showErrorAlert(ioException, "Loader file may not exist or you don't have permission to access it.");
			return;
		}

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		// adding new entry
		try {
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				OutcomeIncomeController outcomeIncomeController = fxmlLoader.getController();
				OutcomeIncome newOutcomeIncome = outcomeIncomeController.getNewOutcomeIncome();
				data.addOutcomeIncome(newOutcomeIncome);
				handleLast30daysButton();
				outcomeIncomesTable.getSortOrder().add(tableColumnDate);
				displayStatistics();
			}
		} catch (IllegalArgumentException iae) {
			showErrorAlert(iae, "Wrong Input Data!\n\nPlease fill correctly all the fields\n\nTotal Value field is required - decimal pointer is comma (.)\nSource field is required\nNotes field is not required");
			return;
		}
	}

	// Showing dialog to edit existing Outcome/Income
	@FXML
	public void showEditingDialog() {
		OutcomeIncome selectedOutcomeIncome = outcomeIncomesTable.getSelectionModel().getSelectedItem();
		// Alert if no entry selected
		if (selectedOutcomeIncome == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No Outcome/Income selected");
			alert.setContentText("Please select the Outcome/Income you want to edit");
			alert.showAndWait();
			return;
		}

		// showing editing dialog
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainPanel.getScene().getWindow());
		dialog.setTitle("Edit Outcome/Income");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("OutcomeIncomeDialog.fxml"));
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException ioeException) {
			showErrorAlert(ioeException, "Couldn't load the editing dialog");
		}

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		OutcomeIncomeController outcomeIncomeController = fxmlLoader.getController();
		outcomeIncomeController.editOutcomeIncome(selectedOutcomeIncome);

		// updating an entry
		try {
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				outcomeIncomeController.updateOutcomeIncome(selectedOutcomeIncome);
				updateEntryDB(selectedOutcomeIncome);
				outcomeIncomesTable.refresh();
				handleLast30daysButton();
				outcomeIncomesTable.getSortOrder().add(tableColumnDate);
				displayStatistics();
			}
		} catch (IllegalArgumentException iae) {
			showErrorAlert(iae, "Wrong Input Data!\n\nPlease fill correctly all the fields\n\nTotal Value field is required - decimal pointer is comma (.)\nSource field is required\nNotes field is not required");
			return;
		}
	}

	@FXML
	public void handleExit() {
		
		// showing confirmation alert
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.setTitle("Quitting...");
		alert.setHeaderText("Do you want to save data before quitting?");
		Optional<ButtonType> result = alert.showAndWait();
		
		// cancel selected by user
		// >> returning
		if (result.get() == ButtonType.CANCEL)
			return;
		else {
			// committing changes
			if (result.get() == ButtonType.YES) {
				commitChangesToDB();
			}
		}
		// closing connection and exiting
		closeConnectionToDB();
		Platform.exit();
	}

	// Deleting an OutcomeIncome
	@FXML
	public void deleteOutcomeIncome() {
		OutcomeIncome selectedOutcomeIncome = outcomeIncomesTable.getSelectionModel().getSelectedItem();
		// Alert if no entry selected
		if (selectedOutcomeIncome == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No Outcome/Income selected");
			alert.setContentText("Please select the Outcome/Income you want to delete");
			alert.showAndWait();
			return;
		}

		// Alert to confirm deleting
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Outcome/Income");
		alert.setHeaderText("Are you sure you want to delete selected Outcome/Income");
		alert.setContentText("You are trying to delete this entry: \n" + selectedOutcomeIncome.toString()
				+ "\nPress OK to confirm, or cancel to back out");
		Optional<ButtonType> result = alert.showAndWait();

		// deleting an entry
		if (result.isPresent() && result.get() == ButtonType.OK) {
			deleteEntryDB(selectedOutcomeIncome);
			handleLast30daysButton();
			displayStatistics();
		}
	}

	// Deleting be pressing "DELETE"
	@FXML
	public void handleKeyPressed(KeyEvent key) {
		if (key.getCode().equals(KeyCode.DELETE))
			deleteOutcomeIncome();
	}

	// Button handler of last 30days entries
	@FXML
	public void handleLast30daysButton() {
		if (last30daysButton.isSelected()) {
			ObservableList<OutcomeIncome> filteredList = FXCollections.observableArrayList();
			for (int i = 0; i < data.getOutcomeIncomes().size(); i++) {
				if (data.getOutcomeIncomes().get(i).getDate().isAfter(LocalDate.now().minusDays(30))) {
					filteredList.add(data.getOutcomeIncomes().get(i));
				}
			}
			outcomeIncomesTable.setItems(filteredList);

		} else {
			outcomeIncomesTable.setItems(data.getOutcomeIncomes());
		}
		displayStatistics();
		outcomeIncomesTable.getSortOrder().add(tableColumnDate);
	}

	//showing fileChooser to open and add/replace
	@FXML
	public void openNewData() {
		
		// creating FileChooser
		FileChooser fc = new FileChooser();
		fc.setTitle("Open a new data file");
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.getExtensionFilters().addAll(new ExtensionFilter("Databases", "*.db"),
				new ExtensionFilter("All files", "*.*"));
		File file = fc.showOpenDialog(mainPanel.getScene().getWindow());
		
		// if file was selected show dialog to choose to add/replace data
		if (file != null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			ButtonType add = new ButtonType("Add");
			ButtonType replace = new ButtonType("Replace");
			alert.setHeaderText("Add or Replace?");
			alert.setContentText("Do you want to Add new data to existing one or Replace it with selected file data?");
			alert.getDialogPane().getButtonTypes().setAll(add, replace, ButtonType.CANCEL);
			
			// cancel selected by user
			// >> returning
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL)
				return;
			else {
				if (result.get() == replace) {
					
					// replace old database by selected new one
					// >> closing old connection
					// >> clearing TableView
					// >> opening new connection
					// >> loading new data 
					
					closeConnectionToDB();
					data.getOutcomeIncomes().clear();
					openConnectionToDB("jdbc:sqlite:" + file.getPath());
					loadDB();
					
				} else {
					
					// adding new data from selected database by adding to existing one
					// >> closing old connection
					// >> opening new connection
					// >> loading new data
					// >> closing new connection
					// >> opening old connection
					
					String oldConnection = data.connection.toString();
					closeConnectionToDB();
					openConnectionToDB("jdbc:sqlite:" + file.getPath());
					loadDB();
					closeConnectionToDB();
					openConnectionToDB(oldConnection);
				}
				
				handleLast30daysButton();
				outcomeIncomesTable.getSortOrder().add(tableColumnDate);
				displayStatistics();
			}
		}
	}

	@FXML
	public void saveAsData() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Save data as...");
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.getExtensionFilters().add(new ExtensionFilter("Databases", "*.db"));
		File file = fc.showSaveDialog(mainPanel.getScene().getWindow());
		
		// user chose file to the current database 
		// >> saving by committing changes
		
		if (file.getPath() == data.DB_NAME) {
			commitChangesToDB();
		}
		
		// user chose new file 
		// >> closing existing connection 
		// >> opening new connection 
		// >> creating database
		// >> saving data to the database 
		// >> committing changes
		
		closeConnectionToDB();
		openConnectionToDB("jdbc:sqlite:" + file.getPath());
		createDB();
		saveDB();
		commitChangesToDB();
	}

	private static void showErrorAlert(Exception e, String contextText) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Critical Error!");
		StringBuilder sb = new StringBuilder();
		sb.append(contextText);
		sb.append("\n\n\n");
		sb.append(e.getClass().getSimpleName());
		sb.append("\n");
		sb.append(e.getMessage());
		alert.setContentText(sb.toString());
		alert.showAndWait();
	}
	
	private void openConnectionToDB(String connection) {
		SQLException sqlException = data.openConnection(connection);
		if (sqlException != null) 
			showErrorAlert(sqlException, "Couldn't connect to the database. File may not exist or may be corrupted.");
	}
	
	private void closeConnectionToDB() {
		SQLException sqlException = data.closeConnection();
		if (sqlException != null) 
			showErrorAlert(sqlException, "Database error. Couldn't close connection.");
	}
	
	private void loadDB() {
		SQLException sqlException = data.loadDB();
		if (sqlException != null) 
			showErrorAlert(sqlException, "Couldn't load the database. File may not exist or may be corrupted.");
	}
	
	private void createDB() {
		SQLException sqlException = data.createDB();
		if (sqlException != null)
			showErrorAlert(sqlException, "Couldn't create new database.");
	}
	
	private void saveDB() {
		SQLException sqlException = data.saveDB();
		if (sqlException != null)
			showErrorAlert(sqlException, "Couldn't save data to the database.");
	}
	
	private void commitChangesToDB() {
		SQLException sqlException = data.commitChanges();
		if (sqlException != null)
			showErrorAlert(sqlException, "Couldn't commit changes to the database.");
	}
	
	private void deleteEntryDB(OutcomeIncome selectedOutcomeIncome) {
		SQLException sqlException = data.deleteOutcomeIncome(selectedOutcomeIncome);
		if (sqlException != null)
			showErrorAlert(sqlException, "Couldn't delete selected entry.");
	}
	
	private void updateEntryDB(OutcomeIncome selectedOutcomeIncome) {
		SQLException sqlException = data.updateEntryInDB(selectedOutcomeIncome);
		if (sqlException != null) 
			showErrorAlert(sqlException, "Couldn't update an entry.");
	}

	
	private void displayStatistics() {
		double totalMoney = 0;
		double outcomeMoney = 0;
		double incomeMoney = 0;
		int nrOfEntries = outcomeIncomesTable.getItems().size();
		int nrOfOutcomes = 0;
		int nrOfIncomes = 0;
		double averageOutcome = 0;
		double averageIncome = 0;

		for (int i = 0; i < outcomeIncomesTable.getItems().size(); i++) {
			// calculating totalValue of entries
			totalMoney += outcomeIncomesTable.getItems().get(i).getTotalValue();
			// calculating in division of outcome or income
			if (outcomeIncomesTable.getItems().get(i).getTotalValue() < 0) {
				outcomeMoney += outcomeIncomesTable.getItems().get(i).getTotalValue();
				nrOfOutcomes++;
			} else {
				incomeMoney += outcomeIncomesTable.getItems().get(i).getTotalValue();
				nrOfIncomes++;
			}
		}

		// setting average to 0 if nrOfOutcomes/Incomes = 0
		averageOutcome = nrOfOutcomes != 0 ? outcomeMoney / nrOfOutcomes : 0;
		averageIncome = nrOfIncomes != 0 ? incomeMoney / nrOfIncomes : 0;

		statisticsLabel.setText("Statistics");
		totalMoneyLabel.setText(String.format("%.2f", totalMoney));
		totalOutcomeLabel.setText(String.format("%.2f", outcomeMoney));
		totalIncomeLabel.setText(String.format("%.2f", incomeMoney));
		nrOfEntriesLabel.setText("" + nrOfEntries);
		nrOfOutcomesLabel.setText("" + nrOfOutcomes);
		nrOfIncomesLabel.setText("" + nrOfIncomes);
		averageOutcomeLabel.setText(String.format("%.2f", averageOutcome));
		averageIncomeLabel.setText(String.format("%.2f", averageIncome));
	}

}
