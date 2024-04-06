package basic;

import basic.BooleanNode.Comparison;
import basic.FunctionNode.Invocation;
import basic.MathOpNode.Operation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * The BASIC Interpreter, which processes and runs an AST from the parser
 */
public class Interpreter {
    private final StatementsNode ast;
    private LinkedList<Node> data;
    private HashMap<String,LabeledStatementNode> labels;

    private HashMap<String,Integer> intVars;
    private HashMap<String,Float> floatVars;
    private HashMap<String,String> stringVars;


    public Interpreter (StatementsNode ast)  {
        this.ast = ast;
        this.data = new LinkedList<Node>();
        this.labels = new HashMap<>();
    }

    /**
     * Preprocesses data statements
     * @modifies data, adding in all data statement contents in order
     */
    public void processData() {
        for (StatementNode s : ast.getAST()) {
            DataNode data = null;
            if (s instanceof DataNode) data = (DataNode) s;
            if (s instanceof LabeledStatementNode) {
                var tmp = (LabeledStatementNode) s;
                if (tmp.getStatement() instanceof DataNode)
                    data = (DataNode) tmp.getStatement();
            }
            if (data != null)
                this.data.addAll(data.getData());
        }
    }

    /**
     * Preprocesses data statements
     * @modifies labels, mapping all label strings to the relevant node
     */
    public void processLabels() {
        for (StatementNode s : ast.getAST()) {
            LabeledStatementNode label = null;
            if (s instanceof LabeledStatementNode) label = (LabeledStatementNode) s;
            if (label != null)
                this.labels.put(label.getLabel(), label);
        }
    }

    /* START OF BUILT-IN FUNCTIONS */
    public static String left(String data, int characters) {
        return data.substring(0, characters);
    }
    public static String mid(String data, int start, int characters) {
        return data.substring(start, start+characters);
    }
    public static String num (float data) {
        return Float.toString(data);
    }
    public static String num (int data) {
        return Integer.toString(data);
    }
    public static String right(String data, int characters) {
        return data.substring(data.length() - characters);
    }
    public static int random() {
        return (new Random()).nextInt();
    }
    public static int val (String data) {
        return Integer.parseInt(data);
    }
    public static float valf (String data) {
        return Float.parseFloat(data);
    }
    /* END OF BUILT-IN FUNTIONS */
}
