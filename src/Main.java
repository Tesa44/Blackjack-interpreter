import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {

        // create a CharStream that reads from standard input
        //CharStream input = CharStreams.fromStream(System.in);

        CharStream input = null;
        try {
            input = CharStreams.fromFileName("input.dsl");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ExprLexer lexer = new ExprLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);

        ParseTree tree = parser.program();

        BooleanVisitor visitor = new BooleanVisitor();
        System.out.println(visitor.visit(tree));
    }
}