import java.util.List;

public interface Classifier {
	
	/**
	 * Use the training data to construct a model for the features
	 * @param trainingData
	 */
	public void train(List<List<Character>> trainingData);
	
	/**
	 * @param testData
	 * @return prediction labels using the model constructed in the train method
	 */
	public List<Character> predict(List<List<Character>> testData);
	
}
