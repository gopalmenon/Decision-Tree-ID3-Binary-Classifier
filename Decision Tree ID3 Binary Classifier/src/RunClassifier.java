import java.io.IOException;
import java.util.List;

public class RunClassifier {

	/**
	 * @param args
	 */
	
	public static final String TRAINING_DATA_A_FILE = "datasets/SettingA/training.data";
	public static final String TESTING_DATA_A_FILE = "datasets/SettingA/test.data";
	public static final String POKEMON_TRAINING_DATA_FILE = "datasets/PokemonGo/Train.data";
	public static final String POKEMON_TESTING_DATA_FILE = "datasets/PokemonGo/Test.data";
	
	public static void main(String[] args) {

		try {

			//Get training and testing data
			List<List<Character>> trainingData = CsvFileReader.getCsvFileContents(POKEMON_TRAINING_DATA_FILE);
			List<List<Character>> testingData = CsvFileReader.getCsvFileContents(POKEMON_TESTING_DATA_FILE);
			
			//Train the classifier
			DecisionTreeId3BinaryClassifier classifier = new DecisionTreeId3BinaryClassifier();
			classifier.train(trainingData);
			
			//Run the prediction
			List<Character> prediction = classifier.predict(testingData);
			
			//Find prediction accuracy
			ClassifierMetrics classifierMetrics = new ClassifierMetrics(testingData, prediction, 'y', 'n');
			System.out.println("Precision: " + classifierMetrics.getPrecision());
			System.out.println("Recall: " + classifierMetrics.getRecall());
			System.out.println("Accuracy: " + classifierMetrics.getAccuracy());
			System.out.println("F1 Score: " + classifierMetrics.getF1Score());
			
			
		} catch (IOException e) {
			System.err.println("Error reading file.");
			e.printStackTrace();
		}
		
	}

}
