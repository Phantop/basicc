package basic;

import basic.BooleanNode.Comparison;
import basic.FunctionNode.Invocation;
import basic.MathOpNode.Operation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
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
    private List<String> ioList;
    private List<String> fullOutList;

    // would be a queue but we put things into the head for certain control simplifications
    private List<StatementNode> stack;

    private HashMap<String,Integer> intVars;
    private HashMap<String,Float> floatVars;
    private HashMap<String,String> stringVars;

    protected Interpreter (StatementsNode ast, boolean test)  {
        this.test = test;
        this.ast = ast;
        this.data = new DataVisitor();
        this.labels = new LabelVisitor();
        this.stack = new LinkedList<>();
        this.fullOutList = new LinkedList<>();

        this.intVars = new HashMap<>();
        this.floatVars = new HashMap<>();
        this.stringVars = new HashMap<>();
    }

    protected Interpreter (StatementsNode ast)  {
        this(ast, false);
    }

    protected void putIO(List<String> in) {
        ioList = in;
    }

    protected List<String> getIO() {
        return ioList;
    }

    protected List<String> getFullIO() {
        return fullOutList;
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

    protected void processOrder() throws Exception {
        var visitor = new OrderVisitor();
        for (int i = ast.getAST().size() - 1; i >= 0; i--)
            ast.getAST().get(i).accept(visitor);
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

    protected String getVar(String s) {
        if (floatVars.containsKey(s))
            return floatVars.get(s).toString();
        if (intVars.containsKey(s))
            return intVars.get(s).toString();
        return stringVars.get(s);
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

    protected boolean evaluateb(BooleanNode n) throws Exception {
        var left = n.getLeft();
        double leftnum;
        var leftval = evaluate(left);
        if (!leftval.isEmpty())
            leftnum = leftval.get();
        else {
            leftnum = evaluatef(left).get();
        }

        var right = n.getRight();
        double rightnum;
        var rightval = evaluate(right);
        if (!rightval.isEmpty())
            rightnum = rightval.get();
        else {
            rightnum = evaluatef(right).get();
        }

        switch (n.getOp()) {
            case Comparison.EQUALS:
                return leftnum == rightnum;
            case Comparison.NOTEQUALS:
                return leftnum != rightnum;
            case Comparison.LESS:
                return leftnum < rightnum;
            case Comparison.GREATER:
                return leftnum > rightnum;
            case Comparison.LEQ:
                return leftnum <= rightnum;
            case Comparison.GEQ:
                return leftnum >= rightnum;
        }
        handleError("Bad boolean " + n);
        return false; // this should never happen
    }

    public void interpret() throws Exception {
        interpret(ast.getAST().get(0));
    }

    protected void interpret(StatementNode n) throws Exception {
        processOrder();
        processData();
        processLabels();

        while (n != null) {
            //System.out.println("Running: " + n);
            if (n instanceof AssignmentNode)
                interpret((AssignmentNode) n);
            if (n instanceof ReadNode)
                interpret((ReadNode) n);
            if (n instanceof InputNode)
                interpret((InputNode) n);
            if (n instanceof PrintNode)
                interpret((PrintNode) n);
            if (n instanceof IfNode) {
                n = interpret((IfNode) n);
                continue;
            }
            if (n instanceof LabeledStatementNode) {
                n = interpret((LabeledStatementNode) n);
                continue;
            }
            if (n instanceof ForNode) {
                n = interpret((ForNode) n);
                continue;
            }
            if (n instanceof WhileNode) {
                n = interpret((WhileNode) n);
                continue;
            }
            if (n instanceof GosubNode) {
                n = interpret((GosubNode) n);
                continue;
            }
            if (n instanceof NextNode) {
                n = interpret((NextNode) n);
                continue;
            }
            // next node has to also increment its vars, so we handle it like this
            if (n instanceof ReturnNode) {
                n = stack.remove(0);
                continue;
            }
            if (n instanceof EndNode)
                break;
            n = n.next();
        }
    }

    protected StatementNode interpret(WhileNode n) throws Exception {
        if (!evaluateb(n.getCondition())) {
            for (StatementNode travel = n.next(); travel != null ; travel = travel.next()) {
                if (travel instanceof LabeledStatementNode &&
                        ((LabeledStatementNode)travel).getLabel().equals(n.getValue()))
                    return travel;
            }
            handleError("No ending label for " + n);
        }
        stack.add(0, n);
        return n.next();
    }

    protected StatementNode interpret(LabeledStatementNode n) throws Exception {
        if (!stack.isEmpty() && stack.get(0) instanceof WhileNode) {
            WhileNode back = (WhileNode)(stack.get(0));
            if (back.getValue().equals(n.getLabel()) && evaluateb(back.getCondition()))
                return back;
        }
        return n.getStatement();
    }

    protected StatementNode interpret(IfNode n) throws Exception {
        if (evaluateb(n.getCondition()))
            return labels.get(n.getTarget());
        return n.next();
    }

    protected StatementNode interpret(GosubNode n) throws Exception {
        stack.add(0, n.next());
        return labels.get(n.getValue());
    }

    protected StatementNode interpret(ForNode n) throws Exception {
        var varName = n.getVar().getValue();
        intVars.put(varName, n.getStart());
        if (n.getStart() > n.getEnd()) {
            StatementNode travel;
            for (travel = n.next(); !(travel instanceof NextNode); travel = travel.next())
                if (travel instanceof LabeledStatementNode)
                    travel = ((LabeledStatementNode)travel).getStatement();
            if (!(travel instanceof NextNode))
                handleError("No NEXT for statement " + n);
            return travel.next();
        }

        stack.add(0, n);
        return n.next();
    }

    protected StatementNode interpret(NextNode n) throws Exception {
        // we can assume that the relevant for node is at the top of the stack
        // effectively, we assume nested loops can't cut each other off
        ForNode back = (ForNode)(stack.get(0));
        var varName = n.getVar().getValue();
        if (!varName.equals(back.getVar().getValue()))
            handleError("Mismatched variable for " + back + " and " + n);
        int track = intVars.get(varName);
        track += back.getInc();
        intVars.put(varName, track);
        if (track > back.getEnd()) {
            return n.next();
        }
        return back.next();
    }

    /** Generates print list for a PrintNode
     *  Called by interpret(), used explicitly for testing
     *  @throws NoSuchElementException if any element of the list is invalid
     */
    protected List<String> print(PrintNode n) throws Exception {
        var out = new LinkedList<String>();
        for (Node x : n.getPrints()) {
            if (x instanceof StringNode) {
                out.add(((StringNode)x).getValue());
                continue;
            }
            if (x instanceof VariableNode) {
                var name = ((VariableNode)x).getValue();
                if (name.endsWith("$")) {
                    out.add(stringVars.get(name));
                    continue;
                }
            }
            var intval = evaluate(x);
            if (!intval.isEmpty())
                out.add(intval.get().toString());
            else {
                var floatval = evaluatef(x);
                out.add(floatval.get().toString());
            }

        }
        return out;
    }

    protected void interpret(PrintNode n) throws Exception {
        ioList = print(n);
        fullOutList.addAll(ioList);
        if (!test) {
            for (String x : ioList)
                System.out.print(x);
            System.out.println();
        }
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
            else if (name.endsWith("$"))
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

        if (!test) {
            ioList = new LinkedList<String>();
            Scanner s = new Scanner(System.in);
            for (int i = 0; i < n.size(); i++)
                ioList.add(s.nextLine());
        }

        interpret(n, ioList);
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
