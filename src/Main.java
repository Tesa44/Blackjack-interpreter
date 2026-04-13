import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        DslExecutionService executionService = new DslExecutionService();
        try {
            System.out.println(executionService.executeFile(Path.of("input.dsl")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
