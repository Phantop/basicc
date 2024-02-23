package basic;

public class IntegerNode extends Node {
    private int value;

    public IntegerNode(int in) {
        value = in;
    }
    public String toString() {
        return String.valueOf(value);
    }
}
