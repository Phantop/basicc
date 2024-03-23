package basic;

/**
 * Represents a lexer token of one of three types, also storing
 * the line, position of starting character, and string value if applicable
 */
public class Token {
    enum TokenType {
        // special
        ENDOFLINE,
        LABEL,
        NUMBER,
        STRINGLITERAL,
        WORD,

        // single char
        COMMA,
        DIVIDE,
        EQUALS, // THERE IS NO == FOR EQUALITY, IT'S JUST ONE =
        GREATER,
        LESS,
        LPAREN,
        MINUS,
        MULTIPLY,
        PLUS,
        RPAREN,

        // two chars
        LEQ,
        NOTEQUALS,
        GEQ,

        // special words
        DATA,
        END,
        FOR,
        FUNCTION,
        GOSUB,
        IF,
        INPUT,
        NEXT,
        PRINT,
        READ,
        RETURN,
        STEP,
        THEN,
        TO,
        WHILE,

        // function invocations
        LEFT,
        MID,
        NUM,
        RANDOM,
        RIGHT,
        VAL,
        VALF
    };

    private final TokenType type;
    private final String value;
    private final int line;
    private final int pos;

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
        if (type == TokenType.ENDOFLINE) return type.toString() + "\n";
        if (value == null) return type.toString() + " ";
        return type + "(" + value + ")" + " ";
    }

    /* 
     * @return the type of this token
     */
    public TokenType getType() {
        return type;
    }

    /* 
     * @return the line of this token
     */
    public int getLine() {
        return line;
    }

    /* 
     * @return the in-line position of this token
     */
    public int getPos() {
        return pos;
    }

    /* 
     * @return the value of this token. null if no value
     */
    public String getValue() {
        return value;
    }
}
