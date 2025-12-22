
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Solver {

    private static final int FACELET_COUNT = 54;
    private static final int ROWS = 9;
    private static final int COLS = 12;
    private static final char[] FACE_LETTERS = { 'U', 'R', 'F', 'D', 'L', 'B' };
    private static final int[][] FACE_COORDS = { { 0, 3 }, { 3, 6 }, { 3, 3 }, { 6, 3 }, { 3, 0 }, { 3, 9 } };

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Solver <scramble-net> <solution-output>");
            System.exit(1);
        }
        Path scrambleNet = Path.of(args[0]);
        Path output = Path.of(args[1]);
        try {
            String facelets = readFacelets(scrambleNet);
            String rawSolution = Search.solution(facelets, 21, 5, false);
            String normalized = normalize(rawSolution);
            Files.writeString(output, normalized + System.lineSeparator());
            System.out.println("Solution written to " + output + " -> " + normalized);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            System.exit(2);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid scramble: " + e.getMessage());
            System.exit(3);
        }
    }

    private static String readFacelets(Path path) throws IOException {
        List<String> rawLines = Files.readAllLines(path);
        List<String> contentLines = new ArrayList<>(ROWS);
        for (String line : rawLines) {
            if (line == null)
                continue;
            if (line.trim().isEmpty())
                continue;
            contentLines.add(line);
        }
        if (contentLines.size() < ROWS)
            throw new IllegalArgumentException("Expected 9 non-empty rows, found " + contentLines.size());
        char[][] grid = new char[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            String line = contentLines.get(row);
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = col < line.length() ? line.charAt(col) : ' ';
            }
        }
        Map<Character, Character> colorMap = buildColorMap(grid);
        StringBuilder facelets = new StringBuilder(FACELET_COUNT);
        for (int face = 0; face < FACE_COORDS.length; face++) {
            appendFace(facelets, grid, FACE_COORDS[face][0], FACE_COORDS[face][1], colorMap);
        }
        if (facelets.length() != FACELET_COUNT)
            throw new IllegalStateException("Facelet string wrong length");
        return facelets.toString();
    }

    private static void appendFace(StringBuilder builder, char[][] grid, int rowStart, int colStart,
            Map<Character, Character> colorMap) {
        for (int row = rowStart; row < rowStart + 3; row++) {
            for (int col = colStart; col < colStart + 3; col++) {
                char c = grid[row][col];
                if (c == ' ' || !Character.isLetter(c))
                    throw new IllegalArgumentException("Missing color at row " + row + " col " + col);
                Character mapped = colorMap.get(c);
                if (mapped == null)
                    throw new IllegalArgumentException("Unexpected color '" + c + "' at row " + row + " col " + col);
                builder.append(mapped);
            }
        }
    }

    private static Map<Character, Character> buildColorMap(char[][] grid) {
        Map<Character, Character> colorMap = new LinkedHashMap<>();
        for (int face = 0; face < FACE_COORDS.length; face++) {
            int rowStart = FACE_COORDS[face][0];
            int colStart = FACE_COORDS[face][1];
            char center = grid[rowStart + 1][colStart + 1];
            if (!Character.isLetter(center))
                throw new IllegalArgumentException("Missing center color for face " + FACE_LETTERS[face]);
            if (colorMap.containsKey(center) && colorMap.get(center) != FACE_LETTERS[face])
                throw new IllegalArgumentException("Color '" + center + "' already assigned to another face");
            colorMap.put(center, FACE_LETTERS[face]);
        }
        if (colorMap.size() != FACE_LETTERS.length)
            throw new IllegalStateException("Expected " + FACE_LETTERS.length + " distinct center colors");
        return colorMap;
    }

    private static String normalize(String rawSolution) {
        String trimmed = rawSolution.trim();
        if (trimmed.isEmpty())
            return "";
        if (trimmed.startsWith("Error"))
            return trimmed;
        StringBuilder compact = new StringBuilder();
        for (String token : trimmed.split("\\s+")) {
            if (token.isEmpty())
                continue;
            char face = token.charAt(0);
            if (!Character.isLetter(face))
                continue;
            if (token.length() == 1) {
                compact.append(face);
            } else if (token.charAt(1) == '2') {
                compact.append(face).append(face);
            } else if (token.charAt(1) == '\'') {
                compact.append(face).append(face).append(face);
            } else {
                compact.append(face);
            }
        }
        return compact.toString();
    }
}
