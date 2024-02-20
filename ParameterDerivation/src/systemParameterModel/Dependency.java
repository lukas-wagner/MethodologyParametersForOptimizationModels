package systemParameterModel;

import java.util.ArrayList;
import java.util.List;

import formalizedProcessDescription.FpdProcessInformation;

public class Dependency {
	
	
	/** The relevant inputs. */
	List<String> relevantInputs = new ArrayList<String>();
	
	/** The input process information. */
	List<FpdProcessInformation> inputProcessInformation = new ArrayList<FpdProcessInformation>();
	
	/** The relevant outputs. */
	List<String> relevantOutputs = new ArrayList<String>();
	
	List<FpdProcessInformation> outputProcessInformation = new ArrayList<FpdProcessInformation>();

	
	/** The type of dependency, default = correlative. */
	String typeOfDependency = "correlative";
	/**
	 * @return the relevantInputs
	 */
	public List<String> getRelevantInputs() {
		return relevantInputs;
	}
	/**
	 * @param relevantInputs the relevantInputs to set
	 */
	public void setRelevantInputs(List<String> relevantInputs) {
		this.relevantInputs = relevantInputs;
	}
	/**
	 * @return the relevantOutputs
	 */
	public List<String> getRelevantOutputs() {
		return relevantOutputs;
	}
	/**
	 * @param relevantOutputs the relevantOutputs to set
	 */
	public void setRelevantOutputs(List<String> relevantOutputs) {
		this.relevantOutputs = relevantOutputs;
	}
	/**
	 * @return the typeOfDependency
	 */
	public String getTypeOfDependency() {
		return typeOfDependency;
	}
	/**
	 * @param typeOfDependency the typeOfDependency to set
	 */
	public void setTypeOfDependency(String typeOfDependency) {
		this.typeOfDependency = typeOfDependency;
	}
	/**
	 * @return the inputProcessInformation
	 */
	public List<FpdProcessInformation> getInputProcessInformation() {
		return inputProcessInformation;
	}
	/**
	 * @param inputProcessInformation the inputProcessInformation to set
	 */
	public void setInputProcessInformation(List<FpdProcessInformation> inputProcessInformation) {
		this.inputProcessInformation = inputProcessInformation;
	}
	/**
	 * @return the outputProcessInformation
	 */
	public List<FpdProcessInformation> getOutputProcessInformation() {
		return outputProcessInformation;
	}
	/**
	 * @param outputProcessInformation the outputProcessInformation to set
	 */
	public void setOutputProcessInformation(List<FpdProcessInformation> outputProcessInformation) {
		this.outputProcessInformation = outputProcessInformation;
	}
	
	
}
