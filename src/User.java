import java.util.*;

public class User {
    int id;
    String username;
    String password;
    List<User> friends;

    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
    public int getId() { return this.id; }
    public List<User> getFriends() { return this.friends; }

    public User(String name, String password, int id) {
        this.username = name;
        this.password = password;
        this.id = id;
        this.friends = new ArrayList<>();
    }

    public User(String name, int id) {
        this(name, "", id);
    }

    public void addFriend(User f) {
        if (friends == null) friends = new ArrayList<>();
        friends.add(f);
    }
    public void removeFriend(String friendName) {
        if (friends != null) {
            friends.removeIf(f -> f.getUsername().equals(friendName));
        }
    }

    public String toJson() {
        return "{\"id\":" + id + ",\"username\":\"" + JsonUtil.escape(username) + "\",\"password\":\"" + JsonUtil.escape(password) + "\"}";
    }

    public static User fromJson(String json) {
        String idStr = JsonUtil.extractField(json, "id");
        String uname = JsonUtil.extractField(json, "username");
        String pass = JsonUtil.extractField(json, "password");
        int userId = 0;
        try {
            if (idStr != null && !idStr.isEmpty()) {
                userId = Integer.parseInt(idStr);
            }
        } catch (NumberFormatException e) {}
        return new User(uname, pass, userId);
    }
}

