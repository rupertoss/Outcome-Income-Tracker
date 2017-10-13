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
	

	public OutcomeIncome getNewOutcomeIncome() {
		double totalValue = Double.parseDouble(totalValueField.getText());
		String source = sourceField.getText();
		String notes = notesArea.getText().trim();
		boolean incomeFlag = incomeButton.isSelected() ? true : false; 
		LocalDate date = datepicker.getValue();

		OutcomeIncome newOutcomeIncome = new OutcomeIncome(date, incomeFlag, totalValue, source, notes);
		return newOutcomeIncome;
	}
	
	public void editOutcomeIncome(OutcomeIncome outcomeIncome) {
		datepicker.setValue(outcomeIncome.getDate());
		if(outcomeIncome.isIncomeFlag())
			incomeButton.setSelected(true);
		totalValueField.setText("" + outcomeIncome.getTotalValue());
		sourceField.setText(outcomeIncome.getSource());
		notesArea.setText(outcomeIncome.getNotes());
	}

	public void updateOutcomeIncome(OutcomeIncome selectedOutcomeIncome) {
		selectedOutcomeIncome.setDate(datepicker.getValue());
		selectedOutcomeIncome.setTotalValue(Double.parseDouble(totalValueField.getText()));
		selectedOutcomeIncome.setSource(sourceField.getText());
		selectedOutcomeIncome.setNotes(notesArea.getText().trim());
	}
	
}
