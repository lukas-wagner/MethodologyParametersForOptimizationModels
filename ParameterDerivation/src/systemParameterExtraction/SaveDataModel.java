package systemParameterExtraction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import designpatterns.ResourceParameters;
import designpatterns.PiecewiseLinearApproximation;
import systemParameterModel.Dependency;
import systemParameterModel.SystemParameters;

/**
 * The Class SaveDataModel.
 */
public class SaveDataModel {

	/** The system parameters. */
	static	SystemParameters systemParameters = new SystemParameters();


	/**
	 * Instantiates a new "save data model".
	 * Write systemParameters to json with timestamp in fileName
	 *
	 * @param sysPara the sys para
	 */
	public SaveDataModel(SystemParameters sysPara) {
		setSystemParameters(sysPara);
		saveSystemParameterModel();
	}
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		setExemplaryValues();
		saveSystemParameterModel();
	}


	/**
	 * Sets exemplary values in system parameters.
	 */
	public static void setExemplaryValues () {
		getSystemParameters().setMainInputSystem("Test");
		List<ResourceParameters> resPara = new ArrayList<ResourceParameters>();
		ResourceParameters exRes = new ResourceParameters();
		exRes.setEfficiency(.5);
		exRes.getCapacitySetPoints().put(0, (double) 2000);
		exRes.getCapacitySetPoints().put(200, (double) 2000);
		exRes.setResourceAsStorage(true);
		exRes.setName("Test");
		exRes.addSystemState(0, "TestState", 0, 0, new int[] {1,2,3}, 0, 5000);
		PiecewiseLinearApproximation pla = new PiecewiseLinearApproximation();
		pla.setIntercept(1);
		pla.setSlope(2);
		pla.setUpperBound(2222);
		pla.setLowerBound(22);
		List<PiecewiseLinearApproximation> listpla = new ArrayList<PiecewiseLinearApproximation>();
		ArrayList<List<PiecewiseLinearApproximation>> listOfAllPla = new ArrayList<List<PiecewiseLinearApproximation>>();

		listpla.add(pla);
		listOfAllPla.add(listpla);
		exRes.getPlaList().addAll(listOfAllPla); // correct, wrong package included
		//		exRes.
		resPara.add(exRes);

		getSystemParameters().getResourceParameters().addAll(resPara);
	}
	
	public static SystemParameters setExemplaryValuesWithLocalVar () {
		SystemParameters sysPara = new SystemParameters();
		sysPara.setMainInputSystem("Test");
		List<ResourceParameters> resPara = new ArrayList<ResourceParameters>();
		ResourceParameters exRes = new ResourceParameters();
		exRes.setEfficiency(.5);
		exRes.getCapacitySetPoints().put(0, (double) 2000);
		exRes.getCapacitySetPoints().put(200, (double) 2000);
		exRes.setResourceAsStorage(true);
		exRes.setName("Test");
		exRes.addSystemState(0, "TestState", 0, 0, new int[] {1,2,3}, 0, 5000);
		PiecewiseLinearApproximation pla = new PiecewiseLinearApproximation();
		pla.setIntercept(1);
		pla.setSlope(2);
		pla.setUpperBound(2222);
		pla.setLowerBound(22);
		List<PiecewiseLinearApproximation> listpla = new ArrayList<PiecewiseLinearApproximation>();
		ArrayList<List<PiecewiseLinearApproximation>> listOfAllPla = new ArrayList<List<PiecewiseLinearApproximation>>();

		listpla.add(pla);
		listOfAllPla.add(listpla);
		exRes.getPlaList().addAll(listOfAllPla); // correct, wrong package included
		//		exRes.
		resPara.add(exRes);

		sysPara.getResourceParameters().addAll(resPara);
		
		
		Dependency dep = new Dependency(); 
		List<String> relevantInputs = new ArrayList<String>();
		List<String> relevantOutputs = new ArrayList<String>();
		relevantInputs.add("Test");
		relevantOutputs.add("Output");
		relevantOutputs.add("Output2");
		dep.setTypeOfDependency("correlative");
		dep.setRelevantInputs(relevantInputs);
		dep.setRelevantOutputs(relevantOutputs);
		List<Dependency> depList = new ArrayList<Dependency>();
		depList.add(dep);
		depList.add(dep);
		sysPara.setDependencies(depList);
		return sysPara;
	}

	/**
	 * Save system parameter model to json.
	 */
	public static void saveSystemParameterModel() {

		Gson gson = new Gson();
		String json = gson.toJson(getSystemParameters());

		writeJson(json, "systemParameters");
	}


	/**
	 * Write json with time stamp.
	 *
	 * @param jsonString the content
	 * @param nameOfFile the name of file
	 */
	public static void writeJson (String jsonString, String nameOfFile) {
		// Get the current date and time
		LocalDateTime currentDateTime = LocalDateTime.now();
		// Define the desired date and time format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
		// Format the current date and time using the formatter
		String formattedDateTime = currentDateTime.format(formatter);
		Path filePath = Path.of("src/output/"+nameOfFile+"_"+formattedDateTime+".json"); // Replace with your desired file path
		try {
			Files.write(filePath, jsonString.getBytes(), StandardOpenOption.CREATE);
			System.out.println("String has been written to the file " + nameOfFile+"_"+formattedDateTime +" successfully.");
		} catch (IOException e) {
			System.err.println("An error occurred while writing to the file: " + e.getMessage());
		}


	}

	/**
	 * Gets the system parameters.
	 *
	 * @return the systemParameters
	 */
	public static SystemParameters getSystemParameters() {
		return systemParameters;
	}

	/**
	 * Sets the system parameters.
	 *
	 * @param systemParameters the systemParameters to set
	 */
	public static void setSystemParameters(SystemParameters systemParameters) {
		SaveDataModel.systemParameters = systemParameters;
	}
}
