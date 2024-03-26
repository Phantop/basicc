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
        tokens.add(new Token(TokenType.WORD, 0, 0, "z"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "z"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.PRINT, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "y"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "z"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "z"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "z=\"z\"\nPRINT (x+y), z, \"z\",\n\n";
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
    public void testBadParenthesis() throws Exception {
        var tokens = new LinkedList<Token>();
        // token representation of 'print 4+(5+4'
        tokens.add(new Token(TokenType.PRINT, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "4"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "5"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "4"));
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

    @Test
    public void testEmptyFile() throws Exception {
        var tokens = new LinkedList<Token>();
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "";
        Assert.assertEquals(expected, output);
    }

    @Test(expected = Exception.class)
    public void testStatementSeparation() throws Exception {
        var tokens = new LinkedList<Token>();
        // token representation of 'x=3.1415\nx=
        tokens.add(new Token(TokenType.PRINT, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "y"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "z"));
        tokens.add(new Token(TokenType.PRINT, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "y"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "z"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }

    @Test
    public void testValidInput() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.INPUT, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "The cow goes moo"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "y"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.INPUT, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "cow"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "y"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected =
            "INPUT \"The cow goes moo\", x, y\n"+
            "INPUT cow, x, y\n\n";
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testValidDataRead() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.DATA, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "The cow goes moo"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
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
        tokens.add(new Token(TokenType.READ, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "y"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected =
            "DATA \"The cow goes moo\", ((4+((-6.11/-2)*(-1--2.12)))+5),\n"+
            "READ x, y,\n\n";
        Assert.assertEquals(expected, output);
    }

    @Test(expected = Exception.class)
    public void testIncompleteAssign() throws Exception {
        var tokens = new LinkedList<Token>();
        // token representation of 'x'
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }

    @Test(expected = Exception.class)
    public void testIncompleteInput() throws Exception {
        var tokens = new LinkedList<Token>();
        // token representation of 'INPUT'
        tokens.add(new Token(TokenType.INPUT, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }

    @Test
    public void testBasicFor() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.FOR, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.TO, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "10"));
        tokens.add(new Token(TokenType.STEP, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.FOR, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.TO, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "10"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.GOSUB, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "lmao"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.NEXT, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "FOR x = 2 TO 10 STEP 2\n" + "FOR x = 2 TO 10 STEP 1\n" + "GOSUB lmao\n" + "NEXT x\n\n";
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testBasicWhile() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WHILE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.RANDOM, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "lmao"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "WHILE (x+2)=RANDOM() lmao\n\n";
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testBasicIf() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.LABEL, 0, 0, "back"));
        tokens.add(new Token(TokenType.IF, 0, 0));
        tokens.add(new Token(TokenType.LEFT, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "The cow goes moo"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "I don't say meow"));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.LEQ, 0, 0));
        tokens.add(new Token(TokenType.RIGHT, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "4.2069"));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.THEN, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "lmao"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.END, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.GOSUB, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "back"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.RETURN, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "back: IF LEFT(\"The cow goes moo\", \"I don't say meow\", )<=RIGHT(4.2069, ) THEN lmao\n" +
            "END\n" +
            "GOSUB back\n" +
            "RETURN\n\n";
        Assert.assertEquals(expected, output);
    }

    @Test(expected = Exception.class)
    public void testIncompleteFor1() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.FOR, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }
    @Test(expected = Exception.class)
    public void testIncompleteFor2() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.FOR, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }
    @Test(expected = Exception.class)
    public void testIncompleteFor3() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.FOR, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }
    @Test(expected = Exception.class)
    public void testIncompleteFor4() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.FOR, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }
    @Test(expected = Exception.class)
    public void testIncompleteFor5() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.FOR, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.TO, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }
    @Test(expected = Exception.class)
    public void testIncompleteFor6() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.FOR, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.TO, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "10"));
        tokens.add(new Token(TokenType.STEP, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }
    @Test
    public void testLessThan() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WHILE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.LESS, 0, 0));
        tokens.add(new Token(TokenType.RANDOM, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "lmao"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "WHILE (x+2)<RANDOM() lmao\n\n";
        Assert.assertEquals(expected, output);
    }
    @Test
    public void testGreaterThan() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WHILE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.GREATER, 0, 0));
        tokens.add(new Token(TokenType.RANDOM, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "lmao"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "WHILE (x+2)>RANDOM() lmao\n\n";
        Assert.assertEquals(expected, output);
    }
    @Test
    public void testLEQ() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WHILE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.LEQ, 0, 0));
        tokens.add(new Token(TokenType.RANDOM, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "lmao"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "WHILE (x+2)<=RANDOM() lmao\n\n";
        Assert.assertEquals(expected, output);
    }
    @Test
    public void testGEQ() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WHILE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.PLUS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.GEQ, 0, 0));
        tokens.add(new Token(TokenType.RANDOM, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "lmao"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "WHILE (x+2)>=RANDOM() lmao\n\n";
        Assert.assertEquals(expected, output);
    }
    @Test(expected = Exception.class)
    public void testBadBoolean() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.FOR, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.TO, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "10"));
        tokens.add(new Token(TokenType.STEP, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
    }

    @Test
    public void testFunctionFactor() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.MULTIPLY, 0, 0));
        tokens.add(new Token(TokenType.RANDOM, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "x=(2*RANDOM())\n\n";
        Assert.assertEquals(expected, output);
    }
}
