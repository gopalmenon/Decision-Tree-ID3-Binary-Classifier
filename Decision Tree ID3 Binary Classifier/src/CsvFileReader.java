import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class CsvFileReader {
	
	public static final String FEATURE_SEPARATOR = ",";
	public static final char MISSING_FEATURE = '?';
	
	public static final int IGNORE_MISSING_FEATURE = 0;
	public static final int SET_MISSING_FEATURE_TO_MAJORITY_FEATURE_VALUE = 1;
	public static final int SET_MISSING_FEATURE_TO_MAJORITY_LABEL_VALUE = 2;
	public static final int SET_MISSING_FEATURE_AS_SPECIAL_FEATURE = 3;
	
	/**
	 * @param filePath
	 * @return a set of lists. Each list will have comma separated character variables.
	 * @throws FileNotFoundException
	 */
	public static List<List<Character>> getCsvFileContents(String filePath, int missingFeatureProcessing) throws IOException {
		
		List<List<Character>> csvFileContents = new ArrayList<List<Character>>();
		BufferedReader bufferedReader = null;
		List<Boolean> missingFeatureVector = new ArrayList<Boolean>();
		
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
			String fileLine = bufferedReader.readLine();
			String[] rawFeatureVector = null;
			int featureCounter = 0;
			boolean firstLine = true;
			
			while (fileLine != null) {
				
				rawFeatureVector = fileLine.split(FEATURE_SEPARATOR);
				List<Character> featureVector = new ArrayList<Character>();
				for (String feature : rawFeatureVector) {
					featureVector.add(feature.trim().charAt(0));
					if ((missingFeatureProcessing == SET_MISSING_FEATURE_TO_MAJORITY_FEATURE_VALUE || 
						 missingFeatureProcessing == SET_MISSING_FEATURE_TO_MAJORITY_LABEL_VALUE) &&
						 firstLine) {
						missingFeatureVector.add(false);
					}
					if ((missingFeatureProcessing == SET_MISSING_FEATURE_TO_MAJORITY_FEATURE_VALUE || 
					     missingFeatureProcessing == SET_MISSING_FEATURE_TO_MAJORITY_LABEL_VALUE) && 
						 feature.trim().charAt(0) == MISSING_FEATURE) {
						missingFeatureVector.set(featureCounter, Boolean.valueOf(true));
					}
					++featureCounter;
				}
				
				firstLine = false;
				csvFileContents.add(featureVector);
				
				fileLine = bufferedReader.readLine();
				featureCounter = 0;
			}
			bufferedReader.close();
		} catch (IOException e) {
			throw e;
		}

		if (missingFeatureProcessing == SET_MISSING_FEATURE_TO_MAJORITY_FEATURE_VALUE || 
			missingFeatureProcessing == SET_MISSING_FEATURE_TO_MAJORITY_LABEL_VALUE) {
			return getCleanedUpFeatures(csvFileContents, missingFeatureProcessing, missingFeatureVector);
		} else {
			return csvFileContents;
		}
		
	}
	
	/**
	 * @param features
	 * @param missingFeatureProcessing
	 * @param missingFeatureVector
	 * @return cleaned up features
	 */
	private static List<List<Character>> getCleanedUpFeatures(List<List<Character>> features, int missingFeatureProcessing, List<Boolean> missingFeatureVector) {
		
		try {
			
			int featureCounter = 0;
			Map<Integer, Character> majorityFeatureValues = new HashMap<Integer, Character>();
			Map<Integer, Character> majorityPositiveLabelValues = new HashMap<Integer, Character>();
			Map<Integer, Character> majorityNegativeLabelValues = new HashMap<Integer, Character>();
			
			//Get positive and negative labels from properties file
			Properties featureProperties = new Properties();
			InputStream inputStream;
			inputStream = new FileInputStream(RunClassifier.MUSHROOM_FEATURES_PROPERTIES_FILE);
			featureProperties.load(inputStream);
			char positiveLabel = featureProperties.getProperty(RunClassifier.POSITIVE_LABEL_PROPERTY).charAt(0);
			char negativeLabel = featureProperties.getProperty(RunClassifier.NEGATIVE_LABEL_PROPERTY).charAt(0);
		
			//Find substitutes for missing values
			for (Boolean isFeatureMissing : missingFeatureVector) {
				
				if (isFeatureMissing.booleanValue()) {
					
					if (missingFeatureProcessing == SET_MISSING_FEATURE_TO_MAJORITY_FEATURE_VALUE) {
						majorityFeatureValues.put(Integer.valueOf(featureCounter), getMajorityFeatureValue(features, featureCounter));
					} else {
						majorityPositiveLabelValues.put(Integer.valueOf(featureCounter), getMajorityLabelValue(features, featureCounter, positiveLabel));
						majorityNegativeLabelValues.put(Integer.valueOf(featureCounter), getMajorityLabelValue(features, featureCounter, negativeLabel));
					}
					
				}
				++featureCounter;
			}
			
			//Replace missing values with substitutes
			for (List<Character> dataRecord : features) {
				
				featureCounter = 0;
				for (Boolean isFeatureMissing : missingFeatureVector) {
					
					if (isFeatureMissing.booleanValue() && dataRecord.get(featureCounter).charValue() == MISSING_FEATURE) {
						
						if (missingFeatureProcessing == SET_MISSING_FEATURE_TO_MAJORITY_FEATURE_VALUE) {
							dataRecord.set(featureCounter, majorityFeatureValues.get(featureCounter).charValue());
						} else {
							if (dataRecord.get(dataRecord.size() - 1).charValue() == positiveLabel) {
								dataRecord.set(featureCounter, majorityPositiveLabelValues.get(featureCounter).charValue());
							} else {
								dataRecord.set(featureCounter, majorityNegativeLabelValues.get(featureCounter).charValue());
							}

						}
				
					}
					
					++featureCounter;
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return features;
	}
	
	/**
	 * @param features
	 * @param featureNumber
	 * @return the majority value for the feature
	 */
	private static char getMajorityFeatureValue(List<List<Character>> features, int featureNumber) {
		
		Map<Character, Integer> featureValueAccumulator = new HashMap<Character, Integer>();
		
		//Go through the feature column and accumulate feature value counts
		for (List<Character> dataRecord : features) {
			
			if (dataRecord.get(featureNumber) != MISSING_FEATURE) {
				if (featureValueAccumulator.containsKey(Character.valueOf(dataRecord.get(featureNumber)))) {
					featureValueAccumulator.put(Character.valueOf(dataRecord.get(featureNumber)), featureValueAccumulator.get(Character.valueOf(dataRecord.get(featureNumber))).intValue() + 1);
				} else {
					featureValueAccumulator.put(Character.valueOf(dataRecord.get(featureNumber)), Integer.valueOf(1));
				}
			}
			
			
		}
		
		//Find the feature value with the majority count
		Set<Character> featureValues = featureValueAccumulator.keySet();
		char majorityFeatureValue = ' ';
		int majorityCount = Integer.MIN_VALUE;
		
		for (Character featureValue : featureValues) {
			if (featureValueAccumulator.get(featureValue).intValue() > majorityCount) {
				majorityFeatureValue = featureValue.charValue();
				majorityCount = featureValueAccumulator.get(featureValue).intValue();
			}
		}
		
		//Return the majority feature value
		return majorityFeatureValue;
	}

	
	private static char getMajorityLabelValue(List<List<Character>> features, int featureNumber, char targetLabel) {
		
		Map<Character, Integer> featureValueAccumulator = new HashMap<Character, Integer>();
		
		//Go through the feature column and accumulate feature value counts
		for (List<Character> dataRecord : features) {
			
			if (dataRecord.get(featureNumber) != MISSING_FEATURE && dataRecord.get(dataRecord.size() - 1).charValue() == targetLabel) {

				if (featureValueAccumulator.containsKey(Character.valueOf(dataRecord.get(featureNumber)))) {
					featureValueAccumulator.put(Character.valueOf(dataRecord.get(featureNumber)), featureValueAccumulator.get(Character.valueOf(dataRecord.get(featureNumber))).intValue() + 1);
				} else {
					featureValueAccumulator.put(Character.valueOf(dataRecord.get(featureNumber)), Integer.valueOf(1));
				}
			}
			
		}
		
		//Find the feature value with the majority count
		Set<Character> featureValues = featureValueAccumulator.keySet();
		char majorityFeatureValue = ' ';
		int majorityCount = Integer.MIN_VALUE;
		
		for (Character featureValue : featureValues) {
			if (featureValueAccumulator.get(featureValue).intValue() > majorityCount) {
				majorityFeatureValue = featureValue.charValue();
				majorityCount = featureValueAccumulator.get(featureValue).intValue();
			}
		}
		
		//Return the majority feature value
		return majorityFeatureValue;
		
	}
	
}
