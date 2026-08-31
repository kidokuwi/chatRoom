
import java.net.*;

public class server {
    public static void main(String[] args) {
        System.out.println("Server started");
        try (ServerSocket mainSocket = new ServerSocket()) {
            mainSocket.bind(new InetSocketAddress("0.0.0.0", 3141));  
            while (true) {
                Socket clientSocket = mainSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                
            }           
        } 
        catch (Exception e){
            System.err.println(e);
        }
    }
}
