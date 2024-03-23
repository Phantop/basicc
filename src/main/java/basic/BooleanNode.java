package basic;

public class BooleanNode extends Node {
    enum Comparison {
        EQUALS,
        LESS,
        GREATER,
        LEQ,
        GEQ,
        NOTEQUALS
    }

    private final Comparison op;
    private final Node left;
    private final Node right;

    public BooleanNode(Node left, Comparison op, Node right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    /**
     * @return math operation as a readable equation surrounded by parenthesis
     */
    public String toString() {
        String o = "";
        switch (this.op) {
            case Comparison.EQUALS:
                o = "=";
                break;
            case Comparison.NOTEQUALS:
                o = "<>";
                break;
            case Comparison.LESS:
                o = "<";
                break;
            case Comparison.GREATER:
                o = ">";
                break;
            case Comparison.LEQ:
                o = "<=";
                break;
            case Comparison.GEQ:
                o = ">=";
                break;
        }
        return left.toString() + o + right.toString();
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Comparison getOp() {
        return op;
    }
}
