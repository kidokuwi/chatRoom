import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, GroupChat> groupChats = new ConcurrentHashMap<>();
    private static Map<String, PrintWriter> onlineUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 3141;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server running on port: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new user connected from: " + clientSocket.getRemoteSocketAddress());

                new Thread(() -> handleClientChat(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClientChat(Socket socket) {
        User currentUser = null;
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received raw request: " + message);
                currentUser = handleClientRequest(message, writer, currentUser);
            }
        } catch (IOException e) {
            System.out.println("User disconnected or connection lost.");
        } finally {
            if (currentUser != null) {
                onlineUsers.remove(currentUser.getUsername().toLowerCase());
                System.out.println("User logged out/disconnected: " + currentUser.getUsername());
            }
        }
    }

    private static User handleClientRequest(String request, PrintWriter writer, User currentUser) {
        if (request == null || request.trim().isEmpty()) return currentUser;

        String command;
        String payload = "";
        int colonIndex = request.indexOf(":");
        if (colonIndex != -1) {
            command = request.substring(0, colonIndex).trim();
            payload = request.substring(colonIndex + 1).trim();
        } else {
            command = request.trim();
        }

        if ("REGISTER".equalsIgnoreCase(command)) {
            String[] args = payload.split(":", 2);
            if (args.length < 2) {
                writer.println("ERROR:Missing username or password.");
                return currentUser;
            }
            String username = args[0];
            String password = args[1];
            User newUser = UserManager.register(username, password);
            if (newUser != null) {
                writer.println("SUCCESS:User registered successfully! You can now log in.");
                System.out.println("Registered new user: " + username);
            } else {
                writer.println("ERROR:Registration failed. User already exists or invalid details.");
            }
            return currentUser;
        }

        if ("LOGIN".equalsIgnoreCase(command)) {
            String[] args = payload.split(":", 2);
            if (args.length < 2) {
                writer.println("ERROR:Missing username or password.");
                return currentUser;
            }
            String username = args[0];
            String password = args[1];
            User authedUser = UserManager.authenticate(username, password);
            if (authedUser != null) {
                writer.println("SUCCESS:Logged in as " + authedUser.getUsername());
                System.out.println("User authenticated: " + authedUser.getUsername());
                onlineUsers.put(authedUser.getUsername().toLowerCase(), writer);
                return authedUser;
            } else {
                writer.println("ERROR:Invalid username or password.");
                return currentUser;
            }
        }

        if (currentUser == null) {
            writer.println("ERROR:Please log in or register first.");
            return null;
        }

        switch (command) {
            case "1":
                List<User> friends = currentUser.getFriends();
                if (friends == null || friends.isEmpty()) {
                    writer.println("No friends added yet.");
                } else {
                    for (User f : friends) {
                        writer.println(f.getUsername());
                    }
                }
                writer.println("END_OF_LIST");
                break;
            case "2":
                if (!payload.isEmpty()) {
                    String friendToAdd = payload;
                    User f = UserManager.getUser(friendToAdd);
                    if (f != null) {
                        currentUser.addFriend(f);
                        writer.println("Added friend: " + f.getUsername());
                    } else {
                        writer.println("User '" + friendToAdd + "' not found.");
                    }
                } else {
                    writer.println("ERROR:Missing friend username.");
                }
                break;
            case "3":
                if (!payload.isEmpty()) {
                    String friendToRemove = payload;
                    writer.println("Removed friend: " + friendToRemove);
                } else {
                    writer.println("ERROR:Missing friend username.");
                }
                break;
            case "4":
                if (!payload.isEmpty()) {
                    Message msgObj;
                    if (payload.startsWith("{")) {
                        msgObj = Message.fromJson(payload);
                    } else {
                        String[] msgParts = payload.split(":", 2);
                        String recipient = msgParts[0];
                        String messageContent = msgParts.length > 1 ? msgParts[1] : "";
                        msgObj = new Message(messageContent, currentUser.getUsername(), recipient);
                    }
                    System.out.println("Server processing direct message: " + msgObj.toString());

                    PrintWriter recipientWriter = onlineUsers.get(msgObj.getRecipientId().toLowerCase());
                    if (recipientWriter != null) {
                        recipientWriter.println("INCOMING_MSG:" + msgObj.toJson());
                        writer.println("Message delivered to " + msgObj.getRecipientId() + "!");
                    } else {
                        writer.println("User '" + msgObj.getRecipientId() + "' is currently offline.");
                    }
                } else {
                    writer.println("ERROR:Invalid message format.");
                }
                break;
            case "5":
                if (!payload.isEmpty()) {
                    String groupName = payload;
                    GroupChat gc = new GroupChat();
                    gc.addUser(currentUser);
                    groupChats.put(groupName.toLowerCase(), gc);
                    writer.println("Group chat '" + groupName + "' created.");
                } else {
                    writer.println("ERROR:Missing group name.");
                }
                break;
            case "6":
                if (!payload.isEmpty()) {
                    String[] gArgs = payload.split(":", 2);
                    if (gArgs.length >= 2) {
                        String groupName = gArgs[0];
                        String userToAdd = gArgs[1];
                        GroupChat gc = groupChats.get(groupName.toLowerCase());
                        if (gc != null) {
                            User targetUser = UserManager.getUser(userToAdd);
                            if (targetUser != null) {
                                gc.addUser(targetUser);
                                writer.println("Added " + targetUser.getUsername() + " to group chat '" + groupName + "'.");
                            } else {
                                writer.println("User '" + userToAdd + "' not found.");
                            }
                        } else {
                            writer.println("Group chat '" + groupName + "' does not exist.");
                        }
                    } else {
                        writer.println("ERROR:Missing username.");
                    }
                } else {
                    writer.println("ERROR:Missing parameters.");
                }
                break;
            case "7":
                if (!payload.isEmpty()) {
                    String[] gArgs = payload.split(":", 2);
                    if (gArgs.length >= 2) {
                        String groupName = gArgs[0];
                        String userToRemove = gArgs[1];
                        GroupChat gc = groupChats.get(groupName.toLowerCase());
                        if (gc != null) {
                            User removed = gc.removeUser(userToRemove);
                            if (removed != null) {
                                writer.println("Removed " + removed.getUsername() + " from group chat '" + groupName + "'.");
                            } else {
                                writer.println("User '" + userToRemove + "' was not in group chat.");
                            }
                        } else {
                            writer.println("Group chat '" + groupName + "' does not exist.");
                        }
                    } else {
                        writer.println("ERROR:Missing username.");
                    }
                } else {
                    writer.println("ERROR:Missing parameters.");
                }
                break;
            case "8":
                if (!payload.isEmpty()) {
                    Message groupMsgObj;
                    if (payload.startsWith("{")) {
                        groupMsgObj = Message.fromJson(payload);
                    } else {
                        String[] msgParts = payload.split(":", 2);
                        String groupName = msgParts[0];
                        String messageContent = msgParts.length > 1 ? msgParts[1] : "";
                        groupMsgObj = new Message(messageContent, currentUser.getUsername(), "Group:" + groupName);
                    }
                    System.out.println("Server processing group message: " + groupMsgObj.toString());

                    String rawGroupName = groupMsgObj.getRecipientId().replace("Group:", "");
                    GroupChat gc = groupChats.get(rawGroupName.toLowerCase());
                    if (gc != null) {
                        int deliveredCount = 0;
                        for (User member : gc.getUsers()) {
                            if (!member.getUsername().equalsIgnoreCase(currentUser.getUsername())) {
                                PrintWriter memberWriter = onlineUsers.get(member.getUsername().toLowerCase());
                                if (memberWriter != null) {
                                    memberWriter.println("GROUP_MSG:" + groupMsgObj.toJson());
                                    deliveredCount++;
                                }
                            }
                        }
                        writer.println("Group message delivered to " + deliveredCount + " online member(s).");
                    } else {
                        writer.println("Group chat '" + rawGroupName + "' does not exist.");
                    }
                } else {
                    writer.println("ERROR:Invalid group message format.");
                }
                break;
            default:
                writer.println("Unknown command.");
        }

        return currentUser;
    }
}