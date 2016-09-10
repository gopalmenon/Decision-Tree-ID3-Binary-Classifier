import java.util.Collections;
import java.util.List;


public class DecisionTreeInternalNode extends DecisionTreeNode {

	private int attributeSplitOn;
	private List<DecisionTreeNode> childNodes;
	
	public DecisionTreeInternalNode(char previousAttributeValue, int attributeSplitOn, List<DecisionTreeNode> childNodes) {
		super(previousAttributeValue);
		this.attributeSplitOn = attributeSplitOn;
		this.childNodes = childNodes;
	}

	public int getAttributeSplitOn() {
		return attributeSplitOn;
	}

	public List<DecisionTreeNode> getChildNodes() {
		return Collections.unmodifiableList(this.childNodes);
	}
	
}
