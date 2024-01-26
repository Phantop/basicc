package basic;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CodeHandler {
    private String content;
    private int index;

    public CodeHandler(String filename) {
        Path fileLoc = Paths.get(filename);
        try {
            content = new String(Files.readAllBytes(fileLoc));
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        index = 0;
    }

    public char peek(int i) {
        return content.charAt(index + i);
    }

    public String peekString(int i) {
        return content.substring(index, index + i);
    }

    public char getChar() {
        return content.charAt(index++);
    }

    public void swallow() {
        index++; 
    }

    public boolean isDone() {
        return index == content.length();
    }

    public String remainder() {
        return content.substring(index);
    }
}
