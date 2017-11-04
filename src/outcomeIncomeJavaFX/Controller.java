package outcomeIncomeJavaFX;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
		//create singleton, loading and sorting data, loading statistics
		data = OutcomeIncomeData.getInstance();
		
		if (!data.open()) {
			//implementation of an alert.error
		}
		
		
		
		data.loadOutcomeIncomes(new File(data.getFilename()), false);
		outcomeIncomesTable.setItems(data.getOutcomeIncomes());
		tableColumnDate.setSortType(TableColumn.SortType.ASCENDING);
		outcomeIncomesTable.getSortOrder().add(tableColumnDate);
		displayStatistics();

		// ContextMenu for TableView entries "edit/delete"
		contextMenu = new ContextMenu();

		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> deleteOutcomeIncome());
		//Anonymous Class
//		deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
//				deleteOutcomeIncome();
//			}
//		});

		MenuItem editMenuItem = new MenuItem("Edit");
		editMenuItem.setOnAction(event -> showEditingDialog());
		//Anonymous Class
//		editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
//				showEditingDialog();
//			}
//		});

		// showing context menu over whole TableView - including empty rows :/
		contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
		outcomeIncomesTable.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.SECONDARY)
				contextMenu.show(outcomeIncomesTable, event.getScreenX(), event.getScreenY());
		});
		//Anonymous Class
//		outcomeIncomesTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				if (event.getButton() == MouseButton.SECONDARY) {
//
//					// lambda expression not working (probably because of row.emptyProperty()
//
//					// TableRow<OutcomeIncome> row = new TableRow<>();
//					// row.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
//					// if (isNowEmpty)
//					// row.setContextMenu(null);
//					// else
//					// row.setContextMenu(contextMenu);
//					// });
//
//					contextMenu.show(outcomeIncomesTable, event.getScreenX(), event.getScreenY());
//				}
//			}
//		});
	}

	// Showing dialog to add new Outcome/Income
	@FXML
	public void showAddingDialog() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		dialog.initOwner(mainPanel.getScene().getWindow());
		dialog.setTitle("Add new Outcome/Income");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("OutcomeIncomeDialog.fxml"));
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException ioe) {
			showIOExceptionAlert(ioe);
			return;
		}

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		try {
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				OutcomeIncomeController outcomeIncomeController = fxmlLoader.getController();
				OutcomeIncome newOutcomeIncome = outcomeIncomeController.getNewOutcomeIncome();
				System.out.println(newOutcomeIncome);
				data.addOutcomeIncome(newOutcomeIncome);
				handleLast30daysButton();
				outcomeIncomesTable.getSortOrder().add(tableColumnDate);
				displayStatistics();
			}
		} catch (IllegalArgumentException iae) {
			showWrongInputAlert(iae);
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

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainPanel.getScene().getWindow());
		dialog.setTitle("Edit Outcome/Income");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("OutcomeIncomeDialog.fxml"));
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException e) {
			System.out.println("Couldn't load the dialog");
			e.printStackTrace();
		}

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		OutcomeIncomeController outcomeIncomeController = fxmlLoader.getController();
		outcomeIncomeController.editOutcomeIncome(selectedOutcomeIncome);

		try {
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				outcomeIncomeController.updateOutcomeIncome(selectedOutcomeIncome);
				Path tempFile = Files.createTempFile("spendingsTracker", ".bin");
				data.saveOutcomeIncomes(tempFile.toFile());
				data.loadOutcomeIncomes(tempFile.toFile(), false);
				handleLast30daysButton();
				outcomeIncomesTable.getSortOrder().add(tableColumnDate);
				displayStatistics();
			}
		} catch (IllegalArgumentException iae) {
			showWrongInputAlert(iae);
			return;
		} catch (IOException ioe) {
			showIOExceptionAlert(ioe);
			return;
		}
	}

	@FXML
	public void handleExit() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.setTitle("Quitting...");
		alert.setHeaderText("Do you want to save data before quitting?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.CANCEL)
			return;
		else {
			if (result.get() == ButtonType.YES)
				data.saveOutcomeIncomes(new File(data.getFilename()));
		}
		Platform.exit();
	}

	public void showWrongInputAlert(IllegalArgumentException e) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Wrong Input Data");
		alert.setContentText(
				"Please fill correctly all the fields\n\nTotal Value field is required - decimal pointer is comma (.)\nSource field is required\nNotes field is not required"
						+ "\n\n\n" + e.getClass().getSimpleName() + "\n" + e.getMessage());
		alert.showAndWait();
	}
	

	public static void showIOExceptionAlert(IOException ioe) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Couldn't reach a file.");
		alert.setContentText("File may not exist or you don't have permission to access it\n\n\n"
				+ ioe.getClass().getSimpleName() + "\n" + ioe.getMessage());
		alert.showAndWait();
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

		if (result.isPresent() && result.get() == ButtonType.OK) {
			data.deleteOutcomeIncome(selectedOutcomeIncome);
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

	@FXML
	public void saveData() {
		data.saveOutcomeIncomes(new File(data.getFilename()));
	}

	public void displayStatistics() {
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
	
	//showing fileChooser to open and add/replace
	public void openNewData() {
		
		//creating FileChooser
		FileChooser fc = new FileChooser();
		fc.setTitle("Open a new data file");
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.getExtensionFilters().addAll(new ExtensionFilter("Binary files", "*.bin", "*.dat"),
				new ExtensionFilter("All files", "*.*"));
		File file = fc.showOpenDialog(mainPanel.getScene().getWindow());
		
		//if file was selected show dialog to choose to add/replace data
		if (file != null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			ButtonType add = new ButtonType("Add");
			ButtonType replace = new ButtonType("Replace");
			alert.setHeaderText("Add or Replace?");
			alert.setContentText("Do you want to Add new data to existing one or Replace it with selected file data?");
			alert.getDialogPane().getButtonTypes().setAll(add, replace, ButtonType.CANCEL);
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.CANCEL)
				return;
			else {
				boolean addFlag = (result.get() == add) ? true : false;
				data.loadOutcomeIncomes(file, addFlag);
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
		fc.getExtensionFilters().add(new ExtensionFilter("Binary files", "*.bin"));
		data.saveOutcomeIncomes(fc.showSaveDialog(mainPanel.getScene().getWindow()));
	}

}
