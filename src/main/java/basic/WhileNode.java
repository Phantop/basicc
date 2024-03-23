package basic;

public class WhileNode extends StatementNode {
    
    private final BooleanNode condition;
    private final String identifier;


    public String getValue() {
        return identifier;
    }

    public WhileNode(BooleanNode condition, String identifier) {
        this.condition = condition;
        this.identifier = identifier;
    }

    public BooleanNode getCondition() {
        return condition;
    }

    public String toString() {
        return "WHILE " + condition + " " + identifier;
    }
}
