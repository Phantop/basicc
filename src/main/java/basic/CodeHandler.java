package basic;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles reading for a single inputted filename
 */
public class CodeHandler {
    private String content;
    private int index;

    /** Creates CodeHandler
     * @param filename string path to input file
     * @throws IOException on invalid file
     */
    public CodeHandler(String filename) throws IOException {
        Path fileLoc = Paths.get(filename);
        try {
            content = new String(Files.readAllBytes(fileLoc));
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            throw x;
        }
        index = 0;
    }

    /**
     * @param i Index relative to read head to return
     * @return char at i chars after read head
     */
    public char peek(int i) {
        return content.charAt(index + i);
    }

    /**
     * @param i Index relative to read head to return up to
     * @return string up to i chars after read head
     */
    public String peekString(int i) {
        return content.substring(index, index + i);
    }

    /**
     * @modifies read head is moved forwards by one
     * @return char at read head
     */
    public char getChar() {
        return content.charAt(index++);
    }

    /**
     * @modifies read head is moved forwards by one
     */
    public void swallow() {
        index++; 
    }

    /**
     * @return true if read head is at end of file
     */
    public boolean isDone() {
        return index == content.length();
    }

    /**
     * @return rest of file starting at read head position
     */
    public String remainder() {
        return content.substring(index);
    }
}
