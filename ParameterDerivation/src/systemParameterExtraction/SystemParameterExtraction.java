package systemParameterExtraction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import designpatterns.ResourceParameters;
import systemParameterModel.Dependency;
import systemParameterModel.SystemParameters;
/**
 * The Class SystemParameterExtraction.
 */
public class SystemParameterExtraction {

	/** The file path system data. */
	static String filePathSystemData; 

	/** The tsd system. */
	static TimeSeriesData tsdSystem = new TimeSeriesData();

	/** The file path resource data list. */
	static List<TimeSeriesData> filePathResourceDataList = new ArrayList<TimeSeriesData>();

	/** The file path form pro desc. */
	static String filePathFormProDesc; 

	/** The system parameters. */
	static SystemParameters systemParameters = new SystemParameters();

	/**
	 * Main.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		executeSystemParameterExtraction();
	}

	/**
	 * Execute system parameter extraction.
	 */
	public static void executeSystemParameterExtraction () {

		setFilePaths(); 

		// import data
		List<TimeSeriesData> timeSeriesData_UN_PreProcessed = 	importData();

		//preprocessing
		List<TimeSeriesData> timeSeriesData_PreProcessed = 		doPreProcessing(timeSeriesData_UN_PreProcessed);

		setSystemParametersFromOtherMethods(timeSeriesData_PreProcessed);

		//		setSystemParameters(SaveDataModel.setExemplaryValusesWithLocalVar());

		// save systemParameters to json
		new SaveDataModel(getSystemParameters());

	}

	/**
	 * Sets the file paths.
	 */
	public static void setFilePaths () {

		setFilePathSystemData("");

		getTsdSystem().setFilePath_TimeStamps("src/timeSeriesDataSet/resource1_timeStamps.csv");
		getTsdSystem().setFilePath_InputValues("src/timeSeriesDataSet/resource1_inputValues.csv");
		getTsdSystem().setFilePath_OutputValues("src/timeSeriesDataSet/resource2_outputValues.csv");

		TimeSeriesData resource1 = new TimeSeriesData();
		resource1.setNameOfResource("gasfired_generator");
		resource1.setFilePath_TimeStamps("src/timeSeriesDataSet/resource1_timeStamps.csv");
		resource1.setFilePath_InputValues("src/timeSeriesDataSet/resource1_inputValues.csv");
		resource1.setFilePath_OutputValues("src/timeSeriesDataSet/resource1_outputValues.csv");
		resource1.setFilePath_SystemStates("src/timeSeriesDataSet/resource1_systemstates.csv");
		getFilePathResourceDataList().add(resource1);


		TimeSeriesData resource2 = new TimeSeriesData();
		resource2.setNameOfResource("heat_exchanger");
		resource2.setFilePath_TimeStamps("src/timeSeriesDataSet/resource1_timeStamps.csv");
		resource2.setFilePath_InputValues("src/timeSeriesDataSet/resource1_outputValues.csv");
		resource2.setFilePath_OutputValues("src/timeSeriesDataSet/resource2_outputValues.csv");
		getFilePathResourceDataList().add(resource2);

		setFilePathFormProDesc("src/timeSeriesDataSet/chp_v1.json"); 

	}

