package basic;

public class MathOpNode extends Node {
    enum Operation {
        ADD,
        SUBTRACT,
        DIVIDE,
        MULTIPLY
    }

    private final Operation op;
    private final Node left;
    private final Node right;

    public MathOpNode(Node left, Operation op, Node right) {
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
            case Operation.ADD:
                o = "+";
                break;
            case Operation.SUBTRACT:
                o = "-";
                break;
            case Operation.DIVIDE:
                o = "/";
                break;
            case Operation.MULTIPLY:
                o = "*";
                break;
        }
        return "(" + left.toString() + o + right.toString() + ")";
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Operation getOp() {
        return op;
    }
}
