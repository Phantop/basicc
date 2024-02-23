package basic;

public class FloatNode extends Node {
    private float value;

    public FloatNode(float in) {
        value = in;
    }
    public String toString() {
        return String.valueOf(value);
    }
}
