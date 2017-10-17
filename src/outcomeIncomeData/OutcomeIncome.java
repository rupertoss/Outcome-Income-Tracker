package outcomeIncomeData;

import java.io.Serializable;
import java.time.LocalDate;

public class OutcomeIncome implements Comparable<OutcomeIncome>, Serializable {
	private LocalDate date;
	private boolean incomeFlag;
	private double totalValue;
	private String source;
	private String notes;
	static final private long serialVersionUID = 27L;

	public OutcomeIncome(LocalDate date, boolean incomeFlag, double totalValue, String shop, String notes) {
		this.date = date;
		this.source = shop;
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

	public boolean isIncomeFlag() {
		return incomeFlag;
	}

	@Override
	public String toString() {
		return "OutcomeIncome [date=" + date + ", incomeFlag=" + incomeFlag + ", totalValue=" + totalValue + ", source="
				+ source + ", notes=" + notes + "]";
	}
	
	@Override
	public int compareTo(OutcomeIncome outcomeIncome) {
		return this.getDate().compareTo(outcomeIncome.getDate());
		
	}


}
