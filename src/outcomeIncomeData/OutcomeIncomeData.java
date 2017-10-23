package outcomeIncomeData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import outcomeIncomeJavaFX.Controller;

public class OutcomeIncomeData {

	// textFile
	// private static String filename = "OutcomeIncome.txt";

	// binaryFile
	// private static String filename = "OutcomeIncome.dat";

	// binaryFile with Serialization
	private static String filename = "OutcomeIncome.bin";

	// not necessary in binaryFile with Serialization
	// private static DateTimeFormatter formatter =
	// DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static OutcomeIncomeData instance = new OutcomeIncomeData();
	private static ObservableList<OutcomeIncome> outcomeIncomesList;

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

	public void addOutcomeIncome(OutcomeIncome outcomeIncome) {
		outcomeIncomesList.add(outcomeIncome);
	}

	public void deleteOutcomeIncome(OutcomeIncome outcomeIncome) {
		outcomeIncomesList.remove(outcomeIncome);
	}

	// saving data to a File
	public void saveOutcomeIncomes(File file) {

		if (file != null) {
			filename = file.getAbsolutePath();

			// binaryFile with Serialization
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(filename)))) {
				for (int i = 0; i < outcomeIncomesList.size(); i++) {
					oos.writeObject(outcomeIncomesList.get(i));
				}
			} catch (IOException ioe) {
				Controller.showIOExceptionAlert(ioe);
			}

			// binaryFile
			// try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new
			// FileOutputStream(filename)))) {
			// for (int i = 0; i < outcomeIncomesList.size(); i++) {
			// dos.writeUTF(outcomeIncomesList.get(i).getDate().format(formatter));
			// dos.writeBoolean(outcomeIncomesList.get(i).isIncomeFlag());
			// double totalValue;
			// totalValue = (outcomeIncomesList.get(i).isIncomeFlag()) ?
			// outcomeIncomesList.get(i).getTotalValue() :
			// -outcomeIncomesList.get(i).getTotalValue();
			// dos.writeDouble(totalValue);
			// dos.writeUTF(outcomeIncomesList.get(i).getSource());
			// dos.writeUTF(outcomeIncomesList.get(i).getNotes());
			// }
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

			// textFile
			// try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
			// for (int i = 0; i < outcomeIncomesList.size(); i++) {
			// OutcomeIncome item = outcomeIncomesList.get(i);
			// StringBuilder sb = new StringBuilder();
			// sb.append(item.getDate().format(formatter));
			// sb.append("//");
			// sb.append(item.isIncomeFlag());
			// sb.append("//");
			// sb = item.isIncomeFlag() ? sb.append(item.getTotalValue()) :
			// sb.append(-item.getTotalValue());
			// sb.append("//");
			// sb.append(item.getSource());
			// sb.append("//");
			// sb.append(item.getNotes());
			// bw.write(sb.toString());
			// bw.newLine();
			// }
			// } catch (Exception e) {
			// e.getMessage();
			// }
		}
	}

	// loading data from a File
	public void loadOutcomeIncomes(File file, boolean addFlag) {
		// outcomeIncomesList = FXCollections.observableArrayList();

		if (file != null) {
			filename = file.getPath();

			if (!addFlag)
				outcomeIncomesList.clear();

			// binaryFile with Serialization
			try (ObjectInputStream ois = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(filename)))) {
				boolean eof = false;
				while (!eof) {
					try {
						outcomeIncomesList.add((OutcomeIncome) ois.readObject());
					} catch (ClassNotFoundException cnfe) {
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setHeaderText("Couldn't read data from a file.\nFile may be corrupted");
						alert.setContentText(cnfe.getClass().getSimpleName() + "\n" + cnfe.getMessage());
						alert.showAndWait();
					} catch (EOFException eofe) {
						eof = true;
					}
				}
			} catch (IOException ioe) {
				Controller.showIOExceptionAlert(ioe);
			}

			// binaryFile
			// try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new
			// FileInputStream(filename)))) {
			// boolean eof = false;
			// while (!eof) {
			// try {
			// String dateString = dis.readUTF();
			// LocalDate date = LocalDate.parse(dateString, formatter);
			// boolean incomeFlag = dis.readBoolean();
			// double totalValue = dis.readDouble();
			// String source = dis.readUTF();
			// String notes = dis.readUTF();
			// outcomeIncomesList.add(new OutcomeIncome(date, incomeFlag, totalValue,
			// source, notes));
			// } catch (EOFException e) {
			// eof = true;
			// }
			// }
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

			// textFile
			// try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			// String input;
			// try {
			// while ((input = br.readLine()) != null) {
			// String[] outcomeIncomePieces = input.split("//");
			// String dateString = outcomeIncomePieces[0];
			// boolean incomeFlag = Boolean.parseBoolean(outcomeIncomePieces[1]);
			// LocalDate date = LocalDate.parse(dateString, formatter);
			// double totalValue = Double.parseDouble(outcomeIncomePieces[2]);
			// String source = outcomeIncomePieces[3];
			// String notes = outcomeIncomePieces[4];
			//
			// outcomeIncomesList.add(new OutcomeIncome(date, incomeFlag, totalValue,
			// source, notes));
			// }
			// } catch (Exception e) {
			// e.getMessage();
			// }
			// } catch (Exception e) {
			// e.getMessage();
			// }
		}
	}


	public String getFilename() {
		return filename;
	}

}
