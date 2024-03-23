package basic;

public class ForNode extends StatementNode {
    
    private final VariableNode variable;
    private final int start;
    private final int end;
    private final int increment;

    public ForNode(VariableNode variable, int start, int end, int increment) {
        this.variable = variable;
        this.start = start;
        this.end = end;
        this.increment = increment;
    }

    public ForNode(VariableNode variable, int start, int end) {
        this(variable, start, end, 1);
    }

    public VariableNode getVar() {
        return variable;
    }

    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
    public int getInc() {
        return increment;
    }

    public String toString() {
        return "FOR " + variable + " = " + start + " TO " + end + " STEP " + increment;
    }
}
