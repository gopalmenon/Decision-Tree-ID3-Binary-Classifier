import java.util.Iterator;
import java.util.List;

public class ClassifierMetrics {

	private double precision;
	private double recall;
	private double accuracy;
	private double f1Score;
	
	public ClassifierMetrics(List<List<Character>> testData, List<Character> prediction, char positiveLabel, char negativeLabel) {
		
		int numberOfTestDataRecords = testData.size(), labelColumnOffset = 0, testDataRecordCounter = 0;
		int truePositives = 0, trueNegatives = 0, falsePositives = 0, falseNegatives = 0;
		assert numberOfTestDataRecords == prediction.size() && numberOfTestDataRecords > 0;
	
		Iterator<List<Character>> testDataIterator = testData.iterator();
		boolean firstTime = true;
		List<Character> testDataRecord = null;
		char testLabelValue = ' ';
		while (testDataIterator.hasNext()) {
			
			testDataRecord = testDataIterator.next();
			
			if (firstTime) {
				firstTime = false;
				labelColumnOffset = testDataRecord.size() - 1;
			}
			
			testLabelValue = testDataRecord.get(labelColumnOffset).charValue();
			if (testLabelValue == positiveLabel) {
				if (testLabelValue == prediction.get(testDataRecordCounter++).charValue()) {
					++truePositives;
				} else {
					++falseNegatives;
				}
			} else {
				if (testLabelValue == prediction.get(testDataRecordCounter++).charValue()) {
					++trueNegatives;
				} else {
					++falsePositives;
				}
			}
		}
		
		this.precision = (double) truePositives /(truePositives + falsePositives);
		this.recall = (double) truePositives /(truePositives + falseNegatives);
		this.accuracy = (double) (truePositives + trueNegatives) / numberOfTestDataRecords;
		this.f1Score = (double) (2 * truePositives) / (2 * truePositives + falsePositives + falseNegatives);

	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public double getF1Score() {
		return f1Score;
	}
	
}
