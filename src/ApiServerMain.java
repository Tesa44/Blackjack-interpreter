import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class ApiServerMain {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/execute", new ExecuteHandler(new DslExecutionService()));
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("API server running on http://localhost:" + PORT);
        System.out.println("POST /api/execute with raw DSL text in the request body.");
    }

    private static class ExecuteHandler implements HttpHandler {
        private final DslExecutionService executionService;

        private ExecuteHandler(DslExecutionService executionService) {
            this.executionService = executionService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeJson(exchange, 405, "{\"error\":\"Only POST is supported on /api/execute.\"}");
                return;
            }

            try {
                String dsl = readRequestBody(exchange.getRequestBody());
                if (dsl.isBlank()) {
                    writeJson(exchange, 400, "{\"error\":\"Request body must contain DSL commands.\"}");
                    return;
                }

                DslExecutionResult result = executionService.execute(dsl);
                writeJson(exchange, 200, result.getJsonOutput());
            } catch (Exception e) {
                writeJson(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
        headers.set("Content-Type", "application/json; charset=utf-8");
    }

    private static String readRequestBody(InputStream requestBody) throws IOException {
        return new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void writeJson(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] responseBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream responseBody = exchange.getResponseBody()) {
            responseBody.write(responseBytes);
        }
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "Unexpected error";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
