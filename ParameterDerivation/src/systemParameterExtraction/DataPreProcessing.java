package systemParameterExtraction;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * The Class DataPreProcessing.
 */
public class DataPreProcessing {

	/**
	 * Instantiates a new data pre processing.
	 */
	public DataPreProcessing () {

	}

	/**
	 * Instantiates a new data pre processing.
	 *
	 * @param timeSeriesData the time series data
	 */
	public DataPreProcessing (TimeSeriesData timeSeriesData) {
		doPreProcessing(timeSeriesData);
	}


	/**
	 * Do pre processing.
	 *
	 * @param timeSeriesData the time series data
	 * @return the time series data
	 */
	public static TimeSeriesData doPreProcessing(TimeSeriesData timeSeriesData) {

		double thresholdCoefficientOfDet = 0.85; 

		if (timeSeriesData.getInputValues_UN_Processed().get(0).length == 1) {
			double coefficientOfDet = checkCorrelation(timeSeriesData);
			if (coefficientOfDet < thresholdCoefficientOfDet) {
				System.out.println("No correlation, " + coefficientOfDet);
				return timeSeriesData; 
			}
		} else {
			// if more than one input, check adj. 
			double adjCcoefficientOfDet = adjustedRSquared(timeSeriesData);
			if (adjCcoefficientOfDet < thresholdCoefficientOfDet) {
				System.out.println("No correlation, " + adjCcoefficientOfDet);
				return timeSeriesData; 
			}
		}

		double pValue = calculatePValue(timeSeriesData);
		double signficiance = 0.05; 
		if (pValue > signficiance) {
			System.out.println("pValue to high "+ pValue);
			return timeSeriesData; 
		}

		boolean isStationary = checkForStationary(timeSeriesData);

		if (!isStationary) {
			System.out.println("The time series is not stationary.");
			return timeSeriesData; 
		} else {
			System.out.println("The time series is stationary.");
		}
		timeSeriesData = identifyAndReplaceOutliers(timeSeriesData);

		return timeSeriesData;
	}


	/**
	 * Check correlation.
	 *
	 * @param timeSeriesData the time series data
	 * @return the double
	 */
	public static double checkCorrelation (TimeSeriesData timeSeriesData) {

		SimpleRegression simpleReg = new SimpleRegression(); 

		for (int timeStamp = 0; timeStamp < timeSeriesData.getInputValues_UN_Processed().size(); timeStamp++) {
			double input = 	timeSeriesData.getInputValues_UN_Processed().get(timeStamp)[0];
			double output = timeSeriesData.getOutputValues_UN_Processed().get(timeStamp);
			simpleReg.addData(input, output);

		}

		double rSquared = simpleReg.getRSquare();
//		System.out.println("R^2: " + rSquared);

		return rSquared;

	}

	/**
	 * Adjusted R squared.
	 *
	 * @param timeSeriesData the time series data
	 * @return the double
	 */
	public static double adjustedRSquared (TimeSeriesData timeSeriesData) {

		SimpleRegression simpleReg = new SimpleRegression(); 


		for (int timeStamp = 0; timeStamp < timeSeriesData.getInputValues_UN_Processed().size(); timeStamp++) {
			double input[] = new double[timeSeriesData.getInputValues_UN_Processed().get(0).length];

			for (int inputNumber = 0; inputNumber < timeSeriesData.getInputValues_UN_Processed().get(0).length; inputNumber++) {
				input[inputNumber]= 	timeSeriesData.getInputValues_UN_Processed().get(timeStamp)[inputNumber];
			}

			double output = timeSeriesData.getOutputValues_UN_Processed().get(timeStamp);

			simpleReg.addObservation(input, output);

		}
		double adjSquared = simpleReg.regress().getAdjustedRSquared();

		return adjSquared; 
	}


