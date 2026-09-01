import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
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
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received: " + message);
                writer.println("Echo from server: " + message);
            }
        } catch (IOException e) {
            System.out.println("User disconnected or connection lost.");
        }
    }
}