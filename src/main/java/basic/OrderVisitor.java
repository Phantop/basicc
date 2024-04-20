package basic;

public class OrderVisitor implements NodeVisitor {
    private StatementNode last;
    public OrderVisitor() {
        last = null;
    }

    public void visit(Node n) throws Exception {
        if (n instanceof LabeledStatementNode)
            visit((LabeledStatementNode) n);
        if (n instanceof StatementNode)
            visit((StatementNode) n);
    }

    public void visit(StatementNode n) throws Exception {
        n.setNext(last);
        last = n;
    }

    public void visit(LabeledStatementNode n) throws Exception {
        n.getStatement().setNext(last);
    }
}
