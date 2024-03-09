package basic;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
public class InputNode extends StatementNode {
    
    private LinkedList<Node> params;
    private Node input;

    public InputNode(Node input) {
        params = new LinkedList<Node>();
        this.input = input;
    }

    public void add(Node next) {
        params.add(next);
    }

    public Node getInput() {
        return input;
    }

    public List<Node> getParams() {
        return Collections.unmodifiableList(params);
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public String toString() {
        var output = "INPUT " + input.toString();
        for (Node n : params)
            output = output + ", " + n.toString();
        return output;
    }
}
