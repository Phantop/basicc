package basic;

public class AssignmentNode extends StatementNode {
    private final VariableNode left;
    private final Node right;

    public AssignmentNode(VariableNode left, Node right) {
        this.left = left;
        this.right = right;
    }

    public VariableNode getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public String toString() {
        return left.toString() + "=" + right.toString();
    }
}
