
import java.util.*;

public class GroupChat {
    List<User> users;

    public GroupChat(List<User> users){
        this.users = users;     
    }
    public void addUser(User user){
        users.add(user);
    }
    public User removeUser(String name){
        for (User usr : users){
            if (name.equals(usr.getUsername()))
                users.remove(usr);
            return usr;
        }
        return null;
    }
}
