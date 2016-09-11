import java.util.List;


/**
 * Used for returning data from a CSV file alng with information on columns with missing features
 *
 */
public class DataContainer {

	private List<List<Character>> data;
	List<Boolean> missingFeatureVector;
	
	public DataContainer(List<List<Character>> data, List<Boolean> missingFeatureVector) {
		this.data = data;
		this.missingFeatureVector = missingFeatureVector;
	}
	
	public List<List<Character>> getData() {
		return data;
	}
	
	public List<Boolean> getMissingFeatureVector() {
		return missingFeatureVector;
	}
}
