package basic;

public class FloatNode extends Node {
    private final float value;

    public FloatNode(float in) {
        value = in;
    }

    public float getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
