package outcomeIncomeData;

import java.time.LocalDate;

public class OutcomeIncome implements Comparable<OutcomeIncome> {
	private int id = -1;
	private LocalDate date;
	private boolean incomeFlag;
	private double totalValue;
	private String source;
	private String notes;

	public OutcomeIncome(LocalDate date, boolean incomeFlag, double totalValue, String source, String notes) {
		this.date = date;
		this.incomeFlag = incomeFlag;
		this.totalValue = incomeFlag ? totalValue : -totalValue;
		this.source = source;
		this.notes = notes;
	}
	
	public OutcomeIncome(int id, LocalDate date, boolean incomeFlag, double totalValue, String source, String notes) {
		this(date, incomeFlag, totalValue, source, notes);
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public boolean isIncomeFlag() {
		return incomeFlag;
	}
	
	public void setIncomeFlag(boolean incomeFlag) {
		this.incomeFlag = incomeFlag;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	

	@Override
	public String toString() {
		return "OutcomeIncome [id=" + id + ", date=" + date + ", incomeFlag=" + incomeFlag + ", totalValue="
				+ totalValue + ", source=" + source + ", notes=" + notes + "]";
	}
	
	@Override
	public int compareTo(OutcomeIncome outcomeIncome) {
		return this.getDate().compareTo(outcomeIncome.getDate());
		
	}



}