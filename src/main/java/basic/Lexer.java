package basic;
import java.lang.Character;
import java.util.*;
import basic.Token.TokenType;

public class Lexer {
    private LinkedList<Token> tokens;
    private CodeHandler reader;
    private int line;
    private int pos;

    public Lexer(String filename) {
        this.tokens = new LinkedList<Token>();
        this.reader = new CodeHandler(filename);
        this.line = 0;
        this.pos = 0;
    }

    public LinkedList<Token> lex() {
        // Potential strategy, assume type is number unless non-digits or multiple '.' found if can't use regex
        // Potential between-token regex: "^( |\t|\r)*"
        // Potential token regex: "^[^ \t\r\n]*"
        // Word sanitizer: "^[a-z][a-z0-9]*[$%]?"
        // Number sanitizer: "^[0-9]*\.?[0-9]*"
        // Clarifying question: verify that carriage return gets removed
        // Clarifying question: behavior at end of word (space/tab/newline required? handling of %/$)
        while (!reader.isDone()) {
            var next = reader.getChar();
            switch(next) {
                case ' ': // Space/tab consumption
                case '\t':
                case '\r': // Acts as a space because we are "between" tokens
                    reader.swallow();
                    pos++;
                    continue;
                case '\n': // Newline handling
                    reader.swallow();
                    tokens.add(new Token(TokenType.ENDOFLINE, line, pos));
                    line++;
                    pos = 0;
                    continue;
            }

            if (Character.isAlphabetic(next))
                tokens.add(processWord(next));
            else if (Character.isDigit(next))
                tokens.add(processNumber(next));


        }
        return this.tokens;
    }

    private Token processWord(char first) {
        String value = String.valueOf(first);
        while (!reader.isDone()) {
            var next = reader.peek(0);
            if (next == '\r') {
                reader.swallow();
                pos++;
            }
            else if (Character.isAlphabetic(next) || Character.isDigit(next) || next == '_') {
                value = value + String.valueOf(next);
                reader.swallow();
                pos++;
            }
            else if (next == '$' || next == '%') { // seemingly we don't care what's after this if it ends with this?
                value = value + String.valueOf(next);
                reader.swallow();
                pos++;
                break;
            }
            else break;
        }
        return new Token(TokenType.WORD, line, pos, value);
    }

    private Token processNumber(char first) {
        String value = String.valueOf(first);
        boolean decimal = false;
        while (!reader.isDone()) {
            var next = reader.peek(0);
            if (next == '\r') {
                reader.swallow();
                pos++;
            }
            else if (Character.isDigit(next)) {
                value = value + String.valueOf(next);
                reader.swallow();
                pos++;
            }
            else if (next == '.') {
                if (!decimal) {
                    value = value + String.valueOf(next);
                    reader.swallow();
                    pos++;
                    decimal = true;
                }
                else break; // stop consuming input if more than one decimal point
            }
            else break;
        }
        return new Token(TokenType.NUMBER, line, pos, value);
    }
}