	/**
	 * Import data.
	 *
	 * @return the list of time series data
	 */
	public static List<TimeSeriesData> importData () {

		List<TimeSeriesData> timeSeriesData = new ArrayList<TimeSeriesData>();

		for (int resource = 0; resource < getFilePathResourceDataList().size(); resource++) {
			TimeSeriesData tsdResource = new TimeSeriesData();
			tsdResource  = getFilePathResourceDataList().get(resource);
			// import values, *only* if filePath is not empty

			if (!(getFilePathResourceDataList().get(resource).getNameOfResource()==null)) {
				tsdResource.setNameOfResource(getFilePathResourceDataList().get(resource).getNameOfResource());
			} else {
				tsdResource.setNameOfResource("Resource " + Integer.toString(resource));
			}

			if (!(getFilePathResourceDataList().get(resource).getFilePath_TimeStamps()==null)) {
				List<Double> timeStamps = 					importTSD_Double_Value(getFilePathResourceDataList().get(resource).getFilePath_TimeStamps());
				tsdResource.getTimeStamps().addAll(timeStamps);
			}

			if (!(getFilePathResourceDataList().get(resource).getFilePath_InputValues()==null)) {
				List<Double[]> inputValues_UN_Processed = 	importTSD_Double_Array(getFilePathResourceDataList().get(resource).getFilePath_InputValues());
				tsdResource.getInputValues_UN_Processed().addAll(inputValues_UN_Processed);
			}

			if (!(getFilePathResourceDataList().get(resource).getFilePath_OutputValues()==null)) {
				List<Double> outputValues_UN_Processed = 	importTSD_Double_Value(getFilePathResourceDataList().get(resource).getFilePath_OutputValues());
				tsdResource.getOutputValues_UN_Processed().addAll(outputValues_UN_Processed); 
			}

			if (!(getFilePathResourceDataList().get(resource).getFilePath_StateOfChargeValues()==null)) {
				List<Double> soc_UN_Processed = 			importTSD_Double_Value(getFilePathResourceDataList().get(resource).getFilePath_StateOfChargeValues());
				tsdResource.getStateOfChargeValues_UN_Processed().addAll(soc_UN_Processed); 
			}

			if (!(getFilePathResourceDataList().get(resource).getFilePath_InputValuesStorage()==null)) {
				List<Double> soc_UN_Processed = 			importTSD_Double_Value(getFilePathResourceDataList().get(resource).getFilePath_InputValuesStorage());
				tsdResource.getStateOfChargeValues_UN_Processed().addAll(soc_UN_Processed); 
			}

			if (!(getFilePathResourceDataList().get(resource).getFilePath_OutputValuesStorage()==null)) {
				List<Double> soc_UN_Processed = 			importTSD_Double_Value(getFilePathResourceDataList().get(resource).getFilePath_OutputValuesStorage());
				tsdResource.getStateOfChargeValues_UN_Processed().addAll(soc_UN_Processed); 
			}

			if (!(getFilePathResourceDataList().get(resource).getFilePath_SystemStates()==null)) {
				List<Integer> statesMeasured = 				importTSD_Integer_Value(getFilePathResourceDataList().get(resource).getFilePath_SystemStates());
				tsdResource.getSystemStates_Measured().addAll(statesMeasured);
			}

			timeSeriesData.add(tsdResource);
		}

		// import system tsd
		if (!(getTsdSystem().getFilePath_TimeStamps()==null)) {
			List<Double> timeStamps = 					importTSD_Double_Value(getTsdSystem().getFilePath_TimeStamps());
			getTsdSystem().getTimeStamps().addAll(timeStamps);
		}

		if (!(getTsdSystem().getFilePath_InputValues()==null)) {
			List<Double[]> inputValues_UN_Processed = 	importTSD_Double_Array(getTsdSystem().getFilePath_InputValues());
			getTsdSystem().getInputValues_UN_Processed().addAll(inputValues_UN_Processed);
		}

		if (!(getTsdSystem().getFilePath_OutputValues()==null)) {
			List<Double> outputValues_UN_Processed = 	importTSD_Double_Value(getTsdSystem().getFilePath_OutputValues());
			getTsdSystem().getOutputValues_UN_Processed().addAll(outputValues_UN_Processed); 
		}

		return timeSeriesData;
	}


	/**
	 * Do pre processing.
	 *
	 * @param timeSeriesData the time series data
	 * @return the list of preprocessed tsd
	 */
	public static List<TimeSeriesData> doPreProcessing (List<TimeSeriesData> timeSeriesData) {
		List<TimeSeriesData> preProcessedData = new ArrayList<TimeSeriesData>();
		for (int resourceData = 0; resourceData < timeSeriesData.size(); resourceData++) {
			TimeSeriesData dataprepro = DataPreProcessing.doPreProcessing(timeSeriesData.get(resourceData));
			preProcessedData.add(dataprepro);
		}
		setTsdSystem(DataPreProcessing.doPreProcessing(getTsdSystem()));
		
		return preProcessedData; 
	}


