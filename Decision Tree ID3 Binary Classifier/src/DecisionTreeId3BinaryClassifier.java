import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

/**
 * Decision Tree classifier that uses the ID3 algorithm
 */
public class DecisionTreeId3BinaryClassifier implements Classifier {
	
	public static final String ATTRIBUTE_VALUE_SEPARATOR = ",";
	
	private String propertiesFileName;
	private DecisionTreeNode decisionTreeRootNode;
	private int labelOffset;
	private char firstLabel, secondLabel;
	private boolean secondLabelFound;
	private Random randomNumberGenerator;

	public DecisionTreeId3BinaryClassifier(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}
	/* (non-Javadoc)
	 * @see Classifier#train(java.util.List)
	 */
	@Override
	public void train(List<List<Character>> trainingData) {
		
		//Training data should be present and duplicates in training data are not allowed
		assert trainingData.size() > 0 && !isDuplicatesInTrainingData(trainingData);
		
		doBookKeeping(trainingData);
		
		this.decisionTreeRootNode = buildDecisionTree(trainingData, getAttributesVector(), ' ');

	}

	/* (non-Javadoc)
	 * @see Classifier#predict(java.util.List)
	 */
	@Override
	public List<Character> predict(List<List<Character>> testData) {

		List<Character> prediction = new ArrayList<Character>();
		for (List<Character> testDataRecord : testData) {
			prediction.add(Character.valueOf(getPrediction(testDataRecord)));
		}
		
		return prediction;
	}
	
	/**
	 * @param testDataRecord
	 * @return the prediction based on the decision tree constructed during the training phase
	 */
	private char getPrediction(List<Character> testDataRecord) {
		
		DecisionTreeNode decisionTreeNode = this.decisionTreeRootNode;
		int attributeSplitOn = 0;
		char attribute = ' ';
		outerLoop:
		while (decisionTreeNode instanceof DecisionTreeInternalNode) {
			
			attributeSplitOn = ((DecisionTreeInternalNode) decisionTreeNode).getAttributeSplitOn();
			attribute = testDataRecord.get(attributeSplitOn);
			
			for (DecisionTreeNode decisionTreeChildNode : ((DecisionTreeInternalNode) decisionTreeNode).getChildNodes()) {
				if (decisionTreeChildNode instanceof DecisionTreeInternalNode) {
					if (decisionTreeChildNode.getPreviousAttributeValue() == attribute) {
						decisionTreeNode = decisionTreeChildNode;
						break;
					}
				} else {
					if (decisionTreeChildNode.getPreviousAttributeValue() == attribute) {
						decisionTreeNode = decisionTreeChildNode;
						break outerLoop;
					}
				}
			}
			
			
		}
		
		return ((DecisionTreeLeafNode) decisionTreeNode).getLabel();
		
	}
	
