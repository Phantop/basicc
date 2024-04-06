package basic;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
public class FunctionNode extends Node {
    enum Invocation {
        LEFT,
        MID,
        NUM,
        NUMF,
        RANDOM,
        RIGHT,
        VAL,
        VALF
    }

    private final Invocation op;
    private LinkedList<Node> data;

    public FunctionNode(Invocation op) {
        this.op = op;
        data = new LinkedList<Node>();
    }

    public void add(Node next) {
        data.add(next);
    }

    public List<Node> getData() {
        return Collections.unmodifiableList(data);
    }

    public Invocation getValue() {
        return op;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public String toString() {
        var output = op.toString() + "(";
        for (Node n : data)
            output = output + n.toString() + ", ";
        output = output + ")";
        return output;
    }
}
