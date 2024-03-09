package basic;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
public class ReadNode extends StatementNode {
    
    private LinkedList<Node> reads;

    public ReadNode() {
        reads = new LinkedList<Node>();
    }

    public void add(Node next) {
        reads.add(next);
    }

    public List<Node> getReads() {
        return Collections.unmodifiableList(reads);
    }

    public boolean isEmpty() {
        return reads.isEmpty();
    }

    public String toString() {
        var output = "READ";
        for (Node n : reads)
            output = output + " " + n.toString() + ",";
        return output;
    }
}
