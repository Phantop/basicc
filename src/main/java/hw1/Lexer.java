package hw1;
import java.util.*;

public class Lexer {
    LinkedList<Token> tokens;
    CodeHandler reader;
    int line;
    int pos;

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
        // Word sanitizer: "^[a-zA-Z0-9_]+[$%]?"
        // Number sanitizer: "^[0-9]*\.?[0-9]*"
        String work = null;
        Token.TokenType workType = null;
        while (!reader.isDone()) {
            switch(reader.peek(0)) {
                case ' ': // Space/tab consumption
                case '\t':
                case '\r':
                    reader.swallow();
                    pos++;
                    break;
                case '\n': // Newline handling
                    reader.swallow();
                    tokens.add(new Token(Token.TokenType.ENDOFLINE, line, pos));
                    line++;
                    pos = 0;
            }


        }
        return this.tokens;
    }
}
