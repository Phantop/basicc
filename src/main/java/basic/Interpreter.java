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
    private DataVisitor data;
    private LabelVisitor labels;

    private HashMap<String,Integer> intVars;
    private HashMap<String,Float> floatVars;
    private HashMap<String,String> stringVars;


    public Interpreter (StatementsNode ast)  {
        this.ast = ast;
        this.data = new DataVisitor();
        this.labels = new LabelVisitor();
    }

    /**
     * Preprocesses data statements
     * @modifies data, adding in all data statement contents in order
     */
    public void processData() throws Exception {
        for (StatementNode s : ast.getAST())
            s.accept(data);
    }

    public Node popData() {
        return data.pop();
    }

    /**
     * Preprocesses data statements
     * @modifies labels, mapping all label strings to the relevant node
     * @throws exception if a label is repeated
     */
    public void processLabels() throws Exception {
        for (StatementNode s : ast.getAST())
            s.accept(labels);
    }

    public LabeledStatementNode getLabel(String s) {
        return labels.get(s);
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