	/**
	 * @param trainingData
	 * @return true of training data has duplicate rows
	 */
	private boolean isDuplicatesInTrainingData(List<List<Character>> trainingData) {
		
		Set<List<Character>> trainingDataSet = new HashSet<List<Character>>(trainingData);
		if (trainingDataSet.size() < trainingData.size()) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Do book keeping in order to store useful information for later use
	 * @param trainingData
	 */
	private void doBookKeeping(List<List<Character>> trainingData) {
		
		this.labelOffset = trainingData.get(0).size() - 1;
		this.firstLabel = trainingData.get(0).get(labelOffset);
		this.randomNumberGenerator = new Random(System.currentTimeMillis());
	}
	
	/**
	 * @param trainingData
	 * @return entropy of the collection of training data records
	 */
	private double getCollectionEntropy(List<List<Character>> trainingData) {
		
		Iterator<List<Character>> trainingDataIterator = trainingData.iterator();
		List<Character> trainingDataRecord = null;
		this.secondLabelFound = false;
		int firstLabelCount = 0, secondLabelCount = 0;
		
		while (trainingDataIterator.hasNext()) {
			
			trainingDataRecord = trainingDataIterator.next();
			if (!this.secondLabelFound && trainingDataRecord.get(this.labelOffset).charValue() != this.firstLabel) {
				this.secondLabel = trainingDataRecord.get(this.labelOffset).charValue();
				this.secondLabelFound = true;
			}
			
			if (trainingDataRecord.get(this.labelOffset).charValue() == this.firstLabel) {
				++firstLabelCount;
			} else {
				++secondLabelCount;	
			}
			
		}

		double totalCount = firstLabelCount + secondLabelCount;
		double firstLabelFraction = firstLabelCount/totalCount, secondLabelFraction = secondLabelCount/totalCount;
		return -1 * firstLabelFraction * logBase2(firstLabelFraction) - secondLabelFraction * logBase2(secondLabelFraction);

	}

	/**
	 * @param trainingData
	 * @param featureToPartitionOn
	 * @return the information gain or expected reduction in entropy by partitioning on an attribute 
	 */
	private double getInformationGain(List<List<Character>> trainingData, int featureToPartitionOn) {
		
		assert featureToPartitionOn < this.labelOffset;
		Map<Character, FeatureValueCounts> counts = new HashMap<Character, FeatureValueCounts>();
		Iterator<List<Character>> trainingDataIterator = trainingData.iterator();
		
		List<Character> trainingDataRecord = null;
		char trainingDataRecordLabel = ' ';
		Character featureValue = null;
		FeatureValueCounts existingFeatureValueCounts = null;
				
		while (trainingDataIterator.hasNext()) {
			
			trainingDataRecord = trainingDataIterator.next();
			featureValue = trainingDataRecord.get(featureToPartitionOn);
			trainingDataRecordLabel = trainingDataRecord.get(this.labelOffset).charValue();
			
			if (counts.containsKey(featureValue)) {
				existingFeatureValueCounts = counts.get(featureValue);
				if (trainingDataRecordLabel == this.firstLabel) {
					existingFeatureValueCounts.incrementFirstLabelCount();
				} else {
					existingFeatureValueCounts.incrementSecondLabelCount();
				}
				counts.put(featureValue, existingFeatureValueCounts);
			} else {
				if (trainingDataRecordLabel == this.firstLabel) {
					counts.put(featureValue, this.new FeatureValueCounts(1, 0));
				} else {
					counts.put(featureValue, this.new FeatureValueCounts(0, 1));
				}
			}
			
		}
		
		return getCollectionEntropy(trainingData) - getWeightedFeatureValuesEntropy(counts);
		
	}
	
	/**
	 * @param counts
	 * @return the weighted sum of the entropies of each feature value
	 */
	private double getWeightedFeatureValuesEntropy(Map<Character, FeatureValueCounts> counts) {
		
		Map<Character, Integer> featureCounts = new HashMap<Character, Integer>();
		int featureCount = 0, totalFeatureCount = 0;
		Set<Character> FeatureValueKeys = counts.keySet();
		
		//Find fraction values for features
		for (Character featureValue : FeatureValueKeys) {
			featureCount = counts.get(featureValue).getFirstLabelCount() + counts.get(featureValue).getSecondLabelCount();
			totalFeatureCount += featureCount;
			featureCounts.put(featureValue, Integer.valueOf(featureCount));
		}
		
		double weightedFeatureValuesEntropy = 0.0;
		for (Character featureValue : FeatureValueKeys) {
			weightedFeatureValuesEntropy += ((double) featureCounts.get(featureValue).intValue()/totalFeatureCount) * 
											getWeightedSubsetEntropy(counts.get(featureValue).getFirstLabelCount(), counts.get(featureValue).getSecondLabelCount());
		}
		
		return weightedFeatureValuesEntropy;
		
	}
	
	/**
	 * @param firstLabelCount
	 * @param secondLabelCount
	 * @return the weighted entropy of a specific value of an attribute
	 */
	private double getWeightedSubsetEntropy(int firstLabelCount, int secondLabelCount) {
		
		double firstLabelFraction = (double) firstLabelCount / (firstLabelCount + secondLabelCount);
		double secondLabelFraction = (double) secondLabelCount / (firstLabelCount + secondLabelCount);
		
		return -1 * firstLabelFraction * logBase2(firstLabelFraction) - secondLabelFraction * logBase2(secondLabelFraction);
		
	}
	
	/**
	 * Store label counts for each value of an attribute
	 *
	 */
	private class FeatureValueCounts {
		private int firstLabelCount;
		private int secondLabelCount;
		public FeatureValueCounts(int firstLabelCount, int secondLabelCount) {
			this.firstLabelCount = firstLabelCount;
			this.secondLabelCount = secondLabelCount;
		}
		public int getFirstLabelCount() {
			return firstLabelCount;
		}
		public void incrementFirstLabelCount() {
			++this.firstLabelCount;
		}
		public void incrementSecondLabelCount() {
			++this.secondLabelCount;
		}
		public int getSecondLabelCount() {
			return secondLabelCount;
		}
	}

	/**
	 * Build a decision tree to classify the training data based on the ID3 algorithm 
	 * @param trainingData
	 * @param attributesVector
	 * @return a root node for the tree (subtree for the recursive case)
	 */
	private DecisionTreeNode buildDecisionTree(List<List<Character>> examples, Set<Integer> attributesVector, char previousAttributeValue) {
		
		//If all examples have the same label, return a leaf node marked with the common label 
		AllExamples allExamples = isAllExamplesTheSame(examples);
		if (allExamples.isAllExamplesSame()) {
			return new DecisionTreeLeafNode(previousAttributeValue, allExamples.getAllExamplesLabel());
		}
		
		//If there are no attributes left, return a leaf node with most common target label
		if (attributesVector.size() == 0) {
			return new DecisionTreeLeafNode(previousAttributeValue, getMostCommonTargetAttribute(examples));
		}
		
		int bestAttribute = getBestClassifyingAttribute(examples, attributesVector);
		
		//Create child nodes for each possible value of the attribute to split on
		List<DecisionTreeNode> childNodes = new ArrayList<DecisionTreeNode>();
		Set<Character> allPossibleAttributeValues = getAllPossibleAttributeValues(bestAttribute);
		
		//For each possible attribute create a new sub tree
		for (Character attributeValue : allPossibleAttributeValues) {
		
			List<List<Character>> trainingDataSubset = getTrainingDataSubset(examples, bestAttribute, attributeValue.charValue());
			if (trainingDataSubset.size() == 0) {
				childNodes.add(new DecisionTreeLeafNode(attributeValue.charValue(), getMostCommonTargetAttribute(examples)));
			} else {
				Set<Integer> reducedAttributesVector = new HashSet<Integer>(attributesVector);
				reducedAttributesVector.remove(Integer.valueOf(bestAttribute));
				childNodes.add(buildDecisionTree(trainingDataSubset, reducedAttributesVector, attributeValue));
			}
			
		}
		
		return new DecisionTreeInternalNode(previousAttributeValue, bestAttribute, childNodes);
		
	}
	
	/**
	 * @param examples
	 * @param attributesVector
	 * @return the attribute that best classifies the examples based on information gain computed from the 
	 * selection of the attribute that maximizes the reduction in entropy
	 */
	private int getBestClassifyingAttribute(List<List<Character>> examples, Set<Integer> attributesVector) {
		
		int bestAttribute = Integer.MIN_VALUE;
		double bestInformationGainSoFar = Double.MIN_VALUE, informationGain = 0.0;
		for (Integer attribute : attributesVector) {
			
			informationGain = getInformationGain(examples, attribute.intValue());
			if (informationGain > bestInformationGainSoFar) {
				bestInformationGainSoFar = informationGain;
				bestAttribute = attribute.intValue();
			}

		}
		
		return bestAttribute;
	}
	
	/**
	 * @param attribute
	 * @return a set of all possible values for the attribute
	 */
	private Set<Character> getAllPossibleAttributeValues(int attribute) {
		
		Set<Character> allPossibleAttributeValues = new HashSet<Character>();
		Properties featureProperties = new Properties();
		
		try {
		
			//Load attribute values from properties file
			InputStream inputStream = new FileInputStream(this.propertiesFileName);
			featureProperties.load(inputStream);
			
			String featureName = featureProperties.getProperty(Integer.valueOf(attribute).toString());
			String commaSeparatedAttributeValues = featureProperties.getProperty(featureName);
			
			String[] attributeValuesArray = commaSeparatedAttributeValues.split(ATTRIBUTE_VALUE_SEPARATOR);
			
			//Load attribute values into set to be returned
			for (String attributeValue : attributeValuesArray) {
				
				if (attributeValue.trim().length() > 0) {
					allPossibleAttributeValues.add(Character.valueOf(attributeValue.trim().charAt(0)));
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	
		return allPossibleAttributeValues;
	}
	
	/**
	 * @param trainingData
	 * @param featureToPartitionOn
	 * @param featureValue
	 * @return a subset of the training data containing only those records with matching feature values
	 */
	private List<List<Character>> getTrainingDataSubset(List<List<Character>> trainingData, int featureToPartitionOn, char featureValue) {
				
		List<List<Character>> trainingDataSubset = new ArrayList<List<Character>>();
		for (List<Character> trainingDataRecord : trainingData) {
			if (trainingDataRecord.get(featureToPartitionOn).charValue() == featureValue) {
				trainingDataSubset.add(new ArrayList<Character>(trainingDataRecord));
			}
		}
		return trainingDataSubset;
		
	}
	
	/**
	 * @return an attribute vector filled with attribute column numbers from zero to one less than number of attributes
	 */
	private Set<Integer> getAttributesVector() {
		
		Set<Integer> attributesVector = new HashSet<Integer>();
		for (int attributeCounter = 0; attributeCounter < this.labelOffset; ++attributeCounter) {
			attributesVector.add(Integer.valueOf(attributeCounter));
		}
		return attributesVector;
		
	}
	
	/**
	 * @param trainingData
	 * @return class containing boolean value saying whether all examples have the same label 
	 * and a character value giving the value of the common attribute
	 */
	private AllExamples isAllExamplesTheSame(List<List<Character>> trainingData) {
		
		boolean firstTime = true;
		char allExamples = ' ';
		for (List<Character> trainingRecord : trainingData) {
			if (firstTime) {
				firstTime = false;
				allExamples = trainingRecord.get(this.labelOffset).charValue();
			} else {
				if (trainingRecord.get(this.labelOffset).charValue() != allExamples) {
					return this.new AllExamples(false, allExamples);
				}
			}
		}
		return this.new AllExamples(true, allExamples); 
	}
	
	/**
	 * Class returned from check to see if all examples in training set have the same attribute

	 */
	private class AllExamples {
		private boolean isAllExamplesSame;
		private char allExamplesLabel;
		public AllExamples(boolean isAllExamplesSame, char allExamplesLabel) {
			this.isAllExamplesSame = isAllExamplesSame;
			this.allExamplesLabel = allExamplesLabel;
		}
		public boolean isAllExamplesSame() {
			return isAllExamplesSame;
		}
		public char getAllExamplesLabel() {
			return allExamplesLabel;
		}
	}
	
	/**
	 * @param trainingData
	 * @return the most common target label in the training set. If there is a tie, choose a random target value;
	 */
	private char getMostCommonTargetAttribute(List<List<Character>> trainingData) {
		
		//Count the number of occurrences for the labels
		int firstLabelCount = 0, secondLabelCount = 0;
		for (List<Character> trainingRecord : trainingData) {
			if (trainingRecord.get(this.labelOffset).charValue() == this.firstLabel) {
				++firstLabelCount;
			} else {
				++secondLabelCount;
			}
		}
		
		//Return the label corresponding to the most frequent value
		if (firstLabelCount > secondLabelCount) {
			return this.firstLabel;
		} else if (secondLabelCount > firstLabelCount) {
			return this.secondLabel;
		} else {
			//Break the tie randomly
			if (this.randomNumberGenerator.nextBoolean()) {
				return this.firstLabel;
			} else {
				return this.secondLabel;
			}
		}
		
	}
	
	/**
	 * @param number
	 * @return log base 2. Log 0 will be defined as zero.
	 */
	private double logBase2(double number) {
		if (number == 0.0) {
			return 0.0;
		} else {
			return Math.log(number) / Math.log(2);
		}
		
	}
	
}
