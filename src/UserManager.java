import java.util.*;

public class UserManager {
    private static final String FILE_NAME = "users.json";
    private static final Map<String, User> users = new HashMap<>();
    private static int nextId = 1;

    static {
        loadUsers();
    }

    public static synchronized void loadUsers() {
        users.clear();
        String json = JsonUtil.readFile(FILE_NAME).trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            String content = json.substring(1, json.length() - 1).trim();
            if (!content.isEmpty()) {
                // Split JSON objects in array
                String[] items = content.split("(?<=\\}),\\s*(?=\\{)");
                for (String item : items) {
                    User u = User.fromJson(item);
                    if (u.getUsername() != null && !u.getUsername().isEmpty()) {
                        users.put(u.getUsername().toLowerCase(), u);
                        if (u.getId() >= nextId) {
                            nextId = u.getId() + 1;
                        }
                    }
                }
            }
        }
    }

    public static synchronized void saveUsers() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        int count = 0;
        for (User u : users.values()) {
            sb.append("  ").append(u.toJson());
            if (++count < users.size()) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]");
        JsonUtil.writeFile(FILE_NAME, sb.toString());
    }

    public static synchronized User register(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        String key = username.toLowerCase();
        if (users.containsKey(key)) {
            return null; // User already exists
        }
        User newUser = new User(username, password, nextId++);
        users.put(key, newUser);
        saveUsers();
        return newUser;
    }

    public static synchronized User authenticate(String username, String password) {
        if (username == null || password == null) return null;
        User user = users.get(username.toLowerCase());
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    public static synchronized User getUser(String username) {
        if (username == null) return null;
        return users.get(username.toLowerCase());
    }
}
