package systemParameterModel;

import java.util.ArrayList;
import java.util.List;

import designpatterns.ResourceParameters; 

/**
 * The Class SystemParameters.
 */
public class SystemParameters {
	
	/** The main input system. */
	String mainInputSystem; 
	
	/** The inputs system. */
	List<String> inputsSystem = new ArrayList<String>();
	
	/** The min power system input. */
	List<Double> minPowerSystemInput  = new ArrayList<Double>();
	
	/** The max power system input. */
	List<Double> maxPowerSystemInput  = new ArrayList<Double>();
	
	// TODO outputs system!
	
	/** The min power system output. */
	double minPowerSystemOutput; 
	
	/** The max power system output. */
	double maxPowerSystemOutput = Double.MAX_VALUE; 
	
	/** The temporal resolution optimization model. */
	double temporalResolutionOptimizationModel = 0.25;
 	
	/** The resourceparameters. */
	List<ResourceParameters> resourceParameters = new ArrayList<ResourceParameters>();

	/** List of dependencies. */
	List<Dependency> dependencies = new ArrayList<Dependency>();
	/**
	 * Gets the main input system.
	 *
	 * @return the mainInputSystem
	 */
	public String getMainInputSystem() {
		return mainInputSystem;
	}

	/**
	 * Sets the main input system.
	 *
	 * @param mainInputSystem the mainInputSystem to set
	 */
	public void setMainInputSystem(String mainInputSystem) {
		this.mainInputSystem = mainInputSystem;
	}

	/**
	 * Gets the inputs system.
	 *
	 * @return the inputsSystem
	 */
	public List<String> getInputsSystem() {
		return inputsSystem;
	}

	/**
	 * Sets the inputs system.
	 *
	 * @param inputsSystem the inputsSystem to set
	 */
	public void setInputsSystem(List<String> inputsSystem) {
		this.inputsSystem = inputsSystem;
	}

	/**
	 * Gets the min power system input.
	 *
	 * @return the minPowerSystemInput
	 */
	public List<Double> getMinPowerSystemInput() {
		return minPowerSystemInput;
	}

	/**
	 * Sets the min power system input.
	 *
	 * @param minPowerSystemInput the minPowerSystemInput to set
	 */
	public void setMinPowerSystemInput(List<Double> minPowerSystemInput) {
		this.minPowerSystemInput = minPowerSystemInput;
	}

	/**
	 * Gets the max power system input.
	 *
	 * @return the maxPowerSystemInput
	 */
	public List<Double> getMaxPowerSystemInput() {
		return maxPowerSystemInput;
	}

	/**
	 * Sets the max power system input.
	 *
	 * @param maxPowerSystemInput the maxPowerSystemInput to set
	 */
	public void setMaxPowerSystemInput(List<Double> maxPowerSystemInput) {
		this.maxPowerSystemInput = maxPowerSystemInput;
	}

	/**
	 * Gets the min power system output.
	 *
	 * @return the minPowerSystemOutput
	 */
	public double getMinPowerSystemOutput() {
		return minPowerSystemOutput;
	}

	/**
	 * Sets the min power system output.
	 *
	 * @param minPowerSystemOutput the minPowerSystemOutput to set
	 */
	public void setMinPowerSystemOutput(double minPowerSystemOutput) {
		this.minPowerSystemOutput = minPowerSystemOutput;
	}

	/**
	 * Gets the max power system output.
	 *
	 * @return the maxPowerSystemOutput
	 */
	public double getMaxPowerSystemOutput() {
		return maxPowerSystemOutput;
	}

	/**
	 * Sets the max power system output.
	 *
	 * @param maxPowerSystemOutput the maxPowerSystemOutput to set
	 */
	public void setMaxPowerSystemOutput(double maxPowerSystemOutput) {
		this.maxPowerSystemOutput = maxPowerSystemOutput;
	}

	/**
	 * Gets the resourceparameters.
	 *
	 * @return the resourceparameters
	 */
	public List<ResourceParameters> getResourceParameters() {
		return resourceParameters;
	}

	/**
	 * Sets the resourceparameters.
	 *
	 * @param resourceparameters the resourceparameters to set
	 */
	public void setResourceParameters(List<ResourceParameters> resourceparameters) {
		this.resourceParameters = resourceparameters;
	}

	/**
	 * Gets the temporal resolution optimization model.
	 * Default value = 0.25 h
	 *
	 * @return the temporalResolutionOptimizationModel
	 */
	public double getTemporalResolutionOptimizationModel() {
		return temporalResolutionOptimizationModel;
	}

	/**
	 * Sets the temporal resolution optimization model.
	 *
	 * @param temporalResolutionOptimizationModel the temporalResolutionOptimizationModel to set
	 */
	public void setTemporalResolutionOptimizationModel(double temporalResolutionOptimizationModel) {
		this.temporalResolutionOptimizationModel = temporalResolutionOptimizationModel;
	}

	/**
	 * @return the dependencies
	 */
	public List<Dependency> getDependencies() {
		return dependencies;
	}

	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
}
