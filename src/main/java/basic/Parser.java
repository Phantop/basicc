package basic;

import basic.Token.TokenType;
import basic.MathOpNode.Operation;
import basic.BooleanNode.Comparison;
import basic.FunctionNode.Invocation;
import java.util.LinkedList;
import java.util.Optional;

/**
 * The BASIC Parser, which creates an AST from an input list of tokens
 */
public class Parser {
    private TokenHandler reader;

    public Parser (LinkedList<Token> stream) throws Exception {
        reader = new TokenHandler(stream);
    }

    public StatementsNode parse() throws Exception {
        return statements();
    }

    private boolean acceptSeparators() {
        return reader.matchAndRemove(TokenType.ENDOFLINE).isPresent();
    }

    private void handleError(String msg) throws Exception {
        var next = reader.peek(0);
        var bad = next.get();
        System.err.format(msg, bad.getLine(), bad.getPos());
        System.out.println("TokenHandler starts with: " + next.get());
        throw new Exception();
    }

    private StatementsNode statements() throws Exception {
        var out = new StatementsNode();
        Optional<StatementNode> line;
        while (reader.moreTokens()) {
            line = statement();
            if (line.isPresent()) out.add(line.get());
            if (!acceptSeparators()) // statements should be separated
                handleError("Missing separator after statement at %d:%d\n");
            while (acceptSeparators()); // eat any additional separators
        }
        return out;
    }

    /**
     * @returns one of the valid BASIC statements
     */
    private Optional<StatementNode> statement() throws Exception {
        Optional<StatementNode> out = Optional.empty();

        Optional<Token> next = reader.matchAndRemove(TokenType.LABEL);
        while (acceptSeparators());

        if (!out.isPresent())
            out = printStatement();
        if (!out.isPresent())
            out = dataStatement();
        if (!out.isPresent())
            out = readStatement();
        if (!out.isPresent())
            out = inputStatement();
        if (!out.isPresent())
            out = gosubStatement();
        if (!out.isPresent())
            out = returnStatement();
        if (!out.isPresent())
            out = endStatement();
        if (!out.isPresent())
            out = forStatement();
        if (!out.isPresent())
            out = nextStatement();
        if (!out.isPresent())
            out = ifStatement();
        if (!out.isPresent())
            out = whileStatement();
        if (!out.isPresent())
            out = assignment();

        if (next.isPresent()) {
            // we assume that a label has a statement after it, so out isn't empty
            var labeled = new LabeledStatementNode(next.get().getValue(), out.get());
            return Optional.of(labeled);
        }

        return out;
    }

    private Optional<Node> functionInvocation() throws Exception {
        Invocation op = null;
        Optional<Token> next;

        if (op == null) {
            next = reader.matchAndRemove(TokenType.LEFT);
            if (next.isPresent())
                op = Invocation.LEFT;
        }
        if (op == null) {
            next = reader.matchAndRemove(TokenType.MID);
            if (next.isPresent())
                op = Invocation.MID;
        }
        if (op == null) {
            next = reader.matchAndRemove(TokenType.NUM);
            if (next.isPresent())
                op = Invocation.NUM;
        }
        if (op == null) {
            next = reader.matchAndRemove(TokenType.NUMF);
            if (next.isPresent())
                op = Invocation.NUMF;
        }
        if (op == null) {
            next = reader.matchAndRemove(TokenType.RANDOM);
            if (next.isPresent())
                op = Invocation.RANDOM;
        }
        if (op == null) {
            next = reader.matchAndRemove(TokenType.RIGHT);
            if (next.isPresent())
                op = Invocation.RIGHT;
        }
        if (op == null) {
            next = reader.matchAndRemove(TokenType.VAL);
            if (next.isPresent())
                op = Invocation.VAL;
        }
        if (op == null) {
            next = reader.matchAndRemove(TokenType.VALF);
            if (next.isPresent())
                op = Invocation.VALF;
        }
        if (op == null)
            return Optional.empty();

        var out = new FunctionNode(op);

        next = reader.matchAndRemove(TokenType.LPAREN);
        if (!next.isPresent())
            handleError("Missing opening parenthesis for function at %d:%d\n");

        boolean argless = reader.matchAndRemove(TokenType.RPAREN).isPresent();
        while (!argless && next.isPresent()) {
            next = reader.matchAndRemove(TokenType.STRINGLITERAL);
            if (next.isPresent()) {
                out.add(new StringNode(next.get().getValue()));
            }
            else {
                out.add(expression());
            }
            next = reader.matchAndRemove(TokenType.COMMA);
        }
        if (!argless) {
            next = reader.matchAndRemove(TokenType.RPAREN);
            if (!next.isPresent())
                handleError("Missing closing parenthesis for function at %d:%d\n");
        }

        return Optional.of(out);
    }

