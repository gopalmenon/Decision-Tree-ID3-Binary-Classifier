import java.util.List;

public interface Classifier {
	
	public void train(List<List<Character>> trainingData);
	public List<Character> predict(List<Character> testData);
	
}
