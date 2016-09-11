import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RunClassifier {
	
	public static final String MUSHROOM_FEATURES_PROPERTIES_FILE = "MushroomFeatures.properties";
	public static final String POKEMON_GO_FEATURES_PROPERTIES_FILE = "PokemonGo.properties";
	public static final String POSITIVE_LABEL_PROPERTY = "PositiveLabel";
	public static final String NEGATIVE_LABEL_PROPERTY = "NegativeLabel";
	
	public static final String TRAINING_DATA_A_FILE = "datasets/SettingA/training.data";
	public static final String TESTING_DATA_A_FILE = "datasets/SettingA/test.data";
	public static final String TRAINING_DATA_A_00_FILE = "datasets/SettingA/CVSplits/training_00.data";
	public static final String TRAINING_DATA_A_01_FILE = "datasets/SettingA/CVSplits/training_01.data";
	public static final String TRAINING_DATA_A_02_FILE = "datasets/SettingA/CVSplits/training_02.data";
	public static final String TRAINING_DATA_A_03_FILE = "datasets/SettingA/CVSplits/training_03.data";
	public static final String TRAINING_DATA_A_04_FILE = "datasets/SettingA/CVSplits/training_04.data";
	public static final String TRAINING_DATA_A_05_FILE = "datasets/SettingA/CVSplits/training_05.data";
	
	public static final String[] TRAINING_DATA_A_SPLITS = {TRAINING_DATA_A_00_FILE, TRAINING_DATA_A_01_FILE, TRAINING_DATA_A_02_FILE,
														   TRAINING_DATA_A_03_FILE, TRAINING_DATA_A_04_FILE, TRAINING_DATA_A_05_FILE};
	
	public static final String POKEMON_TRAINING_DATA_FILE = "datasets/PokemonGo/Train.data";
	public static final String POKEMON_TESTING_DATA_FILE = "datasets/PokemonGo/Test.data";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		runSettingA();
		runSettingB();
		
	}
	
	/**
	 * Run Setting A steps
	 */
	private static void runSettingA() {

		System.out.println("Setting A");
		
		try {

			//Get training and testing data
			List<List<Character>> trainingData = CsvFileReader.getCsvFileContents(TRAINING_DATA_A_FILE);
			List<List<Character>> testingData = CsvFileReader.getCsvFileContents(TESTING_DATA_A_FILE);
			
			//Train the classifier
			DecisionTreeId3BinaryClassifier classifier = new DecisionTreeId3BinaryClassifier(MUSHROOM_FEATURES_PROPERTIES_FILE, Integer.MAX_VALUE);
			classifier.train(trainingData);

			//Report on the tree depth
			System.out.println("Maximum tree depth: " + classifier.getMaximumTreeDepth());
			
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

	/**
	 * Run Setting B steps
	 */
	private static void runSettingB() {
		
		System.out.println("\nSetting B");

		int[] maxDepthSettings = {1,2,3,4,5,10,15,20};
		double averageAccuracy = 0.0, maximumAccuracy = Double.MIN_VALUE;
		int bestDepthSetting = 0;
		
		for (int maximumDepth : maxDepthSettings) {
			averageAccuracy = runKFoldXValidation(TRAINING_DATA_A_SPLITS, maximumDepth);
			if (averageAccuracy > maximumAccuracy) {
				maximumAccuracy = averageAccuracy;
				bestDepthSetting = maximumDepth;
			}
		}
		
		System.out.println("\nBest depth setting: " + bestDepthSetting);
		try{
			//Get training and testing data
			List<List<Character>> trainingData = CsvFileReader.getCsvFileContents(TRAINING_DATA_A_FILE);
			List<List<Character>> testingData = CsvFileReader.getCsvFileContents(TESTING_DATA_A_FILE);
			
			//Train the classifier
			DecisionTreeId3BinaryClassifier classifier = new DecisionTreeId3BinaryClassifier(MUSHROOM_FEATURES_PROPERTIES_FILE, bestDepthSetting);
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
	
	/**
	 * Run k-fold cross validation
	 * @param datafiles
	 * @return mean accuracy value
	 */
	private static double runKFoldXValidation(String[] datafiles, int maximumDepth) {
		
		List<List<Character>> trainingData = new ArrayList<List<Character>>(), testingData = null, tempData = null;
		List<Double> accuracyMeasures = new ArrayList<Double>();
		
		//Report on the tree depth setting
		System.out.println("\nMaximum tree depth setting: " + maximumDepth);

		try{
		
			//Vary the file used as test data. Use the rest as training data.
			for (int testDataCounter = 0; testDataCounter < datafiles.length; ++testDataCounter) {
				
				testingData = CsvFileReader.getCsvFileContents(datafiles[testDataCounter]);
				for (int trainingDataCounter = 0; trainingDataCounter < datafiles.length; ++trainingDataCounter) {
					
					if (trainingDataCounter != testDataCounter) {
						tempData = CsvFileReader.getCsvFileContents(datafiles[trainingDataCounter]);
						trainingData = mergeData(trainingData, tempData);
					}
					
				}
				
				//Train the classifier
				DecisionTreeId3BinaryClassifier classifier = new DecisionTreeId3BinaryClassifier(MUSHROOM_FEATURES_PROPERTIES_FILE, maximumDepth);
				classifier.train(trainingData);
				
				//Run the prediction
				List<Character> prediction = classifier.predict(testingData);
				
				//Get positive and negative labels from properties file
				Properties featureProperties = new Properties();
				InputStream inputStream = new FileInputStream(MUSHROOM_FEATURES_PROPERTIES_FILE);
				featureProperties.load(inputStream);
				
				//Find prediction accuracy
				ClassifierMetrics classifierMetrics = new ClassifierMetrics(testingData, prediction, featureProperties.getProperty(POSITIVE_LABEL_PROPERTY).charAt(0), featureProperties.getProperty(NEGATIVE_LABEL_PROPERTY).charAt(0));
				System.out.println("Accuracy: " + classifierMetrics.getAccuracy());
				
				accuracyMeasures.add(classifierMetrics.getAccuracy());
				
			}
			
		} catch (IOException e) {
			System.err.println("Error reading file.");
			e.printStackTrace();
		}	
		
		return getAverageAccuracy(accuracyMeasures);
		
	}
	
	/**
	 * @param data1
	 * @param data2
	 * @return combined collection of data
	 */
	private static List<List<Character>> mergeData(List<List<Character>> data1, List<List<Character>> data2) {
		
		List<List<Character>> mergedData = new ArrayList<List<Character>>();
		mergedData.addAll(data1);
		mergedData.addAll(data2);
		return mergedData;
	}
	
	/**
	 * Print out mean and standard deviation
	 * @return mean accuracy value
	 */
	private static double getAverageAccuracy(List<Double> accuracyMeasures) {
		
		double mean = 0.0, standardDeviation = 0.0;
		for (Double accuracy : accuracyMeasures) {
			mean += accuracy.doubleValue();
		}
		mean /= accuracyMeasures.size();
		
		System.out.println("Mean: " + mean);
		
		for (Double accuracy : accuracyMeasures) {
			standardDeviation += Math.pow(mean - accuracy, 2.0);
		}
		standardDeviation /= accuracyMeasures.size();
		standardDeviation = Math.sqrt(standardDeviation);
		System.out.println("Standard Deviation: " + standardDeviation);
		
		return mean;
	}

}
