import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CsvFileReader {
	
	public static final String FEATURE_SEPARATOR = ",";;
	
	/**
	 * @param filePath
	 * @return a set of lists. Each list will have comma separated character variables.
	 * @throws FileNotFoundException
	 */
	public static Set<List<Character>> getCsvFileContents(String filePath) throws IOException {
		
		Set<List<Character>> csvFileContents = new HashSet<List<Character>>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
			String fileLine = bufferedReader.readLine();
			String[] rawFeatureVector = null;
			while (fileLine != null) {
				
				rawFeatureVector = fileLine.split(FEATURE_SEPARATOR);
				List<Character> featureVector = new ArrayList<Character>();
				for (String feature : rawFeatureVector) {
					featureVector.add(feature.trim().charAt(0));
				}

				csvFileContents.add(featureVector);
				
				fileLine = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (IOException e) {
			throw e;
		}

		return csvFileContents;
		
	}

}