	/**
	 * Sets the system parameters.
	 *
	 * @param timeSeriesData the new system parameters from other methods
	 */
	public static void setSystemParametersFromOtherMethods (List<TimeSeriesData> timeSeriesData) {
		// gets all parameters for all resources within system
		extractParametersPerResource(timeSeriesData);

		// determines temp res. 
		getSystemParameters().setTemporalResolutionOptimizationModel(determineTemporalResolution(getSystemParameters().getResourceParameters()));
		// converts holding durations of all states of all resources to number of time steps
		convertHoldingDurationsToTempRes();

		List<String> listOfInputs = new ArrayList<String>();
		List<Double> listOfMinPowerSysInp = new ArrayList<Double>();
		List<Double> listOfMaxPowerSysInp = new ArrayList<Double>();

		try {
			for (int inputColumn = 0; inputColumn < getTsdSystem().getInputValues_Processed().get(0).length; inputColumn++) {
				listOfInputs.add("input-"+Integer.toString(inputColumn));	
				double[] inputValues = new double[getTsdSystem().getInputValues_Processed().size()];
				for (int ts = 0 ; ts< getTsdSystem().getInputValues_Processed().size(); ts++) {
					inputValues[ts] = getTsdSystem().getInputValues_Processed().get(ts)[inputColumn];
				}
				listOfMinPowerSysInp.add(Collections.min(Arrays.asList(ArrayUtils.toObject(inputValues))));
				listOfMaxPowerSysInp.add(Collections.max(Arrays.asList(ArrayUtils.toObject(inputValues))));
			}
		} catch (Exception e) {
		}

		getSystemParameters().setInputsSystem(listOfInputs);
		getSystemParameters().setMinPowerSystemInput(listOfMinPowerSysInp);
		getSystemParameters().setMaxPowerSystemInput(listOfMaxPowerSysInp);

		getSystemParameters().setMinPowerSystemOutput(ResourceParameterExtraction.getMinValue(getTsdSystem().getOutputValues_Processed()));
		getSystemParameters().setMaxPowerSystemOutput(ResourceParameterExtraction.getMaxValue(getTsdSystem().getOutputValues_Processed()));

		getSystemParameters().getDependencies().addAll(getDependencies());
	}

	/**
	 * Extract parameters per resource.
	 *
	 * @param timeSeriesData the time series data
	 * set list of resourceParameters to systemparameter object
	 */
	public static void extractParametersPerResource (List<TimeSeriesData> timeSeriesData) {
		List<ResourceParameters> listOfResourceParameters = new ArrayList<ResourceParameters>();
		for (int resource = 0; resource < getFilePathResourceDataList().size(); resource++) {
			TimeSeriesData tsdResource = timeSeriesData.get(resource);

			ResourceParameters resourceParameters = ResourceParameterExtraction.extractParameters(tsdResource);
			listOfResourceParameters.add(resourceParameters);
		}

		getSystemParameters().setResourceParameters(listOfResourceParameters);
	}

	/**
	 * Gets the dependencies.
	 *
	 * @return the dependencies
	 */
	public static List<Dependency> getDependencies () {
		List<Dependency> dependencies = new ArrayList<Dependency>();
		new DependencyExtraction(getFilePathFormProDesc());
		dependencies = DependencyExtraction.getListOfDependencies();
		return dependencies;
	}

	/**
	 * Determine temporal resolution.
	 *
	 * @param listOfResourceParameters the list of resource parameters
	 * @return the double
	 */
	public static double determineTemporalResolution (List<ResourceParameters> listOfResourceParameters) {
		// default res = 0.25 h
		double tempRes = 0.25;
		// get temp res per resource
		// 3 min, 7.5, 15 min res.  
		List<Double> listOfTempResByResource = new ArrayList<Double>();
		for (int resource = 0; resource < listOfResourceParameters.size(); resource++) {
			listOfTempResByResource.add(determineLargestSensibleTempRes(listOfResourceParameters.get(resource)));
		}
		//		for (Entry<String, String> resourceEntry : getFilePathResourceData().entrySet()) {
		//			String nameOfResource = resourceEntry.getKey();
		//		}

		if (!listOfTempResByResource.isEmpty()) {
			tempRes = Collections.min(listOfTempResByResource);
			System.out.println("Temp res: " + tempRes);
		} else {
			tempRes = 0.25; 
			System.err.println("The list is empty. No minimum value. Default value: 0.25 h");
		}

		return tempRes;
	}

