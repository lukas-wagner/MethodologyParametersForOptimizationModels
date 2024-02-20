package systemParameterExtraction;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class TimeSeriesData.
 */
public class TimeSeriesData {
	
	/** The name of resource. */
	String nameOfResource; 
	
	/** The header. */
	List<String> header = new ArrayList<String>();
		
	/** The time stamps. */
	List<Double> timeStamps = new ArrayList<Double>();
	
	/** The input values UN processed. */
	List<Double[]> inputValues_UN_Processed = new ArrayList<Double[]>();
	
	/** The input values processed. */
	List<Double[]> inputValues_Processed = new ArrayList<Double[]>();
	
	/** The output values UN processed. */
	List<Double> outputValues_UN_Processed = new ArrayList<Double>();
	
	/** The output values processed. */
	List<Double> outputValues_Processed = new ArrayList<Double>();

	/** The system states measured. */
	List<Integer> systemStates_Measured = new ArrayList<Integer>();
	
	/** The state of charge values UN processed. */
	List<Double> stateOfChargeValues_UN_Processed = new ArrayList<Double>();
	
	/** The state of charge values processed. */
	List<Double> stateOfChargeValues_Processed = new ArrayList<Double>();

	/** The input values Inside storage processed. */
	List<Double[]> inputValues_INside_Storage_Processed = new ArrayList<Double[]>();

	/** The input values Inside storage UN processed. */
	List<Double[]> inputValues_INside_Storage_UN_Processed = new ArrayList<Double[]>();
	
	/** The output values Inside storage processed. */
	List<Double> outputValues_INside_Storage_Processed = new ArrayList<Double>();
	
	/** The output values Inside storage UN processed. */
	List<Double> outputValues_INside_Storage_UN_Processed = new ArrayList<Double>();

	/** The file path time stamps. */
	String filePath_TimeStamps;

	/** The file path input values. */
	String filePath_InputValues;

	/** The file path output values. */
	String filePath_OutputValues;

	/** The file path system states. */
	String filePath_SystemStates;

	/** The file path state of charge values. */
	String filePath_StateOfChargeValues;
	
	/** The file path input values storage. */
	String filePath_InputValuesStorage; 
	
	/** The file path output values storage. */
	String filePath_OutputValuesStorage; 


	/**
	 * Gets the time stamps.
	 *
	 * @return the timeStamps
	 */
	public List<Double> getTimeStamps() {
		return timeStamps;
	}

	/**
	 * Sets the time stamps.
	 *
	 * @param timeStamps the timeStamps to set
	 */
	public void setTimeStamps(List<Double> timeStamps) {
		this.timeStamps = timeStamps;
	}

	/**
	 * Gets the input values U N processed.
	 *
	 * @return the inputValues_UN_Processed
	 */
	public List<Double[]> getInputValues_UN_Processed() {
		return inputValues_UN_Processed;
	}

	/**
	 * Sets the input values U N processed.
	 *
	 * @param inputValues_UN_Processed the inputValues_UN_Processed to set
	 */
	public void setInputValues_UN_Processed(List<Double[]> inputValues_UN_Processed) {
		this.inputValues_UN_Processed = inputValues_UN_Processed;
	}

	/**
	 * Gets the input values processed.
	 *
	 * @return the inputValues_Processed
	 */
	public List<Double[]> getInputValues_Processed() {
		return inputValues_Processed;
	}

	/**
	 * Sets the input values processed.
	 *
	 * @param inputValues_Processed the inputValues_Processed to set
	 */
	public void setInputValues_Processed(List<Double[]> inputValues_Processed) {
		this.inputValues_Processed = inputValues_Processed;
	}

	/**
	 * Gets the output values U N processed.
	 *
	 * @return the outputValues_UN_Processed
	 */
	public List<Double> getOutputValues_UN_Processed() {
		return outputValues_UN_Processed;
	}

