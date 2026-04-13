public class DslExecutionResult {
    private final String consoleOutput;
    private final String jsonOutput;

    public DslExecutionResult(String consoleOutput, String jsonOutput) {
        this.consoleOutput = consoleOutput;
        this.jsonOutput = jsonOutput;
    }

    public String getConsoleOutput() {
        return consoleOutput;
    }

    public String getJsonOutput() {
        return jsonOutput;
    }
}
