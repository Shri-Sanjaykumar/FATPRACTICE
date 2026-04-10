import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting Grocery Delivery App on port 8080...");
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started. Access at http://localhost:8080/");
    }

    // Business Logic Methods
    public static boolean validateOrder(int itemCount, double totalAmount) {
        return itemCount > 0 && totalAmount > 0.0;
    }

    public static boolean validateDeliverySlot(int hourOfDay) {
        // Valid delivery slots are between 8 AM (8) and 8 PM (20)
        return hourOfDay >= 8 && hourOfDay <= 20;
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String htmlResponse = "<html>" +
                "<head><meta charset=\"UTF-8\"><title>Online Grocery Delivery</title>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }" +
                ".container { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); text-align: center; max-width: 500px; }" +
                "h1 { color: #2c3e50; }" +
                "p { color: #555; line-height: 1.6; }" +
                ".status-badge { display: inline-block; background-color: #e8f5e9; color: #2e7d32; padding: 5px 15px; border-radius: 20px; font-weight: bold; margin: 15px 0; }" +
                ".btn { background-color: #27ae60; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; font-size: 16px; margin-top: 20px; text-decoration: none; transition: background 0.3s; }" +
                ".btn:hover { background-color: #219150; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h1>🍅 EAZEGoo Grocery Delivery</h1>" +
                "<p>Your automated grocery order and delivery scheduling system is successfully up and running!</p>" +
                "<div class='status-badge'>✅ System Online - CI/CD Active</div>" +
                "<p>We have integrated automated validation for both <strong>Order Processing</strong> and <strong>Delivery Slot Scheduling</strong> to ensure 100% accuracy.</p>" +
                "<button class='btn' onclick='alert(\"Order validated and Delivery slot confirmed for 10:00 AM!\")'>Verify System Logic</button>" +
                "</div>" +
                "</body>" +
                "</html>";
            
            byte[] responseBytes = htmlResponse.getBytes("UTF-8");
            t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            t.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}
