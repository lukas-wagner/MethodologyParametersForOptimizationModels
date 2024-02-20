package systemParameterExtraction;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import systemParameterModel.SystemParameters;

public class ReadParametersFromDataModel {
	static SystemParameters systemParameters = new SystemParameters();

	public static void main(String[] args) {

		String filePath = "src/systemParameterModel/systemParameters_2023-10-12_08-40-17.json";
		setSystemParameters(readJson(filePath));

	}

	/**
	 * Read json from filepath
	 *
	 * @param filePath the file path
	 * @return the SystemParameters
	 */
	public static SystemParameters readJson (String filePath) {
		SystemParameters sysPara = new SystemParameters();
		Gson gson = new Gson();	
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filePath);
			sysPara = gson.fromJson(fileReader, SystemParameters.class);
			return sysPara;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("File not found");
			return sysPara; 
		}
	}

	/**
	 * @return the systemParameters
	 */
	public static SystemParameters getSystemParameters() {
		return systemParameters;
	}

	/**
	 * @param systemParameters the systemParameters to set
	 */
	public static void setSystemParameters(SystemParameters systemParameters) {
		ReadParametersFromDataModel.systemParameters = systemParameters;
	}
}
