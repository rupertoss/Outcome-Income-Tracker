package outcomeIncomeData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OutcomeIncomeData {

	private static OutcomeIncomeData instance = new OutcomeIncomeData();
	private static ObservableList<OutcomeIncome> outcomeIncomesList;
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public final String DB_NAME = "oidata.db";
	public final String CONNECTION = "jdbc:sqlite:" + DB_NAME;
	
	public final String TABLE = "outcomeIncomeData";
	public final String COLUMN_ID = "_id";
	public final String COLUMN_DATE = "date";
	public final String COLUMN_INCOMEFLAG = "incomeFlag";
	public final String COLUMN_VALUE = "value";
	public final String COLUMN_SOURCE = "source";
	public final String COLUMN_NOTES = "notes";
	public final int INDEX_ID = 1;
	public final int INDEX_DATE = 2;
	public final int INDEX_INCOMEFLAG = 3;
	public final int INDEX_VALUE = 4;
	public final int INDEX_SOURCE = 5;
	public final int INDEX_NOTES = 6;
	
	
	public Connection connection; 

	private OutcomeIncomeData() {
		outcomeIncomesList = FXCollections.observableArrayList();
	}

	public static OutcomeIncomeData getInstance() {
		return instance;
	}

	// return pre-sorted list by date
	public ObservableList<OutcomeIncome> getOutcomeIncomes() {
		Collections.sort(outcomeIncomesList);
		return outcomeIncomesList;
	}

	public void addOutcomeIncome(OutcomeIncome oi) {
		try {
			Statement statement = connection.createStatement();
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO ");
			sb.append(TABLE);
			sb.append(" (");
			sb.append(COLUMN_DATE);
			sb.append(", ");
			sb.append(COLUMN_INCOMEFLAG);
			sb.append(", ");
			sb.append(COLUMN_VALUE);
			sb.append(", ");
			sb.append(COLUMN_SOURCE);
			sb.append(", ");
			sb.append(COLUMN_NOTES);
			sb.append(") values ('");
			sb.append(oi.getDate().format(formatter));
			sb.append("', '");
			sb.append(oi.isIncomeFlag());
			sb.append("', '");
			sb = oi.isIncomeFlag() ? sb.append(oi.getTotalValue()) : sb.append(-oi.getTotalValue());
			sb.append("', '");
			sb.append(oi.getSource());
			sb.append("', '");
			sb.append(oi.getNotes());
			sb.append("')");
			statement.execute(sb.toString());
			sb = new StringBuilder();
			sb.append("SELECT MAX(");
			sb.append(COLUMN_ID);
			sb.append(") FROM ");
			sb.append(TABLE);
			ResultSet lastAdded = statement.executeQuery(sb.toString());
//			System.out.println(lastAdded.getInt(INDEX_ID));
			oi.setId(lastAdded.getInt(INDEX_ID));
			outcomeIncomesList.add(oi);
		} catch (SQLException e ) {
			System.out.println(e.getMessage());
		}
	}

	public void deleteOutcomeIncome(OutcomeIncome oi) {
		outcomeIncomesList.remove(oi);
		try {
			Statement statement = connection.createStatement();
			StringBuilder sb = new StringBuilder();
			sb.append("DELETE FROM ");
			sb.append(TABLE);
			sb.append(" WHERE ");
			sb.append(COLUMN_ID);
			sb.append(" = '");
			sb.append(oi.getId());
			sb.append("'");
			statement.execute(sb.toString());
			
			//need proper implementation of an error (couldn't process deleting an item >> alert.error
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//opening connection with database
	public boolean open(String connectionString) {
		try {
			connection = DriverManager.getConnection(connectionString);
			connection.setAutoCommit(false);
			return true;
		} catch (SQLException e) {
			//need implementation of alert.error >> controller
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public void close() {
		try {
			connection.rollback();
			connection.close();
		} catch (SQLException e ) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void loadDB() {
		try {
			Statement statement = connection.createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + TABLE);
			
			while (results.next()) {
				int id = results.getInt(INDEX_ID);
				LocalDate date = LocalDate.parse(results.getString(INDEX_DATE), formatter);
				boolean incomeFlag = Boolean.parseBoolean(results.getString(INDEX_INCOMEFLAG));
				double totalValue = results.getDouble(INDEX_VALUE);
				String source = results.getString(INDEX_SOURCE);
				String notes = results.getString(INDEX_NOTES);
//				System.out.println(new OutcomeIncome (id, date, incomeFlag, totalValue, source, notes));
				outcomeIncomesList.add(new OutcomeIncome (id, date, incomeFlag, totalValue, source, notes));
			}
			results.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void updateDB (OutcomeIncome oi) {
		try {
			Statement statement = connection.createStatement();
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE ");
			sb.append(TABLE);
			sb.append(" SET ");
			sb.append(COLUMN_DATE);
			sb.append(" = '");
			sb.append(oi.getDate());
			sb.append("', ");
			sb.append(COLUMN_INCOMEFLAG);
			sb.append(" = '");
			sb.append(oi.isIncomeFlag());
			sb.append("', ");
			sb.append(COLUMN_VALUE);
			sb.append(" = '");
			sb = oi.isIncomeFlag() ? sb.append(oi.getTotalValue()) : sb.append(-oi.getTotalValue());
			sb.append("', ");
			sb.append(COLUMN_SOURCE);
			sb.append(" = '");
			sb.append(oi.getSource());
			sb.append("', ");
			sb.append(COLUMN_NOTES);
			sb.append(" = '");
			sb.append(oi.getNotes());
			sb.append("' WHERE ");
			sb.append(COLUMN_ID);
			sb.append(" = '");
			sb.append(oi.getId());
			sb.append("'");
//			System.out.println(sb.toString());
			statement.execute(sb.toString());
		} catch (SQLException e ) {
			System.out.println(e.getMessage());
		}
	}
	
	public void saveDB () {
		try {
			Statement statement = connection.createStatement();
			
			for (OutcomeIncome oi : outcomeIncomesList) {
				StringBuilder sb = new StringBuilder();
				sb.append("INSERT INTO ");
				sb.append(TABLE);
				sb.append(" (");
				sb.append(COLUMN_DATE);
				sb.append(", ");
				sb.append(COLUMN_INCOMEFLAG);
				sb.append(", ");
				sb.append(COLUMN_VALUE);
				sb.append(", ");
				sb.append(COLUMN_SOURCE);
				sb.append(", ");
				sb.append(COLUMN_NOTES);
				sb.append(") values ('");
				sb.append(oi.getDate().format(formatter));
				sb.append("', '");
				sb.append(oi.isIncomeFlag());
				sb.append("', '");
				sb = oi.isIncomeFlag() ? sb.append(oi.getTotalValue()) : sb.append(-oi.getTotalValue());
				sb.append("', '");
				sb.append(oi.getSource());
				sb.append("', '");
				sb.append(oi.getNotes());
				sb.append("')");
//				System.out.println(sb.toString());
				statement.execute(sb.toString());
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void createDB () {
		try 
			{
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE IF NOT EXISTS ");
			sb.append(TABLE);
			sb.append(" (");
			sb.append(COLUMN_ID);
			sb.append(" INTEGER PRIMARY KEY, ");
			sb.append(COLUMN_DATE);
			sb.append(" TEXT NOT NULL, ");
			sb.append(COLUMN_INCOMEFLAG);
			sb.append(" TEXT NOT NULL, ");
			sb.append(COLUMN_VALUE);
			sb.append(" TEXT NOT NULL, ");
			sb.append(COLUMN_SOURCE);
			sb.append(" TEXT NOT NULL, ");
			sb.append(COLUMN_NOTES);
			sb.append(" TEXT)");
//			System.out.println(sb.toString());
			statement.execute(sb.toString());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
}