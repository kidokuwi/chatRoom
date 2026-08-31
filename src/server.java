
import java.net.*;
import java.util.*;
public class server {
    public static void main(String[] args) {
        System.out.println("Server started");
        List<Integer> ids = new ArrayList<>();
        try (ServerSocket mainSocket = new ServerSocket()) {
            mainSocket.bind(new InetSocketAddress("0.0.0.0", 3141));  
            while (true) {
                Socket clientSocket = mainSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                int clientId = ids.size() + 1;
                ids.add(clientId);
                Thread clientThread = new Thread(() -> handleClient(clientSocket, clientId));
                clientThread.start();
            }           
        } 
        catch (Exception e){
            System.err.println(e);
        }
    }
    private static void handleClient(Socket clientSocket, int id) {
        try {
            
        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
