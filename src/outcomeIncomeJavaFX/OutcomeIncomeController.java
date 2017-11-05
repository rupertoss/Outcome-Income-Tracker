package outcomeIncomeJavaFX;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import outcomeIncomeData.OutcomeIncome;

public class OutcomeIncomeController {
	
	@FXML
	private ToggleGroup outcomeIncomeGroup;
	
	@FXML
	private RadioButton outcomeButton;
	
	@FXML
	private RadioButton incomeButton;
	
	@FXML
	private DatePicker datepicker;

	@FXML
	private TextField totalValueField;
	
	@FXML
	private TextField sourceField;
	
	@FXML
	private TextArea notesArea;
	
	//creating new OutcomeIncome from dialog
	public OutcomeIncome getNewOutcomeIncome() {
		double totalValue = Double.parseDouble(totalValueField.getText());
		String source = sourceField.getText();
		String notes = notesArea.getText().trim();
		boolean incomeFlag = incomeButton.isSelected() ? true : false; 
		LocalDate date = datepicker.getValue();

		OutcomeIncome newOutcomeIncome = new OutcomeIncome(date, incomeFlag, totalValue, source, notes);
		return newOutcomeIncome;
	}
	
	//setting dialog fields by selected OutcomeIncome properties
	public void editOutcomeIncome(OutcomeIncome oi) {
		datepicker.setValue(oi.getDate());
		if(oi.isIncomeFlag())
			incomeButton.setSelected(true);
		totalValueField.setText("" + oi.getTotalValue());
		sourceField.setText(oi.getSource());
		notesArea.setText(oi.getNotes());
	}

	//updating OutcomeIncome properties by values from editDialog 
	public void updateOutcomeIncome(OutcomeIncome oi) {
		oi.setDate(datepicker.getValue());
		if (oi.isIncomeFlag() == incomeButton.isSelected()) {
			oi.setTotalValue(Double.parseDouble(totalValueField.getText()));
		} else {
			oi.setIncomeFlag(incomeButton.isSelected());
			oi.setTotalValue(-Double.parseDouble(totalValueField.getText()));
		}
		oi.setSource(sourceField.getText());
		oi.setNotes(notesArea.getText().trim());
	}
	
}
