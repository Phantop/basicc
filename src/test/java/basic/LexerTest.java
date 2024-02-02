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
            "NUMBER(2) WORD(number) NUMBER(3) ENDOFLINE\n";
        String outputList = new String();
        var tokens = l.lex();
        for (Token t: tokens)
            outputList = outputList + t;
        Assert.assertEquals(expectedList, outputList);
    }

    @Test(expected = Exception.class)
    public void testNaughtyNum() throws Exception {
        l = new Lexer("badexample.txt");
        l.lex();
    }
}
