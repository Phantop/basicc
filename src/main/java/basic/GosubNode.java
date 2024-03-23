package basic;

public class GosubNode extends StatementNode {
    private final String identifier;

    public GosubNode(String identifier) {
        this.identifier = identifier;
    }

    public String getValue() {
        return identifier;
    }

    public String toString() {
        return "GOSUB " + identifier;
    }
}
