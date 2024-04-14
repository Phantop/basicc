package basic;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
public class ReadNode extends StatementNode {
    
    private LinkedList<VariableNode> reads;

    public ReadNode() {
        reads = new LinkedList<>();
    }

    public void add(VariableNode next) {
        reads.add(next);
    }

    public List<VariableNode> getReads() {
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
