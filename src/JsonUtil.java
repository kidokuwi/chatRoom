import java.io.*;
import java.nio.file.*;

public class JsonUtil {

    public static String extractField(String json, String key) {
        if (json == null || key == null) return "";
        String pattern = "\"" + key + "\"\\s*:\\s*";
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex == -1) return "";

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return "";

        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length()) return "";

        if (json.charAt(start) == '"') {
            // String value
            start++;
            StringBuilder sb = new StringBuilder();
            boolean escaped = false;
            for (int i = start; i < json.length(); i++) {
                char c = json.charAt(i);
                if (escaped) {
                    sb.append(c);
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    return sb.toString();
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != ']') {
                end++;
            }
            return json.substring(start, end).trim();
        }
    }

    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static String readFile(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) return "[]";
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return "[]";
        }
    }

    public static void writeFile(String filename, String content) {
        try {
            Files.write(Paths.get(filename), content.getBytes());
        } catch (IOException e) {
            System.err.println("Error saving JSON to " + filename + ": " + e.getMessage());
        }
    }
}
