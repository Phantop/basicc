package basic;

public class Basic {
    /**
     * Main runner for BASIC Lexer. Prints out lexed tokens if no errors occur.
     * Exits with error if an exception occurs.
     * @param args Standard command line args, in this case a single filename
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Invalid argument count. You must provide a filename and only a filename.");
            System.exit(1);
        }
        try {
            Lexer lex = new Lexer(args[0]);
            for (var tokens = lex.lex(); tokens.size() != 0; tokens = lex.lex()) {
                for (Token t: tokens)
                    System.out.print(t);
            }
        }
        catch (Exception x) { // Just so running like this doesn't vomit ugly exceptions
            System.exit(1);
        }
    }
}