	/**
	 * Calculate P value.
	 *
	 * @param timeSeriesData the time series data
	 * @return the double
	 */
	public static double calculatePValue (TimeSeriesData timeSeriesData) {


		double[] inputValues = new double[timeSeriesData.getInputValues_UN_Processed().size()]; 
		double[] outputValues = new double[timeSeriesData.getInputValues_UN_Processed().size()]; 

		for (int timeStamp = 0; timeStamp < timeSeriesData.getInputValues_UN_Processed().size(); timeStamp++) {
			inputValues[timeStamp] = 	timeSeriesData.getInputValues_UN_Processed().get(timeStamp)[0];
			outputValues[timeStamp] = timeSeriesData.getOutputValues_UN_Processed().get(timeStamp);
		}

		// Perform t-test
		TTest tTest = new TTest();
		double tStat = tTest.t(inputValues, outputValues);

		// Degrees of freedom
		int df = inputValues.length + outputValues.length - 2;

		// Create a t-distribution with the degrees of freedom
		TDistribution tDistribution = new TDistribution(df);

		// Calculate p-value
		double pValue = 2 * tDistribution.cumulativeProbability(-Math.abs(tStat));

		return pValue;
	}


	public static boolean checkForStationary (TimeSeriesData tsd) {

		boolean input = false; 
		for (int numberInput = 0; numberInput < tsd.getInputValues_UN_Processed().get(0).length; numberInput++) {
			double[] inputValues = new double[tsd.getInputValues_UN_Processed().size()];
			for (int i = 0; i < inputValues.length; i++) {
				inputValues[i] = tsd.getInputValues_UN_Processed().get(i)[numberInput];
			}
			boolean inputInd =  isStationary(inputValues);
			if (inputInd == false) {
				input = false; 
			} else {
				input = true; 
			}
		}

		boolean output = isStationary(convertListToDoubleArray(tsd.getOutputValues_UN_Processed()));

		return input && output; 
	}


	public static boolean isStationary(double[] timeSeries) {
		int n = timeSeries.length;

		int lag = 1; 
		// Calculate differenced series
		double[] diffTimeSeries = new double[n - lag];
		for (int i = lag; i < n; i++) {
			diffTimeSeries[i - lag] = timeSeries[i] - timeSeries[i - lag];
		}

		// Perform Augmented Dickey-Fuller Test
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		double[] ones = new double[diffTimeSeries.length];
		for (int i = 0; i < ones.length; i++) {
			ones[i] = 1.0;
		}

		double[][] xMatrix = new double[diffTimeSeries.length][1];
		for (int i = 0; i < diffTimeSeries.length; i++) {
			xMatrix[i][0] = ones[i];
		}

		regression.newSampleData(diffTimeSeries, xMatrix);
		double[] residuals = regression.estimateResiduals();

		double sse = 0.0;
		for (double residual : residuals) {
			sse += Math.pow(residual, 2);
		}

		double sst = 0.0;
		for (double value : diffTimeSeries) {
			sst += Math.pow(value, 2);
		}

		double fStatistic = (sst - sse) / lag;
		int df1 = lag;
		int df2 = diffTimeSeries.length - lag - 1;

		TDistribution tDistribution = new TDistribution(df2);
		double criticalValue = tDistribution.inverseCumulativeProbability(0.95);

		// Check if the F-statistic is less than the critical value
		return fStatistic < criticalValue;
	}



