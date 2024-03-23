package basic;

public class LabeledStatementNode extends StatementNode {
    private final String label;
    private final StatementNode orig;

    public LabeledStatementNode(String label, StatementNode orig) {
        this.label = label;
        this.orig = orig;
    }

    public String getLabel() {
        return label;
    }
    public StatementNode getStatement() {
        return orig;
    }

    public String toString() {
        return label + ": " + orig.toString(); 
    }
}
