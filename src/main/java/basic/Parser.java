package basic;

import basic.Token.TokenType;
import basic.MathOpNode.Operation;
import java.io.IOException;
import java.lang.Character;
import java.util.HashMap;
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
        while (acceptSeparators()); // eat any separators between stuff
        while (reader.moreTokens()) {
            program.add(expression()); // just assume anything not a separator is an expression for now
            while (acceptSeparators()); // eat any separators between stuff
        }
        return program;
    }

    private boolean acceptSeparators() {
        return reader.matchAndRemove(TokenType.ENDOFLINE).isPresent();
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
