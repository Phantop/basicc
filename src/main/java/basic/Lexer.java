package basic;

import java.lang.Character;
import java.util.LinkedList;
import java.io.IOException;
import basic.Token.TokenType;

/**
 * The BASIC Lexer, which tokenizes the input file
 */
public class Lexer {
    private CodeHandler reader;
    private int line;
    private int pos;

    /** Constructor. Reads file and attempts to create a CodeHandler for it
     * Also sets the line and pos to the start of the file
     * @param filename the filename
     * @throws IOException if the CodeHandler fails to read the file
     */
    public Lexer(String filename) throws IOException {
        this.reader = new CodeHandler(filename);
        this.line = 1; // lines are indexed from 1
        this.pos = 0;
    }

    /** Line lexing method. Reads in the next line of the input and returns
     * the tokens generated in the process
     * @return a linked list of tokens found in the line of input. Will be size 0 if the full file has been read.
     * @throws Exception if lexer encounters an invalid character
     */
    public LinkedList<Token> lex() throws Exception {
        var tokens = new LinkedList<Token>();
        while (!reader.isDone()) { // first char of all tokens can be safely swallowed
            char next = reader.getChar();
            switch(next) {
                case ' ': // Space/tab consumption
                case '\t':
                    pos++;
                    continue;
                case '\r': // Doesn't increment pos like the other two, basically not there
                    continue;
                case '\n': // Newline handling, lex is per line according to rubric
                    tokens.add(new Token(TokenType.ENDOFLINE, line, pos));
                    line++;
                    pos = 0;
                    continue;
            }

            // pass in first letter to processing and increment pos
            if (Character.isDigit(next) || next == '.')
                tokens.add(processNumber(next, pos++));
            else if (Character.isAlphabetic(next))
                tokens.add(processWord(next, pos++));
            else {
                System.err.format("Invalid character '%c' at %d:%d\n", next, line, pos);
                throw new Exception();
            }
        }
        return tokens;
    }

    /** WORD token processor
     * Accepts letters, digits, and _ in token. Can end in $/%.
     * Stops consuming input at all other characters
     * @param next first character in the token
     * @param ipos position of first character of the token
     * @return full generated token according to rules for what is allowed in a WORD
     */
    private Token processWord(char next, int ipos) {
        String value = String.valueOf(next);
        while (!reader.isDone()) {
            next = reader.peek(0);
            if (next == '\r') {
                reader.swallow(); //not considered a real position
            }
            else if (Character.isAlphabetic(next) || Character.isDigit(next) || next == '_') {
                value = addNext(value);
            }
            else if (next == '$' || next == '%') { // always considered end of word
                value = addNext(value);
                break;
            }
            else break;
        }
        return new Token(TokenType.WORD, line, ipos, value);
    }

    /** NUMBER token processor
     * Accepts digits and one .
     * Stops consuming input at all other characters other a second .
     * @param next first character in the token
     * @param ipos position of first character of the token
     * @return full generated token according to rules for what is allowed in a NUMBER
     */
    private Token processNumber(char next, int ipos) {
        String value = String.valueOf(next);
        boolean decimal = false; // track if decimal has been placed yet
        while (!reader.isDone()) {
            next = reader.peek(0);
            if (next == '\r') {
                reader.swallow(); //not considered a real position
            }
            else if (Character.isDigit(next)) {
                value = addNext(value);
            }
            else if (next == '.' && !decimal) {
                value = addNext(value);
                decimal = true;
            }
            else break;
        }
        return new Token(TokenType.NUMBER, line, ipos, value);
    }

    /** Consistent method of adding next char in reader to input string
     * @param value input string so far
     * @return input string plus next char in reader
     * @modifies increments reader read head
     */
    private String addNext(String value) {
        pos++;
        return value + reader.getChar();
    }
}
