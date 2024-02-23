package basic;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
public class ProgramNode extends Node {
    
    // feels kinda weird doing this with a list, but seems sufficient *for now*
    private LinkedList<Node> program;

    public ProgramNode() {
        program = new LinkedList<Node>();
    }

    public void add(Node next) {
        program.add(next);
    }

    /**
     * Gives immutable copy of the AST/internal node contents
     * Unlikely to be used for now but presumably something of this sort necessary
     * for a later step (or some kind of more specific accessor)
     */
    public List<Node> getAST() {
        return Collections.unmodifiableList(program);
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
