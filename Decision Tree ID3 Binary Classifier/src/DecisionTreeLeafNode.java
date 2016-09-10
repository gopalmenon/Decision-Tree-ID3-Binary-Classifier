
public class DecisionTreeLeafNode extends DecisionTreeNode {

	private char label;
	
	public DecisionTreeLeafNode(char label) {
		this.label = label;
	}

	public char getLabel() {
		return label;
	}

}
