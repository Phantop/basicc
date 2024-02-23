package basic;

import basic.Token.TokenType;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Handles reading for a list of lexed tokens
 */
public class TokenHandler {
    private LinkedList<Token> stream;

    /** Creates TokenHandler
     * @param input list
     * @throws Exception on invalid stream 
     */
    public TokenHandler(LinkedList<Token> stream) throws Exception {
        if (stream == null) throw new Exception();
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
