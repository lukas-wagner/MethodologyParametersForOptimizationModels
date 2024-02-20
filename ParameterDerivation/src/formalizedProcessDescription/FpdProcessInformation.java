package formalizedProcessDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class DependencyInformation.
 */
public class FpdProcessInformation {

	/** The id of process. */
	String idOfProcess; 
	
	/** The input process. */
	List<String> inputProcess = new ArrayList<String>();
	
	/** The output process. */
	List<String> outputProcess = new ArrayList<String>();
	
	/** The connected resources. */
	List<String> connectedResources = new ArrayList<String>();
	
	/** The connected resources name and ID. */
	HashMap<String, String> connectedResourcesNameAndID = new HashMap<String, String>();
	
	/** The input process type. */
	List<FpdState> connectedStatesInputSide = new ArrayList<FpdState>();
	
	/** The output process type. */
	List<FpdState> connectedStatesOutputSide = new ArrayList<FpdState>();
	
	/** The flows input side. */
	List<String> flowsInputSide = new ArrayList<String>();
	
	/** The flows output side. */
	List<String> flowsOutputSide = new ArrayList<String>();
	
	/** The flow type input side. */
	List<String> flowTypeInputSide =  new ArrayList<String>();
	
	/** The flow type output side. */
	List<String> flowTypeOutputSide =  new ArrayList<String>();
	
	/** The flow type input side state. */
	HashMap<String, String> flowTypeInputSideState = new HashMap<String, String>();
	
	/** The flow type output side state. */
	HashMap<String, String> flowTypeOutputSideState = new HashMap<String, String>();

	/**
	 * Instantiates a new dependency information.
	 */
	public FpdProcessInformation () {
	}
	
	
	/**
	 * Instantiates a new dependency information with parameters.
	 *
	 * @param id the id
	 * @param input the input
	 * @param output the output
	 * @param conRes the con res
	 */
	public FpdProcessInformation (String id, List<String> input, List<String> output, List<String> conRes) {
		setIdOfProcess(id);
		getInputProcess().addAll(input);
		getOutputProcess().addAll(output);
		getConnectedResources().addAll(conRes);
	}
	
	/**
	 * Gets the input process.
	 *
	 * @return the inputProcess
	 */
	public List<String> getInputProcess() {
		return inputProcess;
	}
	
	/**
	 * Sets the input process.
	 *
	 * @param inputProcess the inputProcess to set
	 */
	public void setInputProcess(List<String> inputProcess) {
		this.inputProcess = inputProcess;
	}
	
	/**
	 * Gets the output process.
	 *
	 * @return the outputProcess
	 */
	public List<String> getOutputProcess() {
		return outputProcess;
	}
	
	/**
	 * Sets the output process.
	 *
	 * @param outputProcess the outputProcess to set
	 */
	public void setOutputProcess(List<String> outputProcess) {
		this.outputProcess = outputProcess;
	}
	
	/**
	 * Gets the connected resources.
	 *
	 * @return the connectedResources
	 */
	public List<String> getConnectedResources() {
		return connectedResources;
	}
	
	/**
	 * Sets the connected resources.
	 *
	 * @param connectedResources the connectedResources to set
	 */
	public void setConnectedResources(List<String> connectedResources) {
		this.connectedResources = connectedResources;
	}


	/**
	 * Gets the id of process.
	 *
	 * @return the idOfProcess
	 */
	public String getIdOfProcess() {
		return idOfProcess;
	}


	/**
	 * Sets the id of process.
	 *
	 * @param idOfProcess the idOfProcess to set
	 */
	public void setIdOfProcess(String idOfProcess) {
		this.idOfProcess = idOfProcess;
	}


	/**
	 * Gets the input process type.
	 *
	 * @return the inputProcessType
	 */
	public List<FpdState> getConnectedStatesInputSide() {
		return connectedStatesInputSide;
	}


	/**
	 * Sets the input process type.
	 *
	 * @param idAndTypeListInput the inputProcessType to set
	 */
	public void setConnectedStatesInputSide(List<FpdState> idAndTypeListInput) {
		this.connectedStatesInputSide = idAndTypeListInput;
	}


