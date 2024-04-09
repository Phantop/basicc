package basic;

import java.util.LinkedList;
public class DataVisitor implements NodeVisitor {
    private LinkedList<Node> data;
    public DataVisitor() {
        data = new LinkedList<>();
    }

    public void visit(Node n) throws Exception {
        if (n instanceof DataNode)
            visit((DataNode) n);
        if (n instanceof LabeledStatementNode)
            visit((LabeledStatementNode) n);
    }

    public Node pop() {
        return data.pop();
    }

    public void visit(LabeledStatementNode n) throws Exception {
        if (n.getStatement() instanceof DataNode)
            visit((DataNode) n.getStatement());
    }

    public void visit(DataNode n) throws Exception {
        data.addAll(n.getData());
    }
}
