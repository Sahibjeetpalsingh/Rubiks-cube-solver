import java.util.*;

public class CubeTraceUtil {

    public static List<String> trace(String startFacelets, List<String> moves){
        List<String> out = new ArrayList<>();
        String cur = startFacelets;
        out.add(cur);
        for (String mv : moves){
            cur = apply(cur, mv);
            out.add(cur);
        }
        return out;
    }

    public static String applyMoves(String startFacelets, List<String> moves){
        String cur = startFacelets;
        for (String mv : moves) cur = apply(cur, mv);
        return cur;
    }

    public static String apply(String facelets, String move){
        if (move == null || move.isBlank()) return facelets;
        String mv = move.trim();
        char f = mv.charAt(0);
        int axis = "URFDLB".indexOf(f);
        if (axis < 0) throw new IllegalArgumentException("Bad move: " + move);

        int power = 1;
        if (mv.length() > 1){
            char s = mv.charAt(1);
            if (s == '2') power = 2;
            else if (s == '\'') power = 3;
        }

        FaceCube fc = new FaceCube(facelets);
        CubieCube cc = fc.toCubieCube();

        for (int i=0;i<power;i++){
            cc.cornerMultiply(CubieCube.moveCube[axis]);
            cc.edgeMultiply(CubieCube.moveCube[axis]);
        }

        FaceCube out = cc.toFaceCube();
        return faceCubeToString(out);
    }

    private static String faceCubeToString(FaceCube fc){
        StringBuilder sb = new StringBuilder(54);
        for (int i=0;i<54;i++){
            sb.append(fc.f[i].toString());
        }
        return sb.toString();
    }
}
