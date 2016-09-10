import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Decision Tree classifier that uses the ID3 algorithm
 */
public class DecisionTreeId3BinaryClassifier implements Classifier {
	
	private DecisionTreeNode decisionTreeRootNode;
	private int labelOffset;
	private int numberOfTrainingRecords;
	private char firstLabel, secondLabel;
	private boolean secondLabelFound;

	@Override
	public void train(List<List<Character>> trainingData) {
		
		//Training data should be present and duplicates in training data are not allowed
		assert trainingData.size() > 0 && !isDuplicatesInTrainingData(trainingData);
		
		doBookKeeping(trainingData);

	}

	@Override
	public List<Character> predict(List<Character> testData) {
		// TODO Auto-generated method stub
		return null;
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
		this.numberOfTrainingRecords = trainingData.size();
		this.firstLabel = trainingData.get(0).get(labelOffset);
		
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
			if (!this.secondLabelFound && trainingDataRecord.get(this.labelOffset) != this.firstLabel) {
				this.secondLabel = trainingDataRecord.get(this.labelOffset);
				this.secondLabelFound = true;
			}
			
			if (trainingDataRecord.get(this.labelOffset) == this.firstLabel) {
				++firstLabelCount;
			} else {
				++secondLabelCount;	
			}
			
		}

		int totalCount = firstLabelCount + secondLabelCount;
		double firstLabelFraction = firstLabelCount/totalCount, secondLabelFraction = secondLabelCount/totalCount;
		return -1 * firstLabelFraction * Math.log(firstLabelFraction) / Math.log(2) - secondLabelFraction * Math.log(secondLabelFraction) / Math.log(2);

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
			trainingDataRecordLabel = trainingDataRecord.get(this.labelOffset);
			
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
		
		double weightedFeatureValuesEntropy = 0.0;
		Set<Character> FeatureValueKeys = counts.keySet();
		for (Character featureValue : FeatureValueKeys) {
			weightedFeatureValuesEntropy += getWeightedSubsetEntropy(counts.get(featureValue).getFirstLabelCount(), counts.get(featureValue).getSecondLabelCount());
		}
		
		return weightedFeatureValuesEntropy;
		
	}
	
	/**
	 * @param firstLabelCount
	 * @param secondLabelCount
	 * @return the weighted entropy of a specific value of an attribute
	 */
	private double getWeightedSubsetEntropy(int firstLabelCount, int secondLabelCount) {
		
		double firstLabelFraction = firstLabelCount / (firstLabelCount + secondLabelCount);
		double secondLabelFraction = secondLabelCount / (firstLabelCount + secondLabelCount);
		
		return -1 * firstLabelFraction * (Math.log(firstLabelFraction) / Math.log(2)) - secondLabelFraction * (Math.log(secondLabelFraction) / Math.log(2));
		
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
	
}
