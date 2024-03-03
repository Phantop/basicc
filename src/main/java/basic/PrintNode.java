package basic;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
public class PrintNode extends StatementNode {
    
    private LinkedList<Node> prints;

    public PrintNode() {
        prints = new LinkedList<Node>();
    }

    public void add(Node next) {
        prints.add(next);
    }

    public List<Node> getPrints() {
        return Collections.unmodifiableList(prints);
    }

    public boolean isEmpty() {
        return prints.isEmpty();
    }

    public String toString() {
        var output = "PRINT";
        for (Node n : prints)
            output = output + " " + n.toString() + ",";
        return output;
    }
}
