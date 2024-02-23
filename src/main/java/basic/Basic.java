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
            System.out.println("Peforming lexer step:");
            Lexer lex = new Lexer(args[0]);
            var tokens = lex.lex();
            for (Token t: tokens)
                System.out.print(t);

            System.out.println("Peforming parser step:");
            Parser p = new Parser(tokens);
            var ast = p.parse();
            System.out.print(ast.toString());
        }
        catch (Exception x) { // Just so running like this doesn't vomit ugly exceptions
            System.err.format("Exception: %s%n", x);
            //x.printStackTrace(); // uncomment when needed for debugging
            System.exit(1);
        }
    }
}
