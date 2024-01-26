package basic;
import java.lang.Character;
import java.util.LinkedList;
import java.lang.Exception;
import java.io.IOException;
import basic.Token.TokenType;

public class Lexer {
    private CodeHandler reader;
    private int line;
    private int pos;

    public Lexer(String filename) throws IOException {
        this.reader = new CodeHandler(filename);
        this.line = 1;
        this.pos = 0;
    }

    public LinkedList<Token> lex() throws Exception {
        var tokens = new LinkedList<Token>();
        while (!reader.isDone()) {
            char next = reader.getChar();
            switch(next) {
                case ' ': // Space/tab consumption
                case '\t':
                    pos++;
                case '\r': // Doesn't increment pos like the other two
                    continue;
                case '\n': // Newline handling, lex is per line according to rubric
                    tokens.add(new Token(TokenType.ENDOFLINE, line, pos));
                    line++;
                    pos = 0;
                    return tokens;
            }

            // these two don't swallow or move pos just to make programming
            // process number and word a tiny bit simpler
            if (Character.isDigit(next) || next == '.')
                tokens.add(processNumber(next, pos++));
            else if (Character.isAlphabetic(next))
                tokens.add(processWord(next, pos++));
            else {
                System.err.format("Invalid character %c at %d:%d", next, line, pos);
                throw new Exception();
            }


        }
        return tokens;
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