	/**
	 * Sets the output values U N processed.
	 *
	 * @param outputValues_UN_Processed the outputValues_UN_Processed to set
	 */
	public void setOutputValues_UN_Processed(List<Double> outputValues_UN_Processed) {
		this.outputValues_UN_Processed = outputValues_UN_Processed;
	}

	/**
	 * Gets the output values processed.
	 *
	 * @return the outputValues_Processed
	 */
	public List<Double> getOutputValues_Processed() {
		return outputValues_Processed;
	}

	/**
	 * Sets the output values processed.
	 *
	 * @param outputValues_Processed the outputValues_Processed to set
	 */
	public void setOutputValues_Processed(List<Double> outputValues_Processed) {
		this.outputValues_Processed = outputValues_Processed;
	}


	/**
	 * Gets the state of charge values U N processed.
	 *
	 * @return the stateOfChargeValues_UN_Processed
	 */
	public List<Double> getStateOfChargeValues_UN_Processed() {
		return stateOfChargeValues_UN_Processed;
	}

	/**
	 * Sets the state of charge values U N processed.
	 *
	 * @param stateOfChargeValues_UN_Processed the stateOfChargeValues_UN_Processed to set
	 */
	public void setStateOfChargeValues_UN_Processed(List<Double> stateOfChargeValues_UN_Processed) {
		this.stateOfChargeValues_UN_Processed = stateOfChargeValues_UN_Processed;
	}

	/**
	 * Gets the state of charge values processed.
	 *
	 * @return the stateOfChargeValues_Processed
	 */
	public List<Double> getStateOfChargeValues_Processed() {
		return stateOfChargeValues_Processed;
	}

	/**
	 * Sets the state of charge values processed.
	 *
	 * @param stateOfChargeValues_Processed the stateOfChargeValues_Processed to set
	 */
	public void setStateOfChargeValues_Processed(List<Double> stateOfChargeValues_Processed) {
		this.stateOfChargeValues_Processed = stateOfChargeValues_Processed;
	}

	/**
	 * Gets the name of resource.
	 *
	 * @return the nameOfResource
	 */
	public String getNameOfResource() {
		return nameOfResource;
	}

	/**
	 * Sets the name of resource.
	 *
	 * @param nameOfResource the nameOfResource to set
	 */
	public void setNameOfResource(String nameOfResource) {
		this.nameOfResource = nameOfResource;
	}

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public List<String> getHeader() {
		return header;
	}

	/**
	 * Sets the header.
	 *
	 * @param header the header to set
	 */
	public void setHeader(List<String> header) {
		this.header = header;
	}

	/**
	 * Gets the system states measured.
	 *
	 * @return the systemStates_Measured
	 */
	public List<Integer> getSystemStates_Measured() {
		return systemStates_Measured;
	}

	/**
	 * Sets the system states measured.
	 *
	 * @param systemStates_Measured the systemStates_Measured to set
	 */
	public void setSystemStates_Measured(List<Integer> systemStates_Measured) {
		this.systemStates_Measured = systemStates_Measured;
	}

	/**
	 * @return the inputValues_INside_Storage_Processed
	 */
	public List<Double[]> getInputValues_INside_Storage_Processed() {
		return inputValues_INside_Storage_Processed;
	}

	/**
	 * @param inputValues_INside_Storage_Processed the inputValues_INside_Storage_Processed to set
	 */
	public void setInputValues_INside_Storage_Processed(List<Double[]> inputValues_INside_Storage_Processed) {
		this.inputValues_INside_Storage_Processed = inputValues_INside_Storage_Processed;
	}

	/**
	 * @return the inputValues_INside_Storage_UN_Processed
	 */
	public List<Double[]> getInputValues_INside_Storage_UN_Processed() {
		return inputValues_INside_Storage_UN_Processed;
	}

	/**
	 * @param inputValues_INside_Storage_UN_Processed the inputValues_INside_Storage_UN_Processed to set
	 */
	public void setInputValues_INside_Storage_UN_Processed(List<Double[]> inputValues_INside_Storage_UN_Processed) {
		this.inputValues_INside_Storage_UN_Processed = inputValues_INside_Storage_UN_Processed;
	}

