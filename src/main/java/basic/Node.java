package basic;

public abstract class Node {
    public void accept(NodeVisitor v) throws Exception {
        v.visit(this);
    }
    public abstract String toString();
}
