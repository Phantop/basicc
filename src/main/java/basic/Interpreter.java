package basic;

import basic.BooleanNode.Comparison;
import basic.FunctionNode.Invocation;
import basic.MathOpNode.Operation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Optional;
import java.util.Scanner;

/**
 * The BASIC Interpreter, which processes and runs an AST from the parser
 */
public class Interpreter {
    private final StatementsNode ast;
    private DataVisitor data;
    private LabelVisitor labels;

    // to indicate where PRINT and INPUT should output
    private boolean test;

    private HashMap<String,Integer> intVars;
    private HashMap<String,Float> floatVars;
    private HashMap<String,String> stringVars;

    protected Interpreter (StatementsNode ast, boolean test)  {
        this.ast = ast;
        this.data = new DataVisitor();
        this.labels = new LabelVisitor();
        this.test = test;
    }

    protected Interpreter (StatementsNode ast)  {
        this.ast = ast;
        this.data = new DataVisitor();
        this.labels = new LabelVisitor();
        this.test = false;
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

    /* START OF BUILT-IN FUNCTION NODE HANDLING */
    protected static int val (Node data) throws Exception {
        if (!(data instanceof StringNode))
            handleError("Invalid input to val(): " + data);
        return val(((StringNode)data).getValue());
    }
    protected static float valf (Node data) throws Exception {
        if (!(data instanceof StringNode))
            handleError("Invalid input to val%(): " + data);
        return valf(((StringNode)data).getValue());
    }

    protected static Optional<Integer> intFunction(FunctionNode func) throws Exception {
        var op = func.getValue();
        if (op == Invocation.RANDOM) return Optional.of(random());
        if (op == Invocation.VAL) return Optional.of(val(func.getData().get(0)));
        return Optional.empty();
    }
    protected static Optional<Float> floatFunction(FunctionNode func) throws Exception {
        var op = func.getValue();
        if (op == Invocation.VALF) return Optional.of(valf(func.getData().get(0)));
        return Optional.empty();
    }
    /* END OF BUILT-IN FUNCTION NODE HANDLING */

    /**
     * Processes RHS of an int expression
     * @arg Input node of expression (MathOpNode, VariableNode or just an IntegerNode)
     * @return evaluated value of the expression or empty if not an int expression (may be float)
     * @throws NullPointerException if an invalid variable is accessed
     */
    protected Optional<Integer> evaluate(Node in) throws Exception {
        Integer out = null;
        if (in instanceof IntegerNode)
            out = ((IntegerNode) in).getValue();
        if (in instanceof VariableNode) {
            // consider a lookupVarable() method
            var name = ((VariableNode) in).getValue();
            // do this check because there are cases where we try both evals
            if (floatVars.containsKey(name))
                return Optional.empty();
            out = intVars.get(name);
        }
        if (in instanceof FunctionNode)
            return intFunction((FunctionNode) in);
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

    /**
     * Processes RHS of a float expression
     * @arg Input node of expression (MathOpNode, VariableNode or just a FloatNode or IntegerNode)
     * @return evaluated value of the expression or empty if not a float expression
     * @throws NullPointerException if an invalid variable is accessed
     * @throws Exception if a function call fails
     */
    protected Optional<Float> evaluatef(Node in) throws Exception {
        Float out = null;
        if (in instanceof FloatNode)
            out = ((FloatNode) in).getValue();
        if (in instanceof IntegerNode)
            out = (float) ((IntegerNode) in).getValue();
        if (in instanceof VariableNode) {
            // consider a lookupVarable() method
            var name = ((VariableNode) in).getValue();
            if (floatVars.containsKey(name))
                out = floatVars.get(name);
            else
                out = (float) intVars.get(name);
        }
        if (in instanceof FunctionNode) {
            var intval = intFunction((FunctionNode) in);
            if (!intval.isEmpty())
                return Optional.of((float)intval.get());
            return floatFunction((FunctionNode) in);
        }
        if (in instanceof MathOpNode) {
            var op = (MathOpNode) in;
            Optional<Float> oleft = evaluatef(op.getLeft());
            Optional<Float> oright = evaluatef(op.getRight());
            if (oleft.isEmpty() || oright.isEmpty())
                return Optional.empty();
            float left = oleft.get();
            float right = oright.get();
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

    protected void interpret(StatementNode n) throws Exception {
        if (n instanceof AssignmentNode)
            interpret((AssignmentNode) n);
        if (n instanceof ReadNode)
            interpret((ReadNode) n);
        if (n instanceof InputNode)
            interpret((InputNode) n);
        if (n instanceof PrintNode)
            interpret((PrintNode) n);
    }

    /** Generates print list for a PrintNode
     *  Called by interpret(), used explicitly for testing
     *  @throws NoSuchElementException if any element of the list is invalid
     */
    protected List<String> print(PrintNode n) throws Exception {
        var out = new LinkedList<String>();
        for (Node x : n.getPrints()) {
            if (x instanceof StringNode)
                out.add(((StringNode)x).getValue());
            else if (x instanceof VariableNode)
                out.add(stringVars.get(((VariableNode)x).getValue()));
            else {
                var intval = evaluate(x);
                if (!intval.isEmpty())
                    out.add(intval.get().toString());
                else {
                var floatval = evaluatef(x);
                    out.add(floatval.get().toString());
                }
            }

        }
        return null;
    }

    protected void interpret(PrintNode n) throws Exception {
        for (String x : print(n))
            System.out.print(x);
        System.out.println();
    }

    /**
     * InputNode interpretation split in two, this one would be called explicitly for testing purposes
     * @throws NumberFormatException if invalid numerical input provided for numerical variable
     */
    protected void interpret(InputNode n, List<String> in) {
        for (VariableNode x : n.getParams()) {
            String name = x.getValue();
            String input = in.remove(0);

            if (name.endsWith("%"))
                floatVars.put(name, Float.parseFloat(input));
            if (name.endsWith("$"))
                stringVars.put(name, input);
            else
                intVars.put(name, Integer.parseInt(input));
        }
    }

    protected void interpret(InputNode n) {
        var input = n.getInput();
        String str;
        if (input instanceof VariableNode)
            str = stringVars.get(((VariableNode) input).getValue());
        else
            str = ((StringNode) input).getValue();
        System.out.println(str);

        var in = new LinkedList<String>();
        Scanner s = new Scanner(System.in);
        for (int i = 0; i < n.size(); i++)
            in.add(s.nextLine());

    }

    protected void interpret(ReadNode n) throws Exception {
        for (VariableNode x : n.getReads()) {
            // this basically does an assignment using this data sooooo
            var a = new AssignmentNode(x, popData());
            interpret(a);
        }
    }

    protected void interpret(AssignmentNode n) throws Exception {
        String name = (n.getLeft()).getValue();
        if (name.endsWith("%")) {
            var val = evaluatef(n.getRight());
            if (val.isEmpty())
                handleError("Invalid float assignment in statement: " + n + "\n");
            floatVars.put(name, val.get());
        }
        else if (name.endsWith("$")) {
            if (!(n.getRight() instanceof StringNode))
                handleError("Invalid string assignment in statement: " + n + "\n");
            String val = ((StringNode) n.getRight()).getValue();
            stringVars.put(name, val);
        }
        else {
            Optional<Integer> val = evaluate(n.getRight());
            if (val.isEmpty())
                handleError("Invalid int assignment in statement: " + n + "\n");
            intVars.put(name, val.get());
        }
    }

    private static void handleError(String msg) throws Exception {
        System.err.println(msg);
        throw new Exception();
    }
}