	/**
	 * @return the outputValues_INside_Storage_Processed
	 */
	public List<Double> getOutputValues_INside_Storage_Processed() {
		return outputValues_INside_Storage_Processed;
	}

	/**
	 * @param outputValues_INside_Storage_Processed the outputValues_INside_Storage_Processed to set
	 */
	public void setOutputValues_INside_Storage_Processed(List<Double> outputValues_INside_Storage_Processed) {
		this.outputValues_INside_Storage_Processed = outputValues_INside_Storage_Processed;
	}

	/**
	 * @return the outputValues_INside_Storage_UN_Processed
	 */
	public List<Double> getOutputValues_INside_Storage_UN_Processed() {
		return outputValues_INside_Storage_UN_Processed;
	}

	/**
	 * @param outputValues_INside_Storage_UN_Processed the outputValues_INside_Storage_UN_Processed to set
	 */
	public void setOutputValues_INside_Storage_UN_Processed(List<Double> outputValues_INside_Storage_UN_Processed) {
		this.outputValues_INside_Storage_UN_Processed = outputValues_INside_Storage_UN_Processed;
	}

	/**
	 * @return the filePath_TimeStamps
	 */
	public String getFilePath_TimeStamps() {
		return filePath_TimeStamps;
	}

	/**
	 * @param filePath_TimeStamps the filePath_TimeStamps to set
	 */
	public void setFilePath_TimeStamps(String filePath_TimeStamps) {
		this.filePath_TimeStamps = filePath_TimeStamps;
	}

	/**
	 * @return the filePath_InputValues
	 */
	public String getFilePath_InputValues() {
		return filePath_InputValues;
	}

	/**
	 * @param filePath_InputValues the filePath_InputValues to set
	 */
	public void setFilePath_InputValues(String filePath_InputValues) {
		this.filePath_InputValues = filePath_InputValues;
	}

	/**
	 * @return the filePath_OutputValues
	 */
	public String getFilePath_OutputValues() {
		return filePath_OutputValues;
	}

	/**
	 * @param filePath_OutputValues the filePath_OutputValues to set
	 */
	public void setFilePath_OutputValues(String filePath_OutputValues) {
		this.filePath_OutputValues = filePath_OutputValues;
	}

	/**
	 * @return the filePath_SystemStates
	 */
	public String getFilePath_SystemStates() {
		return filePath_SystemStates;
	}

	/**
	 * @param filePath_SystemStates the filePath_SystemStates to set
	 */
	public void setFilePath_SystemStates(String filePath_SystemStates) {
		this.filePath_SystemStates = filePath_SystemStates;
	}

	/**
	 * @return the filePath_StateOfChargeValues
	 */
	public String getFilePath_StateOfChargeValues() {
		return filePath_StateOfChargeValues;
	}

	/**
	 * @param filePath_StateOfChargeValues the filePath_StateOfChargeValues to set
	 */
	public void setFilePath_StateOfChargeValues(String filePath_StateOfChargeValues) {
		this.filePath_StateOfChargeValues = filePath_StateOfChargeValues;
	}

	/**
	 * @return the filePath_InputValuesStorage
	 */
	public String getFilePath_InputValuesStorage() {
		return filePath_InputValuesStorage;
	}

	/**
	 * @param filePath_InputValuesStorage the filePath_InputValuesStorage to set
	 */
	public void setFilePath_InputValuesStorage(String filePath_InputValuesStorage) {
		this.filePath_InputValuesStorage = filePath_InputValuesStorage;
	}

	/**
	 * @return the filePath_OutputValuesStorage
	 */
	public String getFilePath_OutputValuesStorage() {
		return filePath_OutputValuesStorage;
	}

	/**
	 * @param filePath_OutputValuesStorage the filePath_OutputValuesStorage to set
	 */
	public void setFilePath_OutputValuesStorage(String filePath_OutputValuesStorage) {
		this.filePath_OutputValuesStorage = filePath_OutputValuesStorage;
	}


}
