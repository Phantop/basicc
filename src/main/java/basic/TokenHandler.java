package basic;

import basic.Token.TokenType;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Handles reading for a single inputted filename
 */
public class TokenHandler {
    private LinkedList<Token> stream;

    /** Creates CodeHandler
     * @param filename string path to input file
     * @throws IOException on invalid file
     */
    public TokenHandler(LinkedList<Token> stream) {
        this.stream = stream;
    }

    public Optional<Token> peek(int j) {
        if (j >= stream.size())
            return Optional.empty();
        return Optional.of(stream.get(j));
    }

    public boolean moreTokens() {
        return (stream.size() > 0);
    }

    /**
     * If the token type of the head is the same as what was passed in, removes that token from the list and returns it. 
     * In all other cases, returns Optional.Empty()
     */
    Optional<Token> matchAndRemove(TokenType t) {
        if (moreTokens() && stream.peek().getType() == t)
            return Optional.of(stream.pop());
        return Optional.empty();
    }
}
