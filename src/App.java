
import java.net.*;


public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("cli started");
        Socket cli = new Socket();
        InetSocketAddress endpoint = new InetSocketAddress("127.0.0.1", 3141);
        cli.connect(endpoint);  
        cli.close();
        
    }
}
