package basic;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
public class InputNode extends StatementNode {
    
    private final Node input;
    private LinkedList<VariableNode> params;

    public InputNode(Node input) {
        params = new LinkedList<>();
        this.input = input;
    }

    public void add(VariableNode next) {
        params.add(next);
    }

    public Node getInput() {
        return input;
    }

    public List<VariableNode> getParams() {
        return Collections.unmodifiableList(params);
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public int size() {
        return params.size();
    }

    public String toString() {
        var output = "INPUT " + input.toString();
        for (Node n : params)
            output = output + ", " + n.toString();
        return output;
    }
}
