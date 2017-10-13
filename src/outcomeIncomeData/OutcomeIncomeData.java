package outcomeIncomeData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OutcomeIncomeData {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static String filename = "OutcomeIncome.txt";

	private static ObservableList<OutcomeIncome> outcomeIncomesList;

	public OutcomeIncomeData() {
		outcomeIncomesList = FXCollections.observableArrayList();
	}

	
	//probably it is worth trying to sort with comparator
	public ObservableList<OutcomeIncome> getOutcomeIncomes() {
//		System.out.println(outcomeIncomesList);
//		outcomeIncomesList.sorted();
//		System.out.println(outcomeIncomesList);
		return outcomeIncomesList;
	}

	public void addOutcomeIncome(OutcomeIncome outcomeIncome) {
		outcomeIncomesList.add(outcomeIncome);
	}

	public void deleteOutcomeIncome(OutcomeIncome outcomeIncome) {
		outcomeIncomesList.remove(outcomeIncome);
	}

	public void saveOutcomeIncomes() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
			for (int i=0; i<outcomeIncomesList.size(); i++) {
				OutcomeIncome item = outcomeIncomesList.get(i);
				StringBuilder sb = new StringBuilder();
				sb.append(item.getDate().format(formatter));
				sb.append("//");
				sb.append(item.isIncomeFlag());
				sb.append("//");
				sb = item.isIncomeFlag() ? sb.append(item.getTotalValue()): sb.append(-item.getTotalValue());
				sb.append("//");
				sb.append(item.getSource());
				sb.append("//");
				sb.append(item.getNotes());
				bw.write(sb.toString());
				bw.newLine();
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void loadOutcomeIncomes() {
		outcomeIncomesList = FXCollections.observableArrayList();

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String input;
			try {
				while ((input = br.readLine()) != null) {
					String[] outcomeIncomePieces = input.split("//");
					String dateString = outcomeIncomePieces[0];
					boolean incomeFlag = Boolean.parseBoolean(outcomeIncomePieces[1]);
					LocalDate date = LocalDate.parse(dateString, formatter);
					double totalValue = Double.parseDouble(outcomeIncomePieces[2]);
					String source = outcomeIncomePieces[3];
					String notes = outcomeIncomePieces[4];

					outcomeIncomesList.add(new OutcomeIncome(date, incomeFlag, totalValue, source, notes));
				}
			} catch (Exception e) {
				e.getMessage();
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

}
