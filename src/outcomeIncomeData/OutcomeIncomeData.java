package outcomeIncomeData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OutcomeIncomeData {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static String filename = "OutcomeIncome.dat";

	private static ObservableList<OutcomeIncome> outcomeIncomesList;

	public OutcomeIncomeData() {
		outcomeIncomesList = FXCollections.observableArrayList();
	}

	// return pre-sorted list by date
	public ObservableList<OutcomeIncome> getOutcomeIncomes() {
		Collections.sort(outcomeIncomesList);
		return outcomeIncomesList;
	}

	public void addOutcomeIncome(OutcomeIncome outcomeIncome) {
		outcomeIncomesList.add(outcomeIncome);
	}

	public void deleteOutcomeIncome(OutcomeIncome outcomeIncome) {
		outcomeIncomesList.remove(outcomeIncome);
	}

	// saving data to a binaryFile
	public void saveOutcomeIncomes() {
		try (DataOutputStream dis = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(filename)))) {
			for (int i = 0; i < outcomeIncomesList.size(); i++) {
				dis.writeUTF(outcomeIncomesList.get(i).getDate().format(formatter));
				dis.writeBoolean(outcomeIncomesList.get(i).isIncomeFlag());
				double totalValue;
				totalValue = (outcomeIncomesList.get(i).isIncomeFlag()) ? outcomeIncomesList.get(i).getTotalValue() : -outcomeIncomesList.get(i).getTotalValue();
				dis.writeDouble(totalValue);
				dis.writeUTF(outcomeIncomesList.get(i).getSource());
				dis.writeUTF(outcomeIncomesList.get(i).getNotes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// loading data from a binaryFile
	public void loadOutcomeIncomes() {
		outcomeIncomesList = FXCollections.observableArrayList();

		try (DataInputStream dis = new DataInputStream(
				new BufferedInputStream(new FileInputStream(filename)))) {
			boolean eof = false;
			while (!eof) {
				try {
					String dateString = dis.readUTF();
					LocalDate date = LocalDate.parse(dateString, formatter);
					boolean incomeFlag = dis.readBoolean();
					double totalValue = dis.readDouble();
					String source = dis.readUTF();
					String notes = dis.readUTF();
					outcomeIncomesList.add(new OutcomeIncome(date, incomeFlag, totalValue, source, notes));
				} catch (EOFException e) {
					eof = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