	/**
	 * Gets the output process type.
	 *
	 * @return the outputProcessType
	 */
	public List<FpdState> getConnectedStatesOutputSide() {
		return connectedStatesOutputSide;
	}


	/**
	 * Sets the output process type.
	 *
	 * @param outputProcessType the outputProcessType to set
	 */
	public void setConnectedStatesOutputSide(List<FpdState> outputProcessType) {
		this.connectedStatesOutputSide = outputProcessType;
	}


	/**
	 * Gets the connected resources name and ID.
	 *
	 * @return the connectedResourcesNameAndID
	 */
	public HashMap<String, String> getConnectedResourcesNameAndID() {
		return connectedResourcesNameAndID;
	}


	/**
	 * Sets the connected resources name and ID.
	 *
	 * @param connectedResourcesNameAndID the connectedResourcesNameAndID to set
	 */
	public void setConnectedResourcesNameAndID(HashMap<String, String> connectedResourcesNameAndID) {
		this.connectedResourcesNameAndID = connectedResourcesNameAndID;
	}


	/**
	 * Gets the flows input side.
	 *
	 * @return the flowsInputSide
	 */
	public List<String> getFlowsInputSide() {
		return flowsInputSide;
	}


	/**
	 * Sets the flows input side.
	 *
	 * @param flowsInputSide the flowsInputSide to set
	 */
	public void setFlowsInputSide(List<String> flowsInputSide) {
		this.flowsInputSide = flowsInputSide;
	}


	/**
	 * Gets the flows output side.
	 *
	 * @return the flowsOutputSide
	 */
	public List<String> getFlowsOutputSide() {
		return flowsOutputSide;
	}


	/**
	 * Sets the flows output side.
	 *
	 * @param flowsOutputSide the flowsOutputSide to set
	 */
	public void setFlowsOutputSide(List<String> flowsOutputSide) {
		this.flowsOutputSide = flowsOutputSide;
	}


	/**
	 * Gets the flow type input side.
	 *
	 * @return the flowTypeInputSide
	 */
	public List<String> getFlowTypeInputSide() {
		return flowTypeInputSide;
	}


	/**
	 * Sets the flow type input side.
	 *
	 * @param flowTypeInputSide the flowTypeInputSide to set
	 */
	public void setFlowTypeInputSide(List<String> flowTypeInputSide) {
		this.flowTypeInputSide = flowTypeInputSide;
	}


	/**
	 * Gets the flow type output side.
	 *
	 * @return the flowTypeOutputSide
	 */
	public List<String> getFlowTypeOutputSide() {
		return flowTypeOutputSide;
	}


	/**
	 * Sets the flow type output side.
	 *
	 * @param flowTypeOutputSide the flowTypeOutputSide to set
	 */
	public void setFlowTypeOutputSide(List<String> flowTypeOutputSide) {
		this.flowTypeOutputSide = flowTypeOutputSide;
	}


	/**
	 * Gets the flow type input side state.
	 *
	 * @return the flowTypeInputSideState
	 */
	public HashMap<String, String> getFlowTypeInputSideState() {
		return flowTypeInputSideState;
	}


	/**
	 * Sets the flow type input side state.
	 *
	 * @param flowTypeInputSideState the flowTypeInputSideState to set
	 */
	public void setFlowTypeInputSideState(HashMap<String, String> flowTypeInputSideState) {
		this.flowTypeInputSideState = flowTypeInputSideState;
	}


	/**
	 * Gets the flow type output side state.
	 *
	 * @return the flowTypeOutputSideState
	 */
	public HashMap<String, String> getFlowTypeOutputSideState() {
		return flowTypeOutputSideState;
	}


	/**
	 * Sets the flow type output side state.
	 *
	 * @param flowTypeOutputSideState the flowTypeOutputSideState to set
	 */
	public void setFlowTypeOutputSideState(HashMap<String, String> flowTypeOutputSideState) {
		this.flowTypeOutputSideState = flowTypeOutputSideState;
	}




}
