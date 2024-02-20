package systemParameterExtraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeSeriesCleanUp {

	public static void main(String[] args) {
	    // Example usage
	    List<DataPoint> dataPoints = new ArrayList<>();
	    dataPoints.add(new DataPoint(10.0, 0.0));
	    dataPoints.add(new DataPoint(15.0, 1.0));
	    dataPoints.add(new DataPoint(8.0, 2.0));
	    dataPoints.add(new DataPoint(25.0, 3.0));
	    dataPoints.add(new DataPoint(30.0, 4.0));
	
	    double lowerPercentile = 5.0;
	    double upperPercentile = 95.0;
	
	    List<DataPoint> processedDataPoints = preprocessTimeSeries(dataPoints, lowerPercentile, upperPercentile);
	
	    System.out.println("Original Data Points: " + dataPoints);
	    System.out.println("Processed Data Points: " + processedDataPoints);
	}

	public static List<DataPoint> preprocessTimeSeries(List<DataPoint> dataPoints, double lowerPercentile, double upperPercentile) {
        List<DataPoint> processedDataPoints = new ArrayList<>();

        // Extract values for percentile calculation
        List<Double> values = new ArrayList<>();
        for (DataPoint dataPoint : dataPoints) {
            values.add(dataPoint.getValue());
        }

        // Calculate percentiles
        double lowerThreshold = calculatePercentile(values, lowerPercentile);
        double upperThreshold = calculatePercentile(values, upperPercentile);

        for (int i = 0; i < dataPoints.size(); i++) {
            DataPoint currentDataPoint = dataPoints.get(i);
            double value = currentDataPoint.getValue();
            double timestamp = currentDataPoint.getTimestamp();

            // Check if the value is below the lower percentile
            if (value < lowerThreshold) {
                // Linear interpolation using lower threshold to avoid high deviations
                if (i == 0) {
                    DataPoint nextDataPoint = dataPoints.get(i + 1);
                    double nextValue = nextDataPoint.getValue();
                    double nextTimestamp = nextDataPoint.getTimestamp();
                    double interpolatedValue = interpolate(timestamp, value, nextTimestamp, nextValue, -1);
                    processedDataPoints.add(new DataPoint(interpolatedValue, timestamp));
                } else {
                    DataPoint prevDataPoint = dataPoints.get(i - 1);
                    double prevValue = prevDataPoint.getValue();
                    double prevTimestamp = prevDataPoint.getTimestamp();
                    DataPoint nextDataPoint = dataPoints.get(i + 1);
                    double nextValue = nextDataPoint.getValue();
                    double nextTimestamp = nextDataPoint.getTimestamp();
                    double interpolatedValue = -1.0;
                    if (prevValue <= 500 && nextValue <= 500) {
                    	interpolatedValue = prevValue;
                    } else {
                    	 interpolatedValue = interpolate(prevTimestamp, prevValue, nextTimestamp, nextValue, timestamp);
                    }
                    processedDataPoints.add(new DataPoint(interpolatedValue, timestamp));
                }
            }
            // Check if the value is above the upper percentile
            else if (value > upperThreshold) {
                // Linear interpolation using upper threshold to avoid high deviations
                if (i == dataPoints.size() - 1) {
                    DataPoint prevDataPoint = dataPoints.get(i - 1);
                    double prevValue = prevDataPoint.getValue();
                    double prevTimestamp = prevDataPoint.getTimestamp();
                    double interpolatedValue = interpolate(prevTimestamp, prevValue, timestamp, value, timestamp + 1);
                    processedDataPoints.add(new DataPoint(interpolatedValue, timestamp));
                } else {
                    DataPoint prevDataPoint = dataPoints.get(i - 1);
                    double prevValue = prevDataPoint.getValue();
                    double prevTimestamp = prevDataPoint.getTimestamp();
                    DataPoint nextDataPoint = dataPoints.get(i + 1);
                    double nextValue = nextDataPoint.getValue();
                    double nextTimestamp = nextDataPoint.getTimestamp();
                    double interpolatedValue = -1.0;
                    if (prevValue <= 500 && nextValue <= 500) {
                    	interpolatedValue = prevValue;
                    } else {
                    	 interpolatedValue = interpolate(prevTimestamp, prevValue, nextTimestamp, nextValue, timestamp);
                    }
                    processedDataPoints.add(new DataPoint(interpolatedValue, timestamp));
                }
            }
            // If the value is within the specified percentile range, keep the original value
            else {
                processedDataPoints.add(currentDataPoint);
            }
        }

        return processedDataPoints;
    }

    private static double calculatePercentile(List<Double> values, double percentile) {
        Collections.sort(values);
        int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
        return values.get(index);
    }

    private static double interpolate(double x0, double y0, double x1, double y1, double x) {
        return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
    }
}

class DataPoint {
    private double value;
    private double timestamp;

    public DataPoint(double value, double timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public double getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "(" + timestamp + ", " + value + ")";
    }
}