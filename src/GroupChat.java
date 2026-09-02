
import java.util.*;

public class GroupChat {
    List<User> users;

    public GroupChat(List<User> users){
        this.users = users != null ? users : new ArrayList<>();     
    }

    public GroupChat(){
        this.users = new ArrayList<>();
    }

    public void addUser(User user){
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public User removeUser(String name){
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User usr = iterator.next();
            if (name.equals(usr.getUsername())) {
                iterator.remove();
                return usr;
            }
        }
        return null;
    }

    public List<User> getUsers() {
        return users;
    }
}

