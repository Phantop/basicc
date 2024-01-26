package basic;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Iterator;

public class LexerTest {

    private static Graph<String, String> g = null;
    private static String[] s = null;
    private static final int NUM_NODES_TO_TEST = 2;

    @BeforeClass
    public static void setupForTests() throws Exception {
        s = new String[NUM_NODES_TO_TEST];
        for (int i=0; i<NUM_NODES_TO_TEST; i++) {
            s[i] = String.valueOf(1-i);
        }
    }

    @Test
    public void testAddNode() {
        g = new Graph<String, String>();
        for (String i : s) g.addNode(i);
        Iterator<String> iter = g.listNodes();
        assertEquals("0", iter.next());
        assertEquals("1", iter.next());
    }

    @Test
    public void testAddEdge() {
        testAddNode();
        g.addEdge("0", "1", "a");
        g.addEdge("0", "1", "b");
        g.addEdge("1", "0", "b");
        g.addEdge("1", "0", "a");
        for (String i : s) g.addNode(i); //make sure trying to readd doesn't destroy edges
        Iterator<String> iter = g.listChildren("0");
        assertEquals("1(a)", iter.next());
        assertEquals("1(b)", iter.next());
        iter = g.listChildren("1");
        assertEquals("0(a)", iter.next());
        assertEquals("0(b)", iter.next());
    }
}
