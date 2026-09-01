import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        String serverHost = "127.0.0.1";
        int serverPort = 3141;

        try (Socket socket = new Socket(serverHost, serverPort)) {
            System.out.println("Connected to chat server");

            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);
            Scanner consoleScanner = new Scanner(System.in);

            while (true) {
                System.out.print("Type message: ");
                String line = consoleScanner.nextLine();
                if ("exit".equalsIgnoreCase(line)) break;

                serverWriter.println(line);
                System.out.println(serverReader.readLine());
            }
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}