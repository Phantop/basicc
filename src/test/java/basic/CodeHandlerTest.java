package basic;

import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;

public class CodeHandlerTest{

    private static CodeHandler c = null;

    @Test
    public void testOpen() throws IOException {
        c = new CodeHandler("example.txt");
    }

    @Test(expected = IOException.class)
    public void testOpenFail() throws IOException {
        c = new CodeHandler("invalid.txt");
    }

    @Test
    public void testReads() throws IOException {
        testOpen();
        Assert.assertEquals('a', c.peek(0));
        Assert.assertEquals(' ', c.peek(2));
        Assert.assertEquals('a', c.getChar());
        Assert.assertEquals('n', c.getChar());
        Assert.assertEquals(' ', c.getChar());
        Assert.assertEquals("empty", c.peekString(5));
        while (c.getChar() != '1');
        Assert.assertEquals("\n2 number 3\n", c.remainder());
    }

    @Test
    public void testEats() throws IOException {
        testOpen();
        c.swallow();
        Assert.assertEquals('n', c.peek(0));
        int i = 0;
        while (!c.isDone()) {
            c.swallow();
            i++;
        }
        Assert.assertEquals(74, i);
    }
}
