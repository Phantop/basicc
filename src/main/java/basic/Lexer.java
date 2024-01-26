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
        // Clarifying answer: carriage return just entirely act like it isn't there (leave pos alone)
        // Clarifying answer: $/% means multiple words regardless of spacing
        // Clarifying answer: multiples .s in number mean new number at the second
        while (!reader.isDone()) {
            char next = reader.getChar();
            switch(next) {
                case ' ': // Space/tab consumption
                case '\t':
                    pos++;
                case '\r': // Doesn't increment pos like the other two
                    continue;
                case '\n': // Newline handling
                    tokens.add(new Token(TokenType.ENDOFLINE, line, pos));
                    line++;
                    pos = 0;
                    continue;
            }

            // these two don't swallow or move pos just to make programming
            // process number and word a tiny bit simpler
            if (Character.isDigit(next))
                tokens.add(processNumber(next, pos++));
            else if (Character.isAlphabetic(next))
                tokens.add(processWord(next, pos++));
            else {
                System.err.format("Invalid character %c at %d:%d", next, line, pos);
                System.exit(1);
            }


        }
        return this.tokens;
    }

    private Token processWord(char next, int ipos) {
        String value = String.valueOf(next);
        while (!reader.isDone()) {
            next = reader.peek(0);
            if (next == '\r') {
                reader.swallow();
                pos++;
            }
            else if (Character.isAlphabetic(next) || Character.isDigit(next) || next == '_') {
                value = value + String.valueOf(next);
                reader.swallow();
                pos++;
            }
            else if (next == '$' || next == '%') { // always considered end of word
                value = value + String.valueOf(next);
                reader.swallow();
                pos++;
                break;
            }
            else break;
        }
        return new Token(TokenType.WORD, line, ipos, value);
    }

    private Token processNumber(char next, int ipos) {
        String value = String.valueOf(next);
        boolean decimal = false;
        while (!reader.isDone()) {
            next = reader.peek(0);
            if (next == '\r') {
                reader.swallow(); //not considered a real position
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
        return new Token(TokenType.NUMBER, line, ipos, value);
    }
}
