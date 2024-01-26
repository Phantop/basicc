package basic;
import java.util.*;

public class Basic {
    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println("Invalid argument count");
        Lexer lex = new Lexer(args[0]);
        var tokens = lex.lex();
        System.out.println(tokens);
    }
}
