
public abstract class DecisionTreeNode {
	
	private char previousAttributeValue;
	
	public DecisionTreeNode(char previousAttributeValue) {
		this.previousAttributeValue = previousAttributeValue;
	}

	public char getPreviousAttributeValue() {
		return previousAttributeValue;
	}

}