	/**
	 * Removes the outliers.
	 *
	 * @param timeSeriesData the time series data
	 * @return the time series data
	 */
	public static TimeSeriesData identifyAndReplaceOutliers (TimeSeriesData timeSeriesData) {

		if (!(timeSeriesData.getInputValues_UN_Processed().isEmpty())) {
			timeSeriesData.getInputValues_Processed().addAll(
					identifyAndReplaceOutliersDoubleArrayList(
							timeSeriesData.getInputValues_UN_Processed(), 
							timeSeriesData
							)
					);
			writeDataToCsvArray(timeSeriesData.getInputValues_Processed(),timeSeriesData.getNameOfResource()+"_input");

		}

		if (!(timeSeriesData.getOutputValues_UN_Processed().isEmpty())) {
			timeSeriesData.getOutputValues_Processed().addAll(identifyAndReplaceOutliersDoubleList(timeSeriesData.getOutputValues_UN_Processed(),timeSeriesData));
			writeDataToCsv(timeSeriesData.getOutputValues_Processed(),timeSeriesData.getNameOfResource()+"_output");
		}

		if (!(timeSeriesData.getStateOfChargeValues_UN_Processed().isEmpty())) {
			timeSeriesData.getStateOfChargeValues_Processed().addAll(identifyAndReplaceOutliersDoubleList(timeSeriesData.getStateOfChargeValues_UN_Processed(),timeSeriesData));
			writeDataToCsv(timeSeriesData.getStateOfChargeValues_Processed(),timeSeriesData.getNameOfResource()+"_stateOfCharge");
		}

		if (!(timeSeriesData.getInputValues_INside_Storage_UN_Processed().isEmpty())) {
			timeSeriesData.getInputValues_INside_Storage_Processed().addAll(identifyAndReplaceOutliersDoubleArrayList(timeSeriesData.getInputValues_INside_Storage_UN_Processed(),timeSeriesData));
			writeDataToCsvArray(timeSeriesData.getInputValues_INside_Storage_Processed(),timeSeriesData.getNameOfResource()+"_input_inside_storage");
		}

		if (!(timeSeriesData.getOutputValues_INside_Storage_UN_Processed().isEmpty())) {
			timeSeriesData.getOutputValues_INside_Storage_Processed().addAll(identifyAndReplaceOutliersDoubleList(timeSeriesData.getOutputValues_INside_Storage_UN_Processed(),timeSeriesData));
			writeDataToCsv(timeSeriesData.getOutputValues_INside_Storage_Processed(),timeSeriesData.getNameOfResource()+"_output_inside_storage");
		}

		return timeSeriesData;
	}

	/**
	 * Removes the outliers double array. calls removeOutliersDoubleList for all elements of list<double[]>
	 *
	 * @param doubleArray_UN_processed the double array U N processed
	 * @return the list
	 */
	public static List<Double[]> identifyAndReplaceOutliersDoubleArrayList (List<Double[]> doubleArray_UN_processed, TimeSeriesData timeSeriesData) {
		List<Double[]> doubleArray_Processed = new ArrayList<Double[]>();

		// split Doulbe[] list in doulbe listS
		List<ArrayList<Double>> doubleValues_processed_listlist = new ArrayList<ArrayList<Double>>();

		for (int column = 0; column < doubleArray_UN_processed.get(0).length; column++) {
			List<Double> doubleValues_UN_processed = new ArrayList<Double>();
			for (int timeStep = 0; timeStep < doubleArray_UN_processed.size(); timeStep++) {
				doubleValues_UN_processed.add(doubleArray_UN_processed.get(timeStep)[column]);
			}
			ArrayList<Double> doubleValues_Processed = (ArrayList<Double>) identifyAndReplaceOutliersDoubleList(doubleValues_UN_processed,timeSeriesData);
			doubleValues_processed_listlist.add(doubleValues_Processed);
		}

		// merge preprocessed lists back into double[] list
		for (int timeStep = 0; timeStep < doubleValues_processed_listlist.get(0).size(); timeStep++) {
			Double[] values = new Double[doubleArray_UN_processed.get(0).length];
			for (int columnIndex = 0; columnIndex < doubleArray_UN_processed.get(0).length; columnIndex++) {
				values[columnIndex] = doubleValues_processed_listlist.get(columnIndex).get(timeStep); 
			}
			doubleArray_Processed.add(values);
		}

		return doubleArray_Processed;

	}

