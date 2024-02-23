package basic;

import java.util.LinkedList;
public class ProgramNode extends Node {
    
    // feels kinda weird doing this with a list, but seems sufficient *for now*
    private LinkedList<Node> program;

    public ProgramNode() {
        program = new LinkedList<Node>();
    }

    public void add(Node next) {
        program.add(next);
    }

    public String toString() {
        var output = "";
        for (Node n : program)
            output = output + n.toString() + "\n";
        return output;
    }
}
