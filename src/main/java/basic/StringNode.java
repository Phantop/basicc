package basic;

public class StringNode extends Node {
    private final String value;

    public StringNode(String in) {
        value = in;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return "\"" + value + "\"";
    }
}
