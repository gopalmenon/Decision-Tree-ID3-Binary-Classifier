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

	public static final String TRAINING_DATA_B_FILE = "datasets/SettingB/training.data";
	public static final String TESTING_DATA_B_FILE = "datasets/SettingB/test.data";
	public static final String TRAINING_DATA_B_00_FILE = "datasets/SettingB/CVSplits/training_00.data";
	public static final String TRAINING_DATA_B_01_FILE = "datasets/SettingB/CVSplits/training_01.data";
	public static final String TRAINING_DATA_B_02_FILE = "datasets/SettingB/CVSplits/training_02.data";
	public static final String TRAINING_DATA_B_03_FILE = "datasets/SettingB/CVSplits/training_03.data";
	public static final String TRAINING_DATA_B_04_FILE = "datasets/SettingB/CVSplits/training_04.data";
	public static final String TRAINING_DATA_B_05_FILE = "datasets/SettingB/CVSplits/training_05.data";	
	public static final String[] TRAINING_DATA_B_SPLITS = {TRAINING_DATA_B_00_FILE, TRAINING_DATA_B_01_FILE, TRAINING_DATA_B_02_FILE,
														   TRAINING_DATA_B_03_FILE, TRAINING_DATA_B_04_FILE, TRAINING_DATA_B_05_FILE};
	
	public static final String TRAINING_DATA_C_FILE = "datasets/SettingC/training.data";
	public static final String TESTING_DATA_C_FILE = "datasets/SettingC/test.data";
	public static final String TRAINING_DATA_C_00_FILE = "datasets/SettingC/CVSplits/training_00.data";
	public static final String TRAINING_DATA_C_01_FILE = "datasets/SettingC/CVSplits/training_01.data";
	public static final String TRAINING_DATA_C_02_FILE = "datasets/SettingC/CVSplits/training_02.data";
	public static final String TRAINING_DATA_C_03_FILE = "datasets/SettingC/CVSplits/training_03.data";
	public static final String TRAINING_DATA_C_04_FILE = "datasets/SettingC/CVSplits/training_04.data";
	public static final String TRAINING_DATA_C_05_FILE = "datasets/SettingC/CVSplits/training_05.data";	
	public static final String[] TRAINING_DATA_C_SPLITS = {TRAINING_DATA_C_00_FILE, TRAINING_DATA_C_01_FILE, TRAINING_DATA_C_02_FILE,
														   TRAINING_DATA_C_03_FILE, TRAINING_DATA_C_04_FILE, TRAINING_DATA_C_05_FILE};

	public static final String POKEMON_TRAINING_DATA_FILE = "datasets/PokemonGo/Train.data";
	public static final String POKEMON_TESTING_DATA_FILE = "datasets/PokemonGo/Test.data";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		runSettingA1();
		runSettingA2();
		runSettingB1();
		runSettingB2();
		runSettingC();
		
	}
	
	/**
	 * Run Setting A1 steps
	 */
	private static void runSettingA1() {

		System.out.println("Setting A1");
		
		try {

			//Get training and testing data
			List<List<Character>> trainingData = CsvFileReader.getCsvFileContents(TRAINING_DATA_A_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			List<List<Character>> testingData = CsvFileReader.getCsvFileContents(TESTING_DATA_A_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			
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
	 * Run Setting A2 steps
	 */
	private static void runSettingA2() {
		
		System.out.println("\nSetting A2");
		
		try{
		
			//Get training and testing data
			List<List<Character>> trainingData = CsvFileReader.getCsvFileContents(TRAINING_DATA_A_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			List<List<Character>> testingData = CsvFileReader.getCsvFileContents(TESTING_DATA_A_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			runCrossValidation(TRAINING_DATA_A_SPLITS, trainingData, testingData);
		
		} catch (IOException e) {
			System.err.println("Error reading file.");
			e.printStackTrace();
		}
		
	}


	/**
	 * Run Setting B2 steps
	 */
	private static void runSettingB2() {
		
		System.out.println("\nSetting B2");
		
		try{
		
			//Get training and testing data
			List<List<Character>> trainingData = CsvFileReader.getCsvFileContents(TRAINING_DATA_B_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			List<List<Character>> testingData = CsvFileReader.getCsvFileContents(TESTING_DATA_B_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			runCrossValidation(TRAINING_DATA_B_SPLITS, trainingData, testingData);
		
		} catch (IOException e) {
			System.err.println("Error reading file.");
			e.printStackTrace();
		}
		
	}

	/**
	 * Run cross validation
	 * 
	 * @param splitDataToUse
	 * @param trainingData
	 * @param testingData
	 * @return 
	 */
	private static double runCrossValidation(String[] splitDataToUse, List<List<Character>> trainingData, List<List<Character>> testingData) {

		int[] maxDepthSettings = {1,2,3,4,5,10,15,20};
		double averageAccuracy = 0.0, maximumAccuracy = Double.MIN_VALUE;
		int bestDepthSetting = 0;
		
		for (int maximumDepth : maxDepthSettings) {
			averageAccuracy = runKFoldXValidation(splitDataToUse, maximumDepth);
			if (averageAccuracy > maximumAccuracy) {
				maximumAccuracy = averageAccuracy;
				bestDepthSetting = maximumDepth;
			}
		}
		
		System.out.println("\nBest depth setting: " + bestDepthSetting);
	
		//Train the classifier
		DecisionTreeId3BinaryClassifier classifier = new DecisionTreeId3BinaryClassifier(MUSHROOM_FEATURES_PROPERTIES_FILE, bestDepthSetting);
		classifier.train(trainingData);

		//Report on the tree depth
		System.out.println("Maximum tree depth: " + classifier.getMaximumTreeDepth());
		
		//Run the prediction
		List<Character> prediction = classifier.predict(testingData);
		
		//Get positive and negative labels from properties file
		Properties featureProperties = new Properties();
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(MUSHROOM_FEATURES_PROPERTIES_FILE);
			featureProperties.load(inputStream);

		} catch (IOException e) {
			e.printStackTrace();
		}

		//Find prediction accuracy
		ClassifierMetrics classifierMetrics = new ClassifierMetrics(testingData, prediction, featureProperties.getProperty(POSITIVE_LABEL_PROPERTY).charAt(0), featureProperties.getProperty(NEGATIVE_LABEL_PROPERTY).charAt(0));
		System.out.println("Precision: " + classifierMetrics.getPrecision());
		System.out.println("Recall: " + classifierMetrics.getRecall());
		System.out.println("Accuracy: " + classifierMetrics.getAccuracy());
		System.out.println("F1 Score: " + classifierMetrics.getF1Score());
		
		return classifierMetrics.getAccuracy();

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
				
				testingData = CsvFileReader.getCsvFileContents(datafiles[testDataCounter], CsvFileReader.IGNORE_MISSING_FEATURE);
				for (int trainingDataCounter = 0; trainingDataCounter < datafiles.length; ++trainingDataCounter) {
					
					if (trainingDataCounter != testDataCounter) {
						tempData = CsvFileReader.getCsvFileContents(datafiles[trainingDataCounter], CsvFileReader.IGNORE_MISSING_FEATURE);
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
	

	/**
	 * Run Setting B1 steps
	 */
	private static void runSettingB1() {

		System.out.println("\nSetting B1");
		
		try {

			//Get training and testing data
			List<List<Character>> trainingDataB = CsvFileReader.getCsvFileContents(TRAINING_DATA_B_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			List<List<Character>> testingDataB = CsvFileReader.getCsvFileContents(TESTING_DATA_B_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			List<List<Character>> trainingDataA = CsvFileReader.getCsvFileContents(TRAINING_DATA_A_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			List<List<Character>> testingDataA = CsvFileReader.getCsvFileContents(TESTING_DATA_A_FILE, CsvFileReader.IGNORE_MISSING_FEATURE);
			
			//Train the classifier
			DecisionTreeId3BinaryClassifier classifier = new DecisionTreeId3BinaryClassifier(MUSHROOM_FEATURES_PROPERTIES_FILE, Integer.MAX_VALUE);
			classifier.train(trainingDataB);

			//Report on the tree depth
			System.out.println("Maximum tree depth: " + classifier.getMaximumTreeDepth());
			
			//Get positive and negative labels from properties file
			Properties featureProperties = new Properties();
			InputStream inputStream = new FileInputStream(MUSHROOM_FEATURES_PROPERTIES_FILE);
			featureProperties.load(inputStream);
			
			//Run the prediction for Testing Data B
			List<Character> prediction = classifier.predict(testingDataB);
			
			//Find prediction accuracy
			ClassifierMetrics classifierMetrics = new ClassifierMetrics(testingDataB, prediction, featureProperties.getProperty(POSITIVE_LABEL_PROPERTY).charAt(0), featureProperties.getProperty(NEGATIVE_LABEL_PROPERTY).charAt(0));
			System.out.println("Precision with Testing Data B: " + classifierMetrics.getPrecision());
			System.out.println("Recall with Testing Data B: " + classifierMetrics.getRecall());
			System.out.println("Accuracy with Testing Data B: " + classifierMetrics.getAccuracy());
			System.out.println("F1 Score with Testing Data B: " + classifierMetrics.getF1Score());
			
			//Run the prediction for Training Data A
			prediction = classifier.predict(trainingDataA);
			
			//Find prediction accuracy
			classifierMetrics = new ClassifierMetrics(testingDataB, prediction, featureProperties.getProperty(POSITIVE_LABEL_PROPERTY).charAt(0), featureProperties.getProperty(NEGATIVE_LABEL_PROPERTY).charAt(0));
			System.out.println("Precision with Training Data A: " + classifierMetrics.getPrecision());
			System.out.println("Recall with Training Data A: " + classifierMetrics.getRecall());
			System.out.println("Accuracy with Training Data A: " + classifierMetrics.getAccuracy());
			System.out.println("F1 Score with Training Data A: " + classifierMetrics.getF1Score());
			
			//Run the prediction for Testing Data A
			prediction = classifier.predict(testingDataA);
			
			//Find prediction accuracy
			classifierMetrics = new ClassifierMetrics(testingDataB, prediction, featureProperties.getProperty(POSITIVE_LABEL_PROPERTY).charAt(0), featureProperties.getProperty(NEGATIVE_LABEL_PROPERTY).charAt(0));
			System.out.println("Precision with Testing Data A: " + classifierMetrics.getPrecision());
			System.out.println("Recall with Testing Data A: " + classifierMetrics.getRecall());
			System.out.println("Accuracy with Testing Data A: " + classifierMetrics.getAccuracy());
			System.out.println("F1 Score with Testing Data A: " + classifierMetrics.getF1Score());
			
			
		} catch (IOException e) {
			System.err.println("Error reading file.");
			e.printStackTrace();
		}		
		
		
	}

	/**
	 * RUN SETTING C
	 */
	private static void runSettingC() {
		
		System.out.println("\nSetting C");
		
		try{
		
			//Get training and testing data
			System.out.println("\nSetting C - Method 1");
			List<List<Character>> trainingData1 = CsvFileReader.getCsvFileContents(TRAINING_DATA_C_FILE, CsvFileReader.SET_MISSING_FEATURE_TO_MAJORITY_FEATURE_VALUE);
			List<List<Character>> testingData1 = CsvFileReader.getCsvFileContents(TESTING_DATA_C_FILE, CsvFileReader.SET_MISSING_FEATURE_TO_MAJORITY_FEATURE_VALUE);
			double method1Accuracy = runCrossValidation(TRAINING_DATA_C_SPLITS, trainingData1, testingData1);
			System.out.println("Method 1 accuracy is " + method1Accuracy);
			
			//Get training and testing data
			System.out.println("\nSetting C - Method 2");
			List<List<Character>> trainingData2 = CsvFileReader.getCsvFileContents(TRAINING_DATA_C_FILE, CsvFileReader.SET_MISSING_FEATURE_TO_MAJORITY_LABEL_VALUE);
			List<List<Character>> testingData2 = CsvFileReader.getCsvFileContents(TESTING_DATA_C_FILE, CsvFileReader.SET_MISSING_FEATURE_TO_MAJORITY_LABEL_VALUE);
			double method2Accuracy = runCrossValidation(TRAINING_DATA_C_SPLITS, trainingData2, testingData2);
			System.out.println("Method 2 accuracy is " + method2Accuracy);
			
			//Get training and testing data
			System.out.println("\nSetting C - Method 3");
			List<List<Character>> trainingData3 = CsvFileReader.getCsvFileContents(TRAINING_DATA_C_FILE, CsvFileReader.SET_MISSING_FEATURE_AS_SPECIAL_FEATURE);
			List<List<Character>> testingData3 = CsvFileReader.getCsvFileContents(TESTING_DATA_C_FILE, CsvFileReader.SET_MISSING_FEATURE_AS_SPECIAL_FEATURE);
			double method3Accuracy = runCrossValidation(TRAINING_DATA_C_SPLITS, trainingData3, testingData3);
			System.out.println("Method 3 accuracy is " + method3Accuracy);
			
			//Create the classifier
			DecisionTreeId3BinaryClassifier classifier = new DecisionTreeId3BinaryClassifier(MUSHROOM_FEATURES_PROPERTIES_FILE, Integer.MAX_VALUE);
			double maximumAccuracy = Math.max(Math.max(method1Accuracy, method2Accuracy), method3Accuracy);

			//Get positive and negative labels from properties file
			Properties featureProperties = new Properties();
			InputStream inputStream = new FileInputStream(MUSHROOM_FEATURES_PROPERTIES_FILE);
			featureProperties.load(inputStream);
			ClassifierMetrics classifierMetrics = null;
			
			if (maximumAccuracy == method1Accuracy) {

				System.out.println("\nSetting C - Method 1 has maximum accuracy");
				classifier.train(trainingData1);
				//Report on the tree depth
				System.out.println("Maximum tree depth: " + classifier.getMaximumTreeDepth());
				//Run the prediction
				List<Character> prediction = classifier.predict(testingData1);
				//Find prediction accuracy
				classifierMetrics = new ClassifierMetrics(testingData1, prediction, featureProperties.getProperty(POSITIVE_LABEL_PROPERTY).charAt(0), featureProperties.getProperty(NEGATIVE_LABEL_PROPERTY).charAt(0));

			} else if (maximumAccuracy == method2Accuracy) {
				System.out.println("\nSetting C - Method 2 has maximum accuracy");
				classifier.train(trainingData2);
				//Report on the tree depth
				System.out.println("Maximum tree depth: " + classifier.getMaximumTreeDepth());
				//Run the prediction
				List<Character> prediction = classifier.predict(testingData2);
				//Find prediction accuracy
				classifierMetrics = new ClassifierMetrics(testingData2, prediction, featureProperties.getProperty(POSITIVE_LABEL_PROPERTY).charAt(0), featureProperties.getProperty(NEGATIVE_LABEL_PROPERTY).charAt(0));
				
			} else if (maximumAccuracy == method3Accuracy) {
				System.out.println("\nSetting C - Method 3 has maximum accuracy");
				classifier.train(trainingData3);
				//Report on the tree depth
				System.out.println("Maximum tree depth: " + classifier.getMaximumTreeDepth());
				//Run the prediction
				List<Character> prediction = classifier.predict(testingData3);
				//Find prediction accuracy
				classifierMetrics = new ClassifierMetrics(testingData3, prediction, featureProperties.getProperty(POSITIVE_LABEL_PROPERTY).charAt(0), featureProperties.getProperty(NEGATIVE_LABEL_PROPERTY).charAt(0));

			}

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
