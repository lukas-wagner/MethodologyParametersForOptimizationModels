package caseStudy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import designpatterns.DesignPatterns;
import designpatterns.OptimizationResults;
import designpatterns.PiecewiseLinearApproximation;
import designpatterns.ResourceParameters;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;
import systemParameterExtraction.ReadParametersFromDataModel;
import systemParameterModel.Dependency;
import systemParameterModel.SystemParameters;

public class OptimizationModel {
	/** The nolimit. */
	static final double NOLIMIT = 9999;

	/** The Constant INPUT. */
	static final String INPUT = "Input";

	/** The Constant OUTPUT. */
	static final String OUTPUT = "Output";

	/** The Constant SOC. */
	static final String SOC = "SOC";

	/** The Constant POWER. */
	static final String POWER = "Power";

	/** The Constant BINARY. */
	static final String BINARY = "Binary";

	/** The Constant SEGMENT. */
	static final String SEGMENT = "Segment";

	/** The Constant STATE. */
	static final String STATE = "State";

	static final int ONLYONE = -1; 

	static double startupCost = 10; 
	static double constHydrDemand = 900; 

	/** The global system parameters. */
	static SystemParameters globalSystemParameters = new SystemParameters();

	public static void main(String[] args)  {
		setOptimizationParameters();
		try {
			setUpOptimizationModel();
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Problem with optimization model");
		}
	}

	/**
	 * Sets the optimization parameters, primarily in ArrayList<ResourceParameters> resourceParameters.
	 */
	public static void setOptimizationParameters () {

		SystemParameters systemParameters = new SystemParameters();
		String filePath = "src/output/systemParameters_2024-01-18_13-47-12.json"; 
		systemParameters = ReadParametersFromDataModel.readJson(filePath);
		if (systemParameters == null) System.err.println("SystemParameters empty");

		setGlobalSystemParameters(systemParameters);

		for (int resource = 0; resource < systemParameters.getResourceParameters().size(); resource++) {
			ResourceParameters resourceParameters = new ResourceParameters();
			resourceParameters = systemParameters.getResourceParameters().get(resource);
			resourceParameters.setNumberOfSystemStates(resourceParameters.getSystemStates().size());
			DesignPatterns.getResourceParameters().add(resourceParameters);
		}

		designpatterns.DesignPatterns.setOptimalityGap(0.001); // default 10e-4 = 0.001
		designpatterns.DesignPatterns.setTimeInterval(systemParameters.getTemporalResolutionOptimizationModel());

		designpatterns.DesignPatterns.setArrayLength(1201); // set arrayLength in # of time steps
		//0-50 401
		// 0-8 65
		// 0-12 97

		// TODO system parameters
	}


	public static double[] generateTargetTS () {
		String filePath = "src/timeSeriesDataSet/input_tsd_100-250.csv"; 
		return importTSD(filePath);
	}

