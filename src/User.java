import java.util.*;
public class User {
    int id;
    String username;
    List<User> friends;
    public String getUsername(){return this.username;}
    public int getId(){return this.id;}

    public User(String name, int id){
        this.username = name;
        this.id = id;
    }

    public void addFriend(User f){
        friends.add(f);
    }

}
