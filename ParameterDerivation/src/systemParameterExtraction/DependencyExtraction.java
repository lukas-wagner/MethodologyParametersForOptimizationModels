package systemParameterExtraction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import formalizedProcessDescription.FormalizedProcessDescription;
import formalizedProcessDescription.FpdProcessInformation;
import formalizedProcessDescription.FpdState;
import systemParameterModel.Dependency;

/**
 * The Class DependencyExtraction.
 */
public class DependencyExtraction {

	/** The fpd, extracted from json. */
	static FormalizedProcessDescription[] formalizedProcessDescription;

	/** The list of dependencies. */
	static List<Dependency> listOfDependencies = new ArrayList<Dependency>();

	/** The list of processes. */
	static List<FpdProcessInformation> listOfProcesses = new ArrayList<FpdProcessInformation>();

	/** The list of info, energy, and product, their ids, and incoming and outgoing connections. */
	static List<FpdState> listOfStates = new ArrayList<FpdState>();

	/** The resource ID and name. */
	static HashMap<String, String> resourceIDAndName = new HashMap<String, String>();

	/** The Constant FPD_TECHNICALRESOURCE. */
	static final String FPD_TECHNICALRESOURCE = "fpb:TechnicalResource"; 

	/** The Constant FPD_USAGE. */
	static final String FPD_USAGE = "fpb:Usage";

	/** The Constant FPD_PROCESSOPERATOR. */
	static final String FPD_PROCESSOPERATOR = "fpb:ProcessOperator";

	/** The Constant FPD_PRODUCT. */
	static final String FPD_PRODUCT = "fpb:Product";

	/** The Constant FPD_FLOW. */
	static final String FPD_FLOW = "fpb:Flow";

	/** The Constant FPD_PARELLEL_FLOW. */
	static final String FPD_PARELLEL_FLOW = "fpb:ParallelFlow";

	/** The Constant FPD_ALTERNATIVE_FLOW. */
	static final String FPD_ALTERNATIVE_FLOW = "fpb:AlternativeFlow";

	/** The Constant FPD_ENERGY. */
	static final String FPD_ENERGY = "fpb:Energy";

	/** The Constant FPD_INFORMATION. */
	static final String FPD_INFORMATION = "fpb:Information";

	/** The Constant DEPENDENCY_CORRELATIVE. */
	static final String DEPENDENCY_CORRELATIVE = "correlative";

	/** The Constant DEPENDENCY_RESTRICTIVE. */
	static final String DEPENDENCY_RESTRICTIVE = "restrictive";
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		String filePath = "src/testData/fpb_standard.json";
		filePath = "src/testData/FPB(14).json";

