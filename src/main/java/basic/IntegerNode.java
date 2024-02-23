package basic;

public class IntegerNode extends Node {
    private final int value;

    public IntegerNode(int in) {
        value = in;
    }

    public float getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