	/**
	 * Electrolyzer  model i.
	 *
	 * @throws IloException the ilo exception
	 */
	public static void setUpOptimizationModel () throws IloException {
		String nameOfModel = "OJIES_v1";
		try {
			//additional parameters for system
			double maxPowerSystem; 

			try {
				maxPowerSystem= getGlobalSystemParameters().getMaxPowerSystemInput().get(0); 
			} catch (Exception e) {
				maxPowerSystem =  Double.MAX_VALUE;
			}

			//-------------------------------------------------------------------- Decision Variables --------------------------------------------------------------------
			designpatterns.DesignPatterns.creationOfDecisionVariables(maxPowerSystem);


			// create additional dec var

			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------

			// Constraint to equate input to tsd of input
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				designpatterns.DesignPatterns.getCplex().addGe(
						designpatterns.DesignPatterns.getDecisionVariableFromVector("gasfired_generator", INPUT, 0, POWER)[i], 
						generateTargetTS()[i]*0.999999999
						);
				//				designpatterns.DesignPatterns.getCplex().addLe(
				//						designpatterns.DesignPatterns.getDecisionVariableFromVector("gasfired_generator", INPUT, 0, POWER)[i], 
				//						generateTargetTS()[i]*1.2
				//						);
			}

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------

			// ------------------------------------------------------------------------ Parameterize Design patterns based on parameter set --------------------------------------------------------------------

			// Parameterize resource models
			for (int resource=0; resource < getGlobalSystemParameters().getResourceParameters().size(); resource++) {
				ResourceParameters resourceParameters = getGlobalSystemParameters().getResourceParameters().get(resource);
				String nameOfResource = resourceParameters.getName();
				if (resourceParameters.isSecondaryResource()==false 
						&& (
								!(resourceParameters.getPlaList().isEmpty())
								|| !(resourceParameters.getSlope() == 0)
								)
						){
					designpatterns.DesignPatterns.generateInputOutputRelationship(nameOfResource);
				} else {
					designpatterns.DesignPatterns.generateEnergyBalanceForStorageSystem(nameOfResource);
				}

				if (!(resourceParameters.getSystemStates().isEmpty())) {
					designpatterns.DesignPatterns.generateSystemStateSelectionByPowerLimits(nameOfResource);
					designpatterns.DesignPatterns.generateStateSequencesAndHoldingDuration(nameOfResource);
					designpatterns.DesignPatterns.generateRampLimits(nameOfResource, INPUT);
				}
			}

			/**
			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE, POWER)}, 
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("gasfired_generator", INPUT, 0, POWER)}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("gasfired_generator", OUTPUT, ONLYONE, POWER)},
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("heat_exchanger", INPUT, 0, POWER)}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("heat_exchanger", OUTPUT, ONLYONE, POWER)},
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, ONLYONE, POWER)}
					);
			 */
			// Set up and add dependencies

			setUpDependencies(); 

			designpatterns.DesignPatterns.getCplex().exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = designpatterns.DesignPatterns.getCplex().linearNumExpr();
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				objective.addTerm(1,
						//						designpatterns.DesignPatterns.getTimeInterval()*designpatterns.DesignPatterns.getElectricityPrice()[i], 
						//						designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE, POWER)[i]
						designpatterns.DesignPatterns.getDecisionVariableFromVector("gasfired_generator", INPUT, 0, POWER)[i]
						);
			}
			designpatterns.DesignPatterns.getCplex().addMinimize(objective);
			//			designpatterns.DesignPatterns.getCplex().addMaximize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			designpatterns.DesignPatterns.getCplex().setParam(IloCplex.Param.MIP.Tolerances.MIPGap, designpatterns.DesignPatterns.getOptimalityGap());
			long start = System.currentTimeMillis();
			System.out.println("cplex solve");
			if (designpatterns.DesignPatterns.getCplex().solve()) {
				long end = System.currentTimeMillis();
				long solvingTime = 	(end - start);
				System.out.println("obj = "+designpatterns.DesignPatterns.getCplex().getObjValue());
				System.out.println("solvingTime in ms = "+solvingTime);
				System.out.println(designpatterns.DesignPatterns.getCplex().getCplexStatus());

				List<OptimizationResults> optimizationResults = saveResults();
				String filePath = "src/output/";
				writeResultsFromListToFile(optimizationResults, nameOfModel, filePath);

			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
		}
		finally {
			if (designpatterns.DesignPatterns.getCplex()!=null)  {
				designpatterns.DesignPatterns.getCplex().close();
				designpatterns.DesignPatterns.globalCplex=null;
			}
		}
	}

	public static void setUpDependencies() throws IloException {
		int depCounter = 0; 
		for (Dependency dependency: getGlobalSystemParameters().getDependencies()) {
			System.out.println("Dependency"+ depCounter);

			List<IloNumVar[]> inputDecVar = new ArrayList<IloNumVar[]>(); 
			List<IloNumVar[]> outputDecVar = new ArrayList<IloNumVar[]>(); 

			for (int inputCounter = 0; inputCounter < dependency.getRelevantInputs().size(); inputCounter++) {
				String nameOfInput = dependency.getRelevantInputs().get(inputCounter);
				int numberOfInput = -1; 
				System.out.println("input: " + nameOfInput);
				if (nameOfInput.contains("SystemOutput")){
					numberOfInput = Integer.parseInt(nameOfInput.substring(nameOfInput.indexOf("-")));
					nameOfInput = "System";
					IloNumVar[] input;
					if (numberOfInput == 0) {
						input = DesignPatterns.getDecisionVariableFromVector(nameOfInput, OUTPUT, ONLYONE, POWER);
					}
					else {						
						input = DesignPatterns.getCplex().numVarArray(
								DesignPatterns.getArrayLength(),  
								0,
								Double.MAX_VALUE
//								getGlobalSystemParameters().getMinPowerSystemInput().get(numberOfInput), 
//								getGlobalSystemParameters().getMaxPowerSystemInput().get(numberOfInput)
								);
						DesignPatterns.getDecisionVariablesVector().put("System"+"-"+OUTPUT+"-"+Integer.toString(inputCounter)+POWER, input);
					}
					inputDecVar.add(input);
				} else {
					IloNumVar[] input = DesignPatterns.getDecisionVariableFromVector(nameOfInput, INPUT, 0, POWER);
					inputDecVar.add(input);
				}
			}

			for (int outputCounter = 0; outputCounter < dependency.getRelevantOutputs().size(); outputCounter++) {
				String nameOfOutput = dependency.getRelevantOutputs().get(outputCounter);
				System.out.println("output: " + nameOfOutput);
				int numberOfOutput = -1; 
				if (nameOfOutput.contains("SystemInput")){
					numberOfOutput = Integer.parseInt(nameOfOutput.substring(nameOfOutput.indexOf("-")));
					nameOfOutput = "System";
					IloNumVar[] output;
					if (numberOfOutput == 0) {
						//"System", INPUT, ONLYONE,POWER
						output = DesignPatterns.getDecisionVariableFromVector(nameOfOutput, INPUT, ONLYONE, POWER);
					} else {
						output = DesignPatterns.getCplex().numVarArray(
								DesignPatterns.getArrayLength(),  
								0,
								getGlobalSystemParameters().getMaxPowerSystemOutput());
						DesignPatterns.getDecisionVariablesVector().put("System"+"-"+INPUT+"-"+Integer.toString(outputCounter)+POWER, output);
					}
					outputDecVar.add(output);
				} else {
					IloNumVar[] output = DesignPatterns.getDecisionVariableFromVector(nameOfOutput, OUTPUT, ONLYONE, POWER);
					outputDecVar.add(output);
				}
			}

			IloNumVar[][] inputVariablesDependency =  convertListToArray(inputDecVar); 
			IloNumVar[][] outputVariablesDependency = convertListToArray(outputDecVar);

			if (dependency.getTypeOfDependency().equals("correlative")) {
				DesignPatterns.generateCorrelativeDependency(outputVariablesDependency, inputVariablesDependency);
			} else {
				// dependency.getTypeOfDependency().equals("restrictive")
				IloIntVar[][][] restrictiveDependency	= DesignPatterns.generateRestrictiveDependency(outputVariablesDependency, inputVariablesDependency);
			}
			depCounter++; 
		}		
	}

	/**
	 * Save results.
	 *
	 * @return the list
	 */
	public static List<OptimizationResults> saveResults () {
		List<OptimizationResults> optimizationResults = new ArrayList<OptimizationResults>();

		// get all decVars from Vector and save results to List
		for (Entry<String, IloNumVar[]> decisionVariableSet: DesignPatterns.getDecisionVariablesVector().entrySet()) {
			OptimizationResults decVarResults = new OptimizationResults();

			String decisionVariableName = decisionVariableSet.getKey();
			IloNumVar[] decisionVariable = decisionVariableSet.getValue();

			decVarResults.setVariableName(decisionVariableName);
			List<Double> decVarValues = new ArrayList<Double>();

			for (int timeStep = 0; timeStep < decisionVariable.length; timeStep++) {
				try {
					decVarValues.add(timeStep, DesignPatterns.getCplex().getValue(decisionVariable[timeStep]));
				} catch (UnknownObjectException e) {
					System.err.println("Value not found for " + decisionVariableName +" at time step: " + timeStep);
					e.printStackTrace();
				} catch (IloException e) {
					System.err.println("Value not found for " + decisionVariableName +" at time step: " + timeStep);
					e.printStackTrace();
				}
			}
			decVarResults.setOptimizationResults(decVarValues);
			optimizationResults.add(decVarResults);
		}

		// get all decVars from Matrix and save results to List
		for (Entry<String, IloNumVar[][]> decisionVariableSet: DesignPatterns.getDecisionVariablesMatrix().entrySet()) {

			String decisionVariableName = decisionVariableSet.getKey();
			IloNumVar[][] decisionVariable = decisionVariableSet.getValue();


			if (decisionVariableName.contains("State")) {
				System.out.println(decisionVariable[0].length);
				for (int width = 0; width < decisionVariable[0].length; width++) {
					OptimizationResults decVarResults = new OptimizationResults();
					decVarResults.setVariableName(decisionVariableName + "-" + Integer.toString(width));
					List<Double> decVarValues = new ArrayList<Double>();

					for (int timeStep = 0; timeStep < decisionVariable.length; timeStep++) {
						try {
							// state variables defined: statesIntArrayResource[timeStep][state] 
							decVarValues.add(timeStep, DesignPatterns.getCplex().getValue(decisionVariable[timeStep][width]));
						} catch (UnknownObjectException e) {
							System.err.println("Value not found for " + decisionVariableName + "[" + width +"] "+" at time step: " + timeStep);
							decVarValues.add(timeStep, (double) -1);
							e.printStackTrace();
						} catch (IloException e) {
							System.err.println("Value not found for " + decisionVariableName + "[" + width +"] "+" at time step: " + timeStep);
							decVarValues.add(timeStep, (double) -1);
							e.printStackTrace();
						}
					}	
					decVarResults.setOptimizationResults(decVarValues);
					optimizationResults.add(decVarResults);
				}


			} else {
				// other variables defined as [width][timestep]
				for (int width = 0; width < decisionVariable.length; width++) {
					OptimizationResults decVarResults = new OptimizationResults();
					decVarResults.setVariableName(decisionVariableName + "-" + Integer.toString(width));
					List<Double> decVarValues = new ArrayList<Double>();

					for (int timeStep = 0; timeStep < decisionVariable[0].length; timeStep++) {
						try {
							// state variables defined: statesIntArrayResource[timeStep][state] 
							decVarValues.add(timeStep, DesignPatterns.getCplex().getValue(decisionVariable[width][timeStep]));
						} catch (UnknownObjectException e) {
							System.err.println("Value not found for " + decisionVariableName + "[" + width +"] "+" at time step: " + timeStep);
							decVarValues.add(timeStep, (double) -1);
							e.printStackTrace();
						} catch (IloException e) {
							System.err.println("Value not found for " + decisionVariableName + "[" + width +"] "+" at time step: " + timeStep);
							decVarValues.add(timeStep, (double) -1);
							e.printStackTrace();
						}
						decVarResults.setOptimizationResults(decVarValues);
						optimizationResults.add(decVarResults);
					}	
				}
			}

		}
		return optimizationResults;
	}

	/**
	 * Write results to file.
	 *
	 * @param optimizationResults the optimization results
	 * @param fileName the file name
	 * @param filePath the file path
	 */
	public static void writeResultsFromListToFile (List<OptimizationResults> optimizationResults, String fileName, String filePath) {
		// Get the current date and time
		LocalDateTime currentDateTime = LocalDateTime.now();
		// Define the desired date and time format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
		// Format the current date and time using the formatter
		String formattedDateTime = currentDateTime.format(formatter);


		//TODO check if method works as intended!
		double contentToWrite; 
		try {
			FileWriter myWriter = new FileWriter(filePath+fileName+"_"+formattedDateTime+".csv");
			String header = "timeStamp"; 
			for (int i = 0; i < optimizationResults.size(); i++) {
				header = header+","+ optimizationResults.get(i).getVariableName();
			}
			myWriter.write(header);
			myWriter.write("\n");
			for (int timeStep = 0; timeStep < DesignPatterns.getArrayLength(); timeStep++) {
				myWriter.write(Double.toString(timeStep).replace(".", ","));
				for(int resultsCounter = 0; resultsCounter < optimizationResults.size(); resultsCounter++) {
					myWriter.write(";"); // Use semicolon as separator
					//myWriter.write(Double.toString(contentToWrite[i][j]));
					contentToWrite = optimizationResults.get(resultsCounter).getOptimizationResults().get(timeStep);
					myWriter.write(Double.toString(contentToWrite).replace(".", ",")); // Replace decimal point with comma
				}
				myWriter.write("\n");
			}
			myWriter.close();
			System.out.println("Successfully wrote data to the file "+ fileName+".csv.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the globalSystemParameters
	 */
	public static SystemParameters getGlobalSystemParameters() {
		return globalSystemParameters;
	}

	/**
	 * @param globalSystemParameters the globalSystemParameters to set
	 */
	public static void setGlobalSystemParameters(SystemParameters globalSystemParameters) {
		OptimizationModel.globalSystemParameters = globalSystemParameters;
	}

	private static double[] importTSD(String filePath) {
		List<Double> dataList = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				// Assuming the CSV contains only one column of numerical values
				double value = Double.parseDouble(line.trim());
				dataList.add(value);
			}
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}

		// Convert List<Double> to double[]
		double[] dataArray = new double[dataList.size()];
		for (int i = 0; i < dataList.size(); i++) {
			dataArray[i] = dataList.get(i);
		}

		return dataArray;
	}

	/**
	 * Convert list to array.
	 *
	 * @param listOfArrays the list of arrays
	 * @return the ilo num var[][]
	 */
	public static IloNumVar[][] convertListToArray(List<IloNumVar[]> listOfArrays) {
		int size = listOfArrays.size();
		IloNumVar[][] resultArray = new IloNumVar[size][];

		for (int i = 0; i < size; i++) {
			resultArray[i] = listOfArrays.get(i);
		}

		return resultArray;
	}

}
