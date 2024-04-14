package basic;

import basic.BooleanNode.Comparison;
import basic.FunctionNode.Invocation;
import basic.MathOpNode.Operation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Optional;

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


    protected Interpreter (StatementsNode ast)  {
        this.ast = ast;
        this.data = new DataVisitor();
        this.labels = new LabelVisitor();
    }

    /**
     * Preprocesses data statements
     * @modifies data, adding in all data statement contents in order
     */
    protected void processData() throws Exception {
        for (StatementNode s : ast.getAST())
            s.accept(data);
    }

    protected Node popData() {
        return data.pop();
    }

    /**
     * Preprocesses data statements
     * @modifies labels, mapping all label strings to the relevant node
     * @throws exception if a label is repeated
     */
    protected void processLabels() throws Exception {
        for (StatementNode s : ast.getAST())
            s.accept(labels);
    }

    protected LabeledStatementNode getLabel(String s) {
        return labels.get(s);
    }

    /* START OF BUILT-IN FUNCTIONS */
    protected static String left(String data, int characters) {
        return data.substring(0, characters);
    }
    protected static String mid(String data, int start, int characters) {
        return data.substring(start, start+characters);
    }
    protected static String num (float data) {
        return Float.toString(data);
    }
    protected static String num (int data) {
        return Integer.toString(data);
    }
    protected static String right(String data, int characters) {
        return data.substring(data.length() - characters);
    }
    protected static int random() {
        return (new Random()).nextInt();
    }
    protected static int val (String data) {
        return Integer.parseInt(data);
    }
    protected static float valf (String data) {
        return Float.parseFloat(data);
    }
    /* END OF BUILT-IN FUNTIONS */

    /**
     * Processes RHS of an int expression
     * @arg Input node of expression (MathOpNode or just an IntegerNode)
     * @return evaluated value of the expression or empty if not an int expression (may be float)
     * @throws NullPointerException if an invalid variable is accessed
     */
    Optional<Integer> evaluate(Node in) {
        Integer out = null;
        if (in instanceof IntegerNode)
            out = ((IntegerNode) in).getValue();
        if (in instanceof VariableNode) {
            // consider a lookupVarable() method
            var name = ((VariableNode) in).getValue();
            if (floatVars.containsKey(name))
                return Optional.empty();
            out = intVars.get(name);
        }
        if (in instanceof MathOpNode) {
            var op = (MathOpNode) in;
            Optional<Integer> oleft = evaluate(op.getLeft());
            Optional<Integer> oright = evaluate(op.getRight());
            if (oleft.isEmpty() || oright.isEmpty())
                return Optional.empty();
            int left = oleft.get();
            int right = oright.get();
            switch (op.getOp()) {
                case Operation.ADD:
                    out = left + right;
                    break;
                case Operation.SUBTRACT:
                    out = left - right;
                    break;
                case Operation.DIVIDE:
                    out = left / right;
                    break;
                case Operation.MULTIPLY:
                    out = left * right;
                    break;
            }
        }
        if (out == null)
            return Optional.empty();
        return Optional.of(out);
    }
}
