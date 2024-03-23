package basic;

public class IfNode extends StatementNode {
    
    private final BooleanNode condition;

    public IfNode(BooleanNode condition) {
        this.condition = condition;
    }

    public BooleanNode getCondition() {
        return condition;
    }

    public String toString() {
        return "IF " + condition + " THEN";
    }
}
