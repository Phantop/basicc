package basic;

import basic.Token.TokenType;
import basic.MathOpNode.Operation;
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

    public ProgramNode parse() throws Exception {
        var program = new ProgramNode();
        while (acceptSeparators()); // eat any separators before stuff
        while (reader.moreTokens()) {
            statements().ifPresent(x -> program.add(x));
            while (acceptSeparators()); // eat any separators between/after stuff
        }
        return program;
    }

    private boolean acceptSeparators() {
        return reader.matchAndRemove(TokenType.ENDOFLINE).isPresent();
    }

    private Optional<StatementsNode> statements() throws Exception {
        var out = new StatementsNode();
        Optional<StatementNode> line;
        do {
            line = statement();
            if (line.isPresent()) out.add(line.get());
            if (!acceptSeparators()) { // statements should be separated
                var next = reader.peek(0);
                var bad = next.get();
                System.err.format("Missing separator after statement at %d:%d\n", bad.getType().toString(), bad.getLine(), bad.getPos());
            }
            while (acceptSeparators()); // eat any additional separators
        } while (line.isPresent() && reader.moreTokens()); // tokens can finish while building statements list
        if (out.isEmpty()) return Optional.empty();
        return Optional.of(out);
    }

    /**
     * @returns one of the valid BASIC statements
     */
    private Optional<StatementNode> statement() throws Exception {
        var out = printStatement();
        if (out.isPresent()) return out;
        out = assignment();
        if (out.isPresent()) return out;
        return Optional.empty();
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
            var right = expression();
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
        // allowing for print to print nothing
        while (next.isPresent()) {
            out.add(expression());
            next = reader.matchAndRemove(TokenType.COMMA);
        }
        if (out.isEmpty()) return Optional.empty();
        return Optional.of(out);
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
     * Matches and returns a Factor: number | ( EXPRESSION )
     */
    private Node factor() throws Exception {
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
        else {
            next = reader.matchAndRemove(TokenType.WORD);
            if (next.isPresent()) {
                String val = next.get().getValue();
                return new VariableNode(val);
            }
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
        next = reader.peek(0);
        var bad = next.get();
        System.err.format("Invalid token '%s' at %d:%d\n", bad.getType().toString(), bad.getLine(), bad.getPos());
        throw new Exception();
    }
}