    private Optional<StatementNode> forStatement() throws Exception {
        Optional<Token> next;
        next = reader.matchAndRemove(TokenType.FOR);
        if (next.isPresent()) {
            next = reader.matchAndRemove(TokenType.WORD);
            if (!next.isPresent())
                handleError("Missing variable name for FOR at %d:%d\n");
            var variable = new VariableNode(next.get().getValue());

            next = reader.matchAndRemove(TokenType.EQUALS);
            if (!next.isPresent())
                handleError("Missing = sign for FOR at %d:%d\n");

            next = reader.matchAndRemove(TokenType.NUMBER);
            if (!next.isPresent())
                handleError("Missing starting number for FOR at %d:%d\n");
            int start = Integer.parseInt(next.get().getValue());

            next = reader.matchAndRemove(TokenType.TO);
            if (!next.isPresent())
                handleError("Missing TO for FOR at %d:%d\n");

            next = reader.matchAndRemove(TokenType.NUMBER);
            if (!next.isPresent())
                handleError("Missing ending number for FOR at %d:%d\n");
            int end = Integer.parseInt(next.get().getValue());

            next = reader.matchAndRemove(TokenType.STEP);
            if (!next.isPresent()) {
                var out = new ForNode(variable, start, end);
                return Optional.of(out);
            }

            next = reader.matchAndRemove(TokenType.NUMBER);
            if (!next.isPresent())
                handleError("Missing step number for FOR at %d:%d\n");
            int step = Integer.parseInt(next.get().getValue());
            var out = new ForNode(variable, start, end, step);
            return Optional.of(out);
        }
        return Optional.empty();
    }

    private Optional<StatementNode> nextStatement() throws Exception {
        Optional<Token> next;
        next = reader.matchAndRemove(TokenType.NEXT);
        if (next.isPresent()) {
            next = reader.matchAndRemove(TokenType.WORD);
            if (!next.isPresent())
                handleError("Missing variable for NEXT at %d:%d\n");
            var variable = new VariableNode(next.get().getValue());
            var out = new NextNode(variable);
            return Optional.of(out);
        }
        return Optional.empty();
    }

    private Optional<StatementNode> gosubStatement() throws Exception {
        Optional<Token> next;
        next = reader.matchAndRemove(TokenType.GOSUB);
        if (next.isPresent()) {
            next = reader.matchAndRemove(TokenType.WORD);
            if (!next.isPresent())
                handleError("Missing identifier for GOSUB at %d:%d\n");
            var out = new GosubNode(next.get().getValue());
            return Optional.of(out);
        }
        return Optional.empty();
    }

    private Optional<StatementNode> whileStatement() throws Exception {
        Optional<Token> next;
        next = reader.matchAndRemove(TokenType.WHILE);
        if (next.isPresent()) {
            var condition = booleanExpression();
            next = reader.matchAndRemove(TokenType.WORD);
            if (!next.isPresent())
                handleError("Missing end label for WHILEat %d:%d\n");
            var out = new WhileNode(condition, next.get().getValue());
            return Optional.of(out);
        }
        return Optional.empty();
    }

    private Optional<StatementNode> ifStatement() throws Exception {
        Optional<Token> next;
        next = reader.matchAndRemove(TokenType.IF);
        if (next.isPresent()) {
            var condition = booleanExpression();
            next = reader.matchAndRemove(TokenType.THEN);
            if (!next.isPresent()) {
                handleError("Missing then for IF at %d:%d\n");
            }
            next = reader.matchAndRemove(TokenType.WORD);
            if (!next.isPresent())
                handleError("Missing target label for IF at %d:%d\n");
            var out = new IfNode(condition, next.get().getValue());
            return Optional.of(out);
        }
        return Optional.empty();
    }

