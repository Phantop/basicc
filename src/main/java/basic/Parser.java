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
        while (reader.moreTokens()) {
            while (acceptSeparators()); // eat any separators between stuff
            program.add(expression()); // just assume anything not a separator is an expression for now
        }
        return program;
    }

    private boolean acceptSeparators() {
        return reader.matchAndRemove(TokenType.ENDOFLINE).isPresent();
    }

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
            next = reader.matchAndRemove(TokenType.RPAREN);
            if (!next.isPresent()) throw new Exception();
            return expNode;
        }
        throw new Exception();
    }
}
