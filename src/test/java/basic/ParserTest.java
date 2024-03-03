package basic;

import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import java.util.LinkedList;
import basic.Token.TokenType;

public class ParserTest {

    private static Parser p = null;

    /**
     * Tests accurate pemdas, float and int support, negation
     */
    @Test
    public void testOne() throws Exception {
        var tokens = new LinkedList<Token>();
        // going to just set positions to 0 for this test
        // token representation of 'x=4+-6.11/-2*(-1--2.12)+5'
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "4"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.MINUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "6.11"));
        tokens.add(new Token(TokenType.DIVIDE, 0, 0));
        tokens.add(new Token(TokenType.MINUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.MULTIPLY, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.MINUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "1"));
        tokens.add(new Token(TokenType.MINUS, 0, 0));
        tokens.add(new Token(TokenType.MINUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2.12"));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "5"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "x=((4+((-6.11/-2)*(-1--2.12)))+5)\n\n";
        Assert.assertEquals(expected, output);
    }

    @Test(expected = Exception.class)
    public void testBadParent() throws Exception {
        var tokens = new LinkedList<Token>();
        // token representation of 'x=4+(5+'
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "4"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "5"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }

    @Test
    public void testSeparators() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "3.1415"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "y"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "x=3.1415\nx=y\n\n"; 
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testPrint() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.PRINT, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "y"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "z"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "PRINT (x+y), z,\n\n";
        Assert.assertEquals(expected, output);
    }

    @Test(expected = Exception.class)
    public void testBadToken() throws Exception {
        var tokens = new LinkedList<Token>();
        // token representation of 'print 4+(5+*'
        tokens.add(new Token(TokenType.PRINT, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "4"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "5"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.MULTIPLY, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }

    @Test(expected = Exception.class)
    public void testBadAssignment() throws Exception {
        var tokens = new LinkedList<Token>();
        // token representation of 'x=3.1415\nx=
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "3.1415"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }
}
