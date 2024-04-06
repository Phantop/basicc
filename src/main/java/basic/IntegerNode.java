package basic;

public class IntegerNode extends Node {
    private final int value;

    public IntegerNode(int in) {
        value = in;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