	/**
	 * Determine temp res.
	 *
	 * @param resourceParameters the name of resource
	 * @return the double
	 */
	public static double determineLargestSensibleTempRes(ResourceParameters resourceParameters) {
		double tempRes = 0.25;
		// return one of the following temp res. 
		List<Double> possibleTempsRes = new ArrayList<Double>(); 
		possibleTempsRes.add(0.25); // 15 
		possibleTempsRes.add(0.125); // 7.5 
		possibleTempsRes.add(0.0625); // 3.75 
		possibleTempsRes.add(0.05); // 3
		possibleTempsRes.add(0.025); // 1.5 

		List<Double> allStateDurationsOfResource = new ArrayList<Double>();
		for (int state = 0; state < resourceParameters.getSystemStates().size(); state++) {
			allStateDurationsOfResource.add(resourceParameters.getSystemStates().get(state).getMinStateDurationInH());	
			allStateDurationsOfResource.add(resourceParameters.getSystemStates().get(state).getMaxStateDurationInH());			
		}
		// determine optimal temp res per resource
		tempRes = selectDuration(possibleTempsRes, allStateDurationsOfResource);
		return tempRes;
	}

	/**
	 * Select duration.
	 *
	 * @param possibleTempRes the set A
	 * @param actualTempResToBeMatched the set B
	 * @return the double
	 */
	public static double selectDuration(List<Double> possibleTempRes, List<Double> actualTempResToBeMatched) {
		// Sort durations in set A in ascending order
		Collections.sort(possibleTempRes);

		// Initialize variables for minimum distance and selected duration
		double minDistance = Double.MAX_VALUE;
		double selectedDuration = -1.0;

		// Iterate over durations in set A
		for (double onePossibleTempRes : possibleTempRes) {
			// Calculate the absolute average distance with remainder consideration to durations in set B
			double averageDistance = calculateAverageDistanceWithRemainder(onePossibleTempRes, actualTempResToBeMatched);

			// Update the selected duration if the current distance is smaller
			if (averageDistance < minDistance) {
				minDistance = averageDistance;
				selectedDuration = onePossibleTempRes;
			}
		}

		if (selectedDuration==-1) selectedDuration = 0.25;
		// Return the selected duration
		return selectedDuration;
	}

	/**
	 * Calculate average distance with remainder.
	 *
	 * @param onePossibleTempRes the duration A
	 * @param actualTempResToBeMatched the set B
	 * @return the double
	 */
	private static double calculateAverageDistanceWithRemainder(double onePossibleTempRes, List<Double> actualTempResToBeMatched) {
		// Calculate the absolute average distance with remainder consideration to durations in set B
		double sumDistance = 0.0;
		for (double oneOfTheActualTempRes : actualTempResToBeMatched) {
			double remainderA = onePossibleTempRes % oneOfTheActualTempRes;
			double remainderB = oneOfTheActualTempRes % onePossibleTempRes;

			// Add both remainders to the distance calculation
			sumDistance += Math.abs(remainderA) + Math.abs(remainderB);
		}

		return (sumDistance / (2 * actualTempResToBeMatched.size()))/onePossibleTempRes;
	}

