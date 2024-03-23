package basic;

public class IfNode extends StatementNode {
    
    private final BooleanNode condition;
    private final String target;

    public IfNode(BooleanNode condition, String target) {
        this.condition = condition;
        this.target = target;
    }

    public BooleanNode getCondition() {
        return condition;
    }

    public String getTarget() {
        return target;
    }
    public String toString() {
        return "IF " + condition + " THEN " + target;
    }
}