    private Optional<StatementNode> endStatement() {
        Optional<Token> next = reader.matchAndRemove(TokenType.END);
        if (next.isEmpty()) return Optional.empty();
        return Optional.of(new EndNode());
    }

    private Optional<StatementNode> returnStatement() {
        Optional<Token> next = reader.matchAndRemove(TokenType.RETURN);
        if (next.isEmpty()) return Optional.empty();
        return Optional.of(new ReturnNode());
    }

    /**
     * @returns inputNode as a StatementNode
     * looks for first a word or string literal, then variables
     */
    private Optional<StatementNode> inputStatement() throws Exception {
        Optional<Token> next;
        next = reader.matchAndRemove(TokenType.INPUT);
        if (!next.isPresent()) return Optional.empty();

        InputNode out = null;
        next = reader.matchAndRemove(TokenType.STRINGLITERAL);
        if (next.isPresent()) {
            out = new InputNode(new StringNode(next.get().getValue()));
            next = reader.matchAndRemove(TokenType.COMMA);
        }
        else next = reader.peek(0); // just to allow this to work right if its from var
        while (next.isPresent()) {
            next = reader.matchAndRemove(TokenType.WORD);
            if (!next.isPresent())
                handleError("Missing variable for INPUT at %d:%d\n");
            if (out == null)
                out = new InputNode(new VariableNode(next.get().getValue()));
            else
                out.add(new VariableNode(next.get().getValue()));
            next = reader.matchAndRemove(TokenType.COMMA);
        }
        if (out == null || out.isEmpty())
            handleError("Missing variable for INPUT at %d:%d\n");
        return Optional.of(out);
    }

    /**
     * @returns readNode as a StatementNode
     * similar to dataStatement, but only looks for variables (WORD)
     */
    private Optional<StatementNode> readStatement() throws Exception {
        Optional<Token> next;
        var out = new ReadNode();
        next = reader.matchAndRemove(TokenType.READ);
        while (next.isPresent()) {
            next = reader.matchAndRemove(TokenType.WORD);
            if (!next.isPresent())
                handleError("Missing variable for READ at %d:%d\n");
            out.add(new VariableNode(next.get().getValue()));
            next = reader.matchAndRemove(TokenType.COMMA);
        }
        if (out.isEmpty()) return Optional.empty();
        return Optional.of(out);
    }

    /**
     * @returns dataNode as a StatementNode
     * basically functions like printStatement()
     */
    private Optional<StatementNode> dataStatement() throws Exception {
        Optional<Token> next;
        var out = new DataNode();
        next = reader.matchAndRemove(TokenType.DATA);
        while (next.isPresent()) {
            next = reader.matchAndRemove(TokenType.STRINGLITERAL);
            if (next.isPresent())
                out.add(new StringNode(next.get().getValue()));
            else
                out.add(expression());
            next = reader.matchAndRemove(TokenType.COMMA);
        }
        if (out.isEmpty()) return Optional.empty();
        return Optional.of(out);
    }


    /**
     * @returns assignmentNode as a StatementNode
     */
    private Optional<StatementNode> assignment() throws Exception {
        Optional<Token> next;
        // must start with variable word
        next = reader.matchAndRemove(TokenType.WORD);
        if (next.isPresent()) {
            var left = next.get();
            next = reader.matchAndRemove(TokenType.EQUALS);
            if (!next.isPresent()) {
                System.err.format("Missing '=' for assigning variable %s at %d:%d\n", left.getValue(), left.getLine(), left.getPos());
                throw new Exception();
            }
            Node right;
            next = reader.matchAndRemove(TokenType.STRINGLITERAL);
            if (next.isPresent())
                right = new StringNode(next.get().getValue());
            else
                right = expression();

            var leftnode = new VariableNode(left.getValue());
            var out = new AssignmentNode(leftnode, right);
            return Optional.of(out);
        }
        return Optional.empty();
    }

    /**
     * @returns printNode as a StatementNode
     */
    private Optional<StatementNode> printStatement() throws Exception {
        Optional<Token> next;
        var out = new PrintNode();
        next = reader.matchAndRemove(TokenType.PRINT);
        while (next.isPresent()) {
            // we can currently print strings or expressions
            next = reader.matchAndRemove(TokenType.STRINGLITERAL);
            if (next.isPresent())
                out.add(new StringNode(next.get().getValue()));
            else
                out.add(expression());
            next = reader.matchAndRemove(TokenType.COMMA);
        }
        if (out.isEmpty()) return Optional.empty();
        return Optional.of(out);
    }

