package systemParameterExtraction;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import designpatterns.PiecewiseLinearApproximation;
import designpatterns.ResourceParameters;
import designpatterns.SystemState;

/**
 * The Class ResourceParameterExtraction.
 */
public class ResourceParameterExtraction {

	/** The time series data. */
	static List<Double[]> timeSeriesData = new ArrayList<Double[]>();

	/** The system states by time list. */
	static List<Integer> systemStatesByTimeList = new ArrayList<Integer>();

	/** The individual system states. */
	static List<Integer> individualSystemStates = new ArrayList<Integer>();


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			loadDataFromFile("src/testData/time-gas-el-hea.csv");
		} catch (Exception e) {
			System.err.println("File exception. File likely not found");
		}
		TimeSeriesData tsd = new TimeSeriesData();
		extractParameters(tsd);
	}

	/**
	 * Instantiates a new resource parameter extraction.
	 */
	public ResourceParameterExtraction () {

	}

	/**
	 * Instantiates a new resource parameter extraction.
	 *
	 * @param tsd the tsd
	 * @param nameOfResource the name of resource
	 * @param filePath the file path
	 */
	public ResourceParameterExtraction (TimeSeriesData tsd) {
		extractParameters(tsd);
	}

	/**
	 * Extract parameters. calls all methods necessary
	 *
	 * @param tsd the tsd
	 * @param nameOfResource the name of resource
	 * @param isStorage the is storage
	 * @return the resource parameters
	 */
	public static ResourceParameters extractParameters(TimeSeriesData tsd) {
		ResourceParameters resourceParameters = new ResourceParameters();
		resourceParameters.setName(tsd.getNameOfResource());
		int numberOfInputs = tsd.getInputValues_Processed().get(0).length; 
		
		List<Double> minInputs = new ArrayList<Double>();
		List<Double> maxInputs = new ArrayList<Double>();

		if (numberOfInputs == 1) {
			List<Double> inputValues = new ArrayList<Double>();
			for (int timeStep = 0; timeStep < tsd.getInputValues_Processed().size(); timeStep++) {
				inputValues.add(tsd.getInputValues_Processed().get(timeStep)[0]);
			}
			minInputs.add(getMinValue(inputValues));
			maxInputs.add(getMaxValue(inputValues));
			resourceParameters.setMinPowerInput(getMinValue(inputValues));
			resourceParameters.setMaxPowerInput(getMaxValue(inputValues));
		} else {
			for (int numberInput = 0; numberInput < numberOfInputs; numberInput++) {
				List<Double> inputValues = new ArrayList<Double>();
				for (int timeStep = 0; timeStep < tsd.getInputValues_Processed().size(); timeStep++) {
					inputValues.add(tsd.getInputValues_Processed().get(timeStep)[0]);
				}
				minInputs.add(getMinValue(inputValues));
				maxInputs.add(getMaxValue(inputValues));
			}
		}
		resourceParameters.setMinPowerInputs(minInputs);
		resourceParameters.setMaxPowerInputs(maxInputs);

		List<Double> outputValues = tsd.getOutputValues_Processed(); 
		resourceParameters.setMinPowerOutput(getMinValue(outputValues));
		resourceParameters.setMaxPowerOutput(getMaxValue(outputValues));

		if (!(tsd.getSystemStates_Measured().isEmpty())) {
			List<SystemState> systemStateRelatedParameters = compileListOfSystemStateParameters(tsd); 
			resourceParameters.setSystemStates(systemStateRelatedParameters);
		}
//		getRampLimitsByState_V2(tsd);
		// check if there are measured soc values, if not -> resource != storage
		if (tsd.getStateOfChargeValues_Processed().isEmpty()) {
			ArrayList<List<PiecewiseLinearApproximation>> ioParameters = getIOParameters(tsd);
			resourceParameters.setResourceAsStorage(false);

			if (ioParameters.size()==1 && ioParameters.get(0).size() == 1) {
				resourceParameters.setSlope(ioParameters.get(0).get(0).getSlope());
				resourceParameters.setIntercept(ioParameters.get(0).get(0).getIntercept());
			}
			resourceParameters.setPlaList(ioParameters);

		} else {
			resourceParameters.setResourceAsStorage(true);

			ResourceParameters storageParameters = determineStorageRelatedParameters(tsd); 
			resourceParameters.setEfficiencyInputStorage(storageParameters.getEfficiencyInputStorage());
			resourceParameters.setEfficiencyOutputStorage(storageParameters.getEfficiencyOutputStorage());
			resourceParameters.setEfficiencyOutputReciprocal(1/storageParameters.getEfficiencyOutputStorage());
			resourceParameters.setMaximumStorageCapacity(storageParameters.getMaximumStorageCapacity());
			resourceParameters.setMinimumStorageCapacity(storageParameters.getMinimumStorageCapacity());
			resourceParameters.setDegradation(storageParameters.getDegradation());
			resourceParameters.setStaticEnergyLoss(storageParameters.getStaticEnergyLoss());
			resourceParameters.setDynamicEnergyLoss(storageParameters.getDynamicEnergyLoss());
		}

		return resourceParameters;
	}

	/**
	 * Gets the min value.
	 *
	 * @param measurementValuesTSD the measurement values TSD
	 * @return the min value
	 */
	public static double getMinValue(List<Double> measurementValuesTSD) {
		return Collections.min(measurementValuesTSD);
	}

	/**
	 * Gets the max value.
	 *
	 * @param measurementValuesTSD the measurement values TSD
	 * @return the max value
	 */
	public static double getMaxValue(List<Double> measurementValuesTSD) {
		return Collections.max(measurementValuesTSD);
	}

	/**
	 * Gets the IO parameters. checks whether linear regression is sufficiently good
	 * in terms of R^2 > threshold and if improvement of R^2 by pla is worth the
	 * additional computational complexity of optimization model
	 *
	 * @return the IO parameters
	 */
	public static ArrayList<List<PiecewiseLinearApproximation>> getIOParameters(TimeSeriesData tsd) {
		double thresholdRSquared = 0.9;
		double improvementOfPla = 0.025;

		ArrayList<List<PiecewiseLinearApproximation>> regParametersAllInputs = new ArrayList<List<PiecewiseLinearApproximation>>();

		if (tsd.getInputValues_Processed().get(0).length == 1) {
			// normal regression with one x and y
			regParametersAllInputs = getIOParametersNormalReg(tsd, thresholdRSquared, improvementOfPla);
			return regParametersAllInputs;
		} else  {
			// compare adj. r2

			regParametersAllInputs = getIOParametersMultipleReg(tsd, thresholdRSquared, improvementOfPla);
			return regParametersAllInputs; 
		}
	}

	/**
	 * Gets the IO parameters normal reg.
	 *
	 * @param tsd the tsd
	 * @param thresholdRSquared the threshold R squared
	 * @param improvementOfPla the improvement of pla
	 * @return the IO parameters normal reg
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<List<PiecewiseLinearApproximation>> getIOParametersNormalReg(TimeSeriesData tsd, double thresholdRSquared, double improvementOfPla) {
		ArrayList<List<PiecewiseLinearApproximation>> regParametersAllInputs = new ArrayList<List<PiecewiseLinearApproximation>>();

		double[] x = new double[tsd.getInputValues_Processed().size()];
		double[] y = new double[tsd.getOutputValues_Processed().size()];
		for (int i = 0; i < tsd.getInputValues_Processed().size(); i++) {
			x[i] = tsd.getInputValues_Processed().get(i)[0];
			y[i] = tsd.getOutputValues_Processed().get(i);
		}

		// Linear least squares regression
		Object[] resultsLinRegression = Regression.linearRegression(x, y);
		PiecewiseLinearApproximation linReg = (PiecewiseLinearApproximation) resultsLinRegression[0];
		List<PiecewiseLinearApproximation> linRegList = new ArrayList<PiecewiseLinearApproximation>();
		linRegList.add(linReg);

		double[] rsquaredLinReg = (double[]) resultsLinRegression[1];
		// System.out.println("lin reg: R2: "+ rsquaredLinReg[0] + " adjR2: " +
		// rsquaredLinReg[1]);

		// check if r^2 exceeds threshold, if yes: return parameters, if not perform pla
		if (rsquaredLinReg[0] > thresholdRSquared) {
			System.out.println(linReg.getSlope() + "  " + linReg.getIntercept());

			regParametersAllInputs.add(linRegList);
			System.out.println("lin Reg, R^2 above threshold");
			return regParametersAllInputs;
		} else {
			// perform pla, try various numbers of breakpoints, until one succeeds,
			// otherwise return lin. reg. results
			Object[] resultsPla = new Object[2];
			List<PiecewiseLinearApproximation> pla = new ArrayList<PiecewiseLinearApproximation>();
			double[] rsquaredPla = new double[2];

			double[] quantileDistance = new double[] { 0.05, 0.1, 0.2, 0.25, 0.5 };
			int maxAttempts = quantileDistance.length; // Set the maximum number of attempts
			int attempts = 0;
			boolean success = false;

			while (attempts < maxAttempts && !success) {
				try {
					resultsPla = Regression.calcPla(x, y, quantileDistance[attempts]);
					success = true; // The calculation succeeded, exit the loop
				} catch (Exception e) {
					System.out.println("Attempt " + (attempts + 1) + " (quantile distance: "
							+ quantileDistance[attempts] + ") failed: ");
					//					System.err.println(e);
					attempts++;
				}
			}

			if (success) {
				pla = (List<PiecewiseLinearApproximation>) resultsPla[0];
				rsquaredPla = (double[]) resultsPla[1];
				System.out.println("seg R2: " + rsquaredPla[0] + " adjR2: " + rsquaredPla[1]);
				regParametersAllInputs.add(pla);

			} else {
				// Pla calculation failed, return lin. reg. results
				System.err.println("Calculation failed after " + maxAttempts + " attempts. Lin. Reg. used.");
				regParametersAllInputs.add(linRegList);
				return regParametersAllInputs;
			}

			// check whether r^2_pla >> r^2_linReg
			if (rsquaredPla[0] > (rsquaredLinReg[0] + improvementOfPla)) {
				// is greater, improvment given, return pla
				regParametersAllInputs.add(pla);
				System.out.println("pla, significantly better than lin reg");
				return regParametersAllInputs;
			} else {
				// improvement of pla not given, return linear approx.
				regParametersAllInputs.add(linRegList);
				System.out.println("pla not significantly better than lin, thus lin reg");
				return regParametersAllInputs;
			}
		}

	}

	/**
	 * Gets the IO parameters multiple reg.
	 *
	 * @param tsd the tsd
	 * @param thresholdRSquared the threshold R squared
	 * @param improvementOfPla the improvement of pla
	 * comparison of threshold and improvement by means of adj. r2
	 * @return the IO parameters multiple reg
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<List<PiecewiseLinearApproximation>> getIOParametersMultipleReg (TimeSeriesData tsd, double thresholdRSquared, double improvementOfPla) {
		ArrayList<List<PiecewiseLinearApproximation>> regParametersAllInputs = new ArrayList<List<PiecewiseLinearApproximation>>();

		double[][] x = new double[tsd.getInputValues_Processed().size()][tsd.getInputValues_Processed().get(0).length];
		double[] y = new double[tsd.getOutputValues_Processed().size()];

		for (int i = 0; i < tsd.getInputValues_Processed().size(); i++) {

			y[i] = tsd.getOutputValues_Processed().get(i);

			for (int j = 0; j < tsd.getInputValues_Processed().get(0).length; j++) {
				x[i][j] = tsd.getInputValues_Processed().get(i)[j];
			}
		}

		// Linear least squares regression
		Object[] resultsLinRegression = Regression.multipleLinearRegression(x, y);
		PiecewiseLinearApproximation linReg = (PiecewiseLinearApproximation) resultsLinRegression[0];
		List<PiecewiseLinearApproximation> linRegList = new ArrayList<PiecewiseLinearApproximation>();
		linRegList.add(linReg);

		double[] rsquaredLinReg = (double[]) resultsLinRegression[1];
		// System.out.println("lin reg: R2: "+ rsquaredLinReg[0] + " adjR2: " +
		// rsquaredLinReg[1]);

		// check if r^2 exceeds threshold, if yes: return parameters, if not perform pla
		if (rsquaredLinReg[1] > thresholdRSquared) {
			System.out.println(linReg.getSlope() + "  " + linReg.getIntercept());

			regParametersAllInputs.add(linRegList);
			System.out.println("lin Reg, R^2 above threshold");
			return regParametersAllInputs;
		} else {
			// perform pla, try various numbers of breakpoints, until one succeeds,
			// otherwise return lin. reg. results
			Object[] resultsPla = new Object[2];
			List<PiecewiseLinearApproximation> pla = new ArrayList<PiecewiseLinearApproximation>();
			double[] rsquaredPla = new double[2];

			double[] quantileDistance = new double[] { 0.05, 0.1, 0.2, 0.25, 0.5 };
			int maxAttempts = quantileDistance.length; // Set the maximum number of attempts
			int attempts = 0;
			boolean success = false;

			while (attempts < maxAttempts && !success) {
				try {
					resultsPla = Regression.multiplePiecewiseLinearRegression(x, y, quantileDistance[attempts]);
					success = true; // The calculation succeeded, exit the loop
				} catch (Exception e) {
					System.out.println("Attempt " + (attempts + 1) + " (quantile distance: "
							+ quantileDistance[attempts] + ") failed: ");
					//					System.err.println(e);
					attempts++;
				}
			}

			if (success) {
				pla = (List<PiecewiseLinearApproximation>) resultsPla[0];
				rsquaredPla = (double[]) resultsPla[1];
				System.out.println("seg R2: " + rsquaredPla[0] + " adjR2: " + rsquaredPla[1]);
				regParametersAllInputs.add(pla);

			} else {
				// Pla calculation failed, return lin. reg. results
				System.err.println("Calculation failed after " + maxAttempts + " attempts. Lin. Reg. used.");
				regParametersAllInputs.add(linRegList);
				return regParametersAllInputs;
			}

			// check whether r^2_pla >> r^2_linReg
			if (rsquaredPla[1] > (rsquaredLinReg[1] + improvementOfPla)) {
				// is greater, improvment given, return pla
				regParametersAllInputs.add(pla);
				System.out.println("pla, significantly better than lin reg");
				return regParametersAllInputs;
			} else {
				// improvement of pla not given, return linear approx.
				regParametersAllInputs.add(linRegList);
				System.out.println("pla not significantly better than lin, thus lin reg");
				return regParametersAllInputs;
			}
		}

	}

	/**
	 * Multiple linear regression.
	 */
	public static void multipleLinearRegression(TimeSeriesData tsd) {
		double[] input1 = new double[tsd.getInputValues_Processed().size()];
		double[] input2 = new double[tsd.getInputValues_Processed().size()];
		double[] output = new double[tsd.getOutputValues_Processed().size()];

		for (int timeStep = 0; timeStep < tsd.getInputValues_Processed().size(); timeStep++) {
			input1[timeStep] = tsd.getInputValues_Processed().get(timeStep)[0];
			input2[timeStep] = tsd.getInputValues_Processed().get(timeStep)[1];
			output[timeStep] = tsd.getOutputValues_Processed().get(timeStep);// Time, Gas, El, Heat
		}
		// Object[] multRegResults = Regression.multipleLinearRegression(new double[][]
		// {x1,x2}, y);
		Regression.multiplePiecewiseLinearRegression(new double[][] { input1, input2 }, output, 10);
		// @SuppressWarnings("unchecked")
		// List<PiecewiseLinearApproximation> multRegSlIn =
		// (List<PiecewiseLinearApproximation>) multRegResults[0];
		// double[] rsq = (double[]) multRegResults[1];
	}

	/**
	 * Gets the linear regression parameters.
	 *
	 * @return the linear regression parameters of all inputs
	 */
	public static ArrayList<List<PiecewiseLinearApproximation>> getLinRegressionParameters(TimeSeriesData tsd) {
		double[] input = new double[tsd.getInputValues_Processed().size()];
		double[] output = new double[tsd.getOutputValues_Processed().size()];

		for (int timeStep = 0; timeStep < tsd.getInputValues_Processed().size(); timeStep++) {
			input[timeStep] = tsd.getInputValues_Processed().get(timeStep)[0];
			output[timeStep] = tsd.getOutputValues_Processed().get(timeStep);
			// System.out.println(x[i] + " " + y[i]);
		}

		// System.out.println(x.length + " " + y.length);
		Object[] resultsLinRegression = Regression.linearRegression(input, output);
		PiecewiseLinearApproximation linReg = (PiecewiseLinearApproximation) resultsLinRegression[0];
		double[] rsquared = (double[]) resultsLinRegression[1];

		List<PiecewiseLinearApproximation> linRegList = new ArrayList<PiecewiseLinearApproximation>();
		linRegList.add(linReg);
		System.out.println("R2: " + rsquared[0] + " adjR2: " + rsquared[1]);
		System.out.println(linReg.getSlope() + "  " + linReg.getIntercept());

		ArrayList<List<PiecewiseLinearApproximation>> linRegAllInputs = new ArrayList<List<PiecewiseLinearApproximation>>();
		linRegAllInputs.add(linRegList);
		return linRegAllInputs;
	}

	/**
	 * Gets the PLA parameters.
	 *
	 * @return the PLA parameters
	 */
	public static ArrayList<List<PiecewiseLinearApproximation>> getPLAParameters(TimeSeriesData tsd) {
		double[] input = new double[tsd.getInputValues_Processed().size()];
		double[] output = new double[tsd.getOutputValues_Processed().size()];

		for (int timeStep = 0; timeStep < tsd.getInputValues_Processed().size(); timeStep++) {
			input[timeStep] = tsd.getInputValues_Processed().get(timeStep)[0];
			output[timeStep] = tsd.getOutputValues_Processed().get(timeStep);
			// System.out.println(x[i] + " " + y[i]);
		}
		// System.out.println(x.length + " " + y.length);
		Object[] resultsPla = new Object[2];
		double[] quantileDistance = new double[] { 0.05, 0.1, 0.2, 0.25, 0.5 };
		int maxAttempts = quantileDistance.length; // Set the maximum number of attempts
		int attempts = 0;
		boolean success = false;

		while (attempts < maxAttempts && !success) {
			try {
				resultsPla = Regression.calcPla(input, output, quantileDistance[attempts]);
				success = true; // The calculation succeeded, exit the loop
			} catch (Exception e) {
				System.out.println("Attempt " + (attempts + 1) + " (quantile distance: " + quantileDistance[attempts]
						+ ") failed: ");
				System.err.println(e);
				attempts++;
			}
		}

		if (success) {
			@SuppressWarnings("unchecked")
			List<PiecewiseLinearApproximation> pla = (List<PiecewiseLinearApproximation>) resultsPla[0];
			double[] rsquared = (double[]) resultsPla[1];
			System.out.println("seg R2: " + rsquared[0] + " adjR2: " + rsquared[1]);
			for (int i = 0; i < pla.size(); i++) {
				System.out.println(i + " slope " + pla.get(i).getSlope() + " intercept " + pla.get(i).getIntercept()
						+ " lb " + pla.get(i).getLowerBound() + " ub " + pla.get(i).getUpperBound());
			}

			ArrayList<List<PiecewiseLinearApproximation>> listOfMultiplePla = new ArrayList<List<PiecewiseLinearApproximation>>();
			listOfMultiplePla.add(pla);
			return listOfMultiplePla;
		} else {
			System.err.println("Calculation failed after " + maxAttempts + " attempts.");
			return null;
		}

	}

	/**
	 * Compile list of system states from individual state descriptions.
	 *
	 * @param tsd the tsd
	 * @param measuredStates the measured states
	 * @return the list of system states
	 */
	public static List<SystemState> compileListOfSystemStateParameters (TimeSeriesData tsd) {
		List<SystemState> listOfSystemStates = new ArrayList<SystemState>();

		// remove any duplicates from times series list of system states
		setIndividualSystemStates(new ArrayList<>(new HashSet<>(tsd.getSystemStates_Measured())));

		// get system states related parameters from all states
		for (int stateId = 0; stateId < getIndividualSystemStates().size(); stateId++) {
			listOfSystemStates.add(findParametersForState(tsd, stateId));
		}

		return listOfSystemStates;
	}

	/**
	 * Find parameters for state.
	 *
	 * @param tsd the tsd
	 * @param stateId the state id
	 * @return the system state
	 */
	public static SystemState findParametersForState(TimeSeriesData tsd, int stateId) {
		SystemState state = new SystemState();

		state.setStateID(stateId);

		String stateName = "state" + Integer.toString(stateId);
		state.setStateName(stateName);

		SystemState stateBoundaries = getBoundariesByState(tsd, stateId);
		state.setMinPower(stateBoundaries.getMinPowerInput());
		state.setMaxPower(stateBoundaries.getMaxPowerInput());
		state.setMaxPowerOutput(stateBoundaries.getMaxPowerOutput());

		// Follower states
		state.setFollowerStates(getFollowerStates(tsd, stateId));

		// Holding duration
		SystemState holdingDuration = getMinMaxStateDuration(tsd, stateId);
		state.setMinStateDurationInH(holdingDuration.getMinStateDurationInH());
		state.setMaxStateDurationInH(holdingDuration.getMaxStateDurationInH());

		// Ramp
		SystemState ramp = getRampLimitsByState(tsd, stateId);
		state.setMinRampInput(ramp.getMinRampInput());
		state.setMaxRampInput(ramp.getMaxRampInput());

		state.setMinRampOutput(ramp.getMinRampOutput());
		state.setMaxRampOutput(ramp.getMaxRampOutput());

		// TODO
		state.setInputIsEqualToOutput(false);

		return state;
	}

	/**
	 * Flow boundaries by state.
	 *
	 * @param stateId the state id
	 * @return the system state
	 */
	public static SystemState getBoundariesByState(TimeSeriesData tsd, int stateId) {
		SystemState state = new SystemState();
		double minPower = 0;
		double maxPower = Double.MAX_VALUE;
		double maxPowerOuput = Double.MAX_VALUE;

		// if resource != storage
		if (tsd.getStateOfChargeValues_Processed().isEmpty()) {
			List<Double> inputValuesIfStateActive = new ArrayList<Double>();
			List<Double> outputValuesIfStateActive = new ArrayList<Double>();

			for (int timeStep = 0; timeStep < tsd.getSystemStates_Measured().size(); timeStep++) {
				if(tsd.getSystemStates_Measured().get(timeStep)==stateId) {
					inputValuesIfStateActive.add(tsd.getInputValues_Processed().get(timeStep)[0]);
					outputValuesIfStateActive.add(tsd.getOutputValues_Processed().get(timeStep));
				}
			}

			minPower = Collections.min(inputValuesIfStateActive);
			maxPower = Collections.max(inputValuesIfStateActive);
			maxPowerOuput = Collections.max(outputValuesIfStateActive);
		} else {
			List<Double> socValuesIfStateActive = new ArrayList<Double>();
			for (int timeStep = 0; timeStep < tsd.getSystemStates_Measured().size(); timeStep++) {
				if(tsd.getSystemStates_Measured().get(timeStep)==stateId) {
					socValuesIfStateActive.add(tsd.getStateOfChargeValues_Processed().get(timeStep));
				}
			}
			minPower = Collections.min(socValuesIfStateActive);
			maxPower = Collections.max(socValuesIfStateActive);
		}

		state.setMaxPower(maxPower);
		state.setMinPower(minPower);
		state.setMaxPowerOutput(maxPowerOuput);

		return state;
	}

	/**
	 * Gets the min max state duration in h by taking into account time stamps of time series data.
	 *
	 * @param tsd the tsd
	 * @param stateId the state id
	 * @return the min and max state duration
	 */
	public static SystemState getMinMaxStateDuration(TimeSeriesData tsd, int stateId) {
		SystemState state = new SystemState();

		List<Double> timeStamps = tsd.getTimeStamps();

		List<Double> stateDurations = new ArrayList<Double>();

		int succNumberOfTimeSteps = 1;
		double totalActiveDurPerOccurence = 0; 
		int timeCounter = 0; 
		boolean found = false;

		for (int value = 0; value <  tsd.getSystemStates_Measured().size(); value++) {
			if (found) {
				if (tsd.getSystemStates_Measured().get(value) == stateId) {
					succNumberOfTimeSteps++;
				} else {
					found = false;
					// multiply by 24 to convert from timestamp of day to time in hours
					totalActiveDurPerOccurence = 24*(timeStamps.get(timeCounter) - timeStamps.get(timeCounter-succNumberOfTimeSteps));
					stateDurations.add(totalActiveDurPerOccurence);
					//					System.out.println(succNumberOfTimeSteps + " " + totalActiveDurPerOccurence);
					succNumberOfTimeSteps = 1;
				}
			}
			timeCounter++;

			if (tsd.getSystemStates_Measured().get(value) == stateId) {
				found = true;
				totalActiveDurPerOccurence = 0; 
			}
		}

		// Add the count of succeeding values after the loop
		if (succNumberOfTimeSteps != 0 && (timeCounter-succNumberOfTimeSteps)>=0 && timeCounter<tsd.getSystemStates_Measured().size())	{
			totalActiveDurPerOccurence = 24*(timeStamps.get(timeCounter) - timeStamps.get(timeCounter-succNumberOfTimeSteps));
			//			System.out.println(succNumberOfTimeSteps + " " + totalActiveDurPerOccurence);
			stateDurations.add(totalActiveDurPerOccurence);
		}

		double minDuration = Collections.min(stateDurations);
		double maxDuration = Collections.max(stateDurations); 

		double maxMaxDuration = 8; 
		if (maxDuration > maxMaxDuration) maxDuration = 9999; 

		state.setMinStateDurationInH(minDuration);
		state.setMaxStateDurationInH(maxDuration);
		//		System.out.println("state " + stateId + " min duration: " + state.getMinStateDurationInH() + " h, max duration: " + state.getMaxStateDurationInH()+ " h");
		return state;
	}


	public static List<SystemState> getRampLimitsByState_V2 (TimeSeriesData tsd) {
		List<SystemState> listOfSystemStates = new  ArrayList<SystemState>();

		Map<Integer, Double> minRamps = new HashMap<>();
		Map<Integer, Double> maxRamps = new HashMap<>();

		// Initialize minRamps and maxRamps with large initial values
		for (Integer systemState : getIndividualSystemStates()) {
			minRamps.put(systemState, Double.MAX_VALUE);
			maxRamps.put(systemState, Double.MIN_VALUE);
		}
System.err.println(tsd.getTimeStamps().size() + " " + tsd.getSystemStates_Measured().size());
	    for (int i = 1; i < tsd.getTimeStamps().size(); i++) {

            if (tsd.getSystemStates_Measured().get(i-1) == tsd.getSystemStates_Measured().get(i)) {
            	int currentSystemState = tsd.getSystemStates_Measured().get(i); 
                double ramp = Math.abs(tsd.getInputValues_Processed().get(i)[0] - tsd.getInputValues_Processed().get(i-1)[0]) / (tsd.getTimeStamps().get(i) - tsd.getTimeStamps().get(i-1));
                ramp = ramp  /  24; // convert to power/h
                minRamps.put(currentSystemState, Math.min(minRamps.get(currentSystemState), ramp));
                maxRamps.put(currentSystemState, Math.max(maxRamps.get(currentSystemState), ramp));
            }
        }
	    // Print the minimum and maximum ramps for each system state
        for (Integer systemState :  getIndividualSystemStates()) {
            System.out.println("System State " + systemState);
            System.out.println("Minimum Ramp: " + minRamps.get(systemState));
            System.out.println("Maximum Ramp: " + maxRamps.get(systemState));
        }
        
		return listOfSystemStates;
	}
	/**
	 * Gets the ramp limits by state.
	 *
	 * @param tsd the tsd
	 * @param stateId the state id
	 * @return the ramp limits by state
	 */
	public static SystemState getRampLimitsByState(TimeSeriesData tsd, int stateId) {
		SystemState state = new SystemState();
		state.setStateID(stateId);

		List<Double> timeStamps = tsd.getTimeStamps();
		List<Double> inputMeasurementValues = new ArrayList<Double>();
		List<Double> outputMeasurementValues = tsd.getOutputValues_Processed();

		for (int i = 0; i < tsd.getInputValues_Processed().size(); i++) {
			double inputMeasurementValue = tsd.getInputValues_Processed().get(i)[0];
			inputMeasurementValues.add(inputMeasurementValue); 
		}

		// extract ListOfList of state specific time series data
		List<ArrayList<Double>> listOfActiveStateMeasurementValuesInput = 	createTSDByStateListOfLists(tsd, inputMeasurementValues, stateId);
		List<ArrayList<Double>> listOfActiveStateMeasurementValuesOutput = 	createTSDByStateListOfLists(tsd, outputMeasurementValues, stateId);
		List<ArrayList<Double>> listOfActiveStateTimeStamps = 				createTSDByStateListOfLists(tsd, timeStamps, stateId);

		// iterate over listOfList of input/time and output/time
		List<Double> rampInputByState = new ArrayList<Double>();
		List<Double> rampOutputByState = new ArrayList<Double>();

		for (int intervalsOfActiveState = 0; intervalsOfActiveState < listOfActiveStateTimeStamps.size(); intervalsOfActiveState++) {

			if (listOfActiveStateTimeStamps.get(intervalsOfActiveState).size()>1) {
				//d
				rampInputByState.addAll(
						getRampLimitsByStateByTimeSeriesDataColumn(
								listOfActiveStateTimeStamps.get(intervalsOfActiveState), 
								listOfActiveStateMeasurementValuesInput.get(intervalsOfActiveState)
								)
						);

				rampOutputByState.addAll(
						getRampLimitsByStateByTimeSeriesDataColumn( 
								listOfActiveStateTimeStamps.get(intervalsOfActiveState), 
								listOfActiveStateMeasurementValuesOutput.get(intervalsOfActiveState)
								)
						);
			}
		}

		// Find the minimum and maximum slopes in {x}W/h
		// Divisions by 60 and 24 necessary to convert from datetime-stamp to hours
		double minRampInput, maxRampInput, minRampOutput, maxRampOutput;

		if (rampInputByState.size()!=0) {
			minRampInput = Collections.min(rampInputByState) / 24;
			maxRampInput = Collections.max(rampInputByState) / 24;
		} else {
			minRampInput = 0; 
			maxRampInput = Double.MAX_VALUE;
		}

		if (rampOutputByState.size()!=0) {
			minRampOutput = Collections.min(rampOutputByState) / 24;
			maxRampOutput = Collections.max(rampOutputByState) / 24;
		} else {
			minRampOutput = 0; 
			maxRampOutput = Double.MAX_VALUE;
		}
		//		System.out.println("min Ramp: " + minRampInput + " maxRamp: " + maxRampInput);
		//		System.out.println("min Ramp: " + minRampOutput + " maxRamp: " + maxRampOutput);

		state.setMinRampInput(minRampInput);
		state.setMaxRampInput(maxRampInput);
		state.setMinRampOutput(minRampOutput);
		state.setMaxRampOutput(maxRampOutput);
		return state;
	}

	/**
	 * 
	 * Gets the ramp limits by time series data columns.
	 *
	 * @param activeTimeStamps the time
	 * @param measurementValues the measurement values (input or output)
	 * @return list of all possible ramp values
	 */
	public static List<Double> getRampLimitsByStateByTimeSeriesDataColumn (List<Double> activeTimeStamps, List<Double> measurementValues) {
		List<Double> slope = new ArrayList<Double>();

		List<Double> breakpoints = getBreakpoints(activeTimeStamps, measurementValues);
		List<Integer> indexOfBreakpointInX = new ArrayList<Integer>();
		@SuppressWarnings("unused")
		int counterNaN = -1;
		double slopeSegment = 0;


		// Calculate slopes for each segment
		for (int segment = 0; segment < breakpoints.size(); segment++) {
			// variables for values in segment

			// idx: index of breakpoint in x values
			List<Double> xSeg = new ArrayList<Double>();
			List<Double> ySeg = new ArrayList<Double>();

			// get index of breakpoint i in arraylist of x values
			indexOfBreakpointInX.add(getIndexOfValueFromArrayList(activeTimeStamps, breakpoints.get(segment)));

			if (segment == 0) {
				// 0 -> first breakpoint
				// generate sublists of x and y from previous breakpoint (or 0) to current index
				xSeg = activeTimeStamps.subList(0, indexOfBreakpointInX.get(segment));
				ySeg = measurementValues.subList(0, indexOfBreakpointInX.get(segment));

			} else if (segment == breakpoints.size() + 1) {
				// get index of breakpoint i in arraylist of x values

				// generate sublists of x and y from previous breakpoint (or 0) to current index
				// or size()-1
				xSeg = activeTimeStamps.subList(indexOfBreakpointInX.get(segment - 1), activeTimeStamps.size() - 1);
				ySeg = measurementValues.subList(indexOfBreakpointInX.get(segment - 1), measurementValues.size() - 1);

			} else {
				// n-1 -> last breakpoint
				// generate sublists of x and y from previous breakpoint (or 0) to current index
				xSeg = activeTimeStamps.subList(indexOfBreakpointInX.get(segment - 1), indexOfBreakpointInX.get(segment));
				ySeg = measurementValues.subList(indexOfBreakpointInX.get(segment - 1), indexOfBreakpointInX.get(segment));
			}

			// get absolute Value of slope in segment
			slopeSegment = calculateSlope(
					xSeg.get(0), ySeg.get(0), 
					xSeg.get(xSeg.size() - 1),ySeg.get(ySeg.size() - 1)
					);

			if (xSeg.size() == 0 && Double.isNaN(slopeSegment) == true) {

				System.out.println(" NaN " + counterNaN
						+ calculateSlope(xSeg.get(0), ySeg.get(0), xSeg.get(xSeg.size() - 1),
								ySeg.get(ySeg.size() - 1))
						+ "x1: " + xSeg.get(0) + "y1: " + ySeg.get(0) + "x2: " + xSeg.get(xSeg.size() - 1)
						+ "y2: " + ySeg.get(ySeg.size() - 1));

				counterNaN++;
			}
			if (Double.isNaN(slopeSegment) == false) {
				slope.add(Math.abs(slopeSegment));
			}
			// System.out.println("slope " + i + " " + slope.get(i));

		}
		return slope;
	}

	/**
	 * Gets the breakpoints.
	 *
	 * @param x the x
	 * @param y the y
	 * @return Array List of breakpoints
	 */
	public static List<Double> getBreakpoints(List<Double> x, List<Double> y) {


		double previousSlope = 0;
		double currentSlope;
		List<Double> breakpoints = new ArrayList<Double>();
		double yMax = Collections.max(y);// getMaxValue(null);
		double threshold = 0.0 * yMax;
		//TODO
		@SuppressWarnings("unused")
		int counter = 0;

		// Iterate through the dataset to identify sign changes and apply the threshold
		for (int i = 0; i < x.size() - 1; i++) {
			if (i == 0)
				previousSlope = calculateSlope(x.get(i), y.get(i), x.get(i + 1), y.get(i + 1));
			currentSlope = calculateSlope(x.get(i), y.get(i), x.get(i + 1), y.get(i + 1));

			// Check if the absolute difference between y[i] and y[i+1] is greater than the
			// threshold
			if (Math.signum(previousSlope) != Math.signum(currentSlope)
					&& Math.abs(y.get(i + 1) - y.get(i)) > threshold) {
				breakpoints.add(x.get(i));
				// System.out.println("c: " + counter + " new_breakpoint_found: " + x.get(i) + "
				// previous_slope: " + previousSlope + " current_slope: " + currentSlope);
				counter++;
			}
			previousSlope = currentSlope;
		}
		//		System.out.println("number of breakpoints found: " + counter + " in time series data of length " + x.size());
		return breakpoints;
	}

	/**
	 * Determine follower states.
	 *
	 * @param stateId the state id
	 * @return the int[] of follower states of stateID
	 */
	public static int[] getFollowerStates(TimeSeriesData tsd, int stateId) {

		//		int[] systemStatesTimeSeriesData = getSystemStatesArray();
		List<Integer> succeedingValues = new ArrayList<Integer>();

		for (int timeStep = 0; timeStep < tsd.getSystemStates_Measured().size() - 1; timeStep++) {
			// (1) check if current timeStep state = stateID
			// (2) state change occurs between timeStep and timeStep+1
			// (3) succeeding state has not been saved in list
			if (tsd.getSystemStates_Measured().get(timeStep) == stateId
					&& tsd.getSystemStates_Measured().get(timeStep) != tsd.getSystemStates_Measured().get(timeStep+1)
					&& succeedingValues.contains(tsd.getSystemStates_Measured().get(timeStep+1)) == false) {
				succeedingValues.add(tsd.getSystemStates_Measured().get(timeStep+1));
			}
		}

		// convert list to int[]
		int[] followerStates = succeedingValues.stream().mapToInt(i -> i).toArray();
		return followerStates;
	}


	/**
	 * Determine storage related parameters.
	 *
	 * @param tsd the tsd
	 * @return the resource parameters
	 */
	public static ResourceParameters determineStorageRelatedParameters (TimeSeriesData tsd ) {
		ResourceParameters storageParameters = new  ResourceParameters();
		List<Double> socMeasurementValues = tsd.getStateOfChargeValues_Processed();

		// efficiencies
		ResourceParameters storageEfficiency = calcStorageEfficiency(tsd); 
		double efficiencyInput = storageEfficiency.getEfficiencyInputStorage(); 
		double efficiencyOutput = storageEfficiency.getEfficiencyOutputStorage(); 
		storageParameters.setEfficiencyInputStorage(efficiencyInput);
		storageParameters.setEfficiencyOutputStorage(efficiencyOutput);

		//  minimum soc
		double minimumCapacity = Collections.min(socMeasurementValues);
		// maximum capacity
		double maximumCapacity = Collections.max(socMeasurementValues); 
		storageParameters.setMinimumStorageCapacity(minimumCapacity);
		storageParameters.setMaximumStorageCapacity(maximumCapacity);

		// TODO degradation
		// idea: decrease of max value over time, ähnlich wie ramp
		//unit? xWh/h
		double degradation = 0; 
		storageParameters.setDegradation(degradation);

		ResourceParameters storageLosses = determineStorageLosses();

		storageParameters.setStaticEnergyLoss(storageLosses.getStaticEnergyLoss());
		storageParameters.setDynamicEnergyLoss(storageLosses.getDynamicEnergyLoss());
		storageParameters.setReferenceDynamicEnergyLoss(storageLosses.getReferenceDynamicEnergyLoss());

		return storageParameters;
	}


	public static ResourceParameters calcStorageEfficiency(TimeSeriesData tsd) {
		ResourceParameters storageEffci = new  ResourceParameters(); 

		double[] inputEff = new double [tsd.getInputValues_INside_Storage_Processed().size()];
		double[] outputEff = new double [tsd.getInputValues_INside_Storage_Processed().size()];

		for (int timeStep = 0; timeStep < inputEff.length; timeStep++) {
			inputEff[timeStep] = tsd.getInputValues_Processed().get(timeStep)[0]/tsd.getInputValues_INside_Storage_Processed().get(timeStep)[0];
			outputEff[timeStep] = tsd.getOutputValues_Processed().get(timeStep)/tsd.getOutputValues_INside_Storage_Processed().get(timeStep);
		}

		double avgInputEff = calculateAverage(inputEff); 
		double avgOutputEff = calculateAverage(outputEff); 

		storageEffci.setEfficiencyInputStorage(avgInputEff);
		storageEffci.setEfficiencyOutputStorage(avgOutputEff);

		return storageEffci;
	}

	/**
	 * Determine storage losses.
	 *
	 * @return the storage loss parameters
	 */
	public static ResourceParameters determineStorageLosses () {
		ResourceParameters storageLosses = new ResourceParameters();

		// TODO 
		// IDEA plausibilisieren, ob soc durch input/output überhaupt zustande kommen kann

		storageLosses.setDynamicEnergyLoss(0);
		storageLosses.setReferenceDynamicEnergyLoss(0);
		storageLosses.setStaticEnergyLoss(0);

		return storageLosses;


	}

	/**
	 * Load data from file.
	 *
	 * @param filePath the file path
	 * @throws IOException  Signals that an I/O exception has occurred.
	 * @throws CsvException the csv exception
	 */
	public static void loadDataFromFile(String filePath) throws IOException, CsvException {

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
		}
		getTimeSeriesData().addAll(dataList);

		// for (int lengthOfList = 0; lengthOfList < getTimeSeriesData().size();
		// lengthOfList++) {
		// for (int width = 0; width < getTimeSeriesData().get(0).length; width++) {
		// System.out.println("length " + lengthOfList + " width " + width + " " +
		// getTimeSeriesData().get(lengthOfList)[width]);
		// }
		// }
	}



	/**
	 * Calculate slope.
	 *
	 * @param x1 the x 1
	 * @param y1 the y 1
	 * @param x2 the x 2
	 * @param y2 the y 2
	 * @return slope
	 */
	public static double calculateSlope(double x1, double y1, double x2, double y2) {
		return ((y2 - y1) / (x2 - x1));
	}

	/**
	 * Gets the time series data.
	 *
	 * @return the timeSeriesData
	 */
	public static List<Double[]> getTimeSeriesData() {
		return timeSeriesData;
	}

	/**
	 * Gets the index of value from array list.
	 *
	 * @param doubleArrayList the double array list
	 * @param targetValue     the target value
	 * @return the index of value from array list
	 */
	public static int getIndexOfValueFromArrayList(List<Double> doubleArrayList, double targetValue) {
		// Initialize index to -1 to indicate that the value was not found
		int index = -1;

		// Iterate through the ArrayList to find the index of the target value
		for (int i = 0; i < doubleArrayList.size(); i++) {
			if (doubleArrayList.get(i).equals(targetValue)) {
				index = i;
				break; // Exit the loop once the value is found
			}
		}
		return index;
	}


	/**
	 * Gets the system states by time list.
	 *
	 * @return the systemStatesByTimeList
	 */
	public static List<Integer> getSystemStatesByTimeList() {
		return systemStatesByTimeList;
	}

	/**
	 * Sets the system states by time list.
	 *
	 * @param systemStatesByTimeList the systemStatesByTimeList to set
	 */
	public static void setSystemStatesByTimeList(List<Integer> systemStatesByTimeList) {
		ResourceParameterExtraction.systemStatesByTimeList = systemStatesByTimeList;
	}

	/**
	 * Gets the individual system states (sorted by value).
	 *
	 * @return the individualSystemStates
	 */
	public static List<Integer> getIndividualSystemStates() {
		return individualSystemStates;
	}

	/**
	 * Sets the individual system states.
	 * And sorts then by value (Collections.sort(List))
	 *
	 * @param individualSystemStates the individualSystemStates to set
	 */
	public static void setIndividualSystemStates(List<Integer> individualSystemStates) {
		ResourceParameterExtraction.individualSystemStates = individualSystemStates;
		Collections.sort(ResourceParameterExtraction.individualSystemStates);
	}

	/**
	 * Load data from file.
	 *
	 * @param filePath the file path
	 * @return the list
	 * @throws IOException  Signals that an I/O exception has occurred.
	 * @throws CsvException the csv exception
	 */
	public static List<Integer[]> loadIntegerDataFromFileToList (String filePath) throws IOException, CsvException {

		List<Integer[]> dataList = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				Integer[] dataRow = new Integer[nextLine.length];
				for (int i = 0; i < nextLine.length; i++) {
					dataRow[i] = Integer.parseInt(nextLine[i]);
				}
				dataList.add(dataRow);
			}
		}

		return dataList;
	}

	/**
	 * Compare and create list.
	 *
	 * @param valuesToBeSplit the values to be split
	 * @param stateId the state id
	 * @return the list
	 */
	public static List<ArrayList<Double>> createTSDByStateListOfLists(TimeSeriesData tsd, List<Double> valuesToBeSplit, int stateId) {
		List<Integer> tsdStates = tsd.getSystemStates_Measured();
		List<ArrayList<Double>> listC = new ArrayList<>();
		ArrayList<Double> currentList = null;

		for (int i = 0; i < tsdStates.size(); i++) {
			if (tsdStates.get(i) == stateId) {
				if (currentList == null) {
					currentList = new ArrayList<>();
				}
				currentList.add(valuesToBeSplit.get(i));
			} else {
				if (currentList != null) {
					listC.add(currentList);
					currentList = null;
				}
			}

			if (i + 1 == tsdStates.size()) {
				if (currentList != null) {
					listC.add(currentList);
				}
			} else if (tsdStates.get(i) != tsdStates.get(i + 1)) {
				if (currentList != null) {
					listC.add(currentList);
					currentList = null;
				}
			}
		}

		return listC;
	}


	/**
	 * Calculate average.
	 *
	 * @param array the array
	 * @return the double
	 */
	public static double calculateAverage(double[] array) {
		if (array.length == 0) {
			// Handle the case where the array is empty to avoid division by zero
			return 0.0;
		}

		double sum = 0.0;

		// Calculate the sum of all elements in the array
		for (double value : array) {
			sum += value;
		}

		// Calculate the average
		return sum / array.length;
	}

}
