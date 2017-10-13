package outcomeIncomeData;

import java.time.LocalDate;

public class OutcomeIncome {
	private LocalDate date;
	boolean incomeFlag;
	private double totalValue;
	private String source;
	static private int outcomeIncomeId;
	private String notes;

	public OutcomeIncome(LocalDate date, boolean incomeFlag, double totalValue, String shop, String notes) {
		this.date = date;
		this.source = shop;
		outcomeIncomeId++;
		this.notes = notes;
		this.totalValue = incomeFlag ? totalValue : -totalValue;
		this.incomeFlag = incomeFlag;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}

	public static int getOutcomeIncomeId() {
		return outcomeIncomeId;
	}

	public boolean isIncomeFlag() {
		return incomeFlag;
	}

	@Override
	public String toString() {
		return "OutcomeIncome [date=" + date + ", incomeFlag=" + incomeFlag + ", totalValue=" + totalValue + ", source="
				+ source + ", notes=" + notes + "]";
	}


}
