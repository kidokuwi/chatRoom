import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        String serverHost = "127.0.0.1";
        int serverPort = 3141;
        String friendName;
        String messageText;
        String groupName;
        String userName;
        String loggedInUsername = null;

        try {
            Socket socket = new Socket(serverHost, serverPort);
            System.out.println("Connected to chat server");

            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);
            Scanner consoleScanner = new Scanner(System.in);

            while (loggedInUsername == null) {
                System.out.println("\n Welcome to Chat Room ");
                System.out.print("Choose option: 1. Login  2. Register  3. Exit: ");
                String authChoice = consoleScanner.nextLine().trim();

                if ("3".equals(authChoice) || "exit".equalsIgnoreCase(authChoice)) {
                    System.out.println("Goodbye!");
                    socket.close();
                    return;
                }

                if ("1".equals(authChoice)) {
                    System.out.print("Enter username: ");
                    String user = consoleScanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String pass = consoleScanner.nextLine().trim();

                    serverWriter.println("LOGIN:" + user + ":" + pass);
                    String response = serverReader.readLine();
                    System.out.println("Server: " + response);

                    if (response != null && response.startsWith("SUCCESS:")) {
                        loggedInUsername = user;
                    }
                } else if ("2".equals(authChoice)) {
                    System.out.print("Choose username: ");
                    String user = consoleScanner.nextLine().trim();
                    System.out.print("Choose password: ");
                    String pass = consoleScanner.nextLine().trim();

                    serverWriter.println("REGISTER:" + user + ":" + pass);
                    String response = serverReader.readLine();
                    System.out.println("Server: " + response);
                } else {
                    System.out.println("Invalid option. Please choose 1, 2, or 3.");
                }
            }

            Thread receiverThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverReader.readLine()) != null) {
                        if (line.startsWith("INCOMING_MSG:")) {
                            String json = line.substring("INCOMING_MSG:".length());
                            Message msg = Message.fromJson(json);
                            System.out.println("\n\n>>> [DIRECT MESSAGE from " + msg.getSenderId() + "]: " + msg.getContent());
                            System.out.print("Enter choice (1-9): ");
                        } else if (line.startsWith("GROUP_MSG:")) {
                            String json = line.substring("GROUP_MSG:".length());
                            Message msg = Message.fromJson(json);
                            System.out.println("\n\n>>> [" + msg.getRecipientId() + " from " + msg.getSenderId() + "]: " + msg.getContent());
                            System.out.print("Enter choice (1-9): ");
                        } else {
                            System.out.println("Server: " + line);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Connection to server closed.");
                }
            });
            receiverThread.setDaemon(true);
            receiverThread.start();

            // Main chat menu loop
            while (true) {
                System.out.println("\n Main Menu (User: " + loggedInUsername + ") ");
                System.out.print("enter request: 1. list friends 2. add friend 3. remove friend 4. send message to friend 5. create group chat 6. add user to group chat 7. remove user from group chat 8. send message to group chat 9. exit: ");
                String line = consoleScanner.nextLine().trim();

                if ("exit".equalsIgnoreCase(line) || "9".equals(line)) break;

                switch (line) {
                    case "1":
                        serverWriter.println(line);
                        break;
                    case "2":
                        System.out.print("Enter friend username to add: ");
                        friendName = consoleScanner.nextLine().trim();
                        serverWriter.println(line + ":" + friendName);
                        break;
                    case "3":
                        System.out.print("Enter friend username to remove: ");
                        friendName = consoleScanner.nextLine().trim();
                        serverWriter.println(line + ":" + friendName);
                        break;
                    case "4":
                        System.out.print("Enter friend username: ");
                        friendName = consoleScanner.nextLine().trim();
                        System.out.print("Enter message: ");
                        messageText = consoleScanner.nextLine().trim();

                        Message msgObj = new Message(messageText, loggedInUsername, friendName);
                        System.out.println("Created Message object: " + msgObj.toString());

                        serverWriter.println("4:" + msgObj.toJson());
                        break;
                    case "5":
                        System.out.print("Enter group name to create: ");
                        groupName = consoleScanner.nextLine().trim();
                        serverWriter.println(line + ":" + groupName);
                        break;
                    case "6":
                        System.out.print("Enter group name: ");
                        groupName = consoleScanner.nextLine().trim();
                        System.out.print("Enter username to add to group: ");
                        userName = consoleScanner.nextLine().trim();
                        serverWriter.println(line + ":" + groupName + ":" + userName);
                        break;
                    case "7":
                        System.out.print("Enter group name: ");
                        groupName = consoleScanner.nextLine().trim();
                        System.out.print("Enter username to remove from group: ");
                        userName = consoleScanner.nextLine().trim();
                        serverWriter.println(line + ":" + groupName + ":" + userName);
                        break;
                    case "8":
                        System.out.print("Enter group name: ");
                        groupName = consoleScanner.nextLine().trim();
                        System.out.print("Enter group message: ");
                        messageText = consoleScanner.nextLine().trim();

                        Message groupMsgObj = new Message(messageText, loggedInUsername, "Group:" + groupName);
                        System.out.println("Created Group Message object: " + groupMsgObj.toString());

                        serverWriter.println("8:" + groupMsgObj.toJson());
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                        break;
                }
            }

            socket.close();
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}