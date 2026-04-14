import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DslExecutionService {
    private static final Path FRONTEND_JSON_PATH = Path.of("out", "frontend", "latest-simulation.json");

    public DslExecutionResult execute(String dsl) throws IOException {
        CharStream input = CharStreams.fromString(dsl);
        ExprLexer lexer = new ExprLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);

        ParseTree tree = parser.program();
        BooleanVisitor visitor = new BooleanVisitor();
        visitor.visit(tree);

        if (!Files.exists(FRONTEND_JSON_PATH)) {
            throw new IllegalStateException("No frontend JSON was generated. Run a simulation command first.");
        }

        String jsonOutput = Files.readString(FRONTEND_JSON_PATH, StandardCharsets.UTF_8);
        return new DslExecutionResult(jsonOutput);
    }
}
