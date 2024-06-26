package basic;

/**
 * Seems like this would still be abstract, as toString is dependent on
 * statement type and there's nothing this would inherently hold
 * between, say, assignment and print
 */
public abstract class StatementNode extends Node {
    public abstract String toString();
    private StatementNode nextStatement;
    public StatementNode next() {
        return nextStatement;
    }
    public void setNext(StatementNode next) {
        nextStatement = next;
    }
}