	/**
	 * Convert holding durations to temp res.
	 * iterate over all resources and all states and convert min/max state duration in h to numberOfTimeSteps in desired tempRes
	 */
	public static void convertHoldingDurationsToTempRes() {
		double tempRes = getSystemParameters().getTemporalResolutionOptimizationModel();

		// iterate over all resources and all states and convert min/max state duration in h to numberOfTimeSteps in desired tempRes
		for (int resource = 0; resource < getSystemParameters().getResourceParameters().size(); resource++) {
			for (int state = 0; state < getSystemParameters().getResourceParameters().get(resource).getSystemStates().size(); state++) {
				double minStateDurInH = getSystemParameters().getResourceParameters().get(resource).getSystemStates().get(state).getMinStateDurationInH(); 
				double maxStateDurInH = getSystemParameters().getResourceParameters().get(resource).getSystemStates().get(state).getMaxStateDurationInH();

				// Yes, integer would be a better choice than double...
				double minStateDur = Math.round(minStateDurInH/tempRes); 
				double maxStateDur = -1;
				if (maxStateDurInH == 9999) {
					maxStateDur = 9999;
				} else {
					maxStateDur = Math.round(maxStateDurInH/tempRes);
				}

				System.out.println("minStateDur: " + minStateDurInH + " -> " + minStateDur + " maxStateDur: " + maxStateDurInH+ " -> " +maxStateDur);

				getSystemParameters().getResourceParameters().get(resource).getSystemStates().get(state).setMinStateDuration(minStateDur);
				getSystemParameters().getResourceParameters().get(resource).getSystemStates().get(state).setMaxStateDuration(maxStateDur);
			}
		}
	}


	/**
	 * Import TS D double value.
	 *
	 * @param filePath the file path
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CsvException the csv exception
	 */
	public static List<Double> importTSD_Double_Value (String filePath) {

		List<Double> dataList = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				for (int i = 0; i < nextLine.length; i++) {
					dataList.add(Double.parseDouble(nextLine[i]));
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("File likely not found, IOException");
			e.printStackTrace();
		}
		return dataList;
	}


	/**
	 * Import TS D integer value.
	 *
	 * @param filePath the file path
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CsvException the csv exception
	 */
	public static List<Integer> importTSD_Integer_Value (String filePath) {

		List<Integer> dataList = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				for (int i = 0; i < nextLine.length; i++) {
					dataList.add(Integer.parseInt(nextLine[i]));
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("File likely not found, IOException");
			e.printStackTrace();
		}
		return dataList;
	}


	/**
	 * Import TS D double array.
	 *
	 * @param filePath the file path
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CsvException the csv exception
	 */
	public static List<Double[]> importTSD_Double_Array (String filePath) {

		List<Double[]> dataList = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				Double[] dataRow = new Double[nextLine.length];
				for (int i = 0; i < nextLine.length; i++) {
					dataRow[i] = Double.parseDouble(nextLine[i]);
				}
				dataList.add(dataRow);
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("File likely not found, IOException");
			e.printStackTrace();
		}
		return dataList;
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
		SystemParameterExtraction.systemParameters = systemParameters;
	}


	/**
	 * Gets the file path system data.
	 *
	 * @return the filePathSystemData
	 */
	public static String getFilePathSystemData() {
		return filePathSystemData;
	}

	/**
	 * Sets the file path system data.
	 *
	 * @param filePathSystemData the filePathSystemData to set
	 */
	public static void setFilePathSystemData(String filePathSystemData) {
		SystemParameterExtraction.filePathSystemData = filePathSystemData;
	}


	/**
	 * Gets the file path resource data list.
	 *
	 * @return the filePathResourceDataList
	 */
	public static List<TimeSeriesData> getFilePathResourceDataList() {
		return filePathResourceDataList;
	}


	/**
	 * Sets the file path resource data list.
	 *
	 * @param filePathResourceDataList the filePathResourceDataList to set
	 */
	public static void setFilePathResourceDataList(List<TimeSeriesData> filePathResourceDataList) {
		SystemParameterExtraction.filePathResourceDataList = filePathResourceDataList;
	}

	/**
	 * @return the filePathFormProDesc
	 */
	public static String getFilePathFormProDesc() {
		return filePathFormProDesc;
	}

	/**
	 * @param filePathFormProDesc the filePathFormProDesc to set
	 */
	public static void setFilePathFormProDesc(String filePathFormProDesc) {
		SystemParameterExtraction.filePathFormProDesc = filePathFormProDesc;
	}

	/**
	 * @return the tsdSystem
	 */
	public static TimeSeriesData getTsdSystem() {
		return tsdSystem;
	}

	/**
	 * @param tsdSystem the tsdSystem to set
	 */
	public static void setTsdSystem(TimeSeriesData tsdSystem) {
		SystemParameterExtraction.tsdSystem = tsdSystem;
	}
}