		doDependencyExtraction(filePath);
	}


	/**
	 * Instantiates a new dependency extraction.
	 *
	 * @param filePath the file path
	 */
	public DependencyExtraction (String filePath) {
		doDependencyExtraction(filePath);
	}

	/**
	 * Do dependency extraction.
	 *
	 * @param filePath the file path
	 */
	public static void doDependencyExtraction (String filePath) {
		try {
			setFormalizedProcessDescription(importJson(filePath));
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			e.printStackTrace();
		}

		// gets all states, their ids and ingoing and outgoing flows
		getStatesAndId();

		// gets all processes
		getProcessesAndConnectionsAndResourcesIDandName();

		// assign states to processed (also by side, either input or output)
		getStateByProcess();

		getFlowType();

		findDependencies(); 
	}

	/**
	 * Gets the info energy product and id and adds it to ListOfInfoEnergyProduct.
	 *
	 * @return the info energy product and id
	 */
	public static void getStatesAndId () {

		// iterate over fpd object and extract all information if $type == energy || product || information
		for (int i = 0; i < getFormalizedProcessDescription()[1].getElementDataInformation().size(); i++) {

			if (
					getFormalizedProcessDescription()[1].getElementDataInformation().get(i).get$type().equals(FPD_ENERGY)
					|| getFormalizedProcessDescription()[1].getElementDataInformation().get(i).get$type().equals(FPD_PRODUCT)
					|| getFormalizedProcessDescription()[1].getElementDataInformation().get(i).get$type().equals(FPD_INFORMATION)
					) {

				String id = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getId(); 
				String type = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).get$type(); 
				List<String> incoming = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getIncoming();
				List<String> outgoing = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getOutgoing();

				FpdState infEneProd = new FpdState(id, type, incoming, outgoing); 
				getListOfStates().add(infEneProd);
			}
		}
	}

	/**
	 * Gets the processes and connections as well as id and name of all resources.
	 *
	 * @return the processes and connections
	 */
	public static void getProcessesAndConnectionsAndResourcesIDandName () {

		for (int i = 0; i < getFormalizedProcessDescription()[1].getElementDataInformation().size(); i++) {
			// -- get all processes, its inputs and outputs, and connected resources
			if (getFormalizedProcessDescription()[1].getElementDataInformation().get(i).get$type().equals(FPD_PROCESSOPERATOR)) {

				String id;
				List<String> inputProcess = new ArrayList<String>();
				List<String> outputProcess = new ArrayList<String>();
				List<String> connectedResources = new ArrayList<String>();

				id = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getId(); 

				// input = output predecessor
				inputProcess.addAll(getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getIncoming());
				outputProcess.addAll(getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getOutgoing());
				connectedResources.addAll(getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getIsAssignedTo());

				FpdProcessInformation process = new FpdProcessInformation(id, inputProcess, outputProcess, connectedResources);
				getListOfProcesses().add(process);
			}
			// -- get id and name for all resources
			if (getFormalizedProcessDescription()[1].getElementDataInformation().get(i).get$type().equals(FPD_TECHNICALRESOURCE)) {
				String id = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getId(); 
				String name = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getIdentification().getLongName();

				getResourceIDAndName().put(id, name);
			}

		}

		for (int process = 0; process < getListOfProcesses().size(); process++) {
			String id = "";
			String name = ""; 
			for (int resource = 0; resource < getListOfProcesses().get(process).getConnectedResources().size(); resource++) {	
				id =  getListOfProcesses().get(process).getConnectedResources().get(resource);
				name = 	getResourceIDAndName().get(id);
				getListOfProcesses().get(process).getConnectedResourcesNameAndID().put(id, name);
			}		
		}
	}


	/**
	 * Enrich process list by type .
	 *
	 * @return the connected prod energy info
	 */

	public static void getStateByProcess () {

		// idea: compare incoming and outgoing of processes and states

		for (int process = 0; process < getListOfProcesses().size(); process++) {
			for (int state = 0; state < getListOfStates().size(); state++) {

				for (int inputProcess = 0; inputProcess < getListOfProcesses().get(process).getInputProcess().size(); inputProcess++) {
					for (int outputState = 0; outputState < getListOfStates().get(state).getOutgoing().size(); outputState++) {

						if (getListOfProcesses().get(process).getInputProcess().get(inputProcess).equals(getListOfStates().get(state).getOutgoing().get(outputState))) {
							getListOfProcesses().get(process).getConnectedStatesInputSide().add(getListOfStates().get(state));
						}
					}

					for (int outputProcess = 0; outputProcess < getListOfProcesses().get(process).getOutputProcess().size(); outputProcess++) {
						for (int inputState = 0; inputState < getListOfStates().get(state).getIncoming().size(); inputState++) {

							if (getListOfProcesses().get(process).getOutputProcess().get(outputProcess).equals(getListOfStates().get(state).getIncoming().get(inputState))) {
								getListOfProcesses().get(process).getConnectedStatesOutputSide().add(getListOfStates().get(state));

							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the flow type.
	 * 0 flowType; 1 source, 2 target; 3 id
	 * @return the flow type
	 */
	public static void getFlowType () {

		List<String[]> flowTypes = new ArrayList<String[]>();

		for (int i = 0; i < getFormalizedProcessDescription()[1].getElementDataInformation().size(); i++) {
			String flowType = ""; 
			String source = ""; 
			String target= ""; 
			String id = ""; 
			// get flow types, ids of connceting states/processes
			if (getFormalizedProcessDescription()[1].getElementDataInformation().get(i).get$type().equals(FPD_FLOW)
					|| getFormalizedProcessDescription()[1].getElementDataInformation().get(i).get$type().equals(FPD_PARELLEL_FLOW)) {
				flowType= FPD_PARELLEL_FLOW;
				id = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getId();
				source = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getSourceRef();
				target = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getTargetRef();
			}

			if (getFormalizedProcessDescription()[1].getElementDataInformation().get(i).get$type().equals(FPD_ALTERNATIVE_FLOW)) {
				flowType = FPD_ALTERNATIVE_FLOW;
				id = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getId();
				source = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getSourceRef();
				target = getFormalizedProcessDescription()[1].getElementDataInformation().get(i).getTargetRef();
			}
			String[] info = new String[4]; 
			info[0] = flowType; 
			info[1] = source; 
			info[2] = target; 
			info[3] = id; 
			if (!(info[0]=="")) flowTypes.add(info);
		}

		for (int process = 0; process < getListOfProcesses().size(); process++) {
			for (int flows = 0; flows < flowTypes.size(); flows++) {


				for (int i = 0; i < getListOfProcesses().get(process).getInputProcess().size(); i++) {
					if (getListOfProcesses().get(process).getInputProcess().get(i).equals(flowTypes.get(flows)[3])) {
						String type = flowTypes.get(flows)[0];
						String idSource =flowTypes.get(flows)[1];
						getListOfProcesses().get(process).getFlowTypeInputSide().add(type);
						getListOfProcesses().get(process).getFlowTypeInputSideState().put(idSource, type);
					}

				}

				for (int i = 0; i < getListOfProcesses().get(process).getOutputProcess().size(); i++) {
					if (getListOfProcesses().get(process).getOutputProcess().get(i).equals(flowTypes.get(flows)[3])) {
						String type = flowTypes.get(flows)[0];
						String idSource = flowTypes.get(flows)[2];
						getListOfProcesses().get(process).getFlowTypeOutputSide().add(type);
						getListOfProcesses().get(process).getFlowTypeOutputSideState().put(idSource, type);
					}
				}

			}

		}

	}



	/**
	 * Find dependencies.
	 */
	public static void findDependencies() {
		getListOfDependencies().addAll(getResourceDependencies());
		getListOfDependencies().addAll(getSystemInputStatePerspective());
		getListOfDependencies().addAll(getSystemOutputStatePerspective());

		// remove duplicate resources

	}


	public static List<Dependency> getResourceDependencies () {
		List<Dependency> listOfResourceDependencies = new ArrayList<Dependency>(); 

		for (FpdProcessInformation processA : getListOfProcesses()) {
			Dependency dependency = new Dependency(); 

			//			dependency.getRelevantOutputs().addAll(convertValuesToList(processA.getConnectedResourcesNameAndID()));
			dependency.getRelevantOutputs().addAll(new HashSet<>(convertValuesToList(processA.getConnectedResourcesNameAndID())));


			//	dependency.getOutputProcessInformation().add(processA);

			for (FpdProcessInformation processB : getListOfProcesses()) {
				for (int counterOutputSide = 0; counterOutputSide < processA.getConnectedStatesOutputSide().size(); counterOutputSide++) {
					for (int counterInputSide = 0; counterInputSide < processB.getConnectedStatesInputSide().size(); counterInputSide++) {

						if (processA.getConnectedStatesOutputSide().get(counterOutputSide).equals(processB.getConnectedStatesInputSide().get(counterInputSide))
								&& ( processA.getConnectedStatesOutputSide().get(counterOutputSide).getType().equals(FPD_ENERGY)
										||	processA.getConnectedStatesOutputSide().get(counterOutputSide).getType().equals(FPD_PRODUCT)	
										)
								) {
							dependency.getRelevantInputs().addAll(new HashSet<>(convertValuesToList(processB.getConnectedResourcesNameAndID())));
							//dependency.getInputProcessInformation().add(processB);
						}

						String flowTypeOutputProcessA = processA.getFlowTypeOutputSideState().get(processA.getConnectedStatesOutputSide().get(counterOutputSide).getId());
						String flowtypeInputProcessB = processB.getFlowTypeInputSideState().get(processB.getConnectedStatesInputSide().get(counterInputSide).getId());
						if (flowTypeOutputProcessA.equals(FPD_ALTERNATIVE_FLOW)
								|| flowtypeInputProcessB.equals(FPD_ALTERNATIVE_FLOW)
								) {
							dependency.setTypeOfDependency(DEPENDENCY_RESTRICTIVE);	
						}
					}
				}

			}

			// only save dependency if list not empty
			if (!(dependency.getRelevantInputs().size()==0) && !(dependency.getRelevantOutputs().size()==0)) {
				listOfResourceDependencies.add(dependency);
			}
		}
		return listOfResourceDependencies;
	}

	/**
	 * Gets the system input state perspective.
	 *
	 * @return the system input state perspective
	 */
	public static List<Dependency> getSystemInputStatePerspective () {
		List<Dependency> systemInputListOfDependencies = new ArrayList<Dependency>(); 
		int inputCounter = 0; 
		for(FpdState state : getListOfStates()) {
			if (state.getIncoming().isEmpty() 
					&& (state.getType().equals(FPD_ENERGY) || state.getType().equals(FPD_PRODUCT))
					) {
				Dependency dependency = new Dependency();
				dependency.getRelevantOutputs().add("SystemInput-"+Integer.toString(inputCounter));

				inputCounter++;

				// get processes, that are connected to state
				List<FpdProcessInformation> relevantInputs = new ArrayList<FpdProcessInformation>();

				for (FpdProcessInformation process : getListOfProcesses()) {
					for (FpdState stateOfProcess : process.getConnectedStatesInputSide()) {
						if (stateOfProcess.equals(state)) {
							dependency.getRelevantInputs().addAll(convertValuesToList(process.getConnectedResourcesNameAndID()));
							relevantInputs.add(process);
						}
					}
				}

				// determine type of dependency

				for (FpdProcessInformation depInput : relevantInputs) {
					for (FpdState stateInput : depInput.getConnectedStatesInputSide()) {
						if (depInput.getFlowTypeInputSideState().get(stateInput.getId()).equals(FPD_ALTERNATIVE_FLOW)) {
							dependency.setTypeOfDependency(DEPENDENCY_RESTRICTIVE);
						}
					}
				}

				systemInputListOfDependencies.add(dependency); 
			}
		}
		return systemInputListOfDependencies; 
	}

	/**
	 * Gets the system output state perspective.
	 *
	 * @return the system output state perspective
	 */
	public static List<Dependency> getSystemOutputStatePerspective () {
		List<Dependency> systemOutputListOfDependencies = new ArrayList<Dependency>(); 
		int outputCounter = 0; 
		for(FpdState state : getListOfStates()) {
			if (state.getOutgoing().isEmpty()
					&& (state.getType().equals(FPD_ENERGY) || state.getType().equals(FPD_PRODUCT))
					) {
				Dependency dependency = new Dependency();

				dependency.getRelevantInputs().add("SystemOutput-"+Integer.toString(outputCounter));

				outputCounter++;
				// get processes, that are connected to state
				List<FpdProcessInformation> relevantOutputs = new ArrayList<FpdProcessInformation>();
				for (FpdProcessInformation process : getListOfProcesses()) {
					for (FpdState stateOfProcess : process.getConnectedStatesOutputSide()) {
						if (stateOfProcess.equals(state)) {
							dependency.getRelevantOutputs().addAll(convertValuesToList(process.getConnectedResourcesNameAndID()));
							relevantOutputs.add(process);
						}
					}
				}

				for (FpdProcessInformation depOutput : relevantOutputs) {
					for (FpdState stateOutput : depOutput.getConnectedStatesOutputSide()) {
						if (depOutput.getFlowTypeOutputSideState().get(stateOutput.getId()).equals(FPD_ALTERNATIVE_FLOW)) {
							dependency.setTypeOfDependency(DEPENDENCY_RESTRICTIVE);
						}
					}
				}

				systemOutputListOfDependencies.add(dependency); 
			}
		}
		return systemOutputListOfDependencies; 
	}

	/**
	 * Gets the type from list.
	 *
	 * @param side the side
	 * @param process the process
	 * @param counterInputOutput the counter input output
	 * @return the type from list
	 */
	private static FpdState getTypeFromList(String side, int process, int counterInputOutput) {
		FpdState idAndType = new FpdState();
		String id; 
		if (side == "Input") {
			id = getListOfProcesses().get(process).getInputProcess().get(counterInputOutput);
		} else {
			id = getListOfProcesses().get(process).getOutputProcess().get(counterInputOutput);
		}
		idAndType.setId(id);
		int indexOfId = getIndexOfValueFromArrayList(getListOfStates(), id);
		System.out.println(side  +"  " + process + "-"+ counterInputOutput + " " +indexOfId);
		if (indexOfId != -1) {
			String type = getListOfStates().get(indexOfId).getType();
			idAndType.setType(type);
			return idAndType;
		} else {
			return null; 
		}
	}


	/**
	 * Import json.
	 *
	 * @param filePath the file path
	 * @return the formalized process description[]
	 * @throws FileNotFoundException the file not found exception
	 */
	public static FormalizedProcessDescription[] importJson (String filePath) throws FileNotFoundException {
		// Read JSON from a file
		Gson gson = new Gson();
		//		JsonReader reader = new JsonReader(new FileReader("src/testData/fpb_standard.json"));
		JsonReader reader = new JsonReader(new FileReader(filePath));
		FormalizedProcessDescription[] fpd = gson.fromJson(reader, FormalizedProcessDescription[].class);
		return fpd; 
	}

	/**
	 * Gets the index of value from array list.
	 *
	 * @param list the list
	 * @param value the value
	 * @return the index of value from array list
	 */
	public static int getIndexOfValueFromArrayList(List<FpdState> list, String value) {
		// Initialize index to -1 to indicate that the value was not found
		int index = -1;

		// Iterate through the ArrayList to find the index of the target value
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId().equals(value)) {
				index = i;
				break; // Exit the loop once the value is found
			}
		}
		return index;
	}


	/**
	 * Convert values to list.
	 *
	 * @param map the map
	 * @return the list
	 */
	public static List<String> convertValuesToList(Map<?, String> map) {
		List<String> resultList = new ArrayList<>();

		for (String value : map.values()) {
			resultList.add(value);
		}

		return resultList;
	}
	/**
	 * Gets the list of dependencies.
	 *
	 * @return the listOfDependencies
	 */
	public static List<Dependency> getListOfDependencies() {
		return listOfDependencies;
	}

	/**
	 * Sets the list of dependencies.
	 *
	 * @param listOfDependencies the listOfDependencies to set
	 */
	public static void setListOfDependencies(List<Dependency> listOfDependencies) {
		DependencyExtraction.listOfDependencies = listOfDependencies;
	}


	/**
	 * Gets the list of processes.
	 *
	 * @return the listOfProcesses
	 */
	public static List<FpdProcessInformation> getListOfProcesses() {
		return listOfProcesses;
	}


	/**
	 * Sets the list of processes.
	 *
	 * @param listOfProcesses the listOfProcesses to set
	 */
	public static void setListOfProcesses(List<FpdProcessInformation> listOfProcesses) {
		DependencyExtraction.listOfProcesses = listOfProcesses;
	}


	/**
	 * Gets the list of info energy product.
	 *
	 * @return the listOfInfoEnergyProduct
	 */
	public static List<FpdState> getListOfStates() {
		return listOfStates;
	}


	/**
	 * Sets the list of info energy product.
	 *
	 * @param listOfInfoEnergyProduct the listOfInfoEnergyProduct to set
	 */
	public static void setListOfStates(List<FpdState> listOfInfoEnergyProduct) {
		DependencyExtraction.listOfStates = listOfInfoEnergyProduct;
	}


	/**
	 * Gets the fpd.
	 *
	 * @return the fpd
	 */
	public static FormalizedProcessDescription[] getFormalizedProcessDescription() {
		return formalizedProcessDescription;
	}


	/**
	 * Sets the fpd.
	 *
	 * @param fpd the fpd to set
	 */
	public static void setFormalizedProcessDescription(FormalizedProcessDescription[] fpd) {
		DependencyExtraction.formalizedProcessDescription = fpd;
	}


	/**
	 * Gets the resource ID and name.
	 *
	 * @return the resourceIDAndName
	 */
	public static HashMap<String, String> getResourceIDAndName() {
		return resourceIDAndName;
	}


	/**
	 * Sets the resource ID and name.
	 *
	 * @param resourceIDAndName the resourceIDAndName to set
	 */
	public static void setResourceIDAndName(HashMap<String, String> resourceIDAndName) {
		DependencyExtraction.resourceIDAndName = resourceIDAndName;
	}


}
