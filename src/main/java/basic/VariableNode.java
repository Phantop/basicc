package basic;

public class VariableNode extends Node {
    private final String value;

    public VariableNode(String in) {
        value = in;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return value;
    }
}
