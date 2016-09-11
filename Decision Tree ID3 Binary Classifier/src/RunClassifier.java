import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class RunClassifier {
	
	public static final String MUSHROOM_FEATURES_PROPERTIES_FILE = "MushroomFeatures.properties";
	public static final String POKEMON_GO_FEATURES_PROPERTIES_FILE = "PokemonGo.properties";
	public static final String POSITIVE_LABEL_PROPERTY = "PositiveLabel";
	public static final String NEGATIVE_LABEL_PROPERTY = "NegativeLabel";
	
	public static final String TRAINING_DATA_A_FILE = "datasets/SettingA/training.data";
	public static final String TESTING_DATA_A_FILE = "datasets/SettingA/test.data";
	public static final String POKEMON_TRAINING_DATA_FILE = "datasets/PokemonGo/Train.data";
	public static final String POKEMON_TESTING_DATA_FILE = "datasets/PokemonGo/Test.data";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			//Get training and testing data
			List<List<Character>> trainingData = CsvFileReader.getCsvFileContents(TRAINING_DATA_A_FILE);
			List<List<Character>> testingData = CsvFileReader.getCsvFileContents(TESTING_DATA_A_FILE);
			
			//Train the classifier
			DecisionTreeId3BinaryClassifier classifier = new DecisionTreeId3BinaryClassifier(MUSHROOM_FEATURES_PROPERTIES_FILE);
			classifier.train(trainingData);
			
			//Run the prediction
			List<Character> prediction = classifier.predict(testingData);
			
			//Get positive and negative labels from properties file
			Properties featureProperties = new Properties();
			InputStream inputStream = new FileInputStream(MUSHROOM_FEATURES_PROPERTIES_FILE);
			featureProperties.load(inputStream);

			
			
			
			//Find prediction accuracy
			ClassifierMetrics classifierMetrics = new ClassifierMetrics(testingData, prediction, featureProperties.getProperty(POSITIVE_LABEL_PROPERTY).charAt(0), featureProperties.getProperty(NEGATIVE_LABEL_PROPERTY).charAt(0));
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
