package basic;

public class MathOpNode extends Node {
    enum Operation {
        ADD,
        SUBTRACT,
        DIVIDE,
        MULTIPLY
    }

    private Operation op;
    private Node left;
    private Node right;

    public MathOpNode(Node left, Operation op, Node right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "(" + left.toString() + this.opChar() + right.toString() + ")";
    }

    private String opChar() {
        switch (this.op) {
            case Operation.ADD:
                return "+";
            case Operation.SUBTRACT:
                return "-";
            case Operation.DIVIDE:
                return "/";
            case Operation.MULTIPLY:
                return "*";
        }
        return ""; // i don't think this is reachable lol but the compiler complains
    }
}
