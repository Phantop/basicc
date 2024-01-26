package hw1;
import java.util.*;

public class Token {
    enum TokenType { WORD, NUMBER, ENDOFLINE };

    TokenType type;
    String value;
    int line;
    int pos;

    public Token(TokenType type, int line, int pos) {
        this(type, line, pos, null);
    }

    public Token(TokenType type, int line, int pos, String value) {
        this.line = line;
        this.pos = pos;
        this.type = type;
        this.value = value;
    }

    public String toString() {
        if (value == null) return type.toString();
        return type + "(" + value + ")";
    }
}
