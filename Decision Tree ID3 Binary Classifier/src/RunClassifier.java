import java.io.IOException;
import java.util.List;
import java.util.Set;


public class RunClassifier {

	/**
	 * @param args
	 */
	
	public static final String TRAINING_DATA_A_FILE = "datasets/SettingA/test.data";
	
	public static void main(String[] args) {

		try {
			Set<List<Character>> data = CsvFileReader.getCsvFileContents(TRAINING_DATA_A_FILE);
			System.out.println(data);
		} catch (IOException e) {
			System.err.println("Error reading file " + TRAINING_DATA_A_FILE);
			e.printStackTrace();
		}
		
	}

}
