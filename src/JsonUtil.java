import java.util.*;

public class JsonUtil {
    public static String esc(String s){
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        for (int i=0;i<s.length();i++){
            char c = s.charAt(i);
            switch(c){
                case '\\': out.append("\\\\"); break;
                case '"': out.append("\\\""); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 32) out.append(' ');
                    else out.append(c);
            }
        }
        return out.toString();
    }

    public static String arr(List<String> items){
        if (items == null) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i=0;i<items.size();i++){
            if (i>0) sb.append(",");
            sb.append("\"").append(esc(items.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }
}
