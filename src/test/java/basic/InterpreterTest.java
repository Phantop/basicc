package basic;

import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import basic.Token.TokenType;
import basic.MathOpNode.Operation;

public class InterpreterTest {

    private static Parser p = null;
    private static Interpreter i = null;

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    /* TESTING BUILTINS */
    @Test
    public void testRandom() { //this can technically fail if RNG is being silly but lol
        Assert.assertNotEquals(Interpreter.random(), Interpreter.random());
    }
    @Test
    public void testStringBuiltins() {
        var a = "this is a string";
        Assert.assertEquals(Interpreter.left(a, 4), "this");
        Assert.assertEquals(Interpreter.right(a, 6), "string");
        Assert.assertEquals(Interpreter.mid(a, 5, 3), "is ");
    }
    @Test
    public void testVal() {
        Assert.assertEquals(Interpreter.val("1337"), 1337);
        Assert.assertEquals(Interpreter.valf("69.420"), 69.420, .00001);
    }
    @Test
    public void testNum() {
        Assert.assertEquals(Interpreter.num(1337), "1337");
        Assert.assertEquals(Interpreter.num((float)69.420), "69.42");
    }
    /* DONE TESTING BUILTINS */

    @Test
    public void testDataProcessing() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.LABEL, 0, 0, "label1"));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "1"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "x"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "1"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.DATA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "1"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "13.5"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "lol this is a string"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.LABEL, 0, 0, "label2"));
        tokens.add(new Token(TokenType.DATA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "15.1"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "13"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "string two"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected =
            "label1: x=1\n" +
            "x=1\n" +
            "DATA 1, 13.5, \"lol this is a string\",\n" +
            "label2: DATA 15.1, 13, \"string two\",\n";
        Assert.assertEquals(expected, output);

        i = new Interpreter(ast);
        i.processData();
        Assert.assertEquals(((IntegerNode) i.popData()).getValue(), 1);
        Assert.assertEquals(((FloatNode) i.popData()).getValue(), 13.5, .00001);
        Assert.assertEquals(((StringNode) i.popData()).getValue(), "lol this is a string");
        Assert.assertEquals(((FloatNode) i.popData()).getValue(), 15.1, .00001);
        Assert.assertEquals(((IntegerNode) i.popData()).getValue(), 13);
        Assert.assertEquals(((StringNode) i.popData()).getValue(), "string two");
    }

    @Test
    public void testLabelProcessing() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.DATA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "1"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.LABEL, 0, 0, "alabel"));
        tokens.add(new Token(TokenType.DATA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "1"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "13.5"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "lol this is a string"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.LABEL, 0, 0, "label2"));
        tokens.add(new Token(TokenType.DATA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "15.1"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "13"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "string two"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected =
            "DATA 1,\n" +
            "alabel: DATA 1, 13.5, \"lol this is a string\",\n" +
            "label2: DATA 15.1, 13, \"string two\",\n";
        Assert.assertEquals(expected, output);

        i = new Interpreter(ast);
        i.processLabels();
        Assert.assertEquals(i.getLabel("alabel").getLabel(), "alabel");
        Assert.assertTrue(i.getLabel("label2").getStatement() instanceof DataNode);
    }

    @Test
    public void testDuplicateLabel() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.LABEL, 0, 0, "alabel"));
        tokens.add(new Token(TokenType.RETURN, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.LABEL, 0, 0, "alabel"));
        tokens.add(new Token(TokenType.RETURN, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "alabel: RETURN\n";
        Assert.assertEquals(expected + expected, output);

        i = new Interpreter(ast);
        Assert.assertThrows(Exception.class, () -> i.processLabels());
    }

    @Test
    public void testEvaluateInt() throws Exception {
        i = new Interpreter(new StatementsNode());
        var n = new IntegerNode(1);
        var out = i.evaluate(n);
        Assert.assertEquals((int) out.get(), 1);
    }

    @Test
    public void testEvaluateIntOp() throws Exception {
        i = new Interpreter(new StatementsNode());
        var n = new IntegerNode(1);
        var o = new MathOpNode(n, MathOpNode.Operation.ADD, n);
        var out = i.evaluate(o);
        Assert.assertEquals((int) out.get(), 2);
    }

    @Test
    public void testEvaluateIntFail() throws Exception {
        i = new Interpreter(new StatementsNode());
        var n = new ReturnNode();
        var out = i.evaluate(n);
        Assert.assertTrue(out.isEmpty());
    }

    @Test
    public void testEvaluateIntVariableAndFunction() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WORD, 0, 0, "var"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "var2"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "var"));
        tokens.add(new Token(TokenType.MINUS, 0, 0));
        tokens.add(new Token(TokenType.VAL, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "2"));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "var=2\n"
            + "var2=(var-VAL(\"2\", ))\n";
        Assert.assertEquals(expected, output);

        i = new Interpreter(ast);
        var astarr = ast.getAST();
        i.interpret(astarr.get(0));
        Assert.assertEquals("0", i.getVar("var2"));
    }

    @Test
    public void testEvaluateFloat() throws Exception {
        i = new Interpreter(new StatementsNode());
        var n = new FloatNode((float)3.14);
        var out = i.evaluatef(n);
        Assert.assertEquals((float) out.get(), 3.14, 0.00001);
    }

    @Test
    public void testEvaluateFloatOp() throws Exception {
        i = new Interpreter(new StatementsNode());
        var n = new FloatNode((float)3.14);
        var o = new MathOpNode(n, MathOpNode.Operation.ADD, n);
        var out = i.evaluatef(o);
        Assert.assertEquals((float) out.get(), 6.28, 0.00001);
    }

    @Test
    public void testEvaluateFloatFail() throws Exception {
        i = new Interpreter(new StatementsNode());
        var n = new ReturnNode();
        var out = i.evaluatef(n);
        Assert.assertTrue(out.isEmpty());
    }

    @Test
    public void testEvaluateFloatVariableAndFunction() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WORD, 0, 0, "var%"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "var2%"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "var%"));
        tokens.add(new Token(TokenType.MINUS, 0, 0));
        tokens.add(new Token(TokenType.VALF, 0, 0));
        tokens.add(new Token(TokenType.LPAREN, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "2.14"));
        tokens.add(new Token(TokenType.RPAREN, 0, 0));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "var%=2\n"
            + "var2%=(var%-VALF(\"2.14\", ))\n";
        Assert.assertEquals(expected, output);

        i = new Interpreter(ast);
        var astarr = ast.getAST();
        i.interpret(astarr.get(0));
        i.interpret(astarr.get(1));
        Assert.assertEquals(i.getVar("var2%"), "-0.1400001");
    }

    @Test
    public void testAssignStringAndPrint() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WORD, 0, 0, "var$"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "pasta"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.PRINT, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "I like to eat "));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "var$"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "var$=\"pasta\"\n"
            + "PRINT \"I like to eat \", var$, 2,\n";
        Assert.assertEquals(expected, output);

        i = new Interpreter(ast, true);
        var astarr = ast.getAST();
        i.interpret(astarr.get(0));
        Assert.assertEquals("pasta", i.getVar("var$"));

        var expectedString = new LinkedList<String>();
        expectedString.add("I like to eat ");
        expectedString.add("pasta");
        expectedString.add("2");
        Assert.assertEquals(expectedString, i.getIO());
    }

    @Test
    public void testDataInputRead() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.DATA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "1"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "13.5"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.STRINGLITERAL, 0, 0, "lol this is a string"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.READ, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "a"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "b%"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "c$"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.INPUT, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "c$"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "a"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "b%"));
        tokens.add(new Token(TokenType.COMMA, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "c$"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected =
            "DATA 1, 13.5, \"lol this is a string\",\n" +
            "READ a, b%, c$,\n" +
            "INPUT c$, a, b%, c$\n";
        Assert.assertEquals(expected, output);

        i = new Interpreter(ast, true);
        i.processData();

        var astarr = ast.getAST();
        i.interpret((ReadNode)astarr.get(1));
        Assert.assertEquals("1", i.getVar("a"));
        Assert.assertEquals("13.5", i.getVar("b%"));
        Assert.assertEquals("lol this is a string", i.getVar("c$"));

        var input = new LinkedList<String>();
        input.add("4");
        input.add("1.2");
        input.add("words!");
        i.putIO(input);
        i.interpret((InputNode)astarr.get(2));
        Assert.assertEquals("4", i.getVar("a"));
        Assert.assertEquals("1.2", i.getVar("b%"));
        Assert.assertEquals("words!", i.getVar("c$"));
    }

    @Test
    public void testASTNextAssign() throws Exception {
        var tokens = new LinkedList<Token>();
        tokens.add(new Token(TokenType.WORD, 0, 0, "var%"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.NUMBER, 0, 0, "2"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "var2%"));
        tokens.add(new Token(TokenType.EQUALS, 0, 0));
        tokens.add(new Token(TokenType.WORD, 0, 0, "var%"));
        tokens.add(new Token(TokenType.ENDOFLINE, 0, 0));
        p = new Parser(tokens);
        var ast = p.parse();
        var output = ast.toString();
        var expected = "var%=2\n"
            + "var2%=var%\n";
        Assert.assertEquals(expected, output);

        i = new Interpreter(ast);
        i.processOrder();
        var astarr = ast.getAST();
        Assert.assertEquals(astarr.get(1), astarr.get(0).next());
    }

    @Test
    public void testFizzBuzz() throws Exception {
        var l = new Lexer("fizzbuzz.txt");
        var tokens = l.lex();
        var p = new Parser(tokens);
        var ast = p.parse();
        var i = new Interpreter(ast, true);
        var arr = new LinkedList<String>();
        for (int j = 1; j <= 100; j++) {
            if (j % 3 == 0 && j % 5 == 0)
                arr.add("Fizz Buzz");
            if (j % 3 == 0)
                arr.add("Fizz");
            if (j % 5 == 0)
                arr.add("Buzz");
        }
        i.interpret();
        Assert.assertEquals(arr, i.getFullIO());
    }

    @Test
    public void testCollatz() throws Exception {
        var l = new Lexer("collatz.txt");
        var tokens = l.lex();
        var p = new Parser(tokens);
        var ast = p.parse();
        var i = new Interpreter(ast, true);
        var arr = new LinkedList<String>();
        arr.add("3");
        i.putIO(arr);
        i.interpret();
        Assert.assertEquals("7", i.getIO().get(0));
        Assert.assertEquals(" steps.", i.getIO().get(1));
    }
}