    /**
     * Matches and returns a booleans Expression: exp {<|>|=|<>|<=|>=} exp
     */
    private BooleanNode booleanExpression() throws Exception {
        Optional<Token> next;
        Comparison comp = null;
        Node left = expression();

        if (comp == null) {
            next = reader.matchAndRemove(TokenType.EQUALS);
            if (next.isPresent())
                comp = Comparison.EQUALS;
        }
        if (comp == null) {
            next = reader.matchAndRemove(TokenType.LESS);
            if (next.isPresent())
                comp = Comparison.LESS;
        }
        if (comp == null) {
            next = reader.matchAndRemove(TokenType.GREATER);
            if (next.isPresent())
                comp = Comparison.GREATER;
        }
        if (comp == null) {
            next = reader.matchAndRemove(TokenType.NOTEQUALS);
            if (next.isPresent())
                comp = Comparison.NOTEQUALS;
        }
        if (comp == null) {
            next = reader.matchAndRemove(TokenType.LEQ);
            if (next.isPresent())
                comp = Comparison.LEQ;
        }
        if (comp == null) {
            next = reader.matchAndRemove(TokenType.GEQ);
            if (next.isPresent())
                comp = Comparison.GEQ;
        }
        if (comp == null) {
            handleError("Missing comparator for boolean at %d:%d\n");
        }

        Node right = expression();
        var out = new BooleanNode(left, comp, right);

        System.out.println(out);
        return out;
    }


    /**
     * Matches and returns an Expression: TERM {+|- TERM}
     */
    private Node expression() throws Exception {
        Optional<Token> next;
        Node left = term();
        while (true) {
            next = reader.matchAndRemove(TokenType.PLUS);
            if (next.isPresent()) {
                left = new MathOpNode(left, Operation.ADD, term());
                continue;
            }
            next = reader.matchAndRemove(TokenType.MINUS);
            if (next.isPresent()) {
                left = new MathOpNode(left, Operation.SUBTRACT, term());
                continue;
            }
            break;
        }
        return left;
    }

    /**
     * Matches and returns a Term: FACTOR {*|/ FACTOR}
     */
    private Node term() throws Exception {
        Optional<Token> next;
        Node left = factor();
        while (true) {
            next = reader.matchAndRemove(TokenType.MULTIPLY);
            if (next.isPresent()) {
                left = new MathOpNode(left, Operation.MULTIPLY, factor());
                continue;
            }
            next = reader.matchAndRemove(TokenType.DIVIDE);
            if (next.isPresent()) {
                left = new MathOpNode(left, Operation.DIVIDE, factor());
                continue;
            }
            break;
        }
        return left;
    }

    /**
     * Matches and returns a Factor: number | variable | ( EXPRESSION )
     */
    private Node factor() throws Exception {
        var func = functionInvocation();
        if (func.isPresent())
            return func.get();

        Optional<Token> next;
        int sign = 1;
        next = reader.matchAndRemove(TokenType.MINUS);
        if (next.isPresent()) sign = -1;
        next = reader.matchAndRemove(TokenType.NUMBER);
        if (next.isPresent()) {
            try {
                int val = sign * Integer.parseInt(next.get().getValue());
                return new IntegerNode(val);
            }
            catch (NumberFormatException e)
            {
                float val = sign * Float.parseFloat(next.get().getValue());
                return new FloatNode(val);
            }
        }
        next = reader.matchAndRemove(TokenType.WORD);
        if (next.isPresent()) {
            String val = next.get().getValue();
            return new VariableNode(val);
        }
        next = reader.matchAndRemove(TokenType.LPAREN);
        if (next.isPresent()) {
            Node expNode = expression();
            var bad = next.get();
            next = reader.matchAndRemove(TokenType.RPAREN);
            if (!next.isPresent()) {
                System.err.format("Missing closing ')' for opening '(' at %d:%d\n", bad.getLine(), bad.getPos());
                throw new Exception();
            }
            return expNode;
        }
        handleError("Invalid token at %d:%d\n");
        throw new Exception();
    }
}
