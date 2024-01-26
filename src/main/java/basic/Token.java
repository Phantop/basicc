package basic;

/**
 * Represents a lexer token of one of three types, also storing
 * the line, position of starting character, and string value if applicable
 */
public class Token {
    enum TokenType { WORD, NUMBER, ENDOFLINE };

    private TokenType type;
    private String value;
    private int line;
    private int pos;

    /* Valueless constructor. Intended specifically for ENDOFLINE tokens currently
     * @param type the token type
     * @param line the file line at which the token was found
     * @param pos the in line position of the first character of the token
     */
    public Token(TokenType type, int line, int pos) {
        this(type, line, pos, null);
    }

    /* Value-providing constructor. Intended for WORD/NUMBER tokens currently
     * @param type the token type
     * @param line the file line at which the token was found
     * @param pos the in line position of the first character of the token
     * @param value the in-file textual value of the token as a string
     */
    public Token(TokenType type, int line, int pos, String value) {
        this.line = line;
        this.pos = pos;
        this.type = type;
        this.value = value;
    }

    /* toString method
     * @return will first give the token type, and then:
     * ENDOFLINE: a newline
     * WORD/NUMBER: the value in parenthesis followed by a space
     */
    public String toString() {
        if (value == null) return type.toString() + "\n";
        return type + "(" + value + ")" + " ";
    }
}