	/**
	 * Removes the outliers double list.
	 *
	 * @param doubleList_UN_processed the double list U N processed
	 * @return the list
	 */
	public static List<Double> identifyAndReplaceOutliersDoubleList (List<Double> doubleList_UN_processed, TimeSeriesData timeSeriesData) {

		//preprocessing
		double lowerPercentile = 5.0; //3 
		double upperPercentile = 95.0; // 97

		List<Double> double_Processed = new ArrayList<Double>();
		double_Processed = identifyValuesToReplace(lowerPercentile, upperPercentile, timeSeriesData, doubleList_UN_processed);
		/**
		List<DataPoint> tsdToPreProcess = new ArrayList<DataPoint>();

		for (int timeStep = 0; timeStep <doubleList_UN_processed.size(); timeStep++) {
			DataPoint dataPoint = new DataPoint(doubleList_UN_processed.get(timeStep), timeSeriesData.getTimeStamps().get(timeStep));
			tsdToPreProcess.add(dataPoint);
		}

		List<DataPoint> dataPointsPreProcessedTsd = TimeSeriesCleanUp.preprocessTimeSeries(tsdToPreProcess, lowerPercentile, upperPercentile);

		for (int timeStep = 0; timeStep <dataPointsPreProcessedTsd.size(); timeStep++) {
			double_Processed.add(dataPointsPreProcessedTsd.get(timeStep).getValue());
		}
		 */
		return double_Processed;
	}

	/**
	 * Percentiles.
	 *
	 * @param lowerPercentile the lower percentile
	 * @param upperPercentile the upper percentile
	 * @param doubleList_UN_processed the double list UNprocessed
	 * @return the processed list
	 */
	public static List<Double> identifyValuesToReplace (double lowerPercentile, double upperPercentile, TimeSeriesData timeSeriesData, List<Double> doubleList_UN_processed) {
		// Create a list to store values with their indices
		List<ValueWithIndex> valuesWithIndices = new ArrayList<>();

		// Populate the list with values and indices
		for (int i = 0; i < doubleList_UN_processed.size(); i++) {
			valuesWithIndices.add(new ValueWithIndex(doubleList_UN_processed.get(i), i));
		}

		// Sort the list based on values
		Collections.sort(valuesWithIndices);

		// Calculate indices for percentiles
		int lowerIndex = (int) Math.ceil(lowerPercentile / 100.0 * valuesWithIndices.size()) - 1;
		int upperIndex = (int) Math.floor(upperPercentile / 100.0 * valuesWithIndices.size()) - 1;

		// Identify values below and above percentiles while retaining the order
		List<Double> belowPercentile = new ArrayList<>();
		List<Double> abovePercentile = new ArrayList<>();

		for (int i = 0; i < valuesWithIndices.size(); i++) {
			if (i <= lowerIndex) {
				belowPercentile.add(valuesWithIndices.get(i).getValue());
			}
			if (i >= upperIndex) {
				abovePercentile.add(valuesWithIndices.get(i).getValue());
			}
		}

		// Linear interpolation and replacement
		doubleList_UN_processed = interpolateAndReplace(doubleList_UN_processed, timeSeriesData, belowPercentile, lowerPercentile);
		doubleList_UN_processed = interpolateAndReplace(doubleList_UN_processed, timeSeriesData, abovePercentile, upperPercentile);

		List<Double> processedValues = doubleList_UN_processed;

		// Return the updated list
		return processedValues; 
	}

