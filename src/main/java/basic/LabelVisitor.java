package basic;

import java.util.HashMap;
public class LabelVisitor implements NodeVisitor {
    private HashMap<String,LabeledStatementNode> labels;
    public LabelVisitor() {
        labels = new HashMap<>();
    }

    public void visit(Node n) throws Exception {
        if (n instanceof LabeledStatementNode)
            visit((LabeledStatementNode) n);
    }

    public LabeledStatementNode get(String s) {
        return labels.get(s);
    }

    public void visit(LabeledStatementNode n) throws Exception {
        var name = n.getLabel();
        if (labels.containsKey(name)) {
            System.err.println("Repeated label: " + name);
            throw new Exception();
        }
        labels.put(name, n);
    }
}
