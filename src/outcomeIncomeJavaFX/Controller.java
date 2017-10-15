package outcomeIncomeJavaFX;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
		data = new OutcomeIncomeData();
		data.loadOutcomeIncomes();
		outcomeIncomesTable.setItems(data.getOutcomeIncomes());
		tableColumnDate.setSortType(TableColumn.SortType.ASCENDING);
		outcomeIncomesTable.getSortOrder().add(tableColumnDate);
		
		displayStatistics();

		// ContextMenu for TableView entries "edit/delete"
		contextMenu = new ContextMenu();

		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				deleteOutcomeIncome();
			}
		});

		MenuItem editMenuItem = new MenuItem("Edit");
		editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showEditOutcomeIncomeDialog();
			}
		});

		// show context menu over whole TableView including empty rows :/
		contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
		outcomeIncomesTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.SECONDARY) {

					// lambda expression not working (probably because of row.emptyProperty()

					// TableRow<OutcomeIncome> row = new TableRow<>();
					// row.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
					// if (isNowEmpty)
					// row.setContextMenu(null);
					// else
					// row.setContextMenu(contextMenu);
					// });

					contextMenu.show(outcomeIncomesTable, event.getScreenX(), event.getScreenY());
				}
			}
		});
	}

	// Showing dialog to add new Outcome/Income
	@FXML
	public void showOutcomeIncomeDialog() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		dialog.initOwner(mainPanel.getScene().getWindow());
		dialog.setTitle("Add new Outcome/Income");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("OutcomeIncomeDialog.fxml"));
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException e) {
			System.out.println("Couldn't load the dialog");
			e.printStackTrace();
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
				data.saveOutcomeIncomes();
				displayStatistics();
			}
		} catch (Exception e) {
			showErrorAlert();
			e.printStackTrace();
			return;
		}
	}

	// Showing dialog to edit existing Outcome/Income
	@FXML
	public void showEditOutcomeIncomeDialog() {
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

		// for testing
		System.out.println(selectedOutcomeIncome);

		outcomeIncomeController.editOutcomeIncome(selectedOutcomeIncome);

		try {
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				outcomeIncomeController.updateOutcomeIncome(selectedOutcomeIncome);

				// data binding isn't working properly, used another way to deal with and
				// working now correctly
				data.saveOutcomeIncomes();
				data.loadOutcomeIncomes();
				handleLast30daysButton();
				outcomeIncomesTable.getSortOrder().add(tableColumnDate);
				displayStatistics();
			}
		} catch (Exception e) {
			showErrorAlert();
			e.printStackTrace();
			return;
		}
	}

	@FXML
	public void handleExit() {
		Platform.exit();
	}

	private void showErrorAlert() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Wrong Input Data");
		alert.setContentText(
				"Please fill correctly all the fields\n\nTotal Value field is required - decimal pointer is comma (.)\nSource field is required\nNotes field is not required");
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
			data.saveOutcomeIncomes();
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
			System.out.println(data.getOutcomeIncomes().size());
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
		data.saveOutcomeIncomes();
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
		
		for (int i=0; i<outcomeIncomesTable.getItems().size(); i++) {
			//calculating totalValue of entries
			totalMoney += outcomeIncomesTable.getItems().get(i).getTotalValue();
			//calculating in division of outcome or income
			if(outcomeIncomesTable.getItems().get(i).getTotalValue() < 0) {
				outcomeMoney += outcomeIncomesTable.getItems().get(i).getTotalValue();
				nrOfOutcomes++;
			}
			else {
				incomeMoney += outcomeIncomesTable.getItems().get(i).getTotalValue();
				nrOfIncomes++;
			}
		}
		
		//setting average to 0 if nrOfOutcomes/Incomes = 0
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