	/**
	 * Interpolate and replace.
	 *
	 * @param doubleList_UN_processed the double list U N processed
	 * @param valuesToReplace the values to replace
	 * @param targetPercentile the target percentile
	 * @return the list
	 */
	public static List<Double> interpolateAndReplace(List<Double> doubleList_UN_processed,TimeSeriesData timeSeriesData, List<Double> valuesToReplace, double targetPercentile) {
		for (double value : valuesToReplace) {
			int index = doubleList_UN_processed.indexOf(value);

			double replacementValue; 
			// Linear interpolation to find replacement value
			if (index!=0 || index != doubleList_UN_processed.size()-1) {
				//				replacementValue = linearInterpolation(index, index-1, index+1, doubleList_UN_processed.get(index - 1), doubleList_UN_processed.get(index + 1));
				replacementValue = linearInterpolation(
						timeSeriesData.getTimeStamps().get(index),
						timeSeriesData.getTimeStamps().get(index-1), 
						timeSeriesData.getTimeStamps().get(index+1), 
						doubleList_UN_processed.get(index - 1),
						doubleList_UN_processed.get(index + 1));
				if (replacementValue > 2*doubleList_UN_processed.get(index - 1)) {
					replacementValue = doubleList_UN_processed.get(index - 1); 
				}
			} else {
				// Extrapolation to replace first or last value
				//				replacementValue = 0; 
				if (index == 0) {
					replacementValue = extrapolateFirstValue(index + 1, index + 2, doubleList_UN_processed.get(index + 1), doubleList_UN_processed.get(index + 2));
				} else {
					replacementValue = extrapolateLastValue(index - 2, index - 1, doubleList_UN_processed.get(index - 2), doubleList_UN_processed.get(index - 1));
				}
			}
			// Replace the value in the original list
			doubleList_UN_processed.set(index, replacementValue);

		}
		return doubleList_UN_processed;
	}

	/**
	 * Linear interpolation.
	 *
	 * @param x the x
	 * @param x0 the x 0
	 * @param x1 the x 1
	 * @param y0 the y 0
	 * @param y1 the y 1
	 * @return the double
	 */
	private static double linearInterpolation (double x, double x0, double x1, double y0, double y1) {
		return ((y1 - y0) / (x1 - x0)) * (x - x0) + y0;
	}

	/**
	 * Extrapolate first value.
	 *
	 * @param indexNext the index next
	 * @param indexNextNext the index next next
	 * @param valueNext the value next
	 * @param valueNextNext the value next next
	 * @return the double
	 */
	private static double extrapolateFirstValue(int indexNext, int indexNextNext, double valueNext, double valueNextNext) {
		double extrapolationFactor = 2.0; 
		return valueNext - extrapolationFactor * (valueNextNext - valueNext);
	}

	/**
	 * Extrapolate last value.
	 *
	 * @param indexPrevPrev the index prev prev
	 * @param indexPrev the index prev
	 * @param valuePrevPrev the value prev prev
	 * @param valuePrev the value prev
	 * @return the double
	 */
	private static double extrapolateLastValue(int indexPrevPrev, int indexPrev, double valuePrevPrev, double valuePrev) {
		double extrapolationFactor = 2.0; 
		return valuePrev + extrapolationFactor * (valuePrev - valuePrevPrev);
	}

	/**
	 * Write data to csv.
	 *
	 * @param dataList the data list
	 * @param filePath the file path
	 */
	private static void writeDataToCsv (List<Double> dataList, String filePath)  {
		filePath = "src/preprocessedData/"+filePath+".csv"; 

		try (FileWriter writer = new FileWriter(filePath)) {
			for (Double value : dataList) {
				writer.append(value.toString());
				writer.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeDataToCsvArray(List<Double[]> dataList, String filePath) {
		filePath = "src/preprocessedData/"+filePath+".csv"; 

		try (FileWriter writer = new FileWriter(filePath)) {
			for (Double[] row : dataList) {
				for (int i = 0; i < row.length; i++) {
					writer.append(row[i].toString());
					if (i < row.length - 1) {
						writer.append(",");
					}
				}
				writer.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		
	public static double[] convertListToDoubleArray(List<Double> list) {
		if (list == null || list.isEmpty()) {
			return new double[0]; // Return an empty double array if the list is null or empty
		}

		double[] result = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}
		return result;
	}

}

