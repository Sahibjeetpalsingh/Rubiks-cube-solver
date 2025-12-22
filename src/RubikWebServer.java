import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class RubikWebServer {
    private static int PORT = getPort();
    private static int getPort() {
        try {
            String env = System.getenv("PORT");
            if (env != null && !env.isBlank()) return Integer.parseInt(env.trim());
        } catch (Exception ignored) {}
        return 8080;
    }

    private static final Path PUBLIC_DIR = Paths.get("public").toAbsolutePath().normalize();

    public static void main(String[] args) throws Exception {
        HttpServer server = null;
        int attempts = 0;
        int maxAttempts = 10;
        
        while (server == null && attempts < maxAttempts) {
            try {
                server = HttpServer.create(new InetSocketAddress(PORT), 0);
            } catch (Exception e) {
                attempts++;
                if (attempts >= maxAttempts) {
                    System.err.println("Error: Could not bind to any port. Last error: " + e.getMessage());
                    throw e;
                }
                PORT++;
                System.out.println("Port " + (PORT - 1) + " in use, trying " + PORT + "...");
            }
        }

        server.createContext("/api/state", RubikWebServer::handleState);
        server.createContext("/api/solve", RubikWebServer::handleSolve);
        server.createContext("/", RubikWebServer::handleStatic);

        server.setExecutor(null);
        System.out.println("RubikWebServer running on http://localhost:" + PORT);
        System.out.println("If port " + PORT + " is already in use, the server will automatically try the next available port.");
        server.start();
    }

    private static void handleState(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            sendJson(ex, 405, "{\"error\":\"Use POST\"}");
            return;
        }
        String body = readAll(ex.getRequestBody());
        try {
            String facelets = CubeInputUtil.parseToFacelets(body);
            sendJson(ex, 200, "{\"facelets\":\"" + JsonUtil.esc(facelets) + "\"}");
        } catch (IllegalArgumentException e) {
            sendJson(ex, 400, "{\"error\":\"" + JsonUtil.esc(e.getMessage()) + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(ex, 500, "{\"error\":\"Internal error\"}");
        }
    }

    private static void handleSolve(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            sendJson(ex, 405, "{\"error\":\"Use POST\"}");
            return;
        }
        String body = readAll(ex.getRequestBody());
        try {
            String facelets = CubeInputUtil.parseToFacelets(body);

            String raw = Search.solution(facelets, 21, 5, false);
            if (raw == null) raw = "";
            raw = raw.trim();

            if (raw.startsWith("Error")) {
                sendJson(ex, 400, "{\"error\":\"" + JsonUtil.esc(raw) + "\"}");
                return;
            }

            List<String> moves = raw.isEmpty() ? Collections.emptyList() : Arrays.asList(raw.split("\\s+"));
            List<String> trace = CubeTraceUtil.trace(facelets, moves);

            String json = "{"
                + "\"facelets\":\"" + JsonUtil.esc(facelets) + "\","
                + "\"solution\":\"" + JsonUtil.esc(raw.isEmpty() ? "Already solved" : raw) + "\","
                + "\"moves\":" + JsonUtil.arr(moves) + ","
                + "\"trace\":" + JsonUtil.arr(trace)
                + "}";

            sendJson(ex, 200, json);
        } catch (IllegalArgumentException e) {
            sendJson(ex, 400, "{\"error\":\"" + JsonUtil.esc(e.getMessage()) + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(ex, 500, "{\"error\":\"Internal error\"}");
        }
    }

    private static void handleStatic(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path == null || path.isEmpty() || "/".equals(path)) path = "/index.html";

        Path file = PUBLIC_DIR.resolve(path.substring(1)).normalize();
        if (!file.startsWith(PUBLIC_DIR)) {
            sendText(ex, 403, "Forbidden", "text/plain; charset=utf-8");
            return;
        }
        if (!Files.exists(file) || Files.isDirectory(file)) {
            sendText(ex, 404, "Not Found", "text/plain; charset=utf-8");
            return;
        }

        String ct = contentType(file.getFileName().toString());
        byte[] data = Files.readAllBytes(file);
        ex.getResponseHeaders().set("Content-Type", ct);
        ex.sendResponseHeaders(200, data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }

    private static String contentType(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        if (n.endsWith(".html")) return "text/html; charset=utf-8";
        if (n.endsWith(".css")) return "text/css; charset=utf-8";
        if (n.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (n.endsWith(".txt")) return "text/plain; charset=utf-8";
        return "application/octet-stream";
    }

    private static void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }

    private static void sendText(HttpExchange ex, int status, String text, String contentType) throws IOException {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(status, data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }

    private static String readAll(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int r;
        while ((r = is.read(buf)) != -1) bos.write(buf, 0, r);
        return bos.toString(StandardCharsets.UTF_8);
    }
}
