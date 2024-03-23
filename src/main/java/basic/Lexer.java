package basic;

import basic.Token.TokenType;
import java.io.IOException;
import java.lang.Character;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The BASIC Lexer, which tokenizes the input file
 */
public class Lexer {
    private int line;
    private int pos;
    private CodeHandler reader;
    private static HashMap<String, TokenType> knownSymbols;
    private static HashMap<String, TokenType> knownWords;

    /** Constructor. Reads file and attempts to create a CodeHandler for it
     * Also sets the line and pos to the start of the file
     * @param filename the filename
     * @throws IOException if the CodeHandler fails to read the file
     */
    public Lexer(String filename) throws IOException {
        this.reader = new CodeHandler(filename);
        this.line = 1; // lines are indexed from 1
        this.pos = 0;
        if (knownWords == null)
            buildMaps();
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
                case '"':
                    tokens.add(processLiteral(next, pos++));
                    continue;
            }

            // pass in first letter to processing and increment pos
            if (Character.isDigit(next) || next == '.')
                tokens.add(processNumber(next, pos++));
            else if (Character.isAlphabetic(next))
                tokens.add(processWord(next, pos++));
            else if (knownSymbols.containsKey(String.valueOf(next))) {
                tokens.add(processSymbol(next, pos++));
            }
            else {
                System.err.format("Invalid token '%c' at %d:%d\n", next, line, pos);
                throw new Exception();
            }
        }
        // final line *must* end with this, even if there's no final newline
        if (tokens.getLast().getType() != TokenType.ENDOFLINE)
            tokens.add(new Token(TokenType.ENDOFLINE, line, pos));
        return tokens;
    }

    /** WORD token processor
     * Accepts letters, digits, and _ in token. Can end in $/%/:.
     * Stops consuming input at all other characters
     * @param next first character in the token
     * @param ipos position of first character of the token
     * @return full generated token according to rules for what is allowed in a WORD
     * may be a token for a keyword
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
            else if (next == ':') {
                pos++;
                reader.swallow();
                return new Token(TokenType.LABEL, line, ipos, value);
            }
            else break;
        }

        if (knownWords.containsKey(value.toLowerCase()))
            return new Token(knownWords.get(value.toLowerCase()), line, ipos, null);
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

    /** Symbol token processor
     * Accepts known symbols, checks next char for two-char symbols
     * @param next first character in the token
     * @param ipos position of first character of the token
     * @return associated token for found symbol
     */
    private Token processSymbol(char next, int ipos) {
        String value = String.valueOf(next);
        // kinda cheat-y way of accounting for two char symbols
        if (reader.remainder().length() > 0 && knownSymbols.containsKey(value + reader.peekString(1))) {
            value = addNext(value);
        }
        return new Token(knownSymbols.get(value), line, ipos, null);
    }

    /** STRINGLITERAL token processor
     * Reads in all content from read head until next unescaped "
     * @param next first character in the token
     * @param ipos position of first " of token
     * @return literal token with value of contents
     */
    private Token processLiteral(char next, int ipos) throws Exception {
        String value = "";
        while (!reader.isDone()) {
            next = reader.peek(0);
            if (next == '\r') {
                reader.swallow(); //not considered a real position
            }
            else if (next == '\\' && reader.peek(1) == '"') {
                pos++;
                reader.swallow();
                value = addNext(value);
            }
            else if (next == '"') { // always considered end of word
                pos++;
                reader.swallow();
                return new Token(TokenType.STRINGLITERAL, line, ipos, value);
            }
            else if (next == '\n') { // still need to handle newlines in a literal
                line++;
                pos = 0;
                value = value + reader.getChar();
            }
            else 
                value = addNext(value);
        }
        System.err.format("Unclosed string literal at %d:%d\n", line, ipos);
        throw new Exception();
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

    /** Builds hashmaps for known words and symbols
     * Reinitializes both hashmaps on each run
     * @modifies knownWords to have known words
     * @modifies knownSymbols to have known syombols
     */
    private static void buildMaps() {
        knownWords = new HashMap<String, TokenType>();
        knownWords.put("data", TokenType.DATA);
        knownWords.put("end", TokenType.END);
        knownWords.put("for", TokenType.FOR);
        knownWords.put("function", TokenType.FUNCTION);
        knownWords.put("gosub", TokenType.GOSUB);
        knownWords.put("if", TokenType.IF);
        knownWords.put("input", TokenType.INPUT);
        knownWords.put("next", TokenType.NEXT);
        knownWords.put("print", TokenType.PRINT);
        knownWords.put("read", TokenType.READ);
        knownWords.put("return", TokenType.RETURN);
        knownWords.put("step", TokenType.STEP);
        knownWords.put("then", TokenType.THEN);
        knownWords.put("to", TokenType.TO);
        knownWords.put("while", TokenType.WHILE);

        knownWords.put("left$", TokenType.LEFT);
        knownWords.put("right$", TokenType.RIGHT);
        knownWords.put("random", TokenType.RANDOM);
        knownWords.put("mid$", TokenType.MID);
        knownWords.put("num$", TokenType.NUM);
        knownWords.put("val", TokenType.VAL);
        knownWords.put("val%", TokenType.VALF);

        knownSymbols = new HashMap<String, TokenType>();
        knownSymbols.put(",", TokenType.COMMA);
        knownSymbols.put("/", TokenType.DIVIDE);
        knownSymbols.put("=", TokenType.EQUALS);
        knownSymbols.put(">", TokenType.GREATER);
        knownSymbols.put("<", TokenType.LESS);
        knownSymbols.put("(", TokenType.LPAREN);
        knownSymbols.put("-", TokenType.MINUS);
        knownSymbols.put("*", TokenType.MULTIPLY);
        knownSymbols.put("+", TokenType.PLUS);
        knownSymbols.put(")", TokenType.RPAREN);
        knownSymbols.put("<=", TokenType.LEQ);
        knownSymbols.put("<>", TokenType.NOTEQUALS);
        knownSymbols.put(">=", TokenType.GEQ);
    }
}
