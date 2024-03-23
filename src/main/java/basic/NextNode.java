package basic;

public class NextNode extends StatementNode {
    private final VariableNode variable;

    public NextNode(VariableNode variable) {
        this.variable = variable;
    }

    public VariableNode getVariable() {
        return variable;
    }

    public String toString() {
        return "NEXT " + variable;
    }
}
