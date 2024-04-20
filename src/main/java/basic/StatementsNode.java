package basic;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
public class StatementsNode extends Node {
    
    // feels kinda weird doing this with a list, but seems sufficient *for now*
    private LinkedList<StatementNode> program;

    public StatementsNode() {
        program = new LinkedList<StatementNode>();
    }

    public void add(StatementNode next) {
        program.add(next);
    }

    /**
     * Gives mutable copy of the AST/internal node contents
     * Needs to be mutable so Interpreter can process next nodes
     */
    public List<StatementNode> getAST() {
        return program;
    }

    public boolean isEmpty() {
        return program.isEmpty();
    }

    /**
     * Prints out the program "line-by-line"
     */
    public String toString() {
        var output = "";
        for (Node n : program)
            output = output + n.toString() + "\n";
        return output;
    }
}
