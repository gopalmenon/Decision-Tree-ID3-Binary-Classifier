
public class DecisionTreeLeafNode extends DecisionTreeNode {

	private char label;
	
	public DecisionTreeLeafNode(char previousAttributeValue, char label) {
		super(previousAttributeValue);
		this.label = label;
	}

	public char getLabel() {
		return label;
	}

}
