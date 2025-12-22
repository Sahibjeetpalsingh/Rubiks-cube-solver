import java.util.*;

public class CubeInputUtil {
    private static final String SOLVED = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";

    public static String parseToFacelets(String input) {
        String raw = input == null ? "" : input.replace("\r\n", "\n");
        raw = stripTrailing(raw);
        if (raw.trim().isEmpty()) return SOLVED;
        
        // If it looks like a 9x12 net (9 lines, has 12-wide middle rows)
        String[] lines0 = raw.split("\n");
        List<String> lines = new ArrayList<>();
        for (String ln : lines0) {
            if (!ln.isBlank()) lines.add(ln);
        }
        if (lines.size() == 9 && lines.stream().mapToInt(String::length).max().orElse(0) >= 9) {
            return parseNet(lines);
        }

        // If it's 54 letters (facelets) with or without spaces
        String compact = raw.replaceAll("\\s+", "");
        if (compact.length() == 54) {
            return normalize54(compact);
        }

        // Otherwise treat as scramble moves (e.g., "R U R' U'")
        List<String> moves = parseMoves(raw);
        if (moves.isEmpty()) throw new IllegalArgumentException("No valid moves found. Example: R U R' U'");
        return CubeTraceUtil.applyMoves(SOLVED, moves);
    }

    
    private static String stripTrailing(String s){
        if (s == null) return "";
        int end = s.length();
        while (end > 0){
            char c = s.charAt(end-1);
            if (c==' ' || c=='\n' || c=='\r' || c=='\t') end--;
            else break;
        }
        return s.substring(0,end);
    }

private static String normalize54(String s) {
        // If already uses URFDLB, accept
        if (s.matches("[URFDLB]{54}")) return s;

        // If it's color letters (W,R,G,Y,O,B), relabel by centers
        if (!s.matches("[WRGYOB]{54}")) {
            throw new IllegalArgumentException("Invalid 54-character cube string.");
        }
        char uC = s.charAt(4);
        char rC = s.charAt(13);
        char fC = s.charAt(22);
        char dC = s.charAt(31);
        char lC = s.charAt(40);
        char bC = s.charAt(49);

        Map<Character,Character> map = new HashMap<>();
        map.put(uC, 'U'); map.put(rC, 'R'); map.put(fC, 'F');
        map.put(dC, 'D'); map.put(lC, 'L'); map.put(bC, 'B');

        StringBuilder out = new StringBuilder(54);
        for (int i=0;i<54;i++){
            char ch = s.charAt(i);
            Character face = map.get(ch);
            if (face == null) throw new IllegalArgumentException("Unknown color found: " + ch);
            out.append(face);
        }
        return out.toString();
    }

    private static String parseNet(List<String> linesIn) {
        // Pad each line to length 12 (keep spaces for alignment)
        String[] lines = new String[9];
        for (int i=0;i<9;i++){
            String ln = linesIn.get(i);
            if (ln.length() < 12) ln = ln + " ".repeat(12 - ln.length());
            lines[i] = ln;
        }

        // Centers define mapping color -> face letter
        char uC = at(lines, 1, 4);
        char lC = at(lines, 4, 1);
        char fC = at(lines, 4, 4);
        char rC = at(lines, 4, 7);
        char bC = at(lines, 4, 10);
        char dC = at(lines, 7, 4);

        if (uC==' '||lC==' '||fC==' '||rC==' '||bC==' '||dC==' ')
            throw new IllegalArgumentException("Net format looks wrong (missing center colors).");

        Map<Character,Character> map = new HashMap<>();
        map.put(uC,'U'); map.put(rC,'R'); map.put(fC,'F');
        map.put(dC,'D'); map.put(lC,'L'); map.put(bC,'B');

        StringBuilder out = new StringBuilder(54);
        // U (rows 0-2, cols 3-5)
        appendFace(out, lines, 0, 3, map);
        // R (rows 3-5, cols 6-8)
        appendFace(out, lines, 3, 6, map);
        // F (rows 3-5, cols 3-5)
        appendFace(out, lines, 3, 3, map);
        // D (rows 6-8, cols 3-5)
        appendFace(out, lines, 6, 3, map);
        // L (rows 3-5, cols 0-2)
        appendFace(out, lines, 3, 0, map);
        // B (rows 3-5, cols 9-11)
        appendFace(out, lines, 3, 9, map);

        if (out.length() != 54) throw new IllegalArgumentException("Net parsing failed.");
        return out.toString();
    }

    private static void appendFace(StringBuilder out, String[] lines, int r0, int c0, Map<Character,Character> map){
        for (int r=0;r<3;r++){
            for (int c=0;c<3;c++){
                char ch = at(lines, r0+r, c0+c);
                if (ch == ' ') throw new IllegalArgumentException("Missing color in net at row " + (r0+r) + " col " + (c0+c));
                Character face = map.get(ch);
                if (face == null) throw new IllegalArgumentException("Unknown color '" + ch + "' in net. Centers define expected colors.");
                out.append(face);
            }
        }
    }

    private static char at(String[] lines, int r, int c){
        if (r<0||r>=lines.length) return ' ';
        String ln = lines[r];
        if (c<0||c>=ln.length()) return ' ';
        return ln.charAt(c);
    }

    private static List<String> parseMoves(String raw){
        // Accept tokens like U, U', U2, etc.
        String cleaned = raw.replaceAll("[^URFDLBu'r2\\s]", " "); // keep likely chars
        String[] toks = cleaned.trim().split("\\s+");
        List<String> out = new ArrayList<>();
        for (String t : toks){
            t = t.trim();
            if (t.isEmpty()) continue;
            // normalize lowercase
            t = t.toUpperCase(Locale.ROOT).replace("’","'"); // fancy apostrophe
            if (t.matches("[URFDLB]") || t.matches("[URFDLB]'") || t.matches("[URFDLB]2")) out.add(t);
        }
        return out;
    }
}
