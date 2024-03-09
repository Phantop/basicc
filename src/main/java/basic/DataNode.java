package basic;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
public class DataNode extends StatementNode {
    
    private LinkedList<Node> data;

    public DataNode() {
        data = new LinkedList<Node>();
    }

    public void add(Node next) {
        data.add(next);
    }

    public List<Node> getData() {
        return Collections.unmodifiableList(data);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public String toString() {
        var output = "DATA";
        for (Node n : data)
            output = output + " " + n.toString() + ",";
        return output;
    }
}
