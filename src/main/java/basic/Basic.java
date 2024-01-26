package basic;

public class Basic {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Invalid argument count. You must provide a filename and only a filename.");
            System.exit(1);
        }
        Lexer lex = new Lexer(args[0]);
        var tokens = lex.lex();
        for (Token t: tokens)
            System.out.print(t);
    }
}
