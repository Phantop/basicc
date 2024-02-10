package basic;

import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;

public class LexerTest{

    private static Lexer l = null;

    @Test
    public void testOpen() throws IOException {
        l = new Lexer("example.txt");
    }

    @Test(expected = IOException.class)
    public void testOpenFail() throws IOException {
        l = new Lexer("invalid.txt");
    }

    @Test
    public void testNiceList() throws Exception {
        testOpen();
        String expectedList =
            "WORD(an) WORD(empty_line$) WORD(is) WORD(belw) ENDOFLINE\n" +
            "ENDOFLINE\n" +
            "NUMBER(5) WORD(hello) ENDOFLINE\n" +
            "NUMBER(5.23) NUMBER(8.52) NUMBER(.3) ENDOFLINE\n" +
            "NUMBER(8) NUMBER(4) NUMBER(9999) ENDOFLINE\n" +
            "NUMBER(7) NUMBER(4) NUMBER(3) NUMBER(1) ENDOFLINE\n" +
            "NUMBER(2) NUMBER NUMBER(3) ENDOFLINE\n" +
            "ENDOFLINE\n" +
            "STRINGLITERAL(this is a \"string\" literal) ENDOFLINE\n" +
            "NUMBER(5) LEQ NUMBER(1) GREATER NUMBER(2) PLUS NUMBER(1) MINUS NUMBER(2) MULTIPLY NUMBER(3) PLUS MINUS EQUALS DIVIDE ENDOFLINE\n" +
            "ENDOFLINE\n" +
            "DATA NUMBER(1) NUMBER(2) NUMBER(3) NUMBER(4) NUMBER(5.5) ENDOFLINE\n" +
            "FOR WORD(i) LESS NUMBER(1) ENDOFLINE\n" +
            "WORD(this) WORD(is) WORD(not) WORD(a) FUNCTION ENDOFLINE\n" +
            "END THEN WORD(Step123) ENDOFLINE\n" +
            "LABEL(hi) WORD(that) LESS WORD(is) WORD(a) WORD(label) ENDOFLINE\n" +
            "WHILE LPAREN WORD(pizza) IF WORD(req) RPAREN ENDOFLINE\n" +
            "LABEL(look) STRINGLITERAL(this is a multi\n" +
            "line literal) ENDOFLINE\n";
        String outputList = new String();
        var tokens = l.lex();
        Token test = tokens.get(7);
        Assert.assertEquals(2, test.getPos());
        Assert.assertEquals(3, test.getLine());
        for (Token t: tokens)
            outputList = outputList + t;
        Assert.assertEquals(expectedList, outputList);
    }

    @Test(expected = Exception.class)
    public void testInvalidChars() throws Exception {
        l = new Lexer("badexample.txt");
        l.lex();
    }

    @Test(expected = Exception.class)
    public void testInvalidLiteral() throws Exception {
        l = new Lexer("badliteral.txt");
        l.lex();
    }

}
